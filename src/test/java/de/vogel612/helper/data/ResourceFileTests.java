package de.vogel612.helper.data;

import org.junit.Before;
import org.junit.Test;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * Created by vogel612 on 26.07.16.
 */
public class ResourceFileTests {

    private static final Translation[] expected = {
            new Translation("", "TestKey1", "TestValue"),
            new Translation("", "TestKey2", "Another Test Value")
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
}
