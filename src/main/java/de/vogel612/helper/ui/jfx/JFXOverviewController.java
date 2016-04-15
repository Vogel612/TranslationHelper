package de.vogel612.helper.ui.jfx;

import de.vogel612.helper.data.NotableData;
import de.vogel612.helper.data.NotableData.Notability;
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
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;

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

        Callback<TableColumn<TranslationPair,String>, TableCell<TranslationPair, String>> cellRenderer =
          column -> {
              TableCell<TranslationPair, String> cell = new TableCell<TranslationPair, String>() {
                  @Override
                  protected void updateItem(String item, boolean empty) {
                      super.updateItem(item, empty);
                      setText(item);
                      TranslationPair rowValue = (TranslationPair) getTableRow().getItem();
                      if (rowValue != null) {
                          assignHighlightClasses(rowValue);
                      }
                  }

                  private void assignHighlightClasses(TranslationPair rowValue) {
                      getStyleClass().remove("default");
                      getStyleClass().remove("warn");
                      getStyleClass().remove("error");

                      switch (NotableData.assessNotability(rowValue)) {
                          case INFO:
                          case DEFAULT:
                              getStyleClass().add("default");
                              break;
                          case WARNING:
                              getStyleClass().add("warn");
                              break;
                          case ERROR:
                              getStyleClass().add("error");
                              break;
                      }
                  }
              };
              cell.addEventFilter(MouseEvent.MOUSE_CLICKED, evt -> {
                  // assume the double-click selected the relevant row....
                  if (evt.getButton() != MouseButton.PRIMARY || evt.getClickCount() != 2) {
                      return;
                  }
                  translationRequestListeners.forEach(listener -> {
                      listener.accept(table.getSelectionModel().getSelectedItem().getRight().getKey());
                  });
              });
              return cell;
          };

        TableColumn<TranslationPair, String> leftColumn = new TableColumn<>("");
        leftColumn.setCellValueFactory(data -> new ObservableValueBase<String>() {
            @Override
            public String getValue() {
                return data.getValue().getLeft().getValue();
            }
        });
        leftColumn.setCellFactory(cellRenderer);

        TableColumn<TranslationPair, String> rightColumn = new TableColumn<>("");
        rightColumn.setCellValueFactory(data -> new ObservableValueBase<String>() {
            @Override
            public String getValue() {
                return data.getValue().getRight().getValue();
            }
        });
        rightColumn.setCellFactory(cellRenderer);

        table.getColumns().clear();
        table.getColumns().add(leftColumn);
        table.getColumns().add(rightColumn);

        table.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        table.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER && table.getSelectionModel().getSelectedItem() != null) {
                translationRequestListeners.forEach(listener -> {
                    // so many assumptions :/
                    listener.accept(table.getSelectionModel().getSelectedItem().getRight().getKey());
                });
            }
        });

        table.setEditable(false);
    }


    // FIXME this is fugly
    void triggerCloseRequest() {
        windowCloseListeners.forEach(Runnable::run);
    }
}
