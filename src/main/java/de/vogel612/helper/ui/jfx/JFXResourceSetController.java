package de.vogel612.helper.ui.jfx;

import de.vogel612.helper.data.ResourceSet;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Created by vogel612 on 22.12.16.
 */
public class JFXResourceSetController implements Initializable {

    private final Set<Consumer<ResourceSet>> resourceSetRequests;
    @FXML
    private TextField name;
    @FXML
    private TextField path;
    @FXML
    private GridPane subPane;
    @FXML
    private Button addLocale;
    @FXML
    private Button translateButton;

    private final ResourceSet resourceSet;

    public JFXResourceSetController(ResourceSet containedSet, Set<Consumer<ResourceSet>> resourceSetRequests) {
        this.resourceSet = containedSet;
        this.resourceSetRequests = resourceSetRequests; // no def. copy
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        name.setText(resourceSet.getName());
        path.setText(resourceSet.getFolder().toAbsolutePath().toString());
        translateButton.setOnAction(evt -> fireResourceSetRequests());
        addLocale.setOnAction(evt -> {
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Add Locale");
            dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
            dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
            TextField localeField = new TextField();
            dialog.setGraphic(localeField);
            Optional<ButtonType> button = dialog.showAndWait();
            if (button.isPresent() && button.get().equals(ButtonType.OK)) {
                resourceSet.addLocale(localeField.getText());
                renderLocales();
            }
        });
        renderLocales();
    }

    private void renderLocales() {
        // keep label and add button
        subPane.getChildren().retainAll(subPane.getChildren().get(0), subPane.getChildren().get(1));
        int currentRow = 1;
        for (String locale : resourceSet.getLocales()) {
            Label label = new Label(locale);
            label.setPadding(new Insets(0,0,0,10));
            subPane.add(label, 0, currentRow);

            Button removeButton = new Button("-");
            removeButton.setOnAction(evt -> {
                        resourceSet.removeLocale(locale);
                        renderLocales();
                    });
            removeButton.setMinWidth(40);
            subPane.add(removeButton, 1, currentRow);
            currentRow++;
        }
        subPane.requestLayout();
    }

    public void fireResourceSetRequests() {
        resourceSetRequests.forEach(listener -> listener.accept(resourceSet));
    }
}
