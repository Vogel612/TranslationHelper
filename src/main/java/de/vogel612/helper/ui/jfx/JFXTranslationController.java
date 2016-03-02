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
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

/**
 * Created by vogel612 on 02.03.16.
 */
public class JFXTranslationController extends TranslationViewCommon implements Initializable {

    @FXML
    Label header;

    @FXML
    Label original;

    @FXML
    TextField input;

    @FXML
    Button cancel;

    @FXML
    Button submit;

    private Translation translation;

    @Override
    public void show() {
        // nothing
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
        input.setOnKeyTyped((evt) -> {
            if (evt.getCode() == KeyCode.ENTER) {
                if (evt.isShiftDown()) {
                    input.setText(input.getText() + "\r\n");
                } else {
                    submit.fire(); // FIXME verify?
                }
                evt.consume();
            } else if (evt.getCode() == KeyCode.ESCAPE) {
                cancel.fire();
            }
            // should keep bubbling to default handler
        });
    }
}
