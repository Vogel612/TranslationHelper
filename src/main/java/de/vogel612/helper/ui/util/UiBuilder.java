package de.vogel612.helper.ui.util;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.util.Objects;

public final class UiBuilder {

	public static void addToGridBag(final Component component,
			final Container container, final Dimension dimensions,
			final GridBagConstraints constraints) {
		Objects.requireNonNull(component,
				"It's kinda hard to add a component without having one");
		Objects.requireNonNull(container,
				"It's kinda hard to add a component without something to add it to");

		if (dimensions != null) {
			component.setMinimumSize(dimensions);
			component.setPreferredSize(dimensions);
		}

		if (constraints != null) {
			container.add(component, constraints);
		} else {
			container.add(component);
		}
	}

}
