package de.vogel612.helper.ui.util;

import static de.vogel612.helper.ui.util.UiBuilder.addToGridBag;
import static org.mockito.Mockito.*;

import org.junit.Test;

import java.awt.*;

public class UiBuilderTests {

    // no cut since static stuff...
    private final Container container = mock(Container.class);
    private final Component component = mock(Component.class);
    private final Dimension dimension = mock(Dimension.class);
    private final GridBagConstraints constraints = mock(GridBagConstraints.class);

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
