package de.vogel612.helper.ui.impl;

import de.vogel612.helper.data.Side;
import de.vogel612.helper.data.Translation;
import de.vogel612.helper.ui.OverviewModel;
import de.vogel612.helper.ui.OverviewPresenter;
import de.vogel612.helper.ui.OverviewView;
import de.vogel612.helper.ui.TranslationPresenter;

import java.nio.file.Path;

public class OverviewPresenterImpl implements OverviewPresenter {

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
        view.rebuildWith(model.getTranslations(locale), side);
    }

    @Override
    public void onException(final Exception e, final String message) {
        view.displayError(message, e.getMessage());
    }

    @Override
    public void onParseCompletion() {
        view.rebuildWith(model.getTranslations(DEFAULT_ROOT_LOCALE), Side.LEFT);
        view.rebuildWith(model.getTranslations(DEFAULT_TARGET_LOCALE),
          Side.RIGHT);
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
        view.rebuildWith(model.getTranslations(t.getLocale()), Side.RIGHT);
    }

    @Override
    public void onTranslationAbort() {
        translationPresenter.hide();
    }

    @Override
    public void onTranslateRequest(final String key, final String locale) {
        translationPresenter.setRequestedTranslation(
          model.getSingleTranslation(locale, key), "" /*FIXME pass in original*/);
        translationPresenter.show();
    }

    @Override
    public void onSaveRequest() {
        model.saveAll();
    }
}
