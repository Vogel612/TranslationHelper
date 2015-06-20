package de.vogel612.helper.ui;

import java.nio.file.Path;

public interface OverviewPresenter {

	void show();

	void initialize();

	void loadFiles(Path resxFile);
	
	void loadFiles(Path resxFolder, String rootLocale, String targetLocale);

	void onException(Exception e, String message);

	void onParseCompletion();
}
