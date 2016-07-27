package de.vogel612.helper.data;

import org.junit.Before;
import org.junit.Test;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Created by vogel612 on 26.07.16.
 */
public class ResourceFileTests {

    private static final Translation[] expected = {
            new Translation("", "TestKey1", "TestValue"),
            new Translation("", "TestKey2", "Another Test Value")
    };

    private static final Translation[] normalized = {
            new Translation("ts", "TestKey1", "Second Test"),
            new Translation("ts", "TestKey3", "Normalized")
    };

    private ResourceFile cut;
    private final Path canonicalPath;
    private final Path testPath;

    public ResourceFileTests() throws URISyntaxException {
        canonicalPath = Paths.get(getClass().getResource("/RubberduckUI.resx").toURI());
        testPath = Paths.get(getClass().getResource("/RubberduckUI.ts.resx").toURI());
    }

    @Before
    public void setup() throws URISyntaxException {
        cut = new ResourceFile(canonicalPath);
    }

    @Test
    public void localeIsSetCorrectly() {
        assertEquals("", cut.getLocale());
    }

    @Test
    public void nameIsSetCorrectly() {
        assertEquals("RubberduckUI", cut.getName());
    }

    @Test
    public void orderedTranslationsWorks() {
        assertArrayEquals(expected, cut.orderedTranslations().toArray(new Translation[0]));
    }

    @Test
    public void folderPointsToParent() {
        assertEquals(canonicalPath.getParent(), cut.getFolder());
    }

    @Test
    public void extractKeysContainsAllKeys() {
        assertEquals(new HashSet<>(Arrays.asList("TestKey1", "TestKey2")), cut.getKeys());
    }

    @Test
    public void translationsAreReturnedCorrectly() {
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i].getValue(), cut.getTranslation(expected[i].getKey()));
        }
    }

    @Test
    public void normalization() {
        ResourceFile test = new ResourceFile(testPath);
        ResourceFile canonicalMock = mock(ResourceFile.class);
        when(canonicalMock.getTranslation("TestKey3")).thenReturn("Normalized");
        Set<String> canonicalKeys = new HashSet<>(Arrays.asList("TestKey1", "TestKey3"));
        test.normalize(canonicalKeys, canonicalMock);

        verify(canonicalMock).getTranslation("TestKey3");
        assertArrayEquals(normalized, test.orderedTranslations().toArray(new Translation[0]));
    }
    // test normalization
}
