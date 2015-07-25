package de.vogel612.helper.ui;

import java.nio.file.Path;

import de.vogel612.helper.data.Translation;

public interface OverviewPresenter {

	public static final String DEFAULT_TARGET_LOCALE = "de";
	public static final String DEFAULT_ROOT_LOCALE = "";

	void show();

	void initialize();

	void loadFiles(Path resxFolder);

	void onException(Exception e, String message);

	void onParseCompletion();

	void onTranslationSubmit(Translation t);

	void onTranslateRequest(String key);

	void onTranslationAbort();

	void onSaveRequest();
}
