package de.vogel612.helper.ui.jfx;

import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.stage.Modality;
import javafx.stage.StageStyle;

import java.util.Optional;

import static javafx.scene.control.ButtonType.CANCEL;
import static javafx.scene.control.ButtonType.OK;

/**
 * Created by vogel612 on 02.06.16.
 */
public class JFXDialog implements de.vogel612.helper.ui.Dialog {

    private static final ButtonType IGNORE = new ButtonType("Ignore", ButtonBar.ButtonData.RIGHT);

    private static Dialog<ButtonType> createBasicDialog(final String title, final String message) {
        Dialog<ButtonType> d = new Dialog<>();
        d.setTitle(title);
        d.setContentText(message);
        d.initModality(Modality.APPLICATION_MODAL);
        d.initStyle(StageStyle.UNDECORATED);
        return d;
    }

    @Override
    public void info(String title, String message) {
        Dialog<ButtonType> d = createBasicDialog(title, message);
        d.getDialogPane().getButtonTypes().addAll(OK);
        d.show();
    }

    @Override
    public void info(String title, String message, Runnable okCallback) {
        Dialog<ButtonType> d = createBasicDialog(title, message);
        d.getDialogPane().getButtonTypes().addAll(OK);
        Optional<ButtonType> result = d.showAndWait();
        if (result.isPresent() && result.get() == OK) {
            okCallback.run();
        }
    }

    @Override
    public void warn(String title, String message, Runnable okCallback, Runnable cancelCallback) {
        Dialog<ButtonType> d = createBasicDialog(title, message);
        d.getDialogPane().getButtonTypes().addAll(OK, CANCEL);
        Optional<ButtonType> result = d.showAndWait();
        if (result.isPresent()) {
            if (result.get() == OK) {
                okCallback.run();
            } else if (result.get() == CANCEL) {
                cancelCallback.run();
            }
        }
    }

    @Override
    public void warn(String title, String message, Runnable okCallback, Runnable cancelCallback, Runnable ignoreCallback) {
        Dialog<ButtonType> d = createBasicDialog(title, message);

        d.getDialogPane().getButtonTypes().addAll(OK, CANCEL, IGNORE);
        Optional<ButtonType> result = d.showAndWait();
        if (result.isPresent()) {
            if (result.get() == OK) {
                okCallback.run();
            } else if (result.get() == CANCEL) {
                cancelCallback.run();
            } else if (result.get() == IGNORE) {
                ignoreCallback.run();
            }
        }
    }
}
