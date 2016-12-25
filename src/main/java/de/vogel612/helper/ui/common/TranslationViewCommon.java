package de.vogel612.helper.ui.common;

import de.vogel612.helper.data.Translation;
import de.vogel612.helper.ui.TranslationView;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Encapsulates common functionality for {@link TranslationView Translation Views} into an abstract class.<br/>
 * <p>Encapsulated is all functionality related to adding and maintaining Listeners. They are kept in {@link
 * #translationAbortListeners} and {@link #translationSubmitListeners} respectively and available to extending classes.
 * Furthermore it contains a title-format string to use with the currently translated Translation's {@link
 * Translation#key key} and {@link Translation#locale locale}</p>
 */
public abstract class TranslationViewCommon implements TranslationView {
    protected static final String TITLE_FORMAT = "Translating - %s to %s";
    protected final Set<Runnable> translationAbortListeners = new HashSet<>();
    protected final Set<Consumer<Translation>> translationSubmitListeners = new HashSet<>();

    @Override
    public final void addTranslationSubmitListener(Consumer<Translation> listener) {
        translationSubmitListeners.add(listener);
    }

    @Override
    public final void addTranslationAbortListener(Runnable listener) {
        translationAbortListeners.add(listener);
    }
}
