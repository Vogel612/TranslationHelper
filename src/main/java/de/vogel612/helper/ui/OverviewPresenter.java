package de.vogel612.helper.ui;

import javafx.stage.FileChooser.ExtensionFilter;

import java.nio.file.Path;

import static de.vogel612.helper.ui.jfx.JFXDialog.DIALOG;

public class OverviewPresenter {

    public static final String DEFAULT_TARGET_LOCALE = "de";
    public static final String DEFAULT_ROOT_LOCALE = "";

    private final OverviewView view;
    private final ProjectView project;

    public OverviewPresenter(final OverviewView v, ProjectView pv) {
        view = v;
        project = pv;
        pv.addResourceSetListener(resourceSet -> {
            view.loadFiles(resourceSet);
            view.show();
            view.selectLocale();
        });
        view.addFileRequestListener(this::fileChoosing);
    }


    public void show() {
        view.show();
    }

    /**
     * Allows the user to choose an arbitrary *.regex file and asks them to choose out of the available locales for
     * Left
     * and Right in the view.
     */
    public void fileChoosing() {
        project.hide();
        view.hide();
        final Path chosenFile = DIALOG.chooseFile("Select a resx or thp file",
                new ExtensionFilter("Resource file", "*.resx"),
                new ExtensionFilter("TranslationHelper Project file", "*.thp"));
        if (chosenFile.getFileName().toString().endsWith(".thp")) {
            project.loadProject(chosenFile);
            project.show();
        } else {
            view.loadFiles(chosenFile);
            view.show();
            view.selectLocale();
        }
    }
}
