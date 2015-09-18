package de.vogel612.helper.ui;

import de.vogel612.helper.data.Translation;

import java.nio.file.Path;
import java.util.List;

public interface OverviewModel {

    String VALUE_NAME = "value";
    String KEY_NAME = "name";
    String SINGLE_TRUTH_LOCALE = "";

    void register(OverviewPresenter p);

    void loadFromDirectory(Path resxFolder);

    List<Translation> getTranslations(String locale);

    List<String> getAvailableLocales();

    Translation getSingleTranslation(String locale, String key);

    void updateTranslation(String locale, String key, String newTranslation);

    void saveAll();

}
