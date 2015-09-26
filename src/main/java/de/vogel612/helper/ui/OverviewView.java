package de.vogel612.helper.ui;

import de.vogel612.helper.data.Translation;

import java.util.List;

public interface OverviewView {

    void register(OverviewPresenter p);

    void initialize();

    void show();

    void rebuildWith(List<Translation> left, List<Translation> right);

    void displayError(String title, String errorMessage);

    void hide();
}
