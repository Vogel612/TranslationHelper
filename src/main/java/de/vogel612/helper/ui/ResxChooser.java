package de.vogel612.helper.ui;

import de.vogel612.helper.ui.common.ResxChooserCommon.ResxChooserEvent;

import java.nio.file.Path;
import java.util.function.Consumer;

/**
 * Created by vogel612 on 02.03.16.
 */
public interface ResxChooser {
    void setFileset(Path fileset);

    void hide();

    void show();

    void addCompletionListener(Consumer<ResxChooserEvent> listener);
}
