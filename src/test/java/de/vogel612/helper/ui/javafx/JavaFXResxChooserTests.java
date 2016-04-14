package de.vogel612.helper.ui.javafx;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.hasText;

import com.google.common.base.Predicate;
import org.junit.Before;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;
import de.vogel612.helper.ui.common.ResxChooserCommon.ResxChooserEvent;
import de.vogel612.helper.ui.jfx.JFXResxChooserView;

import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

public class JavaFXResxChooserTests extends ApplicationTest {

    private JFXResxChooserView cut;

    private Consumer<ResxChooserEvent> listener;

    public JavaFXResxChooserTests() {
        listener = mock(Consumer.class);
    }

    @Override
    public void start(Stage stage) throws Exception {
        cut = new JFXResxChooserView(stage, getClass().getResource("/ResxChooser.fxml"));
        cut.addCompletionListener(listener);
    }

    @Before
    public void before() throws URISyntaxException {
        cut.setFileset(Paths.get(getClass().getResource("/RubberduckUI.resx").toURI()));
        cut.show();
        sleep(700, TimeUnit.MILLISECONDS);
    }


    @Test
    public void setFileset_updatesUiLabel() throws URISyntaxException {
        cut.setFileset(Paths.get(getClass().getResource("/RubberduckUI.resx").toURI()));

        sleep(300, TimeUnit.MILLISECONDS);
        verifyThat("#fileset", hasText("RubberduckUI"));
        verifyThat("#leftTranslation", hasText("(none)"));
        verifyThat("#rightTranslation", hasText("(none)"));

        verifyNoMoreInteractions(listener);
    }

    @Test
    public void chooseLeft_opensDialog() {
        clickOn("#leftChoose");
        sleep(500, TimeUnit.MILLISECONDS);

        clickOn(".combo-box"); // this opens the dropdown for checking
        ComboBox<String> cb = lookup(".combo-box").queryFirst();
        assertTrue(cb.getItems().containsAll(Arrays.asList("", "ts")));

        verifyNoMoreInteractions(listener);

        // cleanup by closing the dialog
        type(KeyCode.ESCAPE);
    }

    @Test
    public void chooseRight_opensDialog() {
        clickOn("#rightChoose");
        sleep(500, TimeUnit.MILLISECONDS);

        clickOn(".combo-box"); // this opens the dropdown for checking
        ComboBox<String> cb = lookup(".combo-box").queryFirst();
        assertTrue(cb.getItems().containsAll(Arrays.asList("", "ts")));

        verifyNoMoreInteractions(listener);
        // cleanup by closing the dialog
        type(KeyCode.ESCAPE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void submitButton_withUnselectedPath_doesNothing() {
        cut.setFileset(null);
        verifyNoMoreInteractions(listener);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setFileset_withInexistantFile_throws() {
        cut.setFileset(Paths.get("/", "some", "stupid", "file", "that", "does", "not", "exist.resx"));
        verifyNoMoreInteractions(listener);
    }
}
