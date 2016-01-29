package de.vogel612.helper.ui;

import de.vogel612.helper.data.Translation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

/**
 * <p>
 * Presenter for editing a single Translation. This part of the Program does not
 * require a model, since {@link Translation} <b>is</b> the model. The default
 * workflow is simple. From an OverviewPresenter call:
 * </p>
 * <p>
 * <pre>
 * TranslationPresenter tp = new TranslationPresenter();
 * tp.register(this);
 * tp.setRequestedTranslation(translation);
 * </pre>
 * <p>
 * <p>
 * And handle the onTranslate-Event like the following
 * </p>
 * <p>
 * <pre>
 *     onTranslationSubmit((t) -> tp.hide(); /* use translation *\/);
 * </pre>
 *
 * @author vogel612
 * @implNote Implementation will not happen with an additional separated View
 * interface, since there is in no way enough View-Logic to justify it
 */
public class TranslationPresenter {

    private static final Dimension WINDOW_SIZE = new Dimension(600, 200);
    private static final String TITLE_FORMAT = "Translating - %s to %s";
    private final JFrame window;
    private final JTextField input;
    private final JButton submit;
    private final JButton cancel;

    private final JLabel rootValueLabel;

    private final Set<Runnable> translationAbortListeners = new HashSet<>();
    private final Set<Consumer<Translation>> translationSubmitListeners = new HashSet<>();

    private Translation translation;

    public TranslationPresenter() {
        window = new JFrame("Translate:");
        input = new JTextField();
        submit = new JButton("okay");
        cancel = new JButton("cancel");

        rootValueLabel = new JLabel();
        cancel.addActionListener(event -> translationAbortListeners.forEach(Runnable::run));
        submit.addActionListener(event -> {
            translation.setValue(input.getText());
            // binding could be a problem here :(
            translationSubmitListeners.forEach(l -> l.accept(translation));
        });

        submit.setName("submit");
        cancel.setName("abort");

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

        input.addKeyListener(new KeyAdapter() {

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

    public void show() {
        window.setVisible(true);
    }

    public void hide() {
        window.setVisible(false);
    }

    public void addTranslationSubmitListener(Consumer<Translation> listener) {
        translationSubmitListeners.add(listener);
    }

    public void addTranslationAbortListener(Runnable listener) {
        translationAbortListeners.add(listener);
    }

    public void setRequestedTranslation(Translation left, Translation right) {
        this.translation = right;
        this.rootValueLabel.setText(left.getValue());
        input.setText(translation.getValue());
        window.setTitle(String.format(TITLE_FORMAT, translation.getKey(), translation.getLocale()));
    }
}
