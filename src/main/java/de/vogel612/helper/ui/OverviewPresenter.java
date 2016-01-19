package de.vogel612.helper.ui;

import static javax.swing.JOptionPane.*;

import de.vogel612.helper.data.Side;
import de.vogel612.helper.data.Translation;

import javax.swing.*;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.nio.file.Path;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class OverviewPresenter {

    // TODO Can we assume DEFAULTS for this??
    public static final String DEFAULT_TARGET_LOCALE = "de";
    public static final String DEFAULT_ROOT_LOCALE = "";

    private final Map<Side, String> chosenLocale = new EnumMap<>(Side.class);
    private final OverviewModel model;
    private final OverviewView view;
    private final TranslationPresenter translationPresenter;

    private boolean initialized = false;

    public OverviewPresenter(final OverviewModel m, final OverviewView v, final TranslationPresenter p) {
        model = m;
        view = v;
        translationPresenter = p;

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
        view.addLocaleChangeRequestListener(this::onLocaleRequest);
        view.addSaveRequestListener(this::onSaveRequest);
        view.addTranslationRequestListener(this::onTranslateRequest);
        view.addWindowClosingListener(this::onWindowCloseRequest);

        model.addParseCompletionListener(this::onParseCompletion);

        translationPresenter.register(this);
        initialized = true;
    }

    //SMELL public for main...
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

    private void rebuildView() {
        List<Translation> left = model.getTranslations(chosenLocale.getOrDefault(Side.LEFT, DEFAULT_ROOT_LOCALE));
        List<Translation> right = model.getTranslations(chosenLocale.getOrDefault(Side.RIGHT, DEFAULT_TARGET_LOCALE));
        view.rebuildWith(left, right);
    }

    public void loadFiles(final Path resxFolder) {
        try {
            model.loadFromDirectory(resxFolder);
        } catch (IOException ex) {
            String errorMessage = String.format(
              "Could not access %s due to %s", resxFolder, ex);
            System.err.println(errorMessage);
            onException(ex, errorMessage);
        }
    }

    public String[] getLocaleOptions() {
        final List<String> availableLocales = model.getAvailableLocales();
        // minor performance boost over passing in an empty array
        return availableLocales.toArray(new String[availableLocales.size()]);
    }

    public void onTranslationSubmit(final Translation t) {
        translationPresenter.hide();
        model.updateTranslation(t.getLocale(), t.getKey(), t.getValue());
        rebuildView();
    }

    public void onTranslationAbort() {
        translationPresenter.hide();
    }

    protected void onTranslateRequest(final String key) {
        translationPresenter.setRequestedTranslation(
          model.getSingleTranslation(chosenLocale.getOrDefault(Side.LEFT, DEFAULT_ROOT_LOCALE), key),
          model.getSingleTranslation(chosenLocale.getOrDefault(Side.RIGHT, DEFAULT_TARGET_LOCALE), key)
        );
        translationPresenter.show();
    }

    protected void onSaveRequest() {
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
}
