package de.vogel612.helper.ui.swing;

import static org.assertj.swing.finder.WindowFinder.findFrame;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.testfx.util.WaitForAsyncUtils.sleep;

import org.assertj.swing.core.GenericTypeMatcher;
import org.assertj.swing.edt.FailOnThreadViolationRepaintManager;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Before;
import org.junit.Test;
import de.vogel612.helper.data.Translation;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Created by vogel612 on 26.01.16.
 */
public class SwingTranslationViewTests extends AssertJSwingJUnitTestCase {

    private SwingTranslationView cut;

    private FrameFixture frame;
    private Runnable abortListener;
    private Consumer<Translation> submitListener;

    @Override
    protected void onSetUp() {
        //GTFO from my testing code!
        FailOnThreadViolationRepaintManager.uninstall();

        abortListener = mock(Runnable.class);
        submitListener = mock(Consumer.class);

        robot().settings().delayBetweenEvents(20);
    }

    @Before
    public void before() {
        cut = new SwingTranslationView();
        cut.addTranslationAbortListener(abortListener);
        cut.addTranslationSubmitListener(submitListener);
        cut.setRequestedTranslation(new Translation("", "", ""), new Translation("", "", ""));
        cut.show();

        frame = findFrame(new GenericTypeMatcher<Frame>(Frame.class) {
            @Override
            protected boolean isMatching(Frame component) {
                return component.isShowing();
            }
        }).using(robot());
    }

    @Test
    public void abortButton_firesAbortListener() {
        frame.button("abort").click();

        sleep(500, TimeUnit.MILLISECONDS);
        frame.requireVisible();
        verify(abortListener).run();
        verifyNoMoreInteractions(abortListener, submitListener);
    }

    @Test
    public void settingTranslation_fillsRelevantUI() {
        cut.setRequestedTranslation(new Translation("src", "key", "original"),
          new Translation("target", "key", "current"));

        frame.textBox("input").requireText("current");
        frame.textBox("input").requireEditable();
        frame.textBox("rootValue").requireText("original");
        frame.textBox("rootValue").requireNotEditable();
        // FIXME verify title
        verifyNoMoreInteractions(abortListener, submitListener);
    }

    @Test
    public void submitButton_firesSubmitListener() {
        frame.button("submit").click();

        sleep(500, TimeUnit.MILLISECONDS);
        frame.requireVisible();
        verify(submitListener).accept(any());
        verifyNoMoreInteractions(abortListener, submitListener);
    }

    @Test
    public void changingTextInput_changesTranslationValue() {
        frame.textBox("input").pressAndReleaseKeys(KeyEvent.VK_T, KeyEvent.VK_E, KeyEvent.VK_S, KeyEvent.VK_T);
        frame.button("submit").click();

        frame.requireVisible();
        frame.textBox("input").requireEditable();
        frame.textBox("rootValue").requireNotEditable();
        verify(submitListener).accept(eq(new Translation("", "", "test")));
        verifyNoMoreInteractions(submitListener, abortListener);
    }

    @Test
    public void pressingEnter_inTextField_firesSubmitListener() {
        robot().settings().delayBetweenEvents(20);
        frame.textBox("input").pressAndReleaseKeys(KeyEvent.VK_T,
          KeyEvent.VK_E,
          KeyEvent.VK_S,
          KeyEvent.VK_T,
          KeyEvent.VK_ENTER);

        frame.requireVisible();
        frame.textBox("input").requireEditable();
        frame.textBox("rootValue").requireNotEditable();
        verify(submitListener).accept(eq(new Translation("", "", "test")));
        verifyNoMoreInteractions(submitListener, abortListener);
    }

    @Test
    public void pressingEsc_firesAbortListener() {
        frame.textBox("input").pressAndReleaseKeys(KeyEvent.VK_ESCAPE);

        frame.requireVisible();
        verify(abortListener).run();
        verifyNoMoreInteractions(abortListener, submitListener);
    }
}
