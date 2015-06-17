package de.vogel612.helper.ui;

import java.nio.file.Path;

public interface OverviewPresenter {

	void show();

	void initialize();

	void loadFile(Path resxFile);
}
