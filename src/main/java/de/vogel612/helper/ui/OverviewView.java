package de.vogel612.helper.ui;

import de.vogel612.helper.data.Translation;

import java.awt.event.WindowEvent;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public abstract class OverviewView {

    protected final Set<Consumer<String>> translationRequestListeners = new HashSet<>();
    protected final Set<Consumer<WindowEvent>> windowCloseListeners = new HashSet<>();
    protected final Set<Runnable> langChoiceRequestListeners = new HashSet<>();
    protected final Set<Runnable> saveRequestListeners = new HashSet<>();

    public final void addWindowClosingListener(Consumer<WindowEvent> listener) {
        windowCloseListeners.add(listener);
    }

    public final void addLanguageRequestListener(Runnable listener) {
        langChoiceRequestListeners.add(listener);
    }

    public final void addTranslationRequestListener(Consumer<String> listener) {
        translationRequestListeners.add(listener);
    }

    public final void addSaveRequestListener(Runnable listener) {
        saveRequestListeners.add(listener);
    }

    public abstract void initialize();

    public abstract void show();

    public abstract void rebuildWith(List<Translation> left, List<Translation> right);

    public abstract void displayError(String title, String errorMessage);

    public abstract void hide();
}
