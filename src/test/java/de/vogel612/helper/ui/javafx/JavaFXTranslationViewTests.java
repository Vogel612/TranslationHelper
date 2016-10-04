package de.vogel612.helper.ui.javafx;

import static org.mockito.Mockito.*;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.control.TextInputControlMatchers.hasText;

import org.junit.Before;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;
import de.vogel612.helper.data.Translation;
import de.vogel612.helper.ui.jfx.JFXTranslationView;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import javafx.application.Platform;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

/**
 * Created by vogel612 on 11.04.16.
 */
public class JavaFXTranslationViewTests extends ApplicationTest {

    private JFXTranslationView cut;

    private final Runnable abortListener;
    private final Consumer<Translation> submitListener;

    public JavaFXTranslationViewTests() {
        abortListener = mock(Runnable.class);
        submitListener = mock(Consumer.class);
    }

    @Override
    public void start(Stage stage) throws Exception {
        cut = new JFXTranslationView(stage, getClass().getResource("/TranslationView.fxml"));
        stage.setAlwaysOnTop(true);
        cut.addTranslationAbortListener(abortListener);
        cut.addTranslationSubmitListener(submitListener);
        cut.show();
        sleep(700, TimeUnit.MILLISECONDS);
    }

    @Before
    public void before() {
        cut.setRequestedTranslation(new Translation("", "", ""), new Translation("", "", ""));
        cut.show();
        sleep(700, TimeUnit.MILLISECONDS);
    }

    @Test
    public void testUiElementsArePresent() {
        verifyThat("#original", hasText(""));
    }

    @Test
    public void abortButton_firesAbortListener() {
        clickOn("#cancel");

        verify(abortListener).run();
        verifyNoMoreInteractions(abortListener, submitListener);
    }

    @Test
    public void settingTranslation_fillsRelevantUI() {
        Platform.runLater(() -> {
            cut.setRequestedTranslation(new Translation("src", "key", "original"),
              new Translation("target", "key", "current"));
        });
        sleep(200, TimeUnit.MILLISECONDS);
        verifyThat("#input", hasText("current"));
        verifyThat("#input", TextInputControl::isEditable);
        verifyThat("#original", hasText("original"));
        verifyThat("#original", TextInputControl::isEditable);
        // FIXME verify title
        verifyNoMoreInteractions(abortListener, submitListener);
    }

    @Test
    public void submitButton_firesSubmitListener() {
        clickOn("#submit");


        verify(submitListener, times(2)).accept(any());
        verifyNoMoreInteractions(abortListener, submitListener);
    }

    @Test
    public void changingTextInput_changesTranslationValue() {
        clickOn("#input");
        type(KeyCode.T, KeyCode.E, KeyCode.S, KeyCode.T);
        clickOn("#submit");

        verifyThat("#input", TextInputControl::isEditable);
        verifyThat("#original", TextInputControl::isEditable);
        verify(submitListener).accept(eq(new Translation("", "", "test")));
        verify(submitListener).accept(eq(new Translation("", "", "")));
        verifyNoMoreInteractions(submitListener, abortListener);
    }

    @Test
    public void pressingEnter_inTextField_firesSubmitListener() {
        clickOn("#input");
        type(KeyCode.T, KeyCode.E, KeyCode.S, KeyCode.T, KeyCode.ENTER);

        verifyThat("#input", TextInputControl::isEditable);
        verifyThat("#original", TextInputControl::isEditable);
        verify(submitListener).accept(eq(new Translation("", "", "test")));
        verify(submitListener).accept(eq(new Translation("", "", "")));
        verifyNoMoreInteractions(submitListener, abortListener);
    }

    @Test
    public void pressingEsc_firesAbortListener() {
        clickOn("#input");
        type(KeyCode.ESCAPE);
        sleep(200, TimeUnit.MILLISECONDS);

        verify(abortListener).run();
        verifyNoMoreInteractions(abortListener, submitListener);
    }

}
