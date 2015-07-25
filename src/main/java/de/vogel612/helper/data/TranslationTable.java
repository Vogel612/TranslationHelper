package de.vogel612.helper.data;

import java.util.List;

import javax.swing.table.AbstractTableModel;

public class TranslationTable extends AbstractTableModel {

	private static final int COLUMN_COUNT = 2;
	final List<Translation> translations;

	private TranslationTable(final List<Translation> translations) {
		super();
		this.translations = translations;
	}

	@Override
	public int getColumnCount() {
		return 2;
	}

	@Override
	public int getRowCount() {
		return translations.size();
	}

	@Override
	public Object getValueAt(final int row, final int column) {
		if (row < 0 || column < 0 || column >= COLUMN_COUNT) {
			throw new IllegalArgumentException(
					"Negative Row / Column values or Column values greater than 2 are not allowed");
		}
		return column == 0 ? translations.get(row).getValue() : translations
				.get(row).getValue();
	}

	public String getKeyAt(final int row) {
		if (row < 0) {
			throw new IllegalArgumentException(
					"Negative Row values are not allowed");
		}
		return translations.get(row).getKey();
	}

	public static TranslationTable fromTranslations(
			final List<Translation> translations) {
		return new TranslationTable(translations);
	}

}
