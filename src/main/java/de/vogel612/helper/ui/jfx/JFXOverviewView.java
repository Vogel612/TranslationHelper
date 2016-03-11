package de.vogel612.helper.ui.jfx;


import static javafx.scene.control.ButtonType.*;

import de.vogel612.helper.data.Translation;
import de.vogel612.helper.ui.OverviewView;

import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

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
        controller = loader.getController();

        // FIXME windowClosing should be bound to our stage
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
        stage.setScene(ui);
        stage.show();
    }

    @Override
    public void rebuildWith(List<Translation> left, List<Translation> right) {
        controller.rebuildWith(left, right);
    }

    @Override
    public void displayError(String title, String errorMessage) {
        controller.displayError(title, errorMessage);
    }

    @Override
    public void hide() {
        stage.hide();
    }

    @Override
    public void showPrompt(String title, String promptText, Runnable okCallback) {
        Dialog<ButtonType> d = new Dialog<>();
        d.setTitle(title);
        d.setContentText(promptText);
        d.initModality(Modality.APPLICATION_MODAL);
        d.initOwner(stage);
        d.initStyle(StageStyle.UNIFIED);

        d.getDialogPane().getButtonTypes().addAll(YES, NO);

        Optional<ButtonType> res = d.showAndWait();
        if (res.isPresent()) {
            // no switch because ButtonTypes aren't constant
            if (res.get() == YES) {
                okCallback.run();
            }
        }

    }
}
