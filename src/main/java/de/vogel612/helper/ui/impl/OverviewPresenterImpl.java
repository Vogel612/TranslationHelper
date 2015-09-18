package de.vogel612.helper.ui.impl;

import de.vogel612.helper.data.Side;
import de.vogel612.helper.data.Translation;
import de.vogel612.helper.ui.OverviewModel;
import de.vogel612.helper.ui.OverviewPresenter;
import de.vogel612.helper.ui.OverviewView;
import de.vogel612.helper.ui.TranslationPresenter;

import java.nio.file.Path;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class OverviewPresenterImpl implements OverviewPresenter {

    private final Map<Side, String> chosenLocale = new EnumMap<>(Side.class);
    private final OverviewModel model;
    private final OverviewView view;
    private final TranslationPresenter translationPresenter;

    private boolean initialized = false;

    public OverviewPresenterImpl(final OverviewModel m, final OverviewView v,
      final TranslationPresenter p) {
        model = m;
        view = v;
        translationPresenter = p;

        view.initialize();
    }

    @Override
    public void show() {
        if (!initialized) {
            initialize();
        }
        view.show();
    }

    @Override
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

    @Override
    public void onTranslationRequest(final String locale, final Side side) {
        chosenLocale.put(side, locale);
        rebuildView();
    }

    @Override
    public void onException(final Exception e, final String message) {
        view.displayError(message, e.getMessage());
    }

    @Override
    public void onParseCompletion() {
        rebuildView();
    }

    private void rebuildView() {
        List<Translation> left = model.getTranslations(chosenLocale.getOrDefault(Side.LEFT, DEFAULT_ROOT_LOCALE));
        List<Translation> right = model.getTranslations(chosenLocale.getOrDefault(Side.RIGHT, DEFAULT_TARGET_LOCALE));
        view.rebuildWith(left, right);
    }

    @Override
    public void loadFiles(final Path resxFolder) {
        model.loadFromDirectory(resxFolder);
    }

    @Override
    public String[] getLocaleOptions() {
        return model.getAvailableLocales().toArray(new String[]{});
    }

    @Override
    public void onTranslationSubmit(final Translation t) {
        translationPresenter.hide();
        model.updateTranslation(t.getLocale(), t.getKey(), t.getValue());
        rebuildView();
    }

    @Override
    public void onTranslationAbort() {
        translationPresenter.hide();
    }

    @Override
    public void onTranslateRequest(final String key) {
        translationPresenter.setRequestedTranslation(
          model.getSingleTranslation(chosenLocale.getOrDefault(Side.LEFT, DEFAULT_ROOT_LOCALE), key),
          model.getSingleTranslation(chosenLocale.getOrDefault(Side.RIGHT, DEFAULT_TARGET_LOCALE), key)
        );
        translationPresenter.show();
    }

    @Override
    public void onSaveRequest() {
        model.saveAll();
    }
}
