package de.vogel612.helper.ui.jfx;

import de.vogel612.helper.data.Translation;
import de.vogel612.helper.ui.common.OverviewViewCommon;

import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;

/**
 * Created by vogel612 on 01.03.16.
 */
public class JFXOverviewController extends OverviewViewCommon implements Initializable {

    @FXML
    private Button save;

    @FXML
    private Button chooseLang;

    @FXML
    private TableView table;

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
        // FIXME do this
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

//        Objects.requireNonNull(save, "save was not FXML-injected correctly");
//        Objects.requireNonNull(table, "table was not FXML-injected correctly");
//        Objects.requireNonNull(chooseLang, "chooseLang was not FXML-injected correctly");

//        save.setOnAction(evt -> {
//            saveRequestListeners.forEach(Runnable::run);
//        });
//        chooseLang.setOnAction(evt -> {
//            langChoiceRequestListeners.forEach(Runnable::run);
//        });

        // FIXME window close request listeners!!
        // FIXME row selection listeners for Return and Double-Click
    }
}
