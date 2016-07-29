package de.vogel612.helper.ui;

import java.nio.file.Path;

/**
 * Created by vogel612 on 29.07.16.
 */
public interface ProjectView {

    void loadProject(Path file);

    public void show();
    public void hide();
}
