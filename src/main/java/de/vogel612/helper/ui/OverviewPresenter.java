package de.vogel612.helper.ui;

import java.nio.file.Path;

import de.vogel612.helper.data.Side;
import de.vogel612.helper.data.Translation;

public interface OverviewPresenter {

	String DEFAULT_TARGET_LOCALE = "de";
	String DEFAULT_ROOT_LOCALE = "";

	void show();
	void initialize();
	void loadFiles(Path resxFolder);
	String[] getLocaleOptions();

	void onTranslationRequest(String locale, Side side);
	void onException(Exception e, String message);
	void onParseCompletion();
	void onTranslationSubmit(Translation t);
	void onTranslateRequest(String key);
	void onTranslationAbort();
	void onSaveRequest();

}
