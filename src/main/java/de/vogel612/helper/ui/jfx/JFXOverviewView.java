package de.vogel612.helper.ui.jfx;


import de.vogel612.helper.data.Translation;
import de.vogel612.helper.ui.OverviewView;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.function.Consumer;

/**
 * Created by vogel612 on 02.03.16.
 */
public class JFXOverviewView implements OverviewView {

    private final JFXOverviewController controller;
    private final Scene ui;
    private final Stage stage;

    public JFXOverviewView(Stage stage, URL fxml) throws IOException {
        this.stage = stage;
        FXMLLoader loader = new FXMLLoader(fxml);
        ui = new Scene(loader.load());
        ui.getStylesheets().clear();
        // this works fine
        ui.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

        controller = loader.getController();

        stage.setOnCloseRequest(evt -> controller.triggerCloseRequest());
    }

    @Override
    public void addWindowClosingListener(Runnable listener) {
        controller.addWindowClosingListener(listener);
    }

    @Override
    public void addLanguageRequestListener(Runnable listener) {
        controller.addLanguageRequestListener(listener);
    }

    @Override
    public void addTranslationRequestListener(Consumer<String> listener) {
        controller.addTranslationRequestListener(listener);
    }

    @Override
    public void addSaveRequestListener(Runnable listener) {
        controller.addSaveRequestListener(listener);
    }

    @Override
    public void initialize() {
        // shouldn't be needed anymore
    }

    @Override
    public void show() {
        Platform.runLater(() -> {
            stage.setScene(ui);
            stage.show();
        });
    }

    @Override
    public void rebuildWith(List<Translation> left, List<Translation> right) {
        controller.rebuildWith(left, right);
    }

    @Override
    public void hide() {
        stage.hide();
    }
}
