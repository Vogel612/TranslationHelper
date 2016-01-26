package de.vogel612.helper.data.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.junit.Test;

import java.nio.file.Paths;

public class DataUtilitiesTest {

    @Test(expected = IllegalArgumentException.class)
    public void parseFileName_illegalFile_isRejected() {
        DataUtilities.parseFileName(Paths.get("/", "some", "random", "file.foo"));
    }

    @Test
    public void parseFileName_langLocale_parsesProperly() {
        String result = DataUtilities.parseFileName(Paths.get("/", "some", "random", "set.LN-lc.resx"));
        assertEquals("LN-lc", result);
    }

    @Test
    public void parseFileName_languageOnly_parsesProperly() {
        String result = DataUtilities.parseFileName(Paths.get("/", "some", "randome", "set.LN.resx"));
        assertEquals("LN", result);
    }

    @Test
    public void parseFileName_noLanguageOrLocale_returnsSingleTruth() {
        String result = DataUtilities.parseFileName(Paths.get("/", "set.resx"));
        assertSame(DataUtilities.SINGLE_TRUTH_LOCALE, result);
    }

    @Test
    public void fileNameString_withLocale() {
        String result = DataUtilities.fileNameString("set", "LOCALE");
        assertEquals("set.LOCALE.resx", result);
    }

    @Test
    public void fileNameString_withoutLocale() {
        String result = DataUtilities.fileNameString("set","");
        assertEquals("set.resx", result);
    }
}
