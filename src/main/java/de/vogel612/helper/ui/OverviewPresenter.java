package de.vogel612.helper.ui;

import java.nio.file.Path;

public interface OverviewPresenter {

	public static final String DEFAULT_TARGET_LOCALE = "de";
	public static final String DEFAULT_ROOT_LOCALE = "";

	void show();

	void initialize();

	void loadFiles(Path resxFolder, String rootLocale, String targetLocale);

	void onException(Exception e, String message);

	void onParseCompletion();
}
