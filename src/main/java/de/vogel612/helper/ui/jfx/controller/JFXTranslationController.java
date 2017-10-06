package de.vogel612.helper.ui.jfx.controller;

import de.vogel612.helper.data.Translation;

import java.net.URL;
import java.util.HashSet;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.function.Consumer;

import de.vogel612.helper.ui.TranslationView;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;

public class JFXTranslationController implements Initializable, TranslationView {

    private static final String TITLE_FORMAT = "Translating - %s to %s";
    private final Set<Runnable> translationAbortListeners = new HashSet<>();
    private final Set<Consumer<Translation>> translationSubmitListeners = new HashSet<>();

    @Override
    public final void addTranslationSubmitListener(Consumer<Translation> listener) {
        translationSubmitListeners.add(listener);
    }

    @Override
    public final void addTranslationAbortListener(Runnable listener) {
        translationAbortListeners.add(listener);
    }


    @FXML
    private
    Label header;

    @FXML
    private
    TextArea original;

    @FXML
    private
    TextArea input;

    @FXML
    private
    Button cancel;

    @FXML
    private
    Button submit;

    @FXML
    private
    SplitPane pane;

    private Translation translation;
    private Translation leftSide;

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
        this.leftSide = left;
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
        Objects.requireNonNull(pane, "pane was not correctly FXML-Injected");

        pane.setDividerPosition(0, 0.5);
        pane.setOrientation(Orientation.HORIZONTAL);

        submit.setOnAction(evt -> {
            translation.setValue(input.getText());
            translationSubmitListeners.forEach(c -> c.accept(translation));
            leftSide.setValue(original.getText());
            translationSubmitListeners.forEach(c -> c.accept(leftSide));
        });
        cancel.setOnAction(evt -> translationAbortListeners.forEach(Runnable::run));
        input.setOnKeyPressed((evt) -> {
            if (evt.getCode() == KeyCode.ENTER) {
                if (evt.isShiftDown()) {
                    int caret = input.getCaretPosition();
                    String newValue = input.getText();
                    newValue = newValue.substring(0, caret) + "\r\n" // fixed because windows
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
