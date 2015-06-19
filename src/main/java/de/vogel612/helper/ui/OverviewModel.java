package de.vogel612.helper.ui;

import java.nio.file.Path;
import java.util.List;

import de.vogel612.helper.data.Translation;


public interface OverviewModel {

	void register(OverviewPresenter p);
	
	void loadFromFile(Path resxFile);

	List<Translation> getTranslations();
}
