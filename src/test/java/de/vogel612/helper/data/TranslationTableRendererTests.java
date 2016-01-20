package de.vogel612.helper.data;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class TranslationTableRendererTests {

    private final TranslationTableRenderer cut = new TranslationTableRenderer();

    private JTable table;

    @Parameter(0)
    public String leftSide;

    @Parameter(1)
    public String rightSide;

    @Parameter(2)
    public Color expected;

    @Before
    public void setup() {
        table = mock(JTable.class);
    }

    @Parameters
    public static Collection<Object[]> testData() {
        Object[][] data = new Object[][]{
          {"asd", "foo", Color.WHITE},
          {"", "asd", Color.YELLOW},
          {"asd", "asd", Color.YELLOW},
          {"asd", "asd{0}", Color.ORANGE},
          {"asd{0:d.5}", "asd{0}", Color.ORANGE},
          {"asd{1}", "asd{0}", Color.ORANGE},
          {"{0}as{1}d", "asd{0}", Color.ORANGE},
        };
        return Arrays.asList(data);
    }

    @Test
    public void checkBackgroundColors() {
        doReturn(leftSide).when(table).getValueAt(0, 0);
        doReturn(rightSide).when(table).getValueAt(0, 1);
        Component actual = cut.getTableCellRendererComponent(table, null, false, false, 0, 0);
        assertEquals(expected, actual.getBackground());
    }
}
