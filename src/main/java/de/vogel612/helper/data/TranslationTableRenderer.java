package de.vogel612.helper.data;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class TranslationTableRenderer extends DefaultTableCellRenderer {

	@Override
	public Component getTableCellRendererComponent(final JTable table,
			final Object value, final boolean isSelected,
			final boolean hasFocus, final int row, final int column) {
		Component c = super.getTableCellRendererComponent(table, value,
				isSelected, hasFocus, row, column);
		if (table == null) {
			return c;
		}

		if (table.getValueAt(row, 0).equals(table.getValueAt(row, 1))
				|| table.getValueAt(row, 1).toString().isEmpty()) {
			c.setBackground(Color.YELLOW);
		} else {
			c.setBackground(Color.WHITE);
		}
		return c;
	}
}
