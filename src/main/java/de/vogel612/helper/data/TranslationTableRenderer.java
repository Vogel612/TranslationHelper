package de.vogel612.helper.data;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Custom render model for Translations in the table.</br>
 * Highlights rows that fulfil following conditions: </br>
 * <ul><li>Both sides of the row match</li><li>At least one value is empty, when considered a string</li></ul>
 */
public class TranslationTableRenderer extends DefaultTableCellRenderer {

    private static final String FORMAT_FINDER = "(?<!\\{)\\{(?:\\{\\{)*((?:\\d+)(?::.*?)?)\\}(?:\\}\\})*(?!\\})";
    private static final Pattern FORMAT_PATTERN = Pattern.compile(FORMAT_FINDER, Pattern.MULTILINE);

    @Override
    public Component getTableCellRendererComponent(final JTable table,
      final Object value, final boolean isSelected,
      final boolean hasFocus, final int row, final int column) {
        Component c = super.getTableCellRendererComponent(table, value,
          isSelected, hasFocus, row, column);
        if (table == null) {
            return c;
        }

        final String left = table.getValueAt(row, 0).toString();
        final String right = table.getValueAt(row, 1).toString();

        final Set<String> leftFormats = getFormatSpecifiers(left);
        final Set<String> rightFormats = getFormatSpecifiers(right);

        final boolean selected = row == table.getSelectedRow();
        // Chain of responsibility??
        if (!(leftFormats.containsAll(rightFormats) && rightFormats.containsAll(leftFormats))) {
            c.setBackground(selected ? Color.RED : Color.ORANGE);
            c.setForeground(Color.BLACK);
        } else if (left.equals(right) || right.isEmpty() || left.isEmpty()) {
            c.setBackground(selected ? Color.ORANGE : Color.YELLOW);
            c.setForeground(Color.BLACK);
        } else {
            c.setBackground(selected ? Color.BLUE : Color.WHITE);
            c.setForeground(selected ? Color.WHITE : Color.BLACK);
        }
        return c;
    }

    private Set<String> getFormatSpecifiers(String s) {
        final Matcher m = FORMAT_PATTERN.matcher(s);
        final Set<String> result = new HashSet<>();
        while (m.find()) {
            result.add(m.group());
        }
        return result;
    }
}
