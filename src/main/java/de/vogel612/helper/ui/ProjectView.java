package de.vogel612.helper.ui;

import de.vogel612.helper.data.ResourceSet;

import java.nio.file.Path;
import java.util.function.Consumer;

/**
 * Created by vogel612 on 29.07.16.
 */
public interface ProjectView {

    void addResourceSetListener(Consumer<ResourceSet> listener);

    void loadProject(Path file);

    void show();
    void hide();
}
