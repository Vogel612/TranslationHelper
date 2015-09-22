package de.vogel612.helper.ui;

import de.vogel612.helper.data.Side;
import de.vogel612.helper.data.Translation;

import java.nio.file.Path;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class OverviewPresenter {

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
        view.register(this);
        model.register(this);
        translationPresenter.register(this);
        initialized = true;
    }

    public void onTranslationRequest(final String locale, final Side side) {
        chosenLocale.put(side, locale);
        rebuildView();
    }

    public void onException(final Exception e, final String message) {
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
        model.loadFromDirectory(resxFolder);
    }

    public String[] getLocaleOptions() {
        return model.getAvailableLocales().toArray(new String[]{});
    }

    public void onTranslationSubmit(final Translation t) {
        translationPresenter.hide();
        model.updateTranslation(t.getLocale(), t.getKey(), t.getValue());
        rebuildView();
    }

    public void onTranslationAbort() {
        translationPresenter.hide();
    }

    public void onTranslateRequest(final String key) {
        translationPresenter.setRequestedTranslation(
          model.getSingleTranslation(chosenLocale.getOrDefault(Side.LEFT, DEFAULT_ROOT_LOCALE), key),
          model.getSingleTranslation(chosenLocale.getOrDefault(Side.RIGHT, DEFAULT_TARGET_LOCALE), key)
        );
        translationPresenter.show();
    }

    public void onSaveRequest() {
        model.saveAll();
    }
}
