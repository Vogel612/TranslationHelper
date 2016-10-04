package de.vogel612.helper.ui.javafx;

import de.vogel612.helper.ui.jfx.JFXLocaleChooserView;
import de.vogel612.helper.ui.jfx.JFXLocaleChooserView.LocaleChoiceEvent;
import javafx.scene.control.ComboBox;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.service.query.impl.NodeQueryUtils.hasText;

@Ignore("Currently under a complete rewrite ...")
public class JavaFXResxChooserTests extends ApplicationTest {

    private JFXLocaleChooserView cut;

    private final Consumer<LocaleChoiceEvent> listener;

    public JavaFXResxChooserTests() {
        listener = mock(Consumer.class);
    }

    @Override
    public void start(Stage stage) throws Exception {
        cut = new JFXLocaleChooserView(stage, getClass().getResource("/LocaleChooser.fxml"));
        cut.addCompletionListener(listener);
        stage.setAlwaysOnTop(true);
        stage.show();
    }

    @Before
    public void before() throws URISyntaxException {
//        cut.setFileset(Paths.get(getClass().getResource("/RubberduckUI.resx").toURI()));
        cut.show();
        sleep(700, TimeUnit.MILLISECONDS);
    }


    @Test
    public void setFileset_updatesUiLabel() throws URISyntaxException {
//        cut.setFileset(Paths.get(getClass().getResource("/RubberduckUI.resx").toURI()));

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
    }

    @Test
    public void chooseRight_opensDialog() {
        clickOn("#rightChoose");
        sleep(500, TimeUnit.MILLISECONDS);

        clickOn(".combo-box"); // this opens the dropdown for checking
        ComboBox<String> cb = lookup(".combo-box").queryFirst();
        assertTrue(cb.getItems().containsAll(Arrays.asList("", "ts")));

        verifyNoMoreInteractions(listener);
    }


    @Test(expected = IllegalArgumentException.class)
    public void submitButton_withUnselectedPath_doesNothing() {
//        cut.setFileset(null);
        verifyNoMoreInteractions(listener);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setFileset_withInexistantFile_throws() {
//        cut.setFileset(Paths.get("/", "some", "stupid", "file", "that", "does", "not", "exist.resx"));
        verifyNoMoreInteractions(listener);
    }


    @After
    public void cleanupIsh() {
        type(KeyCode.ESCAPE);
        type(KeyCode.ESCAPE);
    }
}
