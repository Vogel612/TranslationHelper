package de.vogel612.helper.ui;

import de.vogel612.helper.data.FilesetOverviewModel;
import de.vogel612.helper.data.Side;
import de.vogel612.helper.data.Translation;
import de.vogel612.helper.ui.common.ResxChooserCommon.ResxChooserEvent;

import java.io.IOException;
import java.nio.file.Path;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class OverviewPresenter {

    public static final String DEFAULT_TARGET_LOCALE = "de";
    public static final String DEFAULT_ROOT_LOCALE = "";

    private final Map<Side, String> chosenLocale = new EnumMap<>(Side.class);
    private final FilesetOverviewModel model;
    private final OverviewView view;
    private final TranslationView translationPresenter;
    private final ResxChooser resxChooser;
    private final Dialog dialog;

    public OverviewPresenter(final FilesetOverviewModel m, final OverviewView v, final TranslationView p, final ResxChooser rc, final Dialog d) {
        model = m;
        view = v;
        translationPresenter = p;
        resxChooser = rc;
        dialog = d;

        view.initialize();
    }


    public void show() {
        view.show();
    }

    public void onException(final Exception e, final String message) {
        dialog.info(message, e.getMessage());
        // FIXME: Allow termination for unrecoverable exception
    }

    public void onParseCompletion() {
        rebuildView();
    }

    public void fileChoiceCompletion(ResxChooserEvent evt) {
        resxChooser.hide();
        chosenLocale.put(Side.LEFT, evt.getLeftLocale());
        chosenLocale.put(Side.RIGHT, evt.getRightLocale());
        loadFiles(evt.getFileset());
    }

    private void rebuildView() {
        List<Translation> left = model.getTranslations(chosenLocale.getOrDefault(Side.LEFT, DEFAULT_ROOT_LOCALE));
        List<Translation> right = model.getTranslations(chosenLocale.getOrDefault(Side.RIGHT, DEFAULT_TARGET_LOCALE));
        view.rebuildWith(left, right);
        view.show();
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

    public void onTranslationSubmit(final Translation t) {
        translationPresenter.hide();
        model.updateTranslation(t.getLocale(), t.getKey(), t.getValue());
        rebuildView();
    }

    public void onTranslationAbort() {
        translationPresenter.hide();
        view.show();
    }

    public void onTranslateRequest(final String key) {
        translationPresenter.setRequestedTranslation(
                model.getSingleTranslation(chosenLocale.getOrDefault(Side.LEFT, DEFAULT_ROOT_LOCALE), key),
                model.getSingleTranslation(chosenLocale.getOrDefault(Side.RIGHT, DEFAULT_TARGET_LOCALE), key)
        );
        translationPresenter.show();
    }

    public void onSaveRequest() {
        try {
            model.saveAll();
            dialog.info("Save success!", "Saving your changes to all resx files completed successfully");
        } catch (IOException e) {
            e.printStackTrace(System.err);
            onException(e, "Could not save File");
        }
    }

    public void onWindowCloseRequest() {
        if (model.isNotSaved()) {
            dialog.warn("Unsaved Changes", "You have unsaved changes. Do you wish to save before exiting?",
                    this::onSaveRequest, () -> {
                        view.hide();
                        System.exit(0);
                    }
            );
        }
        // FIXME allow preventing to close
    }

    /**
     * Allows the user to choose an arbitrary *.regex file and asks them to choose out of the available locales for
     * Left
     * and Right in the view.
     */
    public void fileChoosing() {
        if (model.isNotSaved()) {
            dialog.warn("Unsaved Changes",
                    "You have unsaved changes. Do you wish to save them before changing the resx-fileset?",
                    this::onSaveRequest,
                    () -> {}
            );
            resxChooser.show();
        } else {
            resxChooser.show();
        }
    }
}
