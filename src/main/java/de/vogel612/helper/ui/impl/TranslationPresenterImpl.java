package de.vogel612.helper.ui.impl;

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

		submit.addActionListener(event -> {
			overview.onTranslationSubmit(translation);
		});
		cancel.addActionListener(event -> {
			hide();
			// TODO: notifiy overview instead??
		});
		rootValueLabel = new JLabel();

		window.add(rootValueLabel);
		window.add(input);
		window.add(cancel);
		window.add(submit);

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
	}

}
