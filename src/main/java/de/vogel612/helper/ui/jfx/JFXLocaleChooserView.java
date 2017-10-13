package de.vogel612.helper.ui.jfx;

import de.vogel612.helper.ui.LocaleChooser;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.function.Consumer;

import de.vogel612.helper.ui.jfx.controller.JFXLocaleChooserController;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Created by vogel612 on 02.03.16.
 */
public class JFXLocaleChooserView implements LocaleChooser {

    private final Stage stage;
    private final Scene ui;
    private final JFXLocaleChooserController controller;

    public JFXLocaleChooserView(Stage stage, URL fxml) throws IOException {
        this.stage = stage;
        FXMLLoader loader = new FXMLLoader(fxml);
        ui = new Scene(loader.load());
        controller = loader.getController();
        controller.initOwner(stage);
    }

    @Override
    public void hide() {
        Platform.runLater(stage::hide);
    }

    @Override
    public void show() {
        Platform.runLater(() -> {
            stage.setScene(ui);
            stage.show();
            stage.requestFocus(); // get to the front!
        });
    }

    @Override
    public void addCompletionListener(Consumer<LocaleChoiceEvent> listener) {
        controller.addCompletionListener(listener);
    }

    @Override
    public void updateAvailableLocales(Collection<String> locales) {
        controller.updateAvailableLocales(locales);
    }

    /**
     * An event signaling the choice of fileset and locale is completed
     */
    public static class LocaleChoiceEvent {
        private final String rightLocale;
        private final String leftLocale;

        public LocaleChoiceEvent(final String left, final String right) {
            this.leftLocale = left;
            this.rightLocale = right;
        }

        public String getLeftLocale() {
            return leftLocale;
        }

        public String getRightLocale() {
            return rightLocale;
        }
    }
}
