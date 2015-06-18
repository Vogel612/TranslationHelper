package de.vogel612.helper.ui.impl;

import java.nio.file.Path;

import de.vogel612.helper.ui.OverviewModel;
import de.vogel612.helper.ui.OverviewPresenter;
import de.vogel612.helper.ui.OverviewView;

public class OverviewPresenterImpl implements OverviewPresenter {

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
	public void loadFile(Path resxFile) {
		model.loadFromFile(resxFile);
	}

	@Override
	public void onException(Exception e, String message) {
		view.showError(message, e.getMessage());
	}

	@Override
	public void onParseCompletion() {
		view.rebuildWith(model.getTranslations());
	}
	
}
