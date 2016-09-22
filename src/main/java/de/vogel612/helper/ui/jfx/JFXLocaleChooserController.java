package de.vogel612.helper.ui.jfx;


import de.vogel612.helper.ui.LocaleChooser;
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
 * Encapsulates common functionality for {@link LocaleChooser Resx Choosers} into an abstract class.<br/>
 * <p>
 * Encapsulated is the fields necessary to store data, namely {@link #left}, {@link #right} and {@link
 * #filesetBacking}.
 * Additionally there is an automatic reparsing functionality of the encapsulated {@link #localeOptionCache}, given
 * implementing classes call {@link #onFilesetChange()} when the Fileset changes. That triggers a recheck for the
 * locale
 * options. Additionally {@link #onFilesetChange()} will call {@link #onFilesetChangeInternal()} upon completion to
 * notify the implementing class.
 * </p>
 * <p>
 * There even is a method for internal use, that handles listener notification, namely {@link #completeChoice()}. It
 * will access the fields in the current class to build a {@link LocaleChooserCommon.ResxChooserEvent}
 * with the relevant information
 * </p>
 */
public class JFXLocaleChooserController implements Initializable {

    private final Set<String> localeOptionCache = new HashSet<>();
    private final Set<Consumer<LocaleChoiceEvent>> resxChoiceCompletionListener = new HashSet<>();
    protected String left;
    protected String right;
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
        ChoiceDialog<String> dialog = new ChoiceDialog<>(localeChoices[0], localeChoices);
        dialog.setHeaderText("Choose a Language");
        dialog.setContentText("");
        dialog.show();
        dialog.setOnCloseRequest(evt -> {
            dialog.close();
            callback.accept(dialog.getSelectedItem());
        });
    }


    public void updateAvailableLocales(final Collection<String> locales) {
        localeOptionCache.clear();
        localeOptionCache.addAll(locales);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Objects.requireNonNull(leftTranslation, "leftTranslation was not correctly FXML-injected");
        Objects.requireNonNull(rightTranslation, "rightTranslation was not correctly FXML-injected");
        Objects.requireNonNull(leftChoose, "leftChoose was not correctly FXML-injected");
        Objects.requireNonNull(rightChoose, "rightChoose was not correctly FXML-injected");
        Objects.requireNonNull(submit, "submit was not correctly FXML-injected");

        leftChoose.setDisable(true);
        rightChoose.setDisable(true);
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

    public final void addCompletionListener(Consumer<LocaleChoiceEvent> listener) {
        resxChoiceCompletionListener.add(listener);
    }

    protected final void completeChoice() {
        if (left != null && right != null) {
            final LocaleChoiceEvent evt = new LocaleChoiceEvent(left, right);
            resxChoiceCompletionListener.forEach(l -> l.accept(evt));
        }
    }
}
