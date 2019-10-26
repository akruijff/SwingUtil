/*
 * Copyright Alex de Kruijff {@literal <swingutil@akruijff.dds.nl>}
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its
 *    contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package org.kruijff.utilities.swing;

import java.awt.*;
import java.util.*;
import java.util.concurrent.*;
import javax.swing.*;
import org.awaitility.*;
import org.awaitility.core.*;
import org.hamcrest.core.*;
import org.kruijff.utilities.swing.exceptions.*;
import org.kruijff.utilities.swing.fetchers.*;
import org.kruijff.utilities.swing.matchers.*;
import org.kruijff.utilities.swing.visitors.*;

/**
 * @author Alex de Kruijff {@literal <swingutil@akruijff.dds.nl>}
 */
public class SwingUtil {

    private final ConditionFactory await;

    private static <T> IsNot<T> isNotNull() {
        return new IsNot<>(new IsNull<>());
    }

    public SwingUtil(long timeout) {
        this(timeout, TimeUnit.MILLISECONDS);
    }

    public SwingUtil(long timeout, TimeUnit unit) {
        this(Awaitility.await().atMost(timeout, unit));
    }

    public SwingUtil(ConditionFactory await) {
        this.await = await;
    }

    public <T extends Window> T fetchWindowTitled(Window parent, String title, Class<T> type) {
        try {
            Searcher<T> searcher = new Searcher<>(new WindowFetcher<>(), new TitleMatcher<>(title, type));
            return await.until(() -> searcher.searchChilderen(parent), isNotNull());
        } catch (ConditionTimeoutException ex) {
            throw new ChildNotFoundException(title, ex);
        }
    }

    public <T extends Component> T fetchWindowIndexed(Window parent, int index, Class<T> type) {
        try {
            return await.until(() -> {
                Searcher<T> searcher = new Searcher<>(new WindowFetcher<>(), new IndexMatcher<>(index, type));
                return searcher.searchChilderen(parent);
            }, isNotNull());
        } catch (ConditionTimeoutException ex) {
            throw new ChildNotFoundException(index, ex);
        }
    }

    public <T extends Window> void visitWindows(Window parent, Class<T> type, ComponentVisitor<T> visitor) {
        Searcher<T> searcher = new Searcher<>(new WindowFetcher<>(), new TypeMatcher<>(type));
        searcher.visitChilderen(parent, visitor);
    }

    public void clickButton(Component parent, String name) {
        AbstractButton button = fetchChildNamed(parent, name, AbstractButton.class);
        SwingUtilities.invokeLater(() -> button.doClick());
    }

    public <T extends Component> T fetchChildNamed(Component parent, String name, Class<T> type) {
        try {
            Searcher<T> searcher = new Searcher<>(new DefaultChilderenFetcher<>(), new NameMatcher<>(name, type));
            return await.until(() -> searcher.searchChilderen(parent), isNotNull());
        } catch (ConditionTimeoutException ex) {
            throw new ChildNotFoundException(name);
        }
    }

    public <T extends Component> T fetchChildIndexed(Component parent, int index, Class<T> type) {
        try {
            return await.until(() -> {
                Searcher<T> searcher = new Searcher<>(new DefaultChilderenFetcher<>(), new IndexMatcher<>(index, type));
                return searcher.searchChilderen(parent);
            }, isNotNull());
        } catch (ConditionTimeoutException ex) {
            throw new ChildNotFoundException(index);
        }
    }

    public <T extends Window> void visitChild(Window parent, Class<T> type, ComponentVisitor<T> visitor) {
        Searcher<T> searcher = new Searcher<>(new DefaultChilderenFetcher<>(), new TypeMatcher<>(type));
        searcher.visitChilderen(parent, visitor);
    }

    private static class Searcher<T extends Component> {

        private final Fetcher<T> fetcher;
        private final Matcher<T> matcher;

        private Searcher(Fetcher<T> fetcher, Matcher<T> matcher) {
            this.fetcher = fetcher;
            this.matcher = matcher;
        }

        @SuppressWarnings("unchecked")
        private void visitChilderen(Component parent, ComponentVisitor<T> visitor) {
            Arrays.stream(fetcher.getChilderen(parent))
                    .filter(c -> c instanceof Window == false || c.isDisplayable())
                    .forEach(t -> {
                        if (matcher.childMatches(t))
                            visitor.visit((T) t);
                        visitChilderen(t, visitor);
                    });
        }

        @SuppressWarnings("unchecked")
        private T searchChilderen(Component parent) {
            for (Component child : fetcher.getChilderen(parent)) {
                T found = searchChild(child);
                if (found != null)
                    return found;
            }
            return null;
        }

        @SuppressWarnings("unchecked")
        private T searchChild(Component child) {
            return child instanceof Window && !child.isDisplayable() ? null
                    : matcher.childMatches(child) ? (T) child
                    : searchChilderen(child);
        }
    }

    @SuppressWarnings("PublicInnerClass")
    public static interface Fetcher<T extends Component> {

        Component[] getChilderen(Component parent);
    }

    @SuppressWarnings("PublicInnerClass")
    public static interface Matcher<T extends Component> {

        public boolean childMatches(Component c);
    }
}
