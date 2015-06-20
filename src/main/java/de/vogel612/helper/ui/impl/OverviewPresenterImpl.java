package de.vogel612.helper.ui.impl;

import java.nio.file.Path;

import de.vogel612.helper.ui.OverviewModel;
import de.vogel612.helper.ui.OverviewPresenter;
import de.vogel612.helper.ui.OverviewView;

public class OverviewPresenterImpl implements OverviewPresenter {

	private static final String DEFAULT_TARGET_LOCALE = "de";
	private static final String DEFAULT_ROOT_LOCALE = "";
	final OverviewModel model;
	final OverviewView view;
	private boolean initialized = false;
	
	public OverviewPresenterImpl(OverviewModel m, OverviewView v) {
		model = m;
		view = v;

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
	public void loadFiles(Path resxFile) {
		loadFiles(resxFile, DEFAULT_ROOT_LOCALE, DEFAULT_TARGET_LOCALE);
	}

	@Override
	public void onException(Exception e, String message) {
		e.printStackTrace(System.err);
		view.showError(message, e.getMessage());
	}

	@Override
	public void onParseCompletion() {
		view.rebuildWith(model.getTranslations());
	}

	@Override
	public void loadFiles(Path resxFolder, String rootLocale,
			String targetLocale) {
		model.loadFromDirectory(resxFolder, targetLocale);
	}
	
}
