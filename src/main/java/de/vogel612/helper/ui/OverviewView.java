package de.vogel612.helper.ui;

import java.util.List;

import de.vogel612.helper.data.Side;
import de.vogel612.helper.data.Translation;

public interface OverviewView {

	void register(OverviewPresenter p);

	void initialize();

	void show();

	void rebuildWith(List<Translation> translations, Side side);

	void displayError(String title, String errorMessage);

	void destroy();

}
