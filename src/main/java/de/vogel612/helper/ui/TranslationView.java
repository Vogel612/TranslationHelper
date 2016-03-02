package de.vogel612.helper.ui;

import de.vogel612.helper.data.Translation;

import java.util.function.Consumer;

/**
 * Created by vogel612 on 02.03.16.
 */
public interface TranslationView {
    void addTranslationSubmitListener(Consumer<Translation> listener);

    void addTranslationAbortListener(Runnable listener);

    void show();

    void hide();

    void setRequestedTranslation(Translation left, Translation right);
}
