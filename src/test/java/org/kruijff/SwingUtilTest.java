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
package org.kruijff;

import java.awt.event.ActionEvent;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import static javax.swing.JOptionPane.PLAIN_MESSAGE;
import static javax.swing.JOptionPane.showMessageDialog;
import static javax.swing.SwingUtilities.invokeLater;
import static org.awaitility.Awaitility.await;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.kruijff.utilities.swing.SwingUtil;
import org.kruijff.utilities.swing.exceptions.ChildNotFoundException;

public class SwingUtilTest {

    private static final int DELAY = 200;
    private static final int TIMEOUT = 1000;

    private JFrame frame;
    private SwingUtil util;

    @Before
    public void setup() {
        frame = new JFrame();
        util = new SwingUtil(TIMEOUT);
        invokeLater(() -> {
            await().until(() -> frame != null);
            sleep(DELAY);
            addLabel("label A", "LA");
            addLabel("label B", "LB");
            addButton("button A", "BA", new AbstractAction("Action") {
                  @Override
                  public void actionPerformed(ActionEvent e) {
                      invokeLater(() -> showMessageDialog(frame, "Hallo", "Title A", PLAIN_MESSAGE));
                      invokeLater(() -> showMessageDialog(frame, "Hallo", "Title B", PLAIN_MESSAGE));
                  }
              });
        });
    }

    //<editor-fold defaultstate="collapsed" desc="Helper methods for setup">
    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ex) {
            Logger.getLogger(SwingUtilTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void addLabel(String name, String value) {
        JLabel label = new JLabel();
        label.setName(name);
        label.setText(value);
        frame.add(label);
    }

    private void addButton(String name, String value, Action action) {
        JButton button = new JButton(action);
        button.setName(name);
        button.setText(value);
        frame.add(button);
    }
    //</editor-fold>

    @After
    public void teardown() {
        frame = null;
        util = null;
    }

    @Test(expected = ChildNotFoundException.class)
    public void fetchChildNames_ChildDoesNotExist() {
        util.fetchChildNamed(frame, "foo", JLabel.class);
    }

    @Test
    public void fetchChildNames_ChildExists() {
        JLabel label = util.fetchChildNamed(frame, "label A", JLabel.class);
        assertEquals("LA", label.getText());
    }

    @Test(expected = ChildNotFoundException.class)
    public void fetchChildIndexed_ChildDoesNotExist() {
        JLabel label = util.fetchChildIndexed(frame, 2, JLabel.class);
        assertEquals("LA", label.getText());
    }

    @Test
    public void fetchChildIndexed_ChildExists() {
        JLabel label = util.fetchChildIndexed(frame, 1, JLabel.class);
        assertEquals("LB", label.getText());
    }

    @Test(expected = ChildNotFoundException.class)
    public void button_ChildDoesNotExists() {
        util.clickButton(frame, "foo");
    }

    @Test(expected = ChildNotFoundException.class)
    public void fetchWindowTitled_ChildDoesNotExist() {
        util.fetchWindowTitled(frame, "windows A", JFrame.class);
    }

    @Test
    public void fetchWindowTitled_ChildExist() {
        util.clickButton(frame, "button A");
        JDialog dialog = util.fetchWindowTitled(frame, "Title A", JDialog.class);
        assertEquals("Title A", dialog.getTitle());
    }

    @Test(expected = ChildNotFoundException.class)
    public void fetchWindowIndexed_ChildDoesNotExists() {
        util.fetchWindowIndexed(frame, 0, JDialog.class);
    }

    @Test
    public void fetchWindowIndexed_ChildExists() {
        util.clickButton(frame, "button A");
        JDialog dialog = util.fetchWindowIndexed(frame, 0, JDialog.class);
        assertEquals("Title A", dialog.getTitle());
    }
}
