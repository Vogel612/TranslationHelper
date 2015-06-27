package de.vogel612.helper.ui.impl;

import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import de.vogel612.helper.data.Translation;
import de.vogel612.helper.ui.OverviewPresenter;
import de.vogel612.helper.ui.TranslationPresenter;

public class TranslationPresenterImpl implements TranslationPresenter {

	private final JFrame window;
	private final JTextField input;
	private final JButton submit;
	private final JButton cancel;

	private final JLabel rootValueLabel;

	private OverviewPresenter overview;
	private Translation translation;

	public TranslationPresenterImpl() {
		window = new JFrame("Translate:");
		input = new JTextField();
		submit = new JButton("okay");
		cancel = new JButton("cancel");

		rootValueLabel = new JLabel();
		doLayout();

	}
	private void doLayout() {
		window.setMinimumSize(new Dimension(800, 400));
		window.setPreferredSize(new Dimension(800, 400));
		window.setSize(new Dimension(800, 400));
		window.setLayout(new FlowLayout());

		window.add(rootValueLabel);
		window.add(input);
		window.add(cancel);
		window.add(submit);

		// TODO Set control sizes ...
	}
	@Override
	public void register(final OverviewPresenter p) {
		overview = p;
	}

	@Override
	public void show() {
		window.setVisible(true);
	}

	@Override
	public void hide() {
		window.setVisible(false);
	}

	@Override
	public void setRequestedTranslation(final Translation t) {
		translation = t;
		bindControls();
	}

	private void bindControls() {
		rootValueLabel.setText(translation.getRootValue());
		input.setText(translation.getTranslation());
		submit.addActionListener(event -> {
			translation.setTranslation(input.getText());
			overview.onTranslationSubmit(translation);
		});
		cancel.addActionListener(event -> {
			overview.onTranslationAbort();
		});
	}
}
