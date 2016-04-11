package de.vogel612.helper.ui.javafx;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.control.TextInputControlMatchers.hasText;

import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import de.vogel612.helper.ui.jfx.JFXTranslationView;

import java.util.concurrent.TimeUnit;

import javafx.stage.Stage;

/**
 * Created by vogel612 on 11.04.16.
 */
public class JavaFXTranslationViewTests extends ApplicationTest {

    private JFXTranslationView cut;

    @Override
    public void start(Stage stage) throws Exception {
        cut = new JFXTranslationView(stage, getClass().getResource("/TranslationView.fxml"));
        cut.show();
    }

    @Test
    public void testUiElementsArePresent() {
        sleep(700, TimeUnit.MILLISECONDS);
        verifyThat("#original", hasText(""));
    }

}
