package de.vogel612.helper.ui.jfx;


import de.vogel612.helper.data.FilesetModel;
import de.vogel612.helper.data.ResourceSet;
import de.vogel612.helper.data.Side;
import de.vogel612.helper.data.Translation;
import de.vogel612.helper.ui.LocaleChooser;
import de.vogel612.helper.ui.OverviewView;
import de.vogel612.helper.ui.TranslationView;
import de.vogel612.helper.ui.jfx.controller.JFXTranslationOverviewController;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.*;

import static de.vogel612.helper.data.util.DataUtilities.FALLBACK_LOCALE;
import static de.vogel612.helper.ui.jfx.JFXDialog.DIALOG;
import static de.vogel612.helper.ui.jfx.JFXLocaleChooserView.*;

public class JFXTranslationOverviewView implements OverviewView {
    private final Set<Runnable> windowCloseListeners = new HashSet<>();

    private final Map<Side, String> chosenLocale = new EnumMap<>(Side.class);
    private final LocaleChooser localeChooser;
    private final FilesetModel model;
    private final TranslationView translationView;

    private final JFXTranslationOverviewController controller;
    private final Scene ui;
    private final Stage stage;
    private final Set<Runnable> fileRequestListeners = new HashSet<>();

    public JFXTranslationOverviewView(LocaleChooser localeChooser, FilesetModel model, TranslationView translationView, Stage stage, URL fxml) throws IOException {
        this.localeChooser = localeChooser;
        this.model = model;
        this.translationView = translationView;
        this.stage = stage;

        this.stage.setOnCloseRequest(evt -> windowCloseListeners.forEach(Runnable::run));

        this.translationView.addTranslationSubmitListener(this::onTranslationSubmit);
        this.translationView.addTranslationAbortListener(this::onTranslationAbort);
        this.localeChooser.addCompletionListener(this::fileChoiceCompletion);
        this.model.addParseCompletionListener(this::onParseCompletion);

        FXMLLoader loader = new FXMLLoader(fxml);
        ui = new Scene(loader.load());
        ui.getStylesheets().clear();
        // this works fine
        ui.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        controller = loader.getController();
        // there's so much fun to be had when this blows up ...

        windowCloseListeners.add(this::onWindowCloseRequest);
        controller.addSaveRequestListener(this::onSaveRequest);
        controller.addTranslationRequestListener(this::onTranslateRequest);
        controller.addFileRequestListener(this::onFileRequest);
        controller.addLangChoiceListener(this::selectLocale);
    }

    @Override
    public void addFileRequestListener(Runnable listener) { this.fileRequestListeners.add(listener); }

    private void onFileRequest() {
        if (model.isDirty()) {
            DIALOG.warn("Unsaved Changes",
                    "You have unsaved changes. Do you wish to save them before changing the resx-fileset?",
                    () -> {
                        this.onSaveRequest();
                        fileRequestListeners.forEach(Runnable::run);
                    },
                    () -> fileRequestListeners.forEach(Runnable::run)
            );
        } else {
            fileRequestListeners.forEach(Runnable::run);
        }
    }

    public void onParseCompletion() {
        rebuild();
    }

    public void onTranslationAbort() {
        translationView.hide();
        show();
    }

    public void onTranslationSubmit(final Translation t) {
        translationView.hide();
        model.updateTranslation(t.getLocale(), t.getKey(), t.getValue());
        rebuild();
    }

    public void onTranslateRequest(final String key) {
        translationView.setRequestedTranslation(
                model.getSingleTranslation(chosenLocale.getOrDefault(Side.LEFT, FALLBACK_LOCALE), key),
                model.getSingleTranslation(chosenLocale.getOrDefault(Side.RIGHT, FALLBACK_LOCALE), key)
        );
        translationView.show();
    }

    public void onSaveRequest() {
        try {
            model.saveAll();
            Platform.runLater(() ->
                    DIALOG.info("Save success!", "Saving your changes to all resx files completed successfully"));
        } catch (IOException e) {
            e.printStackTrace(System.err);
            // FIXME onException(e, "Could not save File");
        }
    }

    private void onWindowCloseRequest() {
        if (model.isDirty()) {
            DIALOG.warn("Unsaved Changes", "You have unsaved changes. Do you wish to save before exiting?",
                    () -> {
                        this.onSaveRequest();
                        System.exit(0);
                    }, () -> {
                        System.exit(0); // wonder why stuff still blows up?
                    }
            );
        } else {
            System.exit(0);
        }
        // FIXME allow preventing to close
    }


    @Override
    public void show() {
        Platform.runLater(() -> {
            stage.setScene(ui);
            stage.show();
        });
    }

    @Override
    public void rebuildWith(List<Translation> left, List<Translation> right) {
        controller.rebuildWith(left, right);
    }

    @Override
    public void hide() {
        Platform.runLater(stage::hide);
    }

    private void fileChoiceCompletion(LocaleChoiceEvent evt) {
        localeChooser.hide();

        chosenLocale.put(Side.LEFT, evt.getLeftLocale());
        chosenLocale.put(Side.RIGHT, evt.getRightLocale());
        rebuild();
    }

    @Override
    public void rebuild() {
        List<Translation> left = model.getTranslations(chosenLocale.getOrDefault(Side.LEFT, FALLBACK_LOCALE));
        List<Translation> right = model.getTranslations(chosenLocale.getOrDefault(Side.RIGHT, FALLBACK_LOCALE));
        Platform.runLater(() -> {
            controller.rebuildWith(left, right);
            show();
        });
    }

    @Override
    public void loadFiles(final Path resxFile) {
        try {
            model.loadResxFileset(resxFile);
        } catch (IOException ex) {
            String errorMessage = String.format(
                    "Could not access %s due to %s", resxFile, ex);
            System.err.println(errorMessage);
            // FIXME onException(ex, errorMessage);
        }
    }

    @Override
    public void loadFiles(ResourceSet resourceSet) {
        try {
            model.loadResxFileset(resourceSet);
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
    }

    @Override
    public void selectLocale() {
        Platform.runLater(() -> {
            localeChooser.updateAvailableLocales(model.getAvailableLocales());
            localeChooser.show();
        });
    }

}
