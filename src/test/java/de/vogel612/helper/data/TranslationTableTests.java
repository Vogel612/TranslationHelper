package de.vogel612.helper.data;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

public class TranslationTableTests {

	private static final Translation[] testData = new Translation[]{
		new Translation("key1", "root1", "translation1"),
		new Translation("key2", "root2", "translation2")
	};

	private TranslationTable cut;

	@Before
	public void setup() {
		cut = TranslationTable.fromTranslations(Arrays.asList(testData));
	}

	@Test
	public void columnCount_isTwo() {
		assertEquals(2, cut.getColumnCount());
	}

	@Test
	public void columnZero_isRootValues() {
		assertEquals("root1", cut.getValueAt(0, 0));
		assertEquals("root2", cut.getValueAt(1, 0));
	}

	@Test
	public void columOne_isTranslationValue() {
		assertEquals("translation1", cut.getValueAt(0, 1));
		assertEquals("translation2", cut.getValueAt(1, 1));
	}

	@Test
	public void tableModel_exposesTranslationKeys() {
		assertEquals("key1", cut.getKeyAt(0));
		assertEquals("key2", cut.getKeyAt(1));
	}

	@Test
	public void rowCount_matchesConstructorListLength() {
		assertEquals(testData.length, cut.getRowCount());
	}

	@Test(expected = IllegalArgumentException.class)
	public void column_cannotBeBelowZero() {
		cut.getValueAt(0, -1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void column_cannotBeAboveOne() {
		cut.getValueAt(0, 2);
	}

	@Test(expected = IllegalArgumentException.class)
	public void row_cannotBeNegative() {
		cut.getValueAt(-1, 0);
	}

	@Test(expected = IllegalArgumentException.class)
	public void keyAtRow_cannotBeBelowZero() {
		cut.getKeyAt(-1);
	}

}
