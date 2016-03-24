package de.vogel612.helper.ui.swing;

import static org.assertj.swing.finder.WindowFinder.findFrame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.assertj.swing.core.GenericTypeMatcher;
import org.assertj.swing.edt.FailOnThreadViolationRepaintManager;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Before;
import org.junit.Test;
import de.vogel612.helper.ui.ResxChooser;
import de.vogel612.helper.ui.common.ResxChooserCommon.ResxChooserEvent;

import java.awt.*;
import java.nio.file.Paths;
import java.util.function.Consumer;

/**
 * Created by vogel612 on 26.01.16.
 */
public class SwingResxChooserTests extends AssertJSwingJUnitTestCase {

    private ResxChooser cut;

    private Consumer<ResxChooserEvent> listener;
    private FrameFixture frame;


    @Override
    protected void onSetUp() {
        FailOnThreadViolationRepaintManager.uninstall();

        listener = mock(Consumer.class);
        robot().settings().delayBetweenEvents(20);
    }

    @Before
    public void before() {
        cut = new SwingResxChooser();
        cut.addCompletionListener(listener);
        cut.setFileset(Paths.get(getClass().getResource("/RubberduckUI.resx").getFile()));
        cut.show();

        frame = findFrame(new GenericTypeMatcher<Frame>(Frame.class){
            @Override
            protected boolean isMatching(Frame component) {
                return component.isShowing();
            }
        }).using(robot());
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
