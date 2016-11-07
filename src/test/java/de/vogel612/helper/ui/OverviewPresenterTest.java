package de.vogel612.helper.ui;

import de.vogel612.helper.ui.jfx.JFXDialog;
import javafx.stage.Stage;
import org.junit.Before;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import static org.mockito.Mockito.*;

@SuppressWarnings("ThrowableResultOfMethodCallIgnored")
public class OverviewPresenterTest extends ApplicationTest {

    private OverviewView overviewView;
    private JFXDialog dialog;
    private ProjectView projectView;

    private OverviewPresenter cut;

    @Before
    public void beforeTest() {
        overviewView = mock(OverviewView.class);
        dialog = spy(JFXDialog.DIALOG);
        projectView = mock(ProjectView.class);

        cut = new OverviewPresenter(overviewView, projectView);
        reset(overviewView);
    }

    @Test
    public void show_callsShow_onView() {
        cut.show();
        verify(overviewView).show();
        verifyNoMoreInteractions(overviewView, dialog);
    }

    @Override
    public void start(Stage stage) throws Exception {
        // no-op ... probably ...
    }
}
