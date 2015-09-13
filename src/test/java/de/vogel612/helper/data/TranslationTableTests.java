package de.vogel612.helper.data;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import javax.swing.*;
import java.util.Arrays;


public class TranslationTableTests {

    private static final Translation[] leftSide = new Translation[]{
      new Translation("", "key1", "root1"), new Translation("", "key2", "root2")
    };

    private static final Translation[] rightSide = new Translation[]{
      new Translation("", "key1", "translation1"),
      new Translation("", "key2", "translation2")
    };

    private static final Translation[] unbalancedRightSide = new Translation[] {
      new Translation("", "key1", "translation1")
    };

    private TranslationTable cut;

    @Before
    public void setup() {
        cut = new TranslationTable(Arrays.asList(leftSide), Arrays.asList(rightSide));
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
    public void columnOne_isTranslationValue() {
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
        assertEquals(leftSide.length, cut.getRowCount());
    }

    @Test
    public void getLastRow() {
        assertEquals("root2", cut.getValueAt(cut.getRowCount() - 1, 0));
    }

    @Test
    public void rowCount_matchesJTableRowCount() {
        JTable container = new JTable(cut);
        assertEquals(container.getRowCount(), cut.getRowCount());
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

    @Test(expected = IllegalArgumentException.class)
    public void testUnbalancedData(){
        cut = new TranslationTable(Arrays.asList(leftSide), Arrays.asList(unbalancedRightSide));
    }
}
