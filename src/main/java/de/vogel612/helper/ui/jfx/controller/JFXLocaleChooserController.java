package de.vogel612.helper.ui.jfx.controller;

import de.vogel612.helper.ui.jfx.JFXLocaleChooserView.LocaleChoiceEvent;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.*;
import java.util.function.Consumer;

/**
 * <p>
 * Encapsulated is the fields necessary to store data, namely {@link #left} and {@link #right}
 * </p>
 */
public class JFXLocaleChooserController implements Initializable {

    private final Set<String> localeOptionCache = new TreeSet<>();
    private final Set<Consumer<LocaleChoiceEvent>> localeChoiceCompletionListener = new HashSet<>();
    private String left;
    private String right;
    @FXML
    private Label leftTranslation;

    @FXML
    private Label rightTranslation;

    @FXML
    private Button leftChoose;

    @FXML
    private Button rightChoose;

    @FXML
    private Button submit;

    private void showLocaleDialog(Consumer<String> callback) {
        final String[] localeChoices = localeOptionCache.toArray(new String[localeOptionCache.size()]);
        Arrays.sort(localeChoices);
        ChoiceDialog<String> dialog = new ChoiceDialog<>(localeChoices[0], localeChoices);
        dialog.setHeaderText("Choose a Language");
        dialog.setContentText("");
        dialog.show();
        dialog.setOnCloseRequest(evt -> {
            dialog.close();
            callback.accept(dialog.getSelectedItem());
        });
    }

    /**
     * Updates the currently available locales to the given collection of Strings
     *
     * @param locales
     *         The "new" currently available locales
     */
    public void updateAvailableLocales(final Collection<String> locales) {
        localeOptionCache.clear();
        localeOptionCache.addAll(locales);
        leftTranslation.setText("(none)");
        rightTranslation.setText("(none)");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Objects.requireNonNull(leftTranslation, "leftTranslation was not correctly FXML-injected");
        Objects.requireNonNull(rightTranslation, "rightTranslation was not correctly FXML-injected");
        Objects.requireNonNull(leftChoose, "leftChoose was not correctly FXML-injected");
        Objects.requireNonNull(rightChoose, "rightChoose was not correctly FXML-injected");
        Objects.requireNonNull(submit, "submit was not correctly FXML-injected");

        submit.setOnAction(evt -> completeChoice());
        leftChoose.setOnAction(evt -> showLocaleDialog(result -> {
            left = result;
            Platform.runLater(() -> leftTranslation.setText("Left: " + result));
        }));
        rightChoose.setOnAction(evt -> showLocaleDialog(result -> {
            right = result;
            Platform.runLater(() -> rightTranslation.setText("Right: " + result));
        }));
    }

    public void addCompletionListener(Consumer<LocaleChoiceEvent> listener) {
        localeChoiceCompletionListener.add(listener);
    }

    private void completeChoice() {
        if (left != null && right != null) {
            final LocaleChoiceEvent evt = new LocaleChoiceEvent(left, right);
            localeChoiceCompletionListener.forEach(l -> l.accept(evt));
        }
    }
}
