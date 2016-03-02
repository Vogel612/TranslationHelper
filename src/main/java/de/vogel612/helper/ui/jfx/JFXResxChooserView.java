package de.vogel612.helper.ui.jfx;

import de.vogel612.helper.ui.ResxChooser;
import de.vogel612.helper.ui.common.ResxChooserCommon.ResxChooserEvent;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.function.Consumer;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Created by vogel612 on 02.03.16.
 */
public class JFXResxChooserView implements ResxChooser {

    private final Stage stage;
    private final Scene ui;
    private final JFXResxChooserController controller;

    public JFXResxChooserView(Stage stage, URL fxml) throws IOException {
        this.stage = stage;
        FXMLLoader loader = new FXMLLoader(fxml);
        ui = new Scene(loader.load());
        controller = loader.getController();
    }

    @Override
    public void setFileset(Path fileset) {
        controller.setFileset(fileset);
    }

    @Override
    public void hide() {
        stage.hide();
    }

    @Override
    public void show() {
        stage.setScene(ui);
        stage.show();
    }

    @Override
    public void addCompletionListener(Consumer<ResxChooserEvent> listener) {
        controller.addCompletionListener(listener);
    }
}
