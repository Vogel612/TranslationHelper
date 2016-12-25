package de.vogel612.helper.ui.jfx;

import de.vogel612.helper.data.Project;
import de.vogel612.helper.data.ResourceSet;
import de.vogel612.helper.data.util.ProjectSerializer;
import javafx.beans.value.ObservableValueBase;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/ResourceSetPane.fxml"));
                loader.setController(new JFXResourceSetController(resourceSet.getValue(), resourceSetRequests));
                try {
                    final Pane resourceSetPane = (Pane) loader.load();
                    resourceSetPane.setOnMouseClicked(evt -> {
                        if (evt.getClickCount() == 2) {
                            resourceSetRequests.forEach(l -> l.accept(resourceSet.getValue()));
                        }
                    });
                    return resourceSetPane;
                } catch (IOException e) {
                    // FIXME Log this
                    e.printStackTrace(System.err);
                    return null;
                }
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
