package de.vogel612.helper;

import static org.mockito.Mockito.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.PrintStream;

public class MainTests {

    private PrintStream outMock;

    @Before
    public void setup() {
        outMock = mock(PrintStream.class);
        System.setOut(outMock);
        reset(outMock);
    }

    @Test
    public void main_rejectsTwoArgumentCall() {
        Main.main(new String[]{
          "/random/test/path", "de"
        });

        verify(outMock).println(Main.ARGUMENT_MISMATCH);
        verifyNoMoreInteractions(outMock);
    }

    @After
    public void tearDown() {
        System.setOut(null);
    }

}
