package de.vogel612.helper.ui;

import de.vogel612.helper.data.Side;
import de.vogel612.helper.data.Translation;

import java.nio.file.Path;

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

    void onTranslateRequest(String key, String locale);

    void onTranslationAbort();

    void onSaveRequest();

}
