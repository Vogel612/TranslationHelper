package de.vogel612.helper.ui.impl;

import static java.awt.GridBagConstraints.BOTH;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import de.vogel612.helper.data.Translation;
import de.vogel612.helper.data.TranslationTable;
import de.vogel612.helper.ui.OverviewPresenter;
import de.vogel612.helper.ui.OverviewView;

public class OverviewViewImpl implements OverviewView {

	private static final int DEFAULT_WIDTH = 1000;
	private static final int DEFAULT_HEIGHT = 700;

	private final JFrame window;
	private final JTable translationContainer;
	private final JPanel menuBar;

	private OverviewPresenter presenter;

	public OverviewViewImpl() {
		window = new JFrame("Rubberduck Translation Helper");
		// FIXME: send that to the presenter so we can clean up and close shit!
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		translationContainer = new JTable();
		menuBar = new JPanel();
	}

	@Override
	public void register(final OverviewPresenter p) {
		presenter = p;
	}

	@Override
	public void initialize() {
		window.setLayout(new GridBagLayout());
		window.setSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
		window.setMinimumSize(new Dimension(800, 500));
		window.setBackground(new Color(0.2f, 0.3f, 0.7f, 1.0f));

		addMenuBar();
		addTranslationContainer();
		window.doLayout();
	}

	private void addTranslationContainer() {
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.insets = new Insets(15, 15, 15, 15);
		constraints.weightx = 1.0;
		constraints.weighty = 1.0;
		constraints.fill = BOTH;
		constraints.gridx = 0;
		constraints.gridy = 1;

		JScrollPane scroller = new JScrollPane(translationContainer);
		scroller.setMinimumSize(new Dimension(800, 400));
		scroller.setSize(new Dimension(800, 400));
		window.add(scroller, constraints);
	}

	private void addMenuBar() {
		Dimension menuBarDimensions = new Dimension(800, 100);
		menuBar.setMinimumSize(menuBarDimensions);
		menuBar.setPreferredSize(menuBarDimensions);

		menuBar.setBackground(new Color(0.4f, 0.2f, 0.4f, 0.2f));
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.insets = new Insets(15, 15, 15, 15);
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.weightx = 1.0;
		constraints.weighty = 0.0;
		constraints.fill = BOTH;

		window.add(menuBar, constraints);
	}

	@Override
	public void show() {
		window.setVisible(true);
	}

	@Override
	public void rebuildWith(final List<Translation> translations) {
		TranslationTable model = TranslationTable
				.fromTranslations(translations);
		translationContainer.setModel(model);
		bindEventListener(model);
	}

	private void bindEventListener(final TranslationTable model) {
		translationContainer.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(final MouseEvent event) {
				if (event.getClickCount() != 2) { // only react to doubleclicks!
					return;
				}
				final int row = translationContainer.rowAtPoint(event
						.getPoint());
				presenter.onTranslateRequest(model.getKeyAt(row));
			}

			@Override
			public void mouseEntered(final MouseEvent arg0) {
				// IGNORE
			}

			@Override
			public void mouseExited(final MouseEvent arg0) {
				// IGNORE
			}

			@Override
			public void mousePressed(final MouseEvent arg0) {
				// IGNORE
			}

			@Override
			public void mouseReleased(final MouseEvent arg0) {
				// IGNORE
			}

		});
	}

	@Override
	public void showError(final String title, final String errorMessage) {
		JOptionPane.showMessageDialog(window, errorMessage, title,
				JOptionPane.ERROR_MESSAGE);
	}

}
