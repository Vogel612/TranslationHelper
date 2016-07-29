package de.vogel612.helper.ui;

/**
 * Interface specifying the options that this application needs from dialogues.
 */
public interface Dialog {

    void info(final String title, final String message);

    void info(final String title, final String message, final Runnable okCallback);

    void warn(final String title, final String message, final Runnable okCallback, final Runnable cancelCallback);

    void warn(final String title, final String message, final Runnable okCallback, final Runnable cancelCallback, final Runnable ignoreCallback);
}
