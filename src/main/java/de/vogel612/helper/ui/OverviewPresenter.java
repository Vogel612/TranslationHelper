package de.vogel612.helper.ui;

import de.vogel612.helper.data.Side;
import de.vogel612.helper.data.Translation;
import javafx.application.Platform;
import javafx.stage.FileChooser.ExtensionFilter;

import java.io.IOException;
import java.nio.file.Path;

import static de.vogel612.helper.ui.jfx.JFXDialog.DIALOG;

public class OverviewPresenter {

    public static final String DEFAULT_TARGET_LOCALE = "de";
    public static final String DEFAULT_ROOT_LOCALE = "";

    private final OverviewView view;

    public OverviewPresenter(final OverviewView v) {
        view = v;
    }


    public void show() {
        view.show();
    }

    public void onException(final Exception e, final String message) {
        Platform.runLater(() -> DIALOG.info(message, e.getMessage()));
        // FIXME: Allow termination for unrecoverable exception
    }

    /**
     * Allows the user to choose an arbitrary *.regex file and asks them to choose out of the available locales for
     * Left
     * and Right in the view.
     */
    public void fileChoosing() {
        final Path chosenFile = DIALOG.chooseFile("Select a resx or thp file",
                new ExtensionFilter("Resource file", "resx"),
                new ExtensionFilter("TranslationHelper Project file", "thp"));
        if (chosenFile.endsWith(".thp")) {
            // FIXME show project view in case this is a thp file,
            // FIXME show the overview and trigger locale chooser otherwise
        } else {
            view.loadFiles(chosenFile);
            view.selectLocale();
        }
    }
}
