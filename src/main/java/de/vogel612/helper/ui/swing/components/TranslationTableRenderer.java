package de.vogel612.helper.ui.swing.components;

import de.vogel612.helper.data.NotableData;
import de.vogel612.helper.data.Translation;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.text.TableView.TableCell;
import java.awt.*;

/**
 * Custom render model for Translations in the table.</br>
 * Highlights notable rows using {@link NotableData#assessNotability(Translation, Translation)} to determine their
 * highlight
 */
public class TranslationTableRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(final JTable table,
      final Object value, final boolean isSelected,
      final boolean hasFocus, final int row, final int column) {
        Component c = super.getTableCellRendererComponent(table, ((Translation)value).getValue(),
          isSelected, hasFocus, row, column);
        if (table == null) {
            return c;
        }

        final Translation left = (Translation) table.getValueAt(row, 0);
        final Translation right = (Translation) table.getValueAt(row, 1);

        final boolean selected = row == table.getSelectedRow();

        switch (NotableData.assessNotability(left, right)) {
            case INFO:
            case DEFAULT:
                c.setBackground(selected ? Color.BLUE : Color.WHITE);
                c.setForeground(selected ? Color.WHITE : Color.BLACK);
                break;
            case WARNING:
                c.setBackground(selected ? Color.ORANGE : Color.YELLOW);
                c.setForeground(Color.BLACK);
                break;
            case ERROR:
                c.setBackground(selected ? Color.RED : Color.ORANGE);
                c.setForeground(Color.BLACK);
                break;
        }

        return c;
    }
}
