package de.vogel612.helper.data;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;

import org.junit.Before;
import org.junit.Test;

public class TranslationTableRendererTests {

	private final TranslationTableRenderer cut = new TranslationTableRenderer();

	JTable table;

	@Before
	public void setup() {
		table = mock(JTable.class);
	}

	@Test
	public void testNullTable_returnsDefaultTableRendererResult() {
		Component actual = cut.getTableCellRendererComponent(null, null, false,
				false, 0, 0);
		assertEquals(null, actual.getBackground());
	}

	@Test
	public void testDifferentValues_returnsWhiteBackgroundComponent() {
		doReturn("").when(table).getValueAt(0, 0);
		doReturn("asd").when(table).getValueAt(0, 1);
		Component actual = cut.getTableCellRendererComponent(table, null,
				false, false, 0, 0);
		assertEquals(Color.WHITE, actual.getBackground());
	}

	@Test
	public void testEqualValues_returnsYellowBackgroundComponent() {
		doReturn("asd").when(table).getValueAt(0, 0);
		doReturn("asd").when(table).getValueAt(0, 1);
		Component actual = cut.getTableCellRendererComponent(table, "asd",
				false, false, 0, 0);
		assertEquals(Color.YELLOW, actual.getBackground());
	}
}
