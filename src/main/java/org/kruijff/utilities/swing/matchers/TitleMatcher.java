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
package org.kruijff.utilities.swing.matchers;

import java.awt.*;
import java.lang.reflect.*;
import java.util.*;
import org.kruijff.utilities.swing.*;

/**
 * @author Alex de Kruijff {@literal <swingutil@akruijff.dds.nl>}
 */
public class TitleMatcher<T extends Window>
        implements SwingUtil.Matcher<T> {

    private final String title;
    private final Class<T> type;

    public TitleMatcher(String title, Class<T> type) {
        this.title = title;
        this.type = type;
    }

    @Override
    public boolean childMatches(Component child) {
        if (!type.isAssignableFrom(child.getClass()))
            return false;

        try {
            Class<?>[] parameterTypes = new Class<?>[0];
            Class<?> returnType = String.class;
            for (Method m : child.getClass().getMethods())
                if (methodMatches(m, "getTitle", parameterTypes, returnType))
                    return title.equals((String) m.invoke(child));
            return false;
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            throw new RuntimeException(ex);
        }
    }

    private boolean methodMatches(Method m, String name, Class<?>[] parameterTypes, Class<?> returnType) {
        return m.getName().equals(name)
                && Arrays.equals(m.getParameterTypes(), parameterTypes)
                && m.getReturnType().isAssignableFrom(returnType);
    }
}
