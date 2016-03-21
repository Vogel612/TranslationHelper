package de.vogel612.helper.ui.javafx;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.control.LabeledMatchers.hasText;

import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;
import de.vogel612.helper.ui.jfx.JFXResxChooserView;


import java.net.URISyntaxException;
import java.nio.file.Paths;

import javafx.stage.Stage;

/**
 * Created by vogel612 on 17.03.16.
 */
public class ResxChooserTests extends ApplicationTest {

    private JFXResxChooserView cut;

    @Override
    public void start(Stage stage) throws Exception {
        cut = new JFXResxChooserView(stage, getClass().getResource("/ResxChooser.fxml"));
        cut.show();
    }

    @Test
    public void show_setsSceneOnStage() throws URISyntaxException {
        cut.setFileset(Paths.get(getClass().getResource("/RubberduckUI.resx").toURI()));

        verifyThat("#fileset", hasText("RubberduckUI"));
    }
}
