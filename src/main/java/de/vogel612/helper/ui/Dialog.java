package de.vogel612.helper.ui;

/**
 * Interface specifying the options that this application needs from dialogues.
 */
public interface Dialog {

    /**
     * Displays an info dialog, which has a title, a message and an OK button. That button doesn't do jack
     *
     * @param title   The title to use for the dialog
     * @param message The message of the dialog
     */
    void info(final String title, final String message);

    /**
     * Displays an info dialog, which has a title, a message and an OK button.
     * The given callback is run when the OK button is pressed.
     *
     * @param title      The title to use for the dialog
     * @param message    The message of the dialog
     * @param okCallback Something to run when the user clicks OK
     */
    void info(final String title, final String message, final Runnable okCallback);

    /**
     * Displays a warning dialog with title, message and the options "OK" and "Cancel". T
     * The given callbacks are run when the respective buttons are pressed.
     *
     * @param title          The title to use for the dialog
     * @param message        The message of the dialog
     * @param okCallback     Something to run when the user clicks OK
     * @param cancelCallback Something to run when the user clicks Cancel
     */
    void warn(final String title, final String message, final Runnable okCallback, final Runnable cancelCallback);

    /**
     * Displays a warning dialog with title, message and the options "OK", "Cancel" and "Ignore".
     * The given callbacks are run when the respective buttons are pressed.
     *
     * @param title          The title to use for the dialog
     * @param message        The message of the dialog
     * @param okCallback     Something to run when the user clicks OK
     * @param cancelCallback Something to run when the user clicks Cancel
     * @param ignoreCallback Something to run when the user clicks Ignore
     */
    void warn(final String title, final String message, final Runnable okCallback, final Runnable cancelCallback, final Runnable ignoreCallback);
}
