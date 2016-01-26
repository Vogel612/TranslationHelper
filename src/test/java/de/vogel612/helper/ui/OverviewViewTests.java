package de.vogel612.helper.ui;

import static org.assertj.swing.finder.WindowFinder.findFrame;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.assertj.swing.core.GenericTypeMatcher;
import org.assertj.swing.edt.FailOnThreadViolationRepaintManager;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Before;
import org.junit.Test;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.util.function.Consumer;

/**
 * Created by vogel612 on 26.01.16.
 */
public class OverviewViewTests extends AssertJSwingJUnitTestCase {

    private FrameFixture frame;

    private OverviewView cut;

    private Runnable saveReqListener;
    private Consumer<WindowEvent> windowClosingListener;
    private Consumer<String> translationReqListener;
    private Runnable langReqListener;


    @Override
    protected void onSetUp() {
        FailOnThreadViolationRepaintManager.uninstall(); // FUCK YOU!

        saveReqListener = mock(Runnable.class);
        windowClosingListener = mock(Consumer.class);
        translationReqListener = mock(Consumer.class);
        langReqListener = mock(Runnable.class);
    }

    @Before
    public void before() {
        cut = new SwingOverviewView();
        cut.initialize();
        cut.addSaveRequestListener(saveReqListener);
        cut.addWindowClosingListener(windowClosingListener);
        cut.addTranslationRequestListener(translationReqListener);
        cut.addLanguageRequestListener(langReqListener);
        cut.show();

        frame = findFrame(new GenericTypeMatcher<Frame>(Frame.class) {
            @Override
            protected boolean isMatching(Frame component) {
                return "Rubberduck Translation Helper".equals(component.getTitle()) && component.isShowing();
            }
        }).using(robot());
    }

    @Test
    public void saveButton_firesSaveRequestLister() {
        frame.button("save").click();

        frame.requireVisible();
        verify(saveReqListener).run();
        verifyNoMoreInteractions(saveReqListener, windowClosingListener, translationReqListener, langReqListener);
    }

    @Test
    public void languageRequestButton_firesLanguageRequestListener() {
        frame.button("chooseLang").click();

        frame.requireVisible();
        verify(langReqListener).run();
        verifyNoMoreInteractions(saveReqListener, windowClosingListener, translationReqListener, langReqListener);
    }

    @Test
    public void closingWindow_firesWindowClosingListener() {
        frame.close();

        verify(windowClosingListener).accept(any(WindowEvent.class));
        verifyNoMoreInteractions(saveReqListener, windowClosingListener, translationReqListener, langReqListener);
    }

    // FIXME need some translation table tests
}
