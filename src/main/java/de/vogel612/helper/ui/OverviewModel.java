package de.vogel612.helper.ui;

import java.nio.file.Path;

public interface OverviewModel {

	void register(OverviewPresenter p);
	
	void readFromPath(Path p);

	void loadFromFile(Path resxFile);
}
