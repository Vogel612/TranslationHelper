package de.vogel612.helper.ui.javafx;

import static de.vogel612.helper.data.util.DataUtilities.FALLBACK_LOCALE;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.hasText;

import com.google.common.base.Predicate;
import de.vogel612.helper.data.FilesetModel;
import de.vogel612.helper.ui.LocaleChooser;
import de.vogel612.helper.ui.TranslationView;
import de.vogel612.helper.ui.jfx.JFXDialog;
import org.junit.Before;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;
import de.vogel612.helper.data.Translation;
import de.vogel612.helper.ui.jfx.JFXTranslationOverviewView;
import de.vogel612.helper.ui.jfx.TranslationPair;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

public class JavaFXOverviewViewTests extends ApplicationTest {

    private JFXTranslationOverviewView cut;

    private JFXDialog dialog;
    private FilesetModel filesetModel;
    private LocaleChooser localeChooser;
    private TranslationView translationView;

    @Override
    public void start (Stage stage) throws Exception {
        dialog = mock(JFXDialog.class);
        filesetModel = mock(FilesetModel.class);
        localeChooser = mock(LocaleChooser.class);
        translationView = mock(TranslationView.class);

        cut = new JFXTranslationOverviewView(localeChooser, filesetModel, translationView, stage, getClass().getResource("/TranslationOverviewView.fxml"));
        cut.show();
        reset(dialog, filesetModel, localeChooser, translationView);
    }

    @Before
    public void before() {
        cut.rebuildWith(Collections.emptyList(), Collections.emptyList());
    }

    @Test
    public void hasAllUIElements() {
        sleep(700, TimeUnit.MILLISECONDS);

        verifyThat("#save", hasText("Save"));
        verifyThat("#chooseLang", hasText("Choose Languages"));
        verifyThat("#table", (Predicate<TableView<TranslationPair>>)tv -> tv.getItems().isEmpty());
    }

    @Test
    public void tableContainsItemsFromBuilding() {
        final List<Translation> left = Arrays.asList(new Translation("","key","value"), new Translation("", "key2", "value2"));
        final List<Translation> right = Arrays.asList(new Translation("de", "key", "wert"), new Translation("", "key2", "wert2"));

        Platform.runLater(() -> cut.rebuildWith(left, right));
        sleep(200, TimeUnit.MILLISECONDS);

        TableView<TranslationPair> table = lookup("#table").queryFirst();
        ObservableList<TranslationPair> items = table.getItems();

        assertEquals("key", items.get(0).getLeft().getKey());
        assertEquals("value", items.get(0).getLeft().getValue());
        assertEquals("wert", items.get(0).getRight().getValue());

        assertEquals("key2", items.get(1).getLeft().getKey());
        assertEquals("value2", items.get(1).getLeft().getValue());
        assertEquals("wert2", items.get(1).getRight().getValue());
    }

    @Test
    public void onParseCompletion_rebuildsView() {
        List<Translation> rootList = Collections.singletonList(new Translation("", "", ""));
        doReturn(rootList).when(filesetModel).getTranslations(FALLBACK_LOCALE);

        cut.onParseCompletion();

        verify(filesetModel, times(2)).getTranslations(FALLBACK_LOCALE);
        verifyNoMoreInteractions(filesetModel, translationView, localeChooser, dialog);
    }

    @Test
    public void loadFromFile_delegatesToModel() {
        Path mock = mock(Path.class);

        cut.loadFiles(mock);
        try {
            verify(filesetModel).loadResxFileset(mock);
        } catch (IOException e) {
            // shouldn't ever actually happen
            throw new AssertionError("Error when loading all files in the model", e);
        }
        verifyNoMoreInteractions(filesetModel, translationView, localeChooser, dialog);
    }

    @Test
    public void onTranslateRequest_delegatesToTranslationPresenter() {
        final String key = "Key";
        Translation fakeRoot = mock(Translation.class);
        doReturn(fakeRoot).when(filesetModel).getSingleTranslation(FALLBACK_LOCALE, key);

        cut.onTranslateRequest(key);

        verify(filesetModel, times(2)).getSingleTranslation(FALLBACK_LOCALE, key);
        verify(translationView).setRequestedTranslation(fakeRoot, fakeRoot);
        verify(translationView).show();
        verifyNoMoreInteractions(filesetModel, translationView, localeChooser, dialog);
    }

    @Test
    public void onTranslationSubmit_hidesTranslationView_propagatesEdit_updatesView() {
        final Translation t = new Translation("de", "Key", "Translation");
        final List<Translation> list = Collections.singletonList(new Translation("", "", ""));
        final List<Translation> leftSide = Collections.singletonList(new Translation("", "", ""));
        doReturn(list).when(filesetModel).getTranslations("de");
        doReturn(leftSide).when(filesetModel).getTranslations(FALLBACK_LOCALE);

        cut.onTranslationSubmit(t);

        verify(filesetModel).updateTranslation("de", "Key", "Translation");
        verify(filesetModel, times(2)).getTranslations(FALLBACK_LOCALE);
        verify(translationView).hide();
        verifyNoMoreInteractions(filesetModel, translationView, localeChooser, dialog);
    }

    @Test
    public void onTranslationAbort_hidesTranslationView() {
        cut.onTranslationAbort();

        verify(translationView).hide();
        verifyNoMoreInteractions(filesetModel, translationView, localeChooser, dialog);
    }

    @Test
    public void onSaveRequest_delegatesToModel() {
        doReturn(true).when(filesetModel).isDirty();

        cut.onSaveRequest();

        try {
            verify(filesetModel).saveAll();
        } catch (IOException e) {
            // shouldn't ever actually happen
            throw new AssertionError("IOException when trying to save", e);
        }
        Platform.runLater(() -> verify(dialog).info(any(String.class), any(String.class)));
        sleep(60); // await verification..
        verifyNoMoreInteractions(filesetModel, translationView, localeChooser, dialog);
    }

    // FIXME implement and verify row-highlighting!
}
