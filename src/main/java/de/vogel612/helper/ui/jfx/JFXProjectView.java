package de.vogel612.helper.ui.jfx;

import de.vogel612.helper.data.ResourceSet;
import de.vogel612.helper.ui.ProjectView;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.function.Consumer;

/**
 * Created by vogel612 on 29.07.16.
 */
public class JFXProjectView implements ProjectView {

    private final JFXProjectController controller;
    private final Scene ui;
    private final Stage stage;

    public JFXProjectView(Stage stage, URL fxml) throws IOException {
        this.stage = stage;
        FXMLLoader loader = new FXMLLoader(fxml);
        ui = new Scene(loader.load());
        ui.getStylesheets().clear();
        ui.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

        controller = loader.getController();
    }


    @Override
    public void addResourceSetListener(Consumer<ResourceSet> listener) {
        controller.addResourceSetListener(listener);
    }

    @Override
    public void loadProject(final Path file) {
        controller.loadProject(file);
    }


    @Override
    public void show() {
        Platform.runLater(() -> {
            stage.setScene(ui);
            stage.show();
        });
    }

    @Override
    public void hide() {
        Platform.runLater(stage::hide);
    }
}
