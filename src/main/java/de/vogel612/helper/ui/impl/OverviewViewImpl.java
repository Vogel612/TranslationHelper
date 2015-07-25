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

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import de.vogel612.helper.data.Side;
import de.vogel612.helper.data.Translation;
import de.vogel612.helper.data.TranslationTable;
import de.vogel612.helper.data.TranslationTableRenderer;
import de.vogel612.helper.ui.OverviewPresenter;
import de.vogel612.helper.ui.OverviewView;

public class OverviewViewImpl implements OverviewView {

	private static final int DEFAULT_WIDTH = 1000;
	private static final int DEFAULT_HEIGHT = 700;

	private static final Dimension MENU_BAR_DIMENSION = new Dimension(800, 100);

	private final JFrame window;
	private final JTable translationContainer;
	private final JPanel menuBar;
	private final JButton saveButton;

	private OverviewPresenter presenter;

	public OverviewViewImpl() {
		window = new JFrame("Rubberduck Translation Helper");
		// FIXME: send that to the presenter so we can clean up and close shit!
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		translationContainer = new JTable();
		menuBar = new JPanel();
		saveButton = new JButton("save");
	}

	@Override
	public void register(final OverviewPresenter p) {
		presenter = p;
		saveButton.addActionListener(event -> presenter.onSaveRequest());
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
		bindEventListener();

		translationContainer.setDefaultRenderer(Object.class,
				new TranslationTableRenderer());
	}
	private void addMenuBar() {
		menuBar.setMinimumSize(MENU_BAR_DIMENSION);
		menuBar.setPreferredSize(MENU_BAR_DIMENSION);

		menuBar.setBackground(new Color(0.4f, 0.2f, 0.4f, 0.2f));
		menuBar.add(saveButton); // TODO: nicely layout
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
	public void rebuildWith(final List<Translation> translations,
			final Side side) {
		TranslationTable model = TranslationTable
				.fromTranslations(translations);
		translationContainer.setModel(model);
	}

	private void bindEventListener() {
		translationContainer.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(final MouseEvent event) {
				if (event.getClickCount() != 2) { // only react to doubleclicks!
					return;
				}
				final int row = translationContainer.rowAtPoint(event
						.getPoint());
				final String key = ((TranslationTable) translationContainer
						.getModel()).getKeyAt(row);
				presenter.onTranslateRequest(key);
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
	public void displayError(final String title, final String errorMessage) {
		JOptionPane.showMessageDialog(window, errorMessage, title,
				JOptionPane.ERROR_MESSAGE);
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
	}

}
