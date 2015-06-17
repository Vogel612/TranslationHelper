package de.vogel612.helper.ui;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.Before;
import org.junit.Test;

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
}
