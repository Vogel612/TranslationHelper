package de.vogel612.helper.ui.jfx;

import de.vogel612.helper.data.Translation;
import de.vogel612.helper.ui.common.TranslationViewCommon;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;

public class JFXTranslationController extends TranslationViewCommon implements Initializable {

    @FXML
    Label header;

    @FXML
    TextArea original;

    @FXML
    TextArea input;

    @FXML
    Button cancel;

    @FXML
    Button submit;

    private Translation translation;

    @Override
    public void show() {
        input.requestFocus();
    }

    @Override
    public void hide() {
        // nothing
    }

    @Override
    public void setRequestedTranslation(Translation left, Translation right) {
        this.translation = right;
        this.original.setText(left.getValue());
        input.setText(translation.getValue());
        header.setText(String.format(TITLE_FORMAT, translation.getKey(), translation.getLocale()));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Objects.requireNonNull(header, "header was not correctly FXML-Injected");
        Objects.requireNonNull(original, "original was not correctly FXML-Injected");
        Objects.requireNonNull(input, "input was not correctly FXML-Injected");
        Objects.requireNonNull(cancel, "cancel was not correctly FXML-Injected");
        Objects.requireNonNull(submit, "submit was not correctly FXML-Injected");

        submit.setOnAction(evt -> {
            translation.setValue(input.getText());
            translationSubmitListeners.forEach(c -> c.accept(translation));
        });
        cancel.setOnAction(evt -> translationAbortListeners.forEach(Runnable::run));
        input.setOnKeyPressed((evt) -> {
            if (evt.getCode() == KeyCode.ENTER) {
                if (evt.isShiftDown()) {
                    int caret = input.getCaretPosition();
                    String newValue = input.getText();
                    newValue = newValue.substring(0, caret) + "\r\n"
                      + newValue.substring(caret);
                    input.setText(newValue);
                    input.positionCaret(caret + 1);
                } else {
                    submit.fire();
                }
                evt.consume();
            } else if (evt.getCode() == KeyCode.ESCAPE) {
                cancel.fire();
                evt.consume();
            }
            // should keep bubbling to default handler
        });
    }
}
