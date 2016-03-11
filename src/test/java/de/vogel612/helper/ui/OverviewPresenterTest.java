package de.vogel612.helper.ui;

import static de.vogel612.helper.ui.OverviewPresenter.DEFAULT_ROOT_LOCALE;
import static de.vogel612.helper.ui.OverviewPresenter.DEFAULT_TARGET_LOCALE;

import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import de.vogel612.helper.data.OverviewModel;
import de.vogel612.helper.data.Translation;
import de.vogel612.helper.ui.common.OverviewViewCommon;
import de.vogel612.helper.ui.ResxChooser;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@SuppressWarnings("ThrowableResultOfMethodCallIgnored")
public class OverviewPresenterTest {

    private OverviewView v;
    private OverviewModel m;
    private TranslationView p;
    private ResxChooser rc;

    private OverviewPresenter cut;

    @Before
    public void beforeTest() {
        v = mock(OverviewViewCommon.class);
        m = mock(OverviewModel.class);
        p = mock(TranslationView.class);
        rc = mock(ResxChooser.class);

        cut = new OverviewPresenter(m, v, p, rc);
        reset(v, m, p, rc);
    }


    @Test
    public void show_callsShow_onView() {
        cut.show();
        verify(v).show();
        verifyNoMoreInteractions(m, v, p, rc);
    }

    @Test
    public void loadFromFile_delegatesToModel() {
        Path mock = mock(Path.class);

        cut.loadFiles(mock);
        try {
            verify(m).loadResxFileset(mock);
        } catch (IOException e) {
            // shouldn't ever actually happen
            throw new AssertionError("Error when loading all files in the model", e);
        }
        verifyNoMoreInteractions(m, v, p, rc);
    }

    @Test
    public void onException_delegatesToView() {
        final Exception e = mock(Exception.class);
        final String message = "testingmessage";
        final String errorMessage = "alsdkj";
        doReturn(errorMessage).when(e).getMessage();

        cut.onException(e, message);

        verify(v).displayError(message, errorMessage);
        verify(e).getMessage();
        verifyNoMoreInteractions(m, v, p, rc);
    }

    @Test
    @Ignore("dependencies aren't wired in presenter anymore")
    public void onParseCompletion_rebuildsView() {
        List<Translation> rootList = mock(List.class);
        doReturn(rootList).when(m).getTranslations(DEFAULT_ROOT_LOCALE);
        List<Translation> targetList = mock(List.class);
        doReturn(targetList).when(m).getTranslations(DEFAULT_TARGET_LOCALE);

        cut.onParseCompletion();

        verify(m).getTranslations(DEFAULT_ROOT_LOCALE);
        verify(m).getTranslations(DEFAULT_TARGET_LOCALE);
        verify(v).rebuildWith(rootList, targetList);
        verifyNoMoreInteractions(m, v, p, rc);
    }

    @Test
    public void onTranslateRequest_delegatesToTranslationPresenter() {
        final String key = "Key";
        Translation fakeRoot = mock(Translation.class);
        doReturn(fakeRoot).when(m).getSingleTranslation(DEFAULT_ROOT_LOCALE, key);
        Translation fakeTarget = mock(Translation.class);
        doReturn(fakeTarget).when(m).getSingleTranslation(DEFAULT_TARGET_LOCALE, key);

        cut.onTranslateRequest(key);

        verify(m).getSingleTranslation(DEFAULT_ROOT_LOCALE, key);
        verify(m).getSingleTranslation(DEFAULT_TARGET_LOCALE, key);
        verify(p).setRequestedTranslation(fakeRoot, fakeTarget);
        verify(p).show();
        verifyNoMoreInteractions(m, v, p, rc);
    }

    @Test
    public void onTranslationSubmit_hidesTranslationView_propagatesEdit_updatesView() {
        final Translation t = new Translation(DEFAULT_TARGET_LOCALE, "Key", "Translation");
        final List<Translation> list = mock(List.class);
        final List<Translation> leftSide = mock(List.class);
        doReturn(list).when(m).getTranslations(DEFAULT_TARGET_LOCALE);
        doReturn(leftSide).when(m).getTranslations(DEFAULT_ROOT_LOCALE);

        cut.onTranslationSubmit(t);

        verify(m).updateTranslation(DEFAULT_TARGET_LOCALE, "Key", "Translation");
        verify(m).getTranslations(DEFAULT_TARGET_LOCALE);
        verify(m).getTranslations(DEFAULT_ROOT_LOCALE);
        verify(v).rebuildWith(leftSide, list);
        verify(v).show();
        verify(p).hide();
        verifyNoMoreInteractions(m, v, p, rc);
    }

    @Test
    public void onTranslationAbort_hidesTranslationView() {
        cut.onTranslationAbort();

        verify(p).hide();
        verifyNoMoreInteractions(m, v, p, rc);
    }

    @Test
    public void onSaveRequest_delegatesToModel() {
        cut.onSaveRequest();

        try {
            verify(m).saveAll();
        } catch (IOException e) {
            // shouldn't ever actually happen
            throw new AssertionError("IOException when trying to save", e);
        }
        verifyNoMoreInteractions(m, v, p, rc);
    }
}
