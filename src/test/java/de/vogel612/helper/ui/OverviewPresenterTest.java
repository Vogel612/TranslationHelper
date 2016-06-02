package de.vogel612.helper.ui;

import static de.vogel612.helper.ui.OverviewPresenter.DEFAULT_ROOT_LOCALE;
import static de.vogel612.helper.ui.OverviewPresenter.DEFAULT_TARGET_LOCALE;

import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import de.vogel612.helper.data.OverviewModel;
import de.vogel612.helper.data.Translation;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@SuppressWarnings("ThrowableResultOfMethodCallIgnored")
public class OverviewPresenterTest {

    private OverviewView overviewView;
    private OverviewModel overviewModel;
    private TranslationView translationView;
    private ResxChooser resxChooser;
    private Dialog dialog;

    private OverviewPresenter cut;

    @Before
    public void beforeTest() {
        overviewView = mock(OverviewView.class);
        overviewModel = mock(OverviewModel.class);
        translationView = mock(TranslationView.class);
        resxChooser = mock(ResxChooser.class);
        dialog = mock(Dialog.class);

        cut = new OverviewPresenter(overviewModel, overviewView, translationView, resxChooser, dialog);
        reset(overviewView, overviewModel, translationView, resxChooser, dialog);
    }


    @Test
    public void show_callsShow_onView() {
        cut.show();
        verify(overviewView).show();
        verifyNoMoreInteractions(overviewModel, overviewView, translationView, resxChooser, dialog);
    }

    @Test
    public void loadFromFile_delegatesToModel() {
        Path mock = mock(Path.class);

        cut.loadFiles(mock);
        try {
            verify(overviewModel).loadResxFileset(mock);
        } catch (IOException e) {
            // shouldn't ever actually happen
            throw new AssertionError("Error when loading all files in the model", e);
        }
        verifyNoMoreInteractions(overviewModel, overviewView, translationView, resxChooser, dialog);
    }

    @Test
    public void onException_delegatesToView() {
        final Exception e = mock(Exception.class);
        final String message = "testingmessage";
        final String errorMessage = "alsdkj";
        doReturn(errorMessage).when(e).getMessage();

        cut.onException(e, message);

        verify(dialog).info(message, errorMessage);
        verify(e).getMessage();
        verifyNoMoreInteractions(overviewModel, overviewView, translationView, resxChooser, dialog);
    }

    @Test
    public void onParseCompletion_rebuildsView() {
        List<Translation> rootList = mock(List.class);
        doReturn(rootList).when(overviewModel).getTranslations(DEFAULT_ROOT_LOCALE);
        List<Translation> targetList = mock(List.class);
        doReturn(targetList).when(overviewModel).getTranslations(DEFAULT_TARGET_LOCALE);

        cut.onParseCompletion();

        verify(overviewModel).getTranslations(DEFAULT_ROOT_LOCALE);
        verify(overviewModel).getTranslations(DEFAULT_TARGET_LOCALE);
        verify(overviewView).rebuildWith(rootList, targetList);
        verify(overviewView).show();
        verifyNoMoreInteractions(overviewModel, overviewView, translationView, resxChooser, dialog);
    }

    @Test
    public void onTranslateRequest_delegatesToTranslationPresenter() {
        final String key = "Key";
        Translation fakeRoot = mock(Translation.class);
        doReturn(fakeRoot).when(overviewModel).getSingleTranslation(DEFAULT_ROOT_LOCALE, key);
        Translation fakeTarget = mock(Translation.class);
        doReturn(fakeTarget).when(overviewModel).getSingleTranslation(DEFAULT_TARGET_LOCALE, key);

        cut.onTranslateRequest(key);

        verify(overviewModel).getSingleTranslation(DEFAULT_ROOT_LOCALE, key);
        verify(overviewModel).getSingleTranslation(DEFAULT_TARGET_LOCALE, key);
        verify(translationView).setRequestedTranslation(fakeRoot, fakeTarget);
        verify(translationView).show();
        verifyNoMoreInteractions(overviewModel, overviewView, translationView, resxChooser, dialog);
    }

    @Test
    public void onTranslationSubmit_hidesTranslationView_propagatesEdit_updatesView() {
        final Translation t = new Translation(DEFAULT_TARGET_LOCALE, "Key", "Translation");
        final List<Translation> list = mock(List.class);
        final List<Translation> leftSide = mock(List.class);
        doReturn(list).when(overviewModel).getTranslations(DEFAULT_TARGET_LOCALE);
        doReturn(leftSide).when(overviewModel).getTranslations(DEFAULT_ROOT_LOCALE);

        cut.onTranslationSubmit(t);

        verify(overviewModel).updateTranslation(DEFAULT_TARGET_LOCALE, "Key", "Translation");
        verify(overviewModel).getTranslations(DEFAULT_TARGET_LOCALE);
        verify(overviewModel).getTranslations(DEFAULT_ROOT_LOCALE);
        verify(overviewView).rebuildWith(leftSide, list);
        verify(overviewView).show();
        verify(translationView).hide();
        verifyNoMoreInteractions(overviewModel, overviewView, translationView, resxChooser, dialog);
    }

    @Test
    public void onTranslationAbort_hidesTranslationView() {
        cut.onTranslationAbort();

        verify(translationView).hide();
        verify(overviewView).show();
        verifyNoMoreInteractions(overviewModel, overviewView, translationView, resxChooser, dialog);
    }

    @Test
    public void onSaveRequest_delegatesToModel() {
        cut.onSaveRequest();

        try {
            verify(overviewModel).saveAll();
        } catch (IOException e) {
            // shouldn't ever actually happen
            throw new AssertionError("IOException when trying to save", e);
        }
        verify(dialog).info(any(String.class), any(String.class));
        verifyNoMoreInteractions(overviewModel, overviewView, translationView, resxChooser, dialog);
    }
}
