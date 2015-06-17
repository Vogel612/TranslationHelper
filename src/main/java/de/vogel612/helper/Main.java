package de.vogel612.helper;

import de.vogel612.helper.ui.OverviewModel;
import de.vogel612.helper.ui.OverviewPresenter;
import de.vogel612.helper.ui.OverviewView;
import de.vogel612.helper.ui.impl.OverviewPresenterImpl;

public class Main {

	public static void main(String[] args) {
		// TODO: parse arguments?
		OverviewModel m = null;
		OverviewView v = null;
		
		OverviewPresenter p = new OverviewPresenterImpl(m, v);
		p.show();
	}

}
