package de.vogel612.helper.ui;

import de.vogel612.helper.data.ResourceSet;
import de.vogel612.helper.data.Translation;

import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;

/**
 * Created by vogel612 on 02.03.16.
 */
public interface OverviewView {
    void addFileRequestListener(Runnable listener);

    void show();

    void rebuildWith(List<Translation> left, List<Translation> right);

    void hide();

    void rebuild();

    void loadFiles(Path resxFile);

    void loadFiles(ResourceSet resourceSet);

    void selectLocale();
}
