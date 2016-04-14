package de.vogel612.helper.ui;

import de.vogel612.helper.ui.common.ResxChooserCommon.ResxChooserEvent;

import java.nio.file.Path;
import java.util.function.Consumer;

public interface ResxChooser {
    void setFileset(Path fileset);

    void hide();

    void show();

    void addCompletionListener(Consumer<ResxChooserEvent> listener);
}
