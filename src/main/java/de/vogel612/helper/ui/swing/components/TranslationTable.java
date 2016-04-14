package de.vogel612.helper.ui.swing.components;

import de.vogel612.helper.data.Side;
import de.vogel612.helper.data.Translation;

import javax.swing.table.AbstractTableModel;
import java.util.*;

public class TranslationTable extends AbstractTableModel {

    private static final int COLUMN_COUNT = 2;
    private final Map<Side, List<Translation>> data = new EnumMap<>(Side.class);

    public TranslationTable() {
        this(Collections.emptyList(), Collections.emptyList());
    }

    public TranslationTable(final List<Translation> left, final List<Translation> right) {
        if (left.size() != right.size()) {
            throw new IllegalArgumentException("Translations must be balanced. iow. sides must be of equal size");
        }
        setSide(Side.LEFT, left);
        setSide(Side.RIGHT, right);
    }

    public void setSide(final Side side, final List<Translation> translations) {
        List<Translation> currentOrDefault = data.getOrDefault(side, new ArrayList<>());
        if (currentOrDefault.size() != translations.size()
          && currentOrDefault.size() != 0) {
            throw new IllegalArgumentException(
              "Cannot change number of Translations!");
        }
        data.put(side, translations);
    }

    @Override
    public int getColumnCount() {
        return 2;
    }

    @Override
    public int getRowCount() {
        return data.get(Side.LEFT).size();
    }

    @Override
    public Object getValueAt(final int row, final int column) {
        if (row < 0 || column < 0 || column >= COLUMN_COUNT) {
            throw new IllegalArgumentException(
              "Negative Row / Column values or Column values greater than 2 are not allowed");
        }
        return column == 0
               ? data.get(Side.LEFT).get(row).getValue()
               : data.get(Side.RIGHT).get(row).getValue();
    }

    public String getKeyAt(final int row) {
        if (row < 0) {
            throw new IllegalArgumentException(
              "Negative Row values are not allowed");
        }
        return data.get(Side.LEFT).get(row).getKey();
    }

}
