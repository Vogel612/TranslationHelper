package de.vogel612.helper.ui;

import de.vogel612.helper.data.Translation;

import java.util.List;
import java.util.function.Consumer;

/**
 * Created by vogel612 on 02.03.16.
 */
public interface OverviewView {
    void addWindowClosingListener(Runnable listener);

    void addLanguageRequestListener(Runnable listener);

    void addFileRequestListener(Runnable listener);

    void addTranslationRequestListener(Consumer<String> listener);

    void addSaveRequestListener(Runnable listener);

    void initialize();

    void show();

    void rebuildWith(List<Translation> left, List<Translation> right);

    void hide();
}
