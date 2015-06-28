package de.vogel612.helper.ui;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.nio.file.Path;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.vogel612.helper.data.Translation;
import de.vogel612.helper.ui.impl.OverviewPresenterImpl;

public class OverviewPresenterTest {

	OverviewView v;
	OverviewModel m;
	TranslationPresenter p;

	OverviewPresenter cut;

	@Before
	public void beforeTest() {
		v = mock(OverviewView.class);
		m = mock(OverviewModel.class);
		p = mock(TranslationPresenter.class);

		cut = new OverviewPresenterImpl(m, v, p);
		reset(v, m, p);
	}

	@Test
	public void initialize_registersPresenter() {
		cut.initialize();

		verify(v).register(cut);
		verify(m).register(cut);
		verify(p).register(cut);
		verifyNoMoreInteractions(m, v, p);
	}

	@Test
	public void initialize_registersPresenter_onlyOnce() {
		cut.initialize();
		reset(m, v, p);
		cut.initialize();
		verifyNoMoreInteractions(m, v, p);
	}

	@Test
	public void show_callsInitialize_ifNotInitialized() {
		cut.show();
		verify(v).show();
		verify(v).register(cut);
		verify(m).register(cut);
		verify(p).register(cut);
		verifyNoMoreInteractions(m, v, p);
	}

	@Test
	public void show_callsShow_onView() {
		cut.initialize();
		reset(m, v, p);
		cut.show();
		verify(v).show();
		verifyNoMoreInteractions(m, v, p);
	}

	@Test
	public void loadFromFile_delegatesToModel() {
		Path mock = mock(Path.class);

		cut.loadFiles(mock, OverviewPresenter.DEFAULT_ROOT_LOCALE,
				OverviewPresenter.DEFAULT_TARGET_LOCALE);
		verify(m).loadFromDirectory(mock, "de");
		verifyNoMoreInteractions(m, v, p);
	}

	@Test
	public void onException_delegatesToView() {
		final Exception e = mock(Exception.class);
		final String message = "testingmessage";
		final String errorMessage = "alsdkj";
		doReturn(errorMessage).when(e).getMessage();

		cut.onException(e, message);

		verify(v).showError(message, errorMessage);
		verify(e).getMessage();
		verifyNoMoreInteractions(m, v, p);
	}

	@Test
	public void onParseCompletion_rebuildsView() {
		List<Translation> list = mock(List.class);
		doReturn(list).when(m).getTranslations();

		cut.onParseCompletion();

		verify(m).getTranslations();
		verify(v).rebuildWith(list);
		verifyNoMoreInteractions(m, v, p);
	}

	@Test
	public void onTranslateRequest_delegatesToTranslationPresenter() {
		final String key = "Key";
		Translation fake = mock(Translation.class);
		doReturn(fake).when(m).getSingleTranslation(key);

		cut.onTranslateRequest(key);

		verify(m).getSingleTranslation(key);
		verify(p).setRequestedTranslation(fake);
		verify(p).show();
		verifyNoMoreInteractions(m, v, p);
	}

	@Test
	public void onTranslationSubmit_hidesTranslationView_propagatesEdit_updatesView() {
		final Translation t = new Translation("Key", "Value", "Translation");
		final List<Translation> list = mock(List.class);
		doReturn(list).when(m).getTranslations();

		cut.onTranslationSubmit(t);

		verify(m).updateTranslation("Key", "Translation");
		verify(m).getTranslations();
		verify(v).rebuildWith(list);
		verify(p).hide();
		verifyNoMoreInteractions(m, v, p);
	}

	@Test
	public void onTranslationAbort_hidesTranslationView() {
		cut.onTranslationAbort();

		verify(p).hide();
		verifyNoMoreInteractions(m, v, p);
	}

	@Test
	public void onSaveRequest_delegatesToModel() {
		cut.onSaveRequest();

		verify(m).save();
		verifyNoMoreInteractions(m, v, p);
	}

}
