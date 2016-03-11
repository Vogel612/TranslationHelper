package de.vogel612.helper.ui.common;

import de.vogel612.helper.ui.OverviewView;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public abstract class OverviewViewCommon implements OverviewView {

    protected final Set<Consumer<String>> translationRequestListeners = new HashSet<>();
    protected final Set<Runnable> windowCloseListeners = new HashSet<>();
    protected final Set<Runnable> langChoiceRequestListeners = new HashSet<>();
    protected final Set<Runnable> saveRequestListeners = new HashSet<>();

    @Override
    public final void addWindowClosingListener(Runnable listener) {
        windowCloseListeners.add(listener);
    }

    @Override
    public final void addLanguageRequestListener(Runnable listener) {
        langChoiceRequestListeners.add(listener);
    }

    @Override
    public final void addTranslationRequestListener(Consumer<String> listener) {
        translationRequestListeners.add(listener);
    }

    @Override
    public final void addSaveRequestListener(Runnable listener) {
        saveRequestListeners.add(listener);
    }

}
