package de.vogel612.helper.ui.impl;

import java.nio.file.Path;

import de.vogel612.helper.data.Translation;
import de.vogel612.helper.ui.OverviewModel;
import de.vogel612.helper.ui.OverviewPresenter;
import de.vogel612.helper.ui.OverviewView;
import de.vogel612.helper.ui.TranslationPresenter;

public class OverviewPresenterImpl implements OverviewPresenter {

	final OverviewModel model;
	final OverviewView view;
	final TranslationPresenter translationPresenter;

	private boolean initialized = false;

	public OverviewPresenterImpl(final OverviewModel m, final OverviewView v,
			final TranslationPresenter p) {
		model = m;
		view = v;
		translationPresenter = p;

		view.initialize();
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
	public void show() {
		if (!initialized) {
			initialize();
		}
		view.show();
	}

	@Override
	public void onException(final Exception e, final String message) {
		e.printStackTrace(System.err);
		view.showError(message, e.getMessage());
	}

	@Override
	public void onParseCompletion() {
		view.rebuildWith(model.getTranslations());
	}

	@Override
	public void loadFiles(final Path resxFolder, final String rootLocale,
			final String targetLocale) {
		model.loadFromDirectory(resxFolder, targetLocale);
	}

	@Override
	public void onTranslationSubmit(final Translation t) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onTranslationAbort() {
		translationPresenter.hide();
	}

	@Override
	public void onTranslateRequest(final String key) {
		translationPresenter.setRequestedTranslation(model
				.getSingleTranslation(key));
		translationPresenter.show();
	}

}
