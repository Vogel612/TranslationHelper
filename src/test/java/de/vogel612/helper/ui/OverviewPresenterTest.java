package de.vogel612.helper.ui;

import static org.mockito.Mockito.*;

import java.nio.file.Path;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.vogel612.helper.data.Translation;
import de.vogel612.helper.ui.impl.OverviewPresenterImpl;

public class OverviewPresenterTest {

	OverviewView v;
	OverviewModel m;

	OverviewPresenter cut;

	@Before
	public void beforeTest() {
		v = mock(OverviewView.class);
		m = mock(OverviewModel.class);

		cut = new OverviewPresenterImpl(m, v);
		reset(v,m);
	}

	@Test
	public void initialize_registersPresenter() {
		cut.initialize();
		
		verify(v).register(cut);
		verify(m).register(cut);
		verifyNoMoreInteractions(m,v);
	}
	
	@Test
	public void initialize_registersPresenter_onlyOnce() {
		cut.initialize();
		reset(m, v);
		cut.initialize();
		verifyNoMoreInteractions(m,v);
	}
	
	@Test
	public void show_callsInitialize_ifNotInitialized() {
		cut.show();
		verify(v).show();
		verify(v).register(cut);
		verify(m).register(cut);
		verifyNoMoreInteractions(m,v);
	}
	
	@Test
	public void show_callsShow_onView() {
		cut.initialize();
		reset(m,v);
		cut.show();
		verify(v).show();
		verifyNoMoreInteractions(m,v);
	}
	
	@Test
	public void loadFromFile_delegatesToModel() {
		Path mock = mock(Path.class);
		
		cut.loadFiles(mock);
		verify(m).loadFromDirectory(mock, "de");
		verifyNoMoreInteractions(m,v);
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
		verifyNoMoreInteractions(m,v);
	}
	
	@Test
	public void onParseCompletion_rebuildsView() {
		List<Translation> list = mock(List.class);
		doReturn(list).when(m).getTranslations();
		
		cut.onParseCompletion();
		
		verify(m).getTranslations();
		verify(v).rebuildWith(list);
		verifyNoMoreInteractions(m,v);
	}
	
	
}
