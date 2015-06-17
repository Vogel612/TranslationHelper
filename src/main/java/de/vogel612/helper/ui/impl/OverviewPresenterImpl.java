package de.vogel612.helper.ui.impl;

import de.vogel612.helper.ui.OverviewModel;
import de.vogel612.helper.ui.OverviewPresenter;
import de.vogel612.helper.ui.OverviewView;

public class OverviewPresenterImpl implements OverviewPresenter {

	final OverviewModel model;
	final OverviewView view;
	
	public OverviewPresenterImpl(OverviewModel m, OverviewView v) {
		model = m;
		view = v;
	}

	@Override
	public void show() {
		view.initialize();
		view.show();
	}

}
