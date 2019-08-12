/*
 * Copyright Alex de Kruijff <swingutil@akruijff.dds.nl>
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

import java.io.*;
import java.util.concurrent.*;
import javax.swing.*;
import javax.swing.filechooser.*;
import org.awaitility.core.*;
import org.hamcrest.core.*;

public class JFileChooserUtil {

    private final JFileChooser fileChooser;
    private final JTextField fileNameInputField;
    private final JComboBox<FileFilter> fileFilterComboBox;
    private final JButton upButton, homeButton, createFolderButton, listButton, detailsButton, openButton, cancleButton;
    private final ConditionFactory await;

    public JFileChooserUtil(JFileChooser fileChooser, long timeout) {
        this(fileChooser, timeout, TimeUnit.MILLISECONDS);
    }

    public JFileChooserUtil(JFileChooser fileChooser, long timeout, TimeUnit unit) {
        this(fileChooser, await().atMost(timeout, unit));
    }

    @SuppressWarnings("unchecked")
    public JFileChooserUtil(JFileChooser fileChooser, ConditionFactory await) {
        this.fileChooser = fileChooser;
        this.await = await;

        fileNameInputField = fetchChildIndexed(fileChooser, 0, JTextField.class);
        fileFilterComboBox = fetchChildIndexed(fileChooser, 1, JComboBox.class);

        upButton = fetchChildIndexed(fileChooser, 0, JButton.class);
        homeButton = fetchChildIndexed(fileChooser, 1, JButton.class);
        createFolderButton = fetchChildIndexed(fileChooser, 2, JButton.class);
        listButton = fetchChildIndexed(fileChooser, 3, JButton.class);
        detailsButton = fetchChildIndexed(fileChooser, 4, JButton.class);
        openButton = fetchChildIndexed(fileChooser, 9, JButton.class);
        cancleButton = fetchChildIndexed(fileChooser, 10, JButton.class);
    }

    public void waitUtilVisible() {
        await.until(fileChooser::isVisible);
    }

    public void setFilename(String filename) {
        SwingUtilities.invokeLater(() -> fileNameInputField.setText(filename));
        await.until(() -> fileNameInputField.getText().equals(filename));
    }

    public FileFilter[] getFileTypeOptions() {
        FileFilter[] arr = new FileFilter[fileFilterComboBox.getItemCount()];
        for (int i = 0; i < arr.length; ++i)
            arr[i] = fileFilterComboBox.getItemAt(i);
        return arr;
    }

    public void setFileTypeOption(FileFilter option) {
        SwingUtilities.invokeLater(() -> fileFilterComboBox.setSelectedItem(option));
        await.until(() -> fileFilterComboBox.getSelectedItem().equals(option));
    }

    public void doClickUpButton() {
        SwingUtilities.invokeLater(() -> upButton.doClick());
    }

    public void doClickHomeButton() {
        SwingUtilities.invokeLater(() -> homeButton.doClick());
    }

    public void doClickCreateFolderButton() {
        SwingUtilities.invokeLater(() -> createFolderButton.doClick());
    }

    public void doClickListButton() {
        SwingUtilities.invokeLater(() -> listButton.doClick());
    }

    public void doClickDetailsButton() {
        SwingUtilities.invokeLater(() -> detailsButton.doClick());
    }

    public File doClickOpenButtonAndWait(Callable<File> callable) {
        SwingUtilities.invokeLater(() -> openButton.doClick());
        await.until(() -> !fileChooser.isShowing());
        return await.until(callable, new IsNot<>(new IsNull<File>()));
    }

    public void doClickCancleButton() {
        SwingUtilities.invokeLater(() -> cancleButton.doClick());
        await.until(() -> !fileChooser.isVisible());
    }
}
