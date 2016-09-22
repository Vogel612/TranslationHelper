package de.vogel612.helper.ui;

import de.vogel612.helper.ui.jfx.JFXLocaleChooserView.LocaleChoiceEvent;

import java.util.function.Consumer;

public interface LocaleChooser {

    void hide();

    void show();

    void addCompletionListener(Consumer<LocaleChoiceEvent> listener);
}
