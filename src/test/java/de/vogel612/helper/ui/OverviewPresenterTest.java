package de.vogel612.helper.ui;

import static de.vogel612.helper.ui.OverviewPresenter.DEFAULT_ROOT_LOCALE;
import static de.vogel612.helper.ui.OverviewPresenter.DEFAULT_TARGET_LOCALE;

import static org.mockito.Mockito.*;

import de.vogel612.helper.ui.jfx.JFXDialog;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.junit.Before;
import org.junit.Test;
import de.vogel612.helper.data.FilesetOverviewModel;
import de.vogel612.helper.data.Translation;
import org.testfx.framework.junit.ApplicationTest;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@SuppressWarnings("ThrowableResultOfMethodCallIgnored")
public class OverviewPresenterTest extends ApplicationTest {

    private OverviewView overviewView;
    private FilesetOverviewModel filesetOverviewModel;
    private TranslationView translationView;
    private LocaleChooser localeChooser;
    private JFXDialog dialog;

    private OverviewPresenter cut;

    @Before
    public void beforeTest() {
        overviewView = mock(OverviewView.class);
        filesetOverviewModel = mock(FilesetOverviewModel.class);
        translationView = mock(TranslationView.class);
        localeChooser = mock(LocaleChooser.class);
        dialog = mock(JFXDialog.class);

        cut = new OverviewPresenter(filesetOverviewModel, overviewView, translationView, localeChooser);
        reset(overviewView, filesetOverviewModel, translationView, localeChooser, dialog);
    }


    @Test
    public void show_callsShow_onView() {
        cut.show();
        verify(overviewView).show();
        verifyNoMoreInteractions(filesetOverviewModel, overviewView, translationView, localeChooser, dialog);
    }

    @Test
    public void loadFromFile_delegatesToModel() {
        Path mock = mock(Path.class);

        cut.loadFiles(mock);
        try {
            verify(filesetOverviewModel).loadResxFileset(mock);
        } catch (IOException e) {
            // shouldn't ever actually happen
            throw new AssertionError("Error when loading all files in the model", e);
        }
        verifyNoMoreInteractions(filesetOverviewModel, overviewView, translationView, localeChooser, dialog);
    }

    @Test
    public void onException_delegatesToView() {
        final Exception e = mock(Exception.class);
        final String message = "testingmessage";
        final String errorMessage = "alsdkj";
        doReturn(errorMessage).when(e).getMessage();

        cut.onException(e, message);

        Platform.runLater(() -> verify(dialog).info(eq(message), eq(errorMessage)));
        verify(e).getMessage();
        sleep(60); // await verification
        verifyNoMoreInteractions(filesetOverviewModel, overviewView, translationView, localeChooser, dialog);
    }

    @Test
    public void onParseCompletion_rebuildsView() {
        List<Translation> rootList = mock(List.class);
        doReturn(rootList).when(filesetOverviewModel).getTranslations(DEFAULT_ROOT_LOCALE);
        List<Translation> targetList = mock(List.class);
        doReturn(targetList).when(filesetOverviewModel).getTranslations(DEFAULT_TARGET_LOCALE);

        cut.onParseCompletion();

        verify(filesetOverviewModel).getTranslations(DEFAULT_ROOT_LOCALE);
        verify(filesetOverviewModel).getTranslations(DEFAULT_TARGET_LOCALE);
        verify(overviewView).rebuildWith(rootList, targetList);
        verify(overviewView).show();
        verifyNoMoreInteractions(filesetOverviewModel, overviewView, translationView, localeChooser, dialog);
    }

    @Test
    public void onTranslateRequest_delegatesToTranslationPresenter() {
        final String key = "Key";
        Translation fakeRoot = mock(Translation.class);
        doReturn(fakeRoot).when(filesetOverviewModel).getSingleTranslation(DEFAULT_ROOT_LOCALE, key);
        Translation fakeTarget = mock(Translation.class);
        doReturn(fakeTarget).when(filesetOverviewModel).getSingleTranslation(DEFAULT_TARGET_LOCALE, key);

        cut.onTranslateRequest(key);

        verify(filesetOverviewModel).getSingleTranslation(DEFAULT_ROOT_LOCALE, key);
        verify(filesetOverviewModel).getSingleTranslation(DEFAULT_TARGET_LOCALE, key);
        verify(translationView).setRequestedTranslation(fakeRoot, fakeTarget);
        verify(translationView).show();
        verifyNoMoreInteractions(filesetOverviewModel, overviewView, translationView, localeChooser, dialog);
    }

    @Test
    public void onTranslationSubmit_hidesTranslationView_propagatesEdit_updatesView() {
        final Translation t = new Translation(DEFAULT_TARGET_LOCALE, "Key", "Translation");
        final List<Translation> list = mock(List.class);
        final List<Translation> leftSide = mock(List.class);
        doReturn(list).when(filesetOverviewModel).getTranslations(DEFAULT_TARGET_LOCALE);
        doReturn(leftSide).when(filesetOverviewModel).getTranslations(DEFAULT_ROOT_LOCALE);

        cut.onTranslationSubmit(t);

        verify(filesetOverviewModel).updateTranslation(DEFAULT_TARGET_LOCALE, "Key", "Translation");
        verify(filesetOverviewModel).getTranslations(DEFAULT_TARGET_LOCALE);
        verify(filesetOverviewModel).getTranslations(DEFAULT_ROOT_LOCALE);
        verify(overviewView).rebuildWith(leftSide, list);
        verify(overviewView).show();
        verify(translationView).hide();
        verifyNoMoreInteractions(filesetOverviewModel, overviewView, translationView, localeChooser, dialog);
    }

    @Test
    public void onTranslationAbort_hidesTranslationView() {
        cut.onTranslationAbort();

        verify(translationView).hide();
        verify(overviewView).show();
        verifyNoMoreInteractions(filesetOverviewModel, overviewView, translationView, localeChooser, dialog);
    }

    @Test
    public void onSaveRequest_delegatesToModel() {
        cut.onSaveRequest();

        try {
            verify(filesetOverviewModel).saveAll();
        } catch (IOException e) {
            // shouldn't ever actually happen
            throw new AssertionError("IOException when trying to save", e);
        }
        Platform.runLater(() -> verify(dialog).info(any(String.class), any(String.class)));
        sleep(60); // await verification..
        verifyNoMoreInteractions(filesetOverviewModel, overviewView, translationView, localeChooser, dialog);
    }

    @Override
    public void start(Stage stage) throws Exception {
        // no-op ... probably ...
    }
}
