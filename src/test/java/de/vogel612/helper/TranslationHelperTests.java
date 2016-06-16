package de.vogel612.helper;

import static org.mockito.Mockito.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.PrintStream;

public class TranslationHelperTests {

    private PrintStream outMock;

    @Before
    public void setup() {
        outMock = mock(PrintStream.class);
        System.setOut(outMock);
        reset(outMock);
    }

    @Test
    public void main_rejectsTwoArgumentCall() {
        TranslationHelper.main(new String[]{
          "/random/test/path", "de"
        });

        verify(outMock).println(TranslationHelper.ARGUMENT_MISMATCH);
        verifyNoMoreInteractions(outMock);
    }

    @After
    public void tearDown() {
        System.setOut(null);
    }

}
