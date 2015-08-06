package de.vogel612.helper.ui.util;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;

public final class UiBuilder {

	public static void addToGridBag(final Component component,
			final Container container, final Dimension dimensions,
			final GridBagConstraints constraints) {
		component.setMinimumSize(dimensions);
		component.setPreferredSize(dimensions);

		container.add(component, constraints);
	}

}
