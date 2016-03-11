package de.vogel612.helper.data.util;

import static org.junit.Assert.*;

import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class DataUtilitiesTest {

    @Test(expected = IllegalArgumentException.class)
    public void parseFileName_illegalFile_isRejected() {
        DataUtilities.parseLocale(Paths.get("/", "some", "random", "file.foo"));
    }

    @Test
    public void parseFileName_langLocale_parsesProperly() {
        String result = DataUtilities.parseLocale(Paths.get("/", "some", "random", "set.LN-lc.resx"));
        assertEquals("LN-lc", result);
    }

    @Test
    public void parseFileName_languageOnly_parsesProperly() {
        String result = DataUtilities.parseLocale(Paths.get("/", "some", "randome", "set.LN.resx"));
        assertEquals("LN", result);
    }

    @Test
    public void parseFileName_noLanguageOrLocale_returnsSingleTruth() {
        String result = DataUtilities.parseLocale(Paths.get("/", "set.resx"));
        assertSame(DataUtilities.SINGLE_TRUTH_LOCALE, result);
    }

    @Test
    public void fileNameString_withLocale() {
        String result = DataUtilities.fileNameString("set", "LOCALE");
        assertEquals("set.LOCALE.resx", result);
    }

    @Test
    public void fileNameString_withoutLocale() {
        String result = DataUtilities.fileNameString("set", "");
        assertEquals("set.resx", result);
    }

    @Test
    public void filesetStream() throws URISyntaxException, IOException {
        Set<String> actual = DataUtilities.streamFileset(
          Paths.get(getClass().getResource("/RubberduckUI.resx").toURI()).getParent(),
          "RubberduckUI").map(
          Path::getFileName).map(Path::toString).collect(Collectors.toSet());
        assertTrue(actual.containsAll(Arrays.asList("RubberduckUI.resx", "RubberduckUI.ts.resx")));
    }
}
