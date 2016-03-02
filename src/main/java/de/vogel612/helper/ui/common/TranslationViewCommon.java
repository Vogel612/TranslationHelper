package de.vogel612.helper.ui.common;

import de.vogel612.helper.data.Translation;
import de.vogel612.helper.ui.TranslationView;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Created by vogel612 on 02.03.16.
 */
public abstract class TranslationViewCommon implements TranslationView {
    protected static final String TITLE_FORMAT = "Translating - %s to %s";
    protected final Set<Runnable> translationAbortListeners = new HashSet<>();
    protected final Set<Consumer<Translation>> translationSubmitListeners = new HashSet<>();

    @Override
    public abstract void show();

    @Override
    public abstract void hide();

    @Override
    public abstract void setRequestedTranslation(Translation left, Translation right);

    @Override
    public final void addTranslationSubmitListener(Consumer<Translation> listener) {
        translationSubmitListeners.add(listener);
    }

    @Override
    public final void addTranslationAbortListener(Runnable listener) {
        translationAbortListeners.add(listener);
    }
}
