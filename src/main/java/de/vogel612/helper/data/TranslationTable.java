package de.vogel612.helper.data;

import java.util.List;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import de.vogel612.helper.data.Translation;

public class TranslationTable extends AbstractTableModel {

	final List<Translation> translations;

	private TranslationTable(List<Translation> translations) {
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
	public Object getValueAt(int row, int column) {
		if (row < 0 || column < 0 || column > 2) {
			throw new IllegalArgumentException(
					"Negative Row / Column values or Column values greater than 2 are not allowed");
		}
		return column == 0
				? translations.get(row).getRootValue()
				: translations.get(row).getTranslation();
	}

	public static TableModel fromTranslations(List<Translation> translations) {
		return new TranslationTable(translations);
	}

}
