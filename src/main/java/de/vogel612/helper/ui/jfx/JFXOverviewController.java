package de.vogel612.helper.ui.jfx;

import de.vogel612.helper.data.Translation;
import de.vogel612.helper.ui.common.OverviewViewCommon;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.util.Pair;

/**
 * Created by vogel612 on 01.03.16.
 */
public class JFXOverviewController extends OverviewViewCommon implements Initializable {

    @FXML
    private Button save;

    @FXML
    private Button chooseLang;

    @FXML
    private TableView<Pair<Translation, Translation>> table;

    public JFXOverviewController() {

    }

    @Override
    public void initialize() {
        // empty?
    }

    @Override
    public void show() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void rebuildWith(List<Translation> left, List<Translation> right) {
        // FIXME get this into a cleaner format, preferrably wrapping the Translations into clean and simple Observables
        table.setItems(buildObservableList(left, right));
    }

    private static ObservableList<Pair<Translation, Translation>> buildObservableList(List<Translation> left, List<Translation> right) {
        List<Pair<Translation, Translation>> result = new ArrayList<>();
        final int limit = Math.min(left.size(), right.size());
        for (int i = 0; i < limit; i++) {
            result.add(new Pair<>(left.get(i), right.get(i)));
        }
        return FXCollections.observableList(result);
    }

    @Override
    public void displayError(String title, String errorMessage) {
        // FIXME do this, too
    }

    @Override
    public void hide() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        Objects.requireNonNull(save, "save was not FXML-injected correctly");
        Objects.requireNonNull(table, "table was not FXML-injected correctly");
        Objects.requireNonNull(chooseLang, "chooseLang was not FXML-injected correctly");

        save.setOnAction(evt -> {
            saveRequestListeners.forEach(Runnable::run);
        });
        chooseLang.setOnAction(evt -> {
            langChoiceRequestListeners.forEach(Runnable::run);
        });
        // FIXME bind Table Rendering and Selection models
        // FIXME window close request listeners!!
        // FIXME row selection listeners for Return and Double-Click
    }
}
