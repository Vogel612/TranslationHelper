package de.vogel612.helper.ui;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import de.vogel612.helper.data.Translation;

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

    private OverviewModelImpl cut;
    private OverviewPresenter p;

    @Before
    public void setup() {
        cut = new OverviewModelImpl();
        p = mock(OverviewPresenter.class);
        cut.register(p);
    }

    @Test
    public void loadFromFile_normalizationWorksAsExpected() {
        Path testFolder;
        try {
            testFolder = Paths.get(
              getClass().getResource("RubberduckUI.resx").toURI()
            ).getParent();
        } catch (URISyntaxException e) {
            throw new AssertionError(
              "Testfile could not be found in resources", e);
        }

        cut.loadFromDirectory(testFolder);

        verify(p).onParseCompletion();
        verifyNoMoreInteractions(p);

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
        reset(p); // just to be sure

        cut.updateTranslation("", "TestKey1", "New Translation");

        Translation[] translations;
        translations = cut.getTranslations("").toArray(new Translation[0]);

        assertArrayEquals(expectedAfterEdit, translations);
    }

    @Test
    public void loadFromFile_andSuccessiveGet_returnCorrectInformation() {
        Path testFile;
        try {
            testFile = Paths.get(
              getClass().getResource("RubberduckUI.resx").toURI()
            ).getParent();
        } catch (URISyntaxException e) {
            throw new AssertionError(
              "Testfile could not be found in resources", e);
        }
        cut.loadFromDirectory(testFile);

        verify(p).onParseCompletion();
        verifyNoMoreInteractions(p);

        Translation[] translations;
        translations = cut.getTranslations("").toArray(new Translation[0]);
        assertArrayEquals(expected, translations);
    }

    @Test
    public void getSingleTranslation_returnsExpectedValues() {
        // abusing the loading test as setup
        loadFromFile_andSuccessiveGet_returnCorrectInformation();
        reset(p);
        Translation actual = cut.getSingleTranslation("", "TestKey2");

        assertEquals(expected[1], actual);
    }
}
