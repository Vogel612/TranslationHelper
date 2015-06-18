package de.vogel612.helper.ui.impl;

import java.nio.file.Path;
import java.util.List;

import de.vogel612.helper.data.Translation;
import de.vogel612.helper.ui.OverviewModel;
import de.vogel612.helper.ui.OverviewPresenter;

public class NoOpModel implements OverviewModel {

	@Override
	public void register(OverviewPresenter p) {
		// TODO Auto-generated method stub

	}

	@Override
	public void readFromPath(Path p) {
		// TODO Auto-generated method stub

	}

	@Override
	public void loadFromFile(Path resxFile) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<Translation> getTranslations() {
		// TODO Auto-generated method stub
		return null;
	}

}
