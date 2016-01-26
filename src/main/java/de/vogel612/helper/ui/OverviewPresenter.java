package de.vogel612.helper.ui;

import static javax.swing.JOptionPane.*;

import de.vogel612.helper.data.OverviewModel;
import de.vogel612.helper.data.Side;
import de.vogel612.helper.data.Translation;
import de.vogel612.helper.ui.ResxChooser.ResxChooserEvent;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class OverviewPresenter {

    static final String DEFAULT_TARGET_LOCALE = "de";
    static final String DEFAULT_ROOT_LOCALE = "";

    private final Map<Side, String> chosenLocale = new EnumMap<>(Side.class);
    private final OverviewModel model;
    private final OverviewView view;
    private final TranslationPresenter translationPresenter;
    private final ResxChooser resxChooser;

    private boolean initialized = false;

    public OverviewPresenter(final OverviewModel m, final OverviewView v, final TranslationPresenter p, ResxChooser rc) {
        model = m;
        view = v;
        translationPresenter = p;
        resxChooser = rc;

        view.initialize();
    }

    public void show() {
        if (!initialized) {
            initialize();
        }
        view.show();
    }

    public void initialize() {
        // initialization shall only happen once!
        if (initialized) {
            return;
        }
        view.addLanguageRequestListener(this::fileChoosing);
        view.addSaveRequestListener(this::onSaveRequest);
        view.addTranslationRequestListener(this::onTranslateRequest);
        view.addWindowClosingListener(this::onWindowCloseRequest);

        model.addParseCompletionListener(this::onParseCompletion);

        translationPresenter.addTranslationAbortListener(this::onTranslationAbort);
        translationPresenter.addTranslationSubmitListener(this::onTranslationSubmit);

        resxChooser.addCompletionListener(this::fileChoiceCompletion);

        initialized = true;
    }

    //FIXME SMELL public for main...
    public void onLocaleRequest(final String locale, final Side side) {
        chosenLocale.put(side, locale);
        rebuildView();
    }

    public void onException(final Exception e, final String message) {
        // FIXME: Allow termination for unrecoverable exception
        view.displayError(message, e.getMessage());
    }

    public void onParseCompletion() {
        rebuildView();
    }

    private void fileChoiceCompletion(ResxChooserEvent evt) {
        chosenLocale.put(Side.LEFT, evt.getLeftLocale());
        chosenLocale.put(Side.RIGHT, evt.getRightLocale());
        loadFiles(evt.getFileset());
    }

    private void rebuildView() {
        List<Translation> left = model.getTranslations(chosenLocale.getOrDefault(Side.LEFT, DEFAULT_ROOT_LOCALE));
        List<Translation> right = model.getTranslations(chosenLocale.getOrDefault(Side.RIGHT, DEFAULT_TARGET_LOCALE));
        view.rebuildWith(left, right);
    }

    public void loadFiles(final Path resxFile) {
        try {
            model.loadResxFileset(resxFile);
        } catch (IOException ex) {
            String errorMessage = String.format(
              "Could not access %s due to %s", resxFile, ex);
            System.err.println(errorMessage);
            onException(ex, errorMessage);
        }
    }

    void onTranslationSubmit(final Translation t) {
        translationPresenter.hide();
        model.updateTranslation(t.getLocale(), t.getKey(), t.getValue());
        rebuildView();
    }

    void onTranslationAbort() {
        translationPresenter.hide();
    }

    void onTranslateRequest(final String key) {
        translationPresenter.setRequestedTranslation(
          model.getSingleTranslation(chosenLocale.getOrDefault(Side.LEFT, DEFAULT_ROOT_LOCALE), key),
          model.getSingleTranslation(chosenLocale.getOrDefault(Side.RIGHT, DEFAULT_TARGET_LOCALE), key)
        );
        translationPresenter.show();
    }

    void onSaveRequest() {
        try {
            model.saveAll();
        } catch (IOException e) {
            e.printStackTrace(System.err);
            onException(e, "Could not save File");
        }
    }

    private void onWindowCloseRequest(WindowEvent windowEvent) {
        if (model.isNotSaved()) {
            // prompt to save changes
            int choice = JOptionPane.showConfirmDialog(windowEvent.getWindow(),
              "You have unsaved changes. Do you wish to save before exiting?",
              "Unsaved Changes",
              YES_NO_CANCEL_OPTION);
            switch (choice) {
                case YES_OPTION:
                    onSaveRequest();
                    //FIXME: What if saving fails?
                    // fallthrough intended
                case NO_OPTION:
                    view.hide();
                    System.exit(0);
                    break;
                case CANCEL_OPTION:
                    // do nothing
                    break;
            }
        } else {
            System.exit(0);
        }
    }

    /**
     * Allows the user to choose an arbitrary *.regex file and asks them to choose out of the available locales for Left
     * and Right in the view.
     */
    public void fileChoosing() {
        resxChooser.show();
    }
}
