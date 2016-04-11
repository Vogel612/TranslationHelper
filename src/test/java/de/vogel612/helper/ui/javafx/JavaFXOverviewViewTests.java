package de.vogel612.helper.ui.javafx;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.hasText;

import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;
import de.vogel612.helper.ui.jfx.JFXOverviewView;

import java.util.concurrent.TimeUnit;

import javafx.stage.Stage;

/**
 * Created by vogel612 on 11.04.16.
 */
public class JavaFXOverviewViewTests extends ApplicationTest {

    private JFXOverviewView cut;

    @Override
    public void start (Stage stage) throws Exception {
        cut = new JFXOverviewView(stage, getClass().getResource("/OverviewView.fxml"));
        cut.show();
    }

    @Test
    public void hasAllUIElements() {
        sleep(700, TimeUnit.MILLISECONDS);

        verifyThat("#save", hasText("Save"));
        verifyThat("#chooseLang", hasText("Choose Languages"));
    }


}
