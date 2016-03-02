package de.vogel612.helper.ui.jfx;


import static de.vogel612.helper.data.util.DataUtilities.FILENAME_PATTERN;

import de.vogel612.helper.ui.common.ResxChooserCommon;

import java.io.File;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.regex.Matcher;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class JFXResxChooserController extends ResxChooserCommon implements Initializable {

    @FXML
    private Label leftTranslation;

    @FXML
    private Label rightTranslation;

    @FXML
    private Label fileset;

    @FXML
    private Button leftChoose;

    @FXML
    private Button rightChoose;

    @FXML
    private Button filesetChoose;

    @FXML
    private Button submit;

    private final FileChooser fileChooser = new FileChooser();

    public JFXResxChooserController() {
        fileChooser.setTitle("Choose RESX file");
        fileChooser.getExtensionFilters().add(new ExtensionFilter("Resx Files", "resx"));
    }

    @Override
    protected void onFilesetChangeInternal() {
        final Matcher filesetMatcher = FILENAME_PATTERN.matcher(filesetBacking.getFileName().toString());
        if (filesetMatcher.matches()) { // should always be true
            // group is not optional, so we're good
            final String filesetName = filesetMatcher.group(1);
            Platform.runLater(() -> fileset.setText(filesetName));
            // drop locales we cannot use anymore...
            if (!localeOptionCache.contains(left)) {
                left = null;
                Platform.runLater(() -> leftTranslation.setText("(none)"));
            }
            if (!localeOptionCache.contains(right)) {
                right = null;
                Platform.runLater(() -> rightTranslation.setText("(none)"));
            }
        }
    }

    private void showLocaleDialog(Consumer<String> callback) {
        final String[] localeChoices = localeOptionCache.toArray(new String[localeOptionCache.size()]);
        // FIXME rewrite as JavaFX dialog
        ChoiceDialog<String> dialog = new ChoiceDialog<>(localeChoices[0], localeChoices);
        dialog.setHeaderText("Choose a Language");
        dialog.setContentText("");
        dialog.show();
        dialog.setOnCloseRequest(evt -> {
            dialog.close();
            callback.accept(dialog.getSelectedItem());
        });
    }

    @Override
    public void hide() {

    }

    @Override
    // FIXME probably need to get that into presenter
    public void show() {

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Objects.requireNonNull(leftTranslation, "leftTranslation was not correctly FXML-injected");
        Objects.requireNonNull(rightTranslation, "rightTranslation was not correctly FXML-injected");
        Objects.requireNonNull(fileset, "fileset was not correctly FXML-injected");
        Objects.requireNonNull(leftChoose, "leftChoose was not correctly FXML-injected");
        Objects.requireNonNull(rightChoose, "rightChoose was not correctly FXML-injected");
        Objects.requireNonNull(filesetChoose, "filesetChoose was not correctly FXML-injected");
        Objects.requireNonNull(submit, "submit was not correctly FXML-injected");

        submit.setOnAction(evt -> completeChoice());
        filesetChoose.setOnAction(evt -> {
            evt.consume();
            // FIXME need the owner window for proper modal behaviour
            final File resxFile = fileChooser.showOpenDialog(null);
            setFileset(resxFile.toPath()); // updates UI for us
        });
        leftChoose.setOnAction(evt -> showLocaleDialog(result -> {
            left = result;
            Platform.runLater(() -> leftTranslation.setText("Left: " + result));
        }));
        rightChoose.setOnAction(evt -> showLocaleDialog(result -> {
            right = result;
            Platform.runLater(() -> rightTranslation.setText("Right: " + result));
        }));
    }
}
