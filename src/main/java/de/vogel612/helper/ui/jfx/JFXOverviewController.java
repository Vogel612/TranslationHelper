package de.vogel612.helper.ui.jfx;

import de.vogel612.helper.data.Translation;
import de.vogel612.helper.ui.common.OverviewViewCommon;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

import javafx.beans.value.ObservableValueBase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;

/**
 * Created by vogel612 on 01.03.16.
 */
public class JFXOverviewController extends OverviewViewCommon implements Initializable {

    @FXML
    private Button save;

    @FXML
    private Button chooseLang;

    @FXML
    private TableView<TranslationPair> table;

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
        if (left.isEmpty() || right.isEmpty()) {
            return;
        }
        table.setItems(buildObservableList(left, right));
        table.getColumns().get(0).setText(left.get(0).getLocale());
        table.getColumns().get(1).setText(right.get(0).getLocale());
    }

    private static ObservableList<TranslationPair> buildObservableList(List<Translation> left,
      List<Translation> right) {
        List<TranslationPair> result = new ArrayList<>();
        final int limit = Math.min(left.size(), right.size());
        for (int i = 0; i < limit; i++) {
            result.add(new TranslationPair(left.get(i), right.get(i)));
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
    public void showPrompt(String title, String promptText, Runnable okCallback) {
        throw new UnsupportedOperationException(); // done in the View Class
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        Objects.requireNonNull(save, "save was not FXML-injected correctly");
        Objects.requireNonNull(table, "table was not FXML-injected correctly");
        Objects.requireNonNull(chooseLang, "chooseLang was not FXML-injected correctly");

        save.setOnAction(evt -> saveRequestListeners.forEach(Runnable::run));
        chooseLang.setOnAction(evt -> langChoiceRequestListeners.forEach(Runnable::run));

        TableColumn<TranslationPair, String> leftColumn = new TableColumn<>("");
        leftColumn.setCellValueFactory(data -> new ObservableValueBase<String>() {
            @Override
            public String getValue() {
                return data.getValue().getLeft().getValue();
            }
        });
        TableColumn<TranslationPair, String> rightColumn = new TableColumn<>("");
        rightColumn.setCellValueFactory(data -> new ObservableValueBase<String>() {
            @Override
            public String getValue() {
                return data.getValue().getRight().getValue();
            }
        });
        table.getColumns().clear();
        table.getColumns().add(leftColumn);
        table.getColumns().add(rightColumn);

        table.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        table.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                translationRequestListeners.forEach(listener -> {
                    listener.accept(table.getSelectionModel().getSelectedItem().getRight().getKey());
                });
            }
        });

        // FIXME row selection listeners for Return and Double-Click
    }


    // FIXME this is fugly
    void triggerCloseRequest() {
        windowCloseListeners.forEach(Runnable::run);
    }
}
