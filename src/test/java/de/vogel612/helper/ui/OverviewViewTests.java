package de.vogel612.helper.ui;

import static junit.framework.Assert.assertEquals;
import static org.assertj.swing.finder.WindowFinder.findFrame;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

import org.assertj.swing.core.GenericTypeMatcher;
import org.assertj.swing.core.KeyPressInfo;
import org.assertj.swing.data.TableCell;
import org.assertj.swing.edt.FailOnThreadViolationRepaintManager;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Before;
import org.junit.Test;
import de.vogel612.helper.data.Translation;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.function.Consumer;

/**
 * Created by vogel612 on 26.01.16.
 */
public class OverviewViewTests extends AssertJSwingJUnitTestCase {

    private static final Translation[] dataLeft = new Translation[]{
      new Translation("", "key", "value"), new Translation("", "another_key", "some better value")
    };
    private static final Translation[] dataRight = new Translation[]{
      new Translation("ts", "key", "something"), new Translation("ts", "another_key", "awesome value")
    };
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

        robot().settings().delayBetweenEvents(20);
    }

    @Before
    public void before() {
        cut = new SwingOverviewView();
        cut.initialize();
        cut.addSaveRequestListener(saveReqListener);
        cut.addWindowClosingListener(windowClosingListener);
        cut.addTranslationRequestListener(translationReqListener);
        cut.addLanguageRequestListener(langReqListener);

        cut.rebuildWith(Arrays.asList(dataLeft), Arrays.asList(dataRight));
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

    @Test
    public void table_containsCorrectRows() {
        assertEquals(dataLeft.length, frame.table().rowCount());

        verifyNoMoreInteractions(saveReqListener, windowClosingListener, translationReqListener, langReqListener);
    }

    @Test
    public void tableClick_doesNothing() {
        frame.table().cell(TableCell.row(0).column(1)).click();

        verifyNoMoreInteractions(saveReqListener, windowClosingListener, translationReqListener, langReqListener);
    }

    @Test
    public void tableDoubleClick_firesTranslationReqListener() {
        frame.table().cell(TableCell.row(0).column(1)).doubleClick();

        verify(translationReqListener).accept(eq("key"));
        verifyNoMoreInteractions(saveReqListener, windowClosingListener, translationReqListener, langReqListener);
    }

    @Test
    public void keyBasedTranslation_firesTranslationReqListener() {
        frame.table().cell(TableCell.row(0).column(1)).click();
        frame.table().pressAndReleaseKey(KeyPressInfo.keyCode(KeyEvent.VK_ENTER));

        verify(translationReqListener).accept(eq("key"));
        verifyNoMoreInteractions(saveReqListener, windowClosingListener, translationReqListener, langReqListener);
    }
}
