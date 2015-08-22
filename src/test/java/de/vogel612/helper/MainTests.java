package de.vogel612.helper;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.io.PrintStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MainTests {

	private PrintStream outMock;

	@Before
	public void setup() {
		outMock = mock(PrintStream.class);
		System.setOut(outMock);
		reset(outMock);
	}

	@Test
	public void main_rejectsZeroArgCall() {
		Main.main(new String[0]);

		verify(outMock).println(Main.ARGUMENT_MISMATCH);
		verifyNoMoreInteractions(outMock);
	}

	@Test
	public void main_rejectsTwoArgumentCall() {
		Main.main(new String[]{
			"/random/test/path", "de"
		});

		verify(outMock).println(Main.ARGUMENT_MISMATCH);
		verifyNoMoreInteractions(outMock);
	}

	@Test
	public void main_rejectsNonRubberduckPaths() {
		Main.main(new String[]{
			"random/test/path"
		});

		verify(outMock).println(Main.ILLEGAL_FOLDER);
		verifyNoMoreInteractions(outMock);
	}

	@After
	public void tearDown() {
		System.setOut(null);
	}

}
