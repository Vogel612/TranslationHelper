package de.vogel612.helper.ui.swing.components;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.Mockito;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class TranslationTableRendererTests {
    // FIXME rewrite for NotableData
    private final TranslationTableRenderer cut = new TranslationTableRenderer();

    private JTable table;

    @Parameter(0)
    public String leftSide;

    @Parameter(1)
    public String rightSide;

    @Parameter(2)
    public Color expected;

    @Parameter(3)
    public int selectedRow;

    @Before
    public void setup() {
        table = Mockito.mock(JTable.class);
    }

    @Parameters
    public static Collection<Object[]> testData() {
        Object[][] data = new Object[][]{
          { "asd", "foo", Color.WHITE, -1 },
          { "", "asd", Color.YELLOW, -1 },
          { "asd", "asd", Color.YELLOW, -1 },
          { "asd", "asd{0}", Color.ORANGE, -1 },
          { "asd{0:d.5}", "asd{0}", Color.ORANGE, -1 },
          { "asd{1}", "asd{0}", Color.ORANGE, -1 },
          { "{0}as{1}d", "asd{0}", Color.ORANGE, -1 },

          { "asd", "foo", Color.BLUE, 0 },
          { "", "asd", Color.ORANGE, 0 },
          { "asd", "asd", Color.ORANGE, 0 },
          { "asd", "asd{0}", Color.RED, 0 },
          { "asd{0:d.5}", "asd{0}", Color.RED, 0 },
          { "asd{1}", "asd{0}", Color.RED, 0 },
          { "{0}as{1}d", "asd{0}", Color.RED, 0 },
          };
        return Arrays.asList(data);
    }

    @Test
    public void checkBackgroundColors() {
        Mockito.doReturn(leftSide).when(table).getValueAt(0, 0);
        Mockito.doReturn(rightSide).when(table).getValueAt(0, 1);
        Mockito.doReturn(selectedRow).when(table).getSelectedRow();
        Component actual = cut.getTableCellRendererComponent(table, null, false, false, 0, 0);
        assertEquals(expected, actual.getBackground());
    }
}
