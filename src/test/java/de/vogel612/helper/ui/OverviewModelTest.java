package de.vogel612.helper.ui;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CountDownLatch;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import de.vogel612.helper.data.Translation;
import de.vogel612.helper.ui.impl.OverviewModelImpl;

public class OverviewModelTest {

	private static final Translation[] expected = {
		new Translation("TestKey1", "TestValue", "TestValue"),
		new Translation("TestKey2", "Another Test Value", "Another Test Value")
	};
	private OverviewModelImpl cut;
	private OverviewPresenter p;
	
	@Before
	public void setup() {
		cut = new OverviewModelImpl();
		p = mock(OverviewPresenter.class);
		cut.register(p);
	}
	
	@Ignore // temporarily ignore... need to clear up the Resource naming
	@Test
	public void loadFromFile_andSuccessiveGet_returnCorrectInformation() {
		final CountDownLatch latch = new CountDownLatch(1);
		doAnswer((invocationOnMock) ->  {latch.countDown(); System.out.println("TestFile was parsed"); return null;}).when(p).onParseCompletion();
		doThrow(new AssertionError("Exception when parsing testfile")).when(p).onException(any(Exception.class), any(String.class));
		Path testFile;
		try {
			testFile = Paths.get(getClass().getResource("TestFile.resx").toURI());
		} catch (URISyntaxException e) {
			throw new AssertionError("Testfile could not be found in resources", e);
		}
//		cut.loadFromDirectory(testFile, "", "de");
		try {
			latch.await();
		} catch (InterruptedException e) {
			// we shouldn't get interrupted, but if we do, eh well.
			Thread.currentThread().interrupt();
		}
		
		verify(p).onParseCompletion();
		verifyNoMoreInteractions(p);
		
		Translation[] translations;
		translations = cut.getTranslations().toArray(new Translation[0]);
		
		assertArrayEquals(expected, translations);
	}
	
}
