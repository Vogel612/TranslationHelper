package de.vogel612.helper.ui;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import de.vogel612.helper.data.Translation;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class OverviewModelTest {

    private static final Translation[] expected = {
      new Translation("", "TestKey1", "TestValue"),
      new Translation("", "TestKey2", "Another Test Value")
    };
    private static final Translation[] expected2 = {
      new Translation("", "TestKey1", "Second Test"),
      new Translation("", "TestKey2", "Another Test Value")
    };
    private static final Translation[] expectedAfterEdit = {
      new Translation("", "TestKey1", "New Translation"),
      new Translation("", "TestKey2", "Another Test Value")
    };
    private static final Translation[] expectedNormalized = {
      new Translation("test", "TestKey1", "Second Test"),
      new Translation("test", "TestKey2", "")
    };

    private OverviewModel cut;
    private Runnable parseCallback;

    @Before
    public void setup() {
        cut = new OverviewModel();
        parseCallback = mock(Runnable.class);
        cut.addParseCompletionListener(parseCallback);
    }

    @Test
    public void loadFromFile_normalizationWorksAsExpected() {
        Path testFile;
        try {
            testFile = Paths.get(
              getClass().getResource("RubberduckUI.resx").toURI()
            );
        } catch (URISyntaxException e) {
            throw new AssertionError(
              "Testfile could not be found in resources", e);
        }

        try {
            cut.loadResxFileset(testFile);
        } catch (IOException e) {
            throw new AssertionError("Failed to load files from directory", e);
        }
        verify(parseCallback).run();
        verifyNoMoreInteractions(parseCallback);

        Translation[] rootTranslations;
        rootTranslations = cut.getTranslations("").toArray(new Translation[0]);
        assertArrayEquals(expected, rootTranslations);

        Translation[] normalizedTranslations;
        normalizedTranslations  = cut.getTranslations("ts").toArray(new Translation[0]);
        assertArrayEquals(expectedNormalized, normalizedTranslations);
    }

    @Test
    public void editTranslation_updatesDocument() {
        // abusing the loading test as a setup...
        loadFromFile_andSuccessiveGet_returnCorrectInformation();
        reset(parseCallback);

        cut.updateTranslation("", "TestKey1", "New Translation");

        Translation[] translations;
        translations = cut.getTranslations("").toArray(new Translation[0]);

        verifyNoMoreInteractions(parseCallback);
        assertArrayEquals(expectedAfterEdit, translations);
    }

    @Test
    public void loadFromFile_andSuccessiveGet_returnCorrectInformation() {
        Path testFile;
        try {
            testFile = Paths.get(
              getClass().getResource("RubberduckUI.resx").toURI()
            );
        } catch (URISyntaxException e) {
            throw new AssertionError(
              "Testfile could not be found in resources", e);
        }
        try {
            cut.loadResxFileset(testFile);
        } catch (IOException e) {
            throw new AssertionError("Failed to load files from directory", e);
        }
        verify(parseCallback).run();
        verifyNoMoreInteractions(parseCallback);

        Translation[] translations;
        translations = cut.getTranslations("").toArray(new Translation[0]);
        assertArrayEquals(expected, translations);
    }

    @Test
    public void getSingleTranslation_returnsExpectedValues() {
        // abusing the loading test as setup
        loadFromFile_andSuccessiveGet_returnCorrectInformation();
        reset(parseCallback);
        Translation actual = cut.getSingleTranslation("", "TestKey2");

        assertEquals(expected[1], actual);
        verifyNoMoreInteractions(parseCallback);
    }

    @Test
    public void isNotSaved_isFalse_afterSaving() {
        // abusing the loading test as setup
        loadFromFile_andSuccessiveGet_returnCorrectInformation();
        reset(parseCallback);

        verifyNoMoreInteractions(parseCallback);
        assertTrue(cut.isNotSaved());
        try {
            cut.saveAll();
        } catch (IOException e) {
            throw new AssertionError("Error during saving, failing Unit-Test", e);
        }
        assertFalse(cut.isNotSaved());
        verifyNoMoreInteractions(parseCallback);
    }
}
