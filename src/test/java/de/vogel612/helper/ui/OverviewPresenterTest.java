package de.vogel612.helper.ui;

import static org.mockito.Mockito.*;

import de.vogel612.helper.ui.jfx.JFXDialog;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.junit.Before;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

@SuppressWarnings("ThrowableResultOfMethodCallIgnored")
public class OverviewPresenterTest extends ApplicationTest {

    private OverviewView overviewView;
    private JFXDialog dialog;

    private OverviewPresenter cut;

    @Before
    public void beforeTest() {
        overviewView = mock(OverviewView.class);
        dialog = mock(JFXDialog.class);
        cut = new OverviewPresenter(overviewView, pv);
        reset(overviewView);
    }

    @Test
    public void show_callsShow_onView() {
        cut.show();
        verify(overviewView).show();
        verifyNoMoreInteractions(overviewView, dialog);
    }

    @Test
    public void onException_delegatesToView() {
        final Exception e = mock(Exception.class);
        final String message = "testingmessage";
        final String errorMessage = "alsdkj";
        doReturn(errorMessage).when(e).getMessage();

        cut.onException(e, message);

        Platform.runLater(() -> verify(dialog).info(message, errorMessage));
        verify(e).getMessage();
        sleep(60); // await verification
        verifyNoMoreInteractions(overviewView, dialog);
    }

    @Override
    public void start(Stage stage) throws Exception {
        // no-op ... probably ...
    }
}
