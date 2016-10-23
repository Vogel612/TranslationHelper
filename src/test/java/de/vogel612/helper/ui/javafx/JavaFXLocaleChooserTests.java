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
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.service.query.impl.NodeQueryUtils.hasText;

@Ignore("Currently under a complete rewrite ...")
public class JavaFXLocaleChooserTests extends ApplicationTest {

    private JFXLocaleChooserView cut;

    private final Consumer<LocaleChoiceEvent> listener;

    public JavaFXLocaleChooserTests() {
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
        cut.show();
        sleep(700, TimeUnit.MILLISECONDS);
    }


    @Test
    public void setFileset_updatesUiLabel() throws URISyntaxException {
        verifyThat("#fileset", hasText("RubberduckUI"));
        verifyThat("#leftTranslation", hasText("(none)"));
        verifyThat("#rightTranslation", hasText("(none)"));

        verifyNoMoreInteractions(listener);
    }

    @Test
    public void chooseLeft_opensDialog() {
        clickOn("#leftChoose");
        // FIXME Use an await instead
        sleep(500, TimeUnit.MILLISECONDS);

        clickOn(".combo-box"); // this opens the dropdown for checking
        ComboBox<String> cb = lookup(".combo-box").queryFirst();
        assertTrue(cb.getItems().containsAll(Arrays.asList("", "ts")));

        verifyNoMoreInteractions(listener);
    }

    @Test
    public void chooseRight_opensDialog() {
        clickOn("#rightChoose");
        // FIXME Use an await instead
        sleep(500, TimeUnit.MILLISECONDS);

        clickOn(".combo-box"); // this opens the dropdown for checking
        ComboBox<String> cb = lookup(".combo-box").queryFirst();
        assertTrue(cb.getItems().containsAll(Arrays.asList("", "ts")));

        verifyNoMoreInteractions(listener);
    }

    @After
    public void cleanupIsh() {
        // this should drop all focus and remove all popups
        type(KeyCode.ESCAPE);
        type(KeyCode.ESCAPE);
    }
}
