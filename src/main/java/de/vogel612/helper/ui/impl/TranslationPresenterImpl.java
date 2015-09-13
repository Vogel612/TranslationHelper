package de.vogel612.helper.ui.impl;

import de.vogel612.helper.data.Translation;
import de.vogel612.helper.ui.OverviewPresenter;
import de.vogel612.helper.ui.TranslationPresenter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class TranslationPresenterImpl implements TranslationPresenter {

    private static final Dimension WINDOW_SIZE = new Dimension(600, 200);
    private static final String TITLE_FORMAT = "Translating - %s to %s";
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
        window.setMinimumSize(WINDOW_SIZE);
        window.setPreferredSize(WINDOW_SIZE);
        window.setSize(WINDOW_SIZE);
        window.setLayout(new GridBagLayout());

        addRootValueLabel();
        addInputText();
        addCancel();
        addSubmit();
    }

    private void addSubmit() {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.weightx = 1.0;
        constraints.insets = new Insets(15, 15, 15, 15);
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 1;
        constraints.gridy = 2;
        constraints.weighty = 0.33;
        window.add(submit, constraints);
    }

    private void addCancel() {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.weightx = 1.0;
        constraints.insets = new Insets(15, 15, 15, 15);
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.weighty = 0.33;
        constraints.gridwidth = 1;
        window.add(cancel, constraints);
    }

    private void addInputText() {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.weightx = 1.0;
        constraints.insets = new Insets(15, 15, 15, 15);
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.weighty = 0.33;
        window.add(input, constraints);

        input.addKeyListener(new KeyListener() {

            @Override
            public void keyPressed(final KeyEvent event) {
                switch (event.getKeyCode()) {
                    case KeyEvent.VK_ESCAPE:
                        cancel.doClick();
                        break;
                    case KeyEvent.VK_ENTER:
                        handleEnter(event);
                        break;
                    default:
                        break;
                }
            }

            private void handleEnter(final KeyEvent event) {
                if (event.isShiftDown()) {
                    int caret = input.getCaretPosition();
                    String newValue = input.getText();
                    newValue = newValue.substring(0, caret) + "\r\n"
                      + newValue.substring(caret);
                    input.setText(newValue);
                } else {
                    submit.doClick();
                }
            }

            @Override
            public void keyReleased(final KeyEvent event) {
                // Ignore, bubbles to default
            }

            @Override
            public void keyTyped(final KeyEvent event) {
                // Ignore, bubbles to default
            }

        });
    }

    private void addRootValueLabel() {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.weightx = 1.0;
        constraints.insets = new Insets(15, 15, 15, 15);
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.weighty = 0.33;

        window.add(rootValueLabel, constraints);
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
    public void setRequestedTranslation(final Translation t, final String original) {
        this.translation = t;
        this.rootValueLabel.setText(original);
        input.setText(translation.getValue());
        submit.addActionListener(event -> {
            translation.setValue(input.getText());
            overview.onTranslationSubmit(translation);
        });
        cancel.addActionListener(event -> overview.onTranslationAbort());
        window.setTitle(String.format(TITLE_FORMAT, translation.getKey(), translation.getLocale()));
    }
}
