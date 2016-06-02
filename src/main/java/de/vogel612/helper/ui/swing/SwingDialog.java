package de.vogel612.helper.ui.swing;

import de.vogel612.helper.ui.Dialog;

import static javax.swing.JOptionPane.*;

/**
 * Created by vogel612 on 02.06.16.
 */
public class SwingDialog implements Dialog {
    @Override
    public void info(String title, String message) {
        showConfirmDialog(null, message, title, OK_OPTION);
    }

    @Override
    public void info(String title, String message, Runnable okCallback) {
        int result = showConfirmDialog(null, message, title, OK_OPTION);
        if (result == OK_OPTION) {
            okCallback.run();
        }
    }

    @Override
    public void warn(String title, String message, Runnable okCallback, Runnable cancelCallback) {
        int result = showConfirmDialog(null, message, title, OK_CANCEL_OPTION);
        if (result == OK_OPTION) {
            okCallback.run();
        } else if (result == CANCEL_OPTION) {
            cancelCallback.run();
        }
    }

    @Override
    public void warn(String title, String message, Runnable okCallback, Runnable cancelCallback, Runnable ignoreCallback) {
        int result = showConfirmDialog(null, message, title, YES_NO_CANCEL_OPTION);
        if (result == YES_OPTION) {
            okCallback.run();
        } else if (result == NO_OPTION) {
            cancelCallback.run();
        } else if (result == CANCEL_OPTION) {
            ignoreCallback.run();
        }
    }
}
