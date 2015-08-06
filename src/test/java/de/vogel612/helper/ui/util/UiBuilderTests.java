package de.vogel612.helper.ui.util;

import static de.vogel612.helper.ui.util.UiBuilder.addToGridBag;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;

import org.junit.Test;

public class UiBuilderTests {

	// no cut since static stuff...
	Container container = mock(Container.class);
	Component component = mock(Component.class);
	Dimension dimension = mock(Dimension.class);
	GridBagConstraints constraints = mock(GridBagConstraints.class);

	@Test(expected = NullPointerException.class)
	public void addToGridBag_nullContainer_blowsUp() {
		addToGridBag(component, null, dimension, constraints);
	}

	@Test(expected = NullPointerException.class)
	public void addToGridBag_nullComponent_blowsUp() {
		addToGridBag(null, container, dimension, constraints);
	}

	@Test
	public void addToGridBag_correctly_setsDimensionAndUsesConstraints() {
		addToGridBag(component, container, dimension, constraints);

		verify(component).setMinimumSize(dimension);
		verify(component).setPreferredSize(dimension);
		verify(container).add(component, constraints);
		verifyNoMoreInteractions(container, component, dimension, constraints);
	}

	@Test
	public void addToGridBag_withoutSize_UsesConstraints() {
		addToGridBag(component, container, null, constraints);

		verify(container).add(component, constraints);
		verifyNoMoreInteractions(container, component, dimension, constraints);
	}

	@Test
	public void addToGridBag_withoutConstraints_setsDimension() {
		addToGridBag(component, container, dimension, null);

		verify(component).setMinimumSize(dimension);
		verify(component).setPreferredSize(dimension);
		verify(container).add(component);
		verifyNoMoreInteractions(container, component, dimension, constraints);
	}
}