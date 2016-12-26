package de.vogel612.helper.ui.jfx;

import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.StageStyle;

import java.io.File;
import java.nio.file.Path;
import java.util.Optional;

import static javafx.scene.control.ButtonType.CANCEL;
import static javafx.scene.control.ButtonType.OK;

/**
 * Created by vogel612 on 02.06.16.
 */
public class JFXDialog {

    public static final JFXDialog DIALOG = new JFXDialog();

    private static final ButtonType IGNORE = new ButtonType("Ignore", ButtonBar.ButtonData.RIGHT);
    private static final FileChooser fileChooser = new FileChooser();

    private JFXDialog() {
        // fuck it we'll do singleton now...
    }

    public Path chooseFile(final String title, final FileChooser.ExtensionFilter... extensions) {
        fileChooser.setTitle(title);
        fileChooser.getExtensionFilters().clear();
        fileChooser.getExtensionFilters().addAll(extensions);
        File result = fileChooser.showOpenDialog(null);
        return result == null ?  null : result.toPath();
    }

    private Dialog<ButtonType> createBasicDialog(final String title, final String message) {
        Dialog<ButtonType> d = new Dialog<>();
        d.setTitle(title);
        d.setContentText(message);
        d.initModality(Modality.APPLICATION_MODAL);
        d.initStyle(StageStyle.UNDECORATED);
        return d;
    }

    /**
     * Displays an info dialog, which has a title, a message and an OK button. That button doesn't do jack
     *
     * @param title
     *         The title to use for the dialog
     * @param message
     *         The message of the dialog
     */
    public void info(String title, String message) {
        Dialog<ButtonType> d = createBasicDialog(title, message);
        d.getDialogPane().getButtonTypes().addAll(OK);
        d.show();
    }

    /**
     * Displays an info dialog, which has a title, a message and an OK button.
     * The given callback is run when the OK button is pressed.
     *
     * @param title
     *         The title to use for the dialog
     * @param message
     *         The message of the dialog
     * @param okCallback
     *         Something to run when the user clicks OK
     */
    public void info(String title, String message, Runnable okCallback) {
        Dialog<ButtonType> d = createBasicDialog(title, message);
        d.getDialogPane().getButtonTypes().addAll(OK);
        Optional<ButtonType> result = d.showAndWait();
        if (result.isPresent() && result.get() == OK) {
            okCallback.run();
        }
    }

    /**
     * Displays a warning dialog with title, message and the options "OK" and "Cancel". T
     * The given callbacks are run when the respective buttons are pressed.
     *
     * @param title
     *         The title to use for the dialog
     * @param message
     *         The message of the dialog
     * @param okCallback
     *         Something to run when the user clicks OK
     * @param cancelCallback
     *         Something to run when the user clicks Cancel
     */
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

    /**
     * Displays a warning dialog with title, message and the options "OK", "Cancel" and "Ignore".
     * The given callbacks are run when the respective buttons are pressed.
     *
     * @param title
     *         The title to use for the dialog
     * @param message
     *         The message of the dialog
     * @param okCallback
     *         Something to run when the user clicks OK
     * @param cancelCallback
     *         Something to run when the user clicks Cancel
     * @param ignoreCallback
     *         Something to run when the user clicks Ignore
     */
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
