package de.vogel612.helper.ui.jfx;

import de.vogel612.helper.data.Project;
import de.vogel612.helper.data.ResourceSet;
import de.vogel612.helper.data.util.ProjectSerializer;
import javafx.application.Platform;
import javafx.beans.value.ObservableValueBase;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Consumer;

import static de.vogel612.helper.ui.jfx.JFXDialog.DIALOG;
import static java.lang.System.err;

/**
 * Created by vogel612 on 29.07.16.
 */
public class JFXProjectController implements Initializable {

    private final Set<Consumer<ResourceSet>> resourceSetRequests = new HashSet<>();
    private final Set<Runnable> fileChoiceRequestListeners = new HashSet<>();

    @FXML
    private Button save;

    @FXML
    private Button chooser;

    @FXML
    private TableView<ResourceSet> table;

    @FXML
    private TextField name;

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
        table.getColumns().add(createActionColumn());
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

        chooser.setOnAction(evt -> fileChoiceRequestListeners.forEach(Platform::runLater));
    }

    private TableColumn<ResourceSet, Button> createActionColumn() {
        TableColumn<ResourceSet, Button> column = new TableColumn<>("");
        Button addNewButton = new Button("+");
        addNewButton.setOnAction(evt -> {
            resourceSetInput().ifPresent(project::associate);
            // FIXME crutch for actually using Properties in the model-classes
            table.setItems(FXCollections.observableList(project.getAssociatedResources()));
        });
        addNewButton.setMinWidth(40);
        column.setGraphic(addNewButton);
        column.setCellValueFactory(resourceSet -> new ObservableValueBase<Button>() {
            @Override
            public Button getValue() {
                Button removeButton = new Button("-");
                removeButton.setOnAction(evt -> {
                    project.disassociate(resourceSet.getValue());
                    table.setItems(FXCollections.observableList(project.getAssociatedResources()));
                });
                removeButton.setMinWidth(40);
                return removeButton;
            }
        });
        column.setCellFactory(col -> new TableCell<ResourceSet, Button>() {
            @Override
            public void updateItem(Button item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(item);
            }
        });
        column.setMaxWidth(40);
        column.setMinWidth(40);
        return column;
    }

    private Optional<ResourceSet> resourceSetInput() {
        final Path resxFile = DIALOG.chooseFile("Choose a resx file part of the resource set you wish to add",
                new FileChooser.ExtensionFilter("Resource file", "*.resx"));
        return resxFile == null ? Optional.empty() : Optional.ofNullable(ResourceSet.create(resxFile));
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
                    final Pane resourceSetPane = loader.load();
                    resourceSetPane.setOnMouseClicked(evt -> {
                        if (evt.getClickCount() == 2) {
                            resourceSetRequests.forEach(l -> l.accept(resourceSet.getValue()));
                        }
                    });
                    return resourceSetPane;
                } catch (IOException e) {
                    e.printStackTrace(err);
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
        table.setItems(FXCollections.observableList(project.getAssociatedResources()));
    }

    public final void addFileRequestListener(Runnable listener) {
        fileChoiceRequestListeners.add(listener);
    }
}
