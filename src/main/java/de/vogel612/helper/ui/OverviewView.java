package de.vogel612.helper.ui;

import de.vogel612.helper.data.Side;
import de.vogel612.helper.data.Translation;

import java.awt.event.WindowEvent;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface OverviewView {

    void addWindowClosingListener(Consumer<WindowEvent> listener);

    void initialize();

    void show();

    void rebuildWith(List<Translation> left, List<Translation> right);

    void displayError(String title, String errorMessage);

    void hide();

    void addLocaleChangeRequestListener(BiConsumer<String, Side> listener);

    void addTranslationRequestListener(Consumer<String> listener);

    void addSaveRequestListener(Runnable listener);
}
