package de.vogel612.helper.ui;

import static org.junit.Assert.assertArrayEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CountDownLatch;

import org.junit.Before;
import org.junit.Test;

import de.vogel612.helper.data.Translation;
import de.vogel612.helper.ui.impl.OverviewModelImpl;

public class OverviewModelTest {

	private static final Translation[] expected = {
		new Translation("TestKey1", "TestValue", "TestValue"),
		new Translation("TestKey2", "Another Test Value", "Another Test Value")
	};
	private static final Translation[] expected2 = {
		new Translation("TestKey1", "TestValue", "Second Test"),
		new Translation("TestKey2", "Another Test Value", "Another Test Value")
	};
	private static final Translation[] expectedAfterEdit = {
		new Translation("TestKey1", "TestValue", "New Translation"),
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

	@Test
	public void loadFromFile_andSuccessiveGet_returnCorrectInformation() {
		final CountDownLatch latch = new CountDownLatch(1);
		doAnswer((invocationOnMock) -> {
			latch.countDown();
			System.out.println("TestFile was parsed");
			return null;
		}).when(p).onParseCompletion();
		doThrow(new AssertionError("Exception when parsing testfile")).when(p)
				.onException(any(Exception.class), any(String.class));
		Path testFile;
		try {
			testFile = Paths.get(
					getClass().getResource("RubberduckUI.resx").toURI())
					.getParent();
		} catch (URISyntaxException e) {
			throw new AssertionError(
					"Testfile could not be found in resources", e);
		}
		cut.loadFromDirectory(testFile, "");
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

	@Test
	public void loadFromFile_normalizationWorksAsExpected() {
		final CountDownLatch latch = new CountDownLatch(1);
		doAnswer((invocationOnMock) -> {
			latch.countDown();
			System.out.println("TestFile was parsed");
			return null;
		}).when(p).onParseCompletion();
		doThrow(new AssertionError("Exception when parsing testfile")).when(p)
				.onException(any(Exception.class), any(String.class));

		Path testFolder;
		try {
			testFolder = Paths.get(
					getClass().getResource("RubberduckUI.resx").toURI())
					.getParent();
		} catch (URISyntaxException e) {
			throw new AssertionError(
					"Testfile could not be found in resources", e);
		}

		cut.loadFromDirectory(testFolder, "test");
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

		assertArrayEquals(expected2, translations);
	}

	@Test
	public void editTranslation_updatesDocument() {
		// abusing the loading test as a setup...
		loadFromFile_andSuccessiveGet_returnCorrectInformation();
		reset(p); // just to be sure

		cut.updateTranslation("TestKey1", "New Translation");

		Translation[] translations;
		translations = cut.getTranslations().toArray(new Translation[0]);

		assertArrayEquals(expectedAfterEdit, translations);
	}
}
