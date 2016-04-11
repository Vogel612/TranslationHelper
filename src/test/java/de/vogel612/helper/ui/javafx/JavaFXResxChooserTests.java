package de.vogel612.helper.ui.javafx;

import static org.junit.Assert.assertTrue;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.hasText;

import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;
import de.vogel612.helper.ui.jfx.JFXResxChooserView;

import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;

/**
 * Created by vogel612 on 17.03.16.
 */
public class JavaFXResxChooserTests extends ApplicationTest {

    private JFXResxChooserView cut;

    @Override
    public void start(Stage stage) throws Exception {
        cut = new JFXResxChooserView(stage, getClass().getResource("/ResxChooser.fxml"));
        cut.setFileset(Paths.get(getClass().getResource("/RubberduckUI.resx").toURI()));
        cut.show();
    }

    @Test
    public void setFileset_updatesUiLabel() throws URISyntaxException {
        cut.setFileset(Paths.get(getClass().getResource("/RubberduckUI.resx").toURI()));

        sleep(700, TimeUnit.MILLISECONDS);
        verifyThat("#fileset", hasText("RubberduckUI"));
        verifyThat("#leftTranslation", hasText("(none)"));
        verifyThat("#rightTranslation", hasText("(none)"));
    }

    @Test
    public void chooseLeft_opensDialog() {
        sleep(500, TimeUnit.MILLISECONDS);
        clickOn((Node)lookup(".button").selectAt(0).queryFirst());
        sleep(500, TimeUnit.MILLISECONDS);

        clickOn(".combo-box"); // this opens the dropdown for checking
        ComboBox<String> cb = lookup(".combo-box").queryFirst();
        assertTrue(cb.getItems().containsAll(Arrays.asList("", "ts")));
    }
}
