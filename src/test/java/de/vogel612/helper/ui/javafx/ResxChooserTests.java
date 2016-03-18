package de.vogel612.helper.ui.javafx;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import com.sun.javafx.application.PlatformImpl;
import com.sun.javafx.tk.Toolkit;
import javafx.application.Platform;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import de.vogel612.helper.ui.jfx.JFXResxChooserView;

import java.io.IOException;

import javafx.stage.Stage;

/**
 * Created by vogel612 on 17.03.16.
 */
public class ResxChooserTests {

    private JFXResxChooserView cut;

    @Mock
    Stage stage;

    @Before
    public void start() throws IOException {
        stage = mock(Stage.class);
        cut = new JFXResxChooserView(stage, getClass().getResource("/ResxChooser.fxml"));
    }

    @Test
    public void show_setsSceneOnStage() {
        cut.show();

        verify(stage).setScene(any());
        verify(stage).show();
        verifyNoMoreInteractions(stage);
    }
}
