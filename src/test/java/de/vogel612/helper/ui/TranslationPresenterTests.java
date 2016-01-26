package de.vogel612.helper.ui;

import static org.assertj.swing.finder.WindowFinder.findFrame;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.assertj.swing.core.GenericTypeMatcher;
import org.assertj.swing.edt.FailOnThreadViolationRepaintManager;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Before;
import org.junit.Test;
import de.vogel612.helper.data.Translation;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.function.Consumer;

/**
 * Created by vogel612 on 26.01.16.
 */
public class TranslationPresenterTests extends AssertJSwingJUnitTestCase {

    private TranslationPresenter cut;

    private FrameFixture frame;
    private Runnable abortListener;
    private Consumer<Translation> submitListener;

    @Override
    protected void onSetUp() {
        //GTFO from my testing code!
        FailOnThreadViolationRepaintManager.uninstall();

        abortListener = mock(Runnable.class);
        submitListener = mock(Consumer.class);
    }

    @Before
    public void before() {
        cut = new TranslationPresenter();
        cut.addTranslationAbortListener(abortListener);
        cut.addTranslationSubmitListener(submitListener);
        cut.setRequestedTranslation(new Translation("","",""), new Translation("","",""));
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

        frame.requireVisible();
        verify(abortListener).run();
        verifyNoMoreInteractions(abortListener, submitListener);
    }

    @Test
    public void submitButton_firesSubmitListener() {
        frame.button("submit").click();

        frame.requireVisible();
        verify(submitListener).accept(any());
        verifyNoMoreInteractions(abortListener, submitListener);
    }

    @Test
    public void changingTextInput_changesTranslationValue() {
        frame.textBox().pressAndReleaseKeys(KeyEvent.VK_T, KeyEvent.VK_E, KeyEvent.VK_S, KeyEvent.VK_T);
        frame.button("submit").click();

        frame.requireVisible();
        frame.textBox().requireEditable();
        verify(submitListener).accept(eq(new Translation("", "", "test")));
        verifyNoMoreInteractions(submitListener, abortListener);
    }

    @Test
    public void pressingEnter_inTextField_firesSubmitListener() {
        robot().settings().delayBetweenEvents(20);
        frame.textBox().pressAndReleaseKeys(KeyEvent.VK_T,
          KeyEvent.VK_E,
          KeyEvent.VK_S,
          KeyEvent.VK_T,
          KeyEvent.VK_ENTER);

        frame.requireVisible();
        frame.textBox().requireEditable();
        verify(submitListener).accept(eq(new Translation("", "", "test")));
        verifyNoMoreInteractions(submitListener, abortListener);
    }

    @Test
    public void pressingEsc_firesAbortListener() {
        frame.textBox().pressAndReleaseKeys(KeyEvent.VK_ESCAPE);

        frame.requireVisible();
        verify(abortListener).run();
        verifyNoMoreInteractions(abortListener, submitListener);
    }
}
