package de.vogel612.helper.ui.jfx;

import de.vogel612.helper.data.Project;
import de.vogel612.helper.data.ResourceSet;
import de.vogel612.helper.data.util.ProjectSerializer;
import javafx.beans.value.ObservableValueBase;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

import static de.vogel612.helper.ui.jfx.JFXDialog.DIALOG;

/**
 * Created by vogel612 on 29.07.16.
 */
public class JFXProjectController implements Initializable {

    private final Set<Consumer<ResourceSet>> resourceSetRequests = new HashSet<>();
    @FXML
    public Button save;

    @FXML
    public Button chooser;

    @FXML
    public TableView<ResourceSet> table;

    @FXML
    public TextField name;

    private Project project;
    private Path projectFilePath;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Objects.requireNonNull(save, "save was not injected correctly");
        Objects.requireNonNull(chooser, "chooser was not injected correctly");
        Objects.requireNonNull(table, "table was not injected correctly");
        Objects.requireNonNull(name, "name was not injeted correctly");

        table.getColumns().clear();
        table.getColumns().add(createTableColumn());
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        save.setOnAction(evt -> {
            if (projectFilePath == null) {
                return;
            }
            try {
                ProjectSerializer.serialize(project, projectFilePath);
                DIALOG.info("Success", "Successfully saved TranslationHelper Project Configuration to " + projectFilePath);
            } catch (IOException e) {
                DIALOG.info("Error during saving!", "Could not save project due to following exception: " + e.getMessage());
            }
        });
    }

    public void addResourceSetListener(Consumer<ResourceSet> listener) {
        resourceSetRequests.add(listener);
    }

    private TableColumn<ResourceSet, Pane> createTableColumn() {
        TableColumn<ResourceSet, Pane> column = new TableColumn<>("Resource Sets");
        column.setCellValueFactory(resourceSet -> new ObservableValueBase<Pane>() {
            @Override
            public Pane getValue() {
                final ResourceSet set = resourceSet.getValue();
                final GridPane resourcePane = new GridPane();
                setResourcePaneConstraints(resourcePane);
                resourcePane.setGridLinesVisible(false);
                resourcePane.setPrefWidth(Double.MAX_VALUE);

                TextField name = new TextField();
                resourcePane.add(name, 0, 0);
                name.setText(set.getName());

                TextField path = new TextField();
                resourcePane.add(path, 1, 0);
                path.setText(set.getFolder().toString());

                GridPane subPane = new GridPane();
                updateResourceLocale(set, subPane);
                setSubpaneConstraints(subPane);
                resourcePane.add(subPane, 0, 1, 2, 1);

                Button translateButton = new Button("Translate");
                translateButton.setOnAction(evt -> resourceSetRequests.forEach(listener -> listener.accept(set)));
                resourcePane.add(translateButton, 0, 2);
                resourcePane.setOnMouseClicked(mouseEvent -> {
                    if (mouseEvent.getClickCount() == 2) {
                        resourceSetRequests.forEach(listener -> listener.accept(set));
                    }
                });
                return resourcePane;
            }

            private void updateResourceLocale(ResourceSet set, GridPane subPane) {
                subPane.getChildren().clear();
                subPane.setGridLinesVisible(true);
                subPane.setPadding(new Insets(5,5,5,5));
                subPane.add(new Label("Locales"), 0, 0);
                Button addLocale = new Button("+");
                addLocale.setOnAction(evt -> {
                    Dialog<ButtonType> dialog = new Dialog<>();
                    dialog.setTitle("Add Locale");
                    dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
                    dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
                    TextField localeField = new TextField();
                    dialog.setGraphic(localeField);
                    Optional<ButtonType> button = dialog.showAndWait();
                    if (button.isPresent() && button.get().equals(ButtonType.OK)) {
                        set.addLocale(localeField.getText());
                        updateResourceLocale(set, subPane);
                    }
                });
                addLocale.setMinWidth(40);
                subPane.add(addLocale, 1, 0);
                int currentRow = 1;
                for (String locale : set.getLocales()) {
                    Label label = new Label(locale);
                    label.setPadding(new Insets(0,0,0,10));
                    subPane.add(label, 0, currentRow);

                    Button removeButton = new Button("-");
                    removeButton.setOnAction(evt -> {
                        set.removeLocale(locale);
                        updateResourceLocale(set, subPane);
                    });
                    removeButton.setMinWidth(40);
                    subPane.add(removeButton, 1, currentRow);

                    currentRow++;
                }
                subPane.requestLayout();
            }

            private void setResourcePaneConstraints(GridPane resourcePane) {
                ColumnConstraints left = new ColumnConstraints();
                left.setHgrow(Priority.SOMETIMES);
                ColumnConstraints right = new ColumnConstraints();
                right.setHgrow(Priority.SOMETIMES);
                resourcePane.getColumnConstraints().clear();
                resourcePane.getColumnConstraints().add(left);
                resourcePane.getColumnConstraints().add(right);
            }

            private void setSubpaneConstraints(GridPane subPane) {
                subPane.getColumnConstraints().clear();
                ColumnConstraints left = new ColumnConstraints();
                left.setFillWidth(true);
                left.setHgrow(Priority.ALWAYS);
                subPane.getColumnConstraints().add(left);
                ColumnConstraints right = new ColumnConstraints();
                right.setMinWidth(40);
                right.setHgrow(Priority.NEVER);
                subPane.getColumnConstraints().add(right);
            }
        });
        column.setCellFactory(col -> new TableCell<ResourceSet, Pane>() {
            @Override
            protected void updateItem(Pane item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(item);
            }
        });
        return column;
    }

    public void loadProject(final Path file) {
        table.getItems().clear();
        projectFilePath = null;
        try {
            project = ProjectSerializer.deserialize(file);
            projectFilePath = file;
        } catch (IOException e) {
            e.printStackTrace();
            name.setText("Error during load, please retry");
            return;
        }
        name.setText(project.getName());
        table.getItems().addAll(project.getAssociatedResources());
    }
}
