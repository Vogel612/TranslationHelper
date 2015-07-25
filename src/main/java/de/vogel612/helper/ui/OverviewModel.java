package de.vogel612.helper.ui;

import java.nio.file.Path;
import java.util.List;

import de.vogel612.helper.data.Translation;

public interface OverviewModel {

	void register(OverviewPresenter p);

	void loadFromDirectory(Path resxFolder);

	List<Translation> getTranslations(String locale);

	List<String> getAvailableLocales();

	Translation getSingleTranslation(String locale, String key);

	void updateTranslation(String locale, String key, String newTranslation);

	void saveAll();

}
