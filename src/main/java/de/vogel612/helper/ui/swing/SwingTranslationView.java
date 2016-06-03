package de.vogel612.helper.ui.swing;

import de.vogel612.helper.data.Translation;
import de.vogel612.helper.ui.common.TranslationViewCommon;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

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
public class SwingTranslationView extends TranslationViewCommon {

    private static final Dimension WINDOW_SIZE = new Dimension(600, 200);
    private final JFrame window;
    private final JTextArea rootValue;
    private final JTextArea input;
    private final JButton submit;

    private final JButton cancel;

    private Translation translation;
    private Translation leftSide;

    public SwingTranslationView() {
        window = new JFrame("Translate:");
        input = new JTextArea();
        submit = new JButton("okay");
        cancel = new JButton("cancel");

        rootValue = new JTextArea();
        cancel.addActionListener(event -> translationAbortListeners.forEach(Runnable::run));
        submit.addActionListener(event -> {
            translation.setValue(input.getText());
            // binding could be a problem here :(
            translationSubmitListeners.forEach(l -> l.accept(translation));
            leftSide.setValue(rootValue.getText());
            translationSubmitListeners.forEach(l -> l.accept(leftSide));
        });

        input.setName("input");
        rootValue.setName("rootValue");
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
        constraints.gridy = 1;
        constraints.weighty = 0.0;
        window.add(submit, constraints);
    }

    private void addCancel() {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.weightx = 1.0;
        constraints.insets = new Insets(15, 15, 15, 15);
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.weighty = 0.0;
        constraints.gridwidth = 1;
        window.add(cancel, constraints);
    }

    private void addInputText() {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.weightx = .5;
        constraints.insets = new Insets(15, 15, 15, 15);
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.weighty = 1.0;
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
                    input.setCaretPosition(caret + 2);
                } else {
                    submit.doClick();
                }
            }
        });
    }

    private void addRootValueLabel() {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.weightx = .5;
        constraints.insets = new Insets(15, 15, 15, 15);
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weighty = 1.0;

        window.add(rootValue, constraints);
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
    public void setRequestedTranslation(Translation left, Translation right) {
        this.translation = right;
        this.leftSide = left;
        this.rootValue.setText(left.getValue());
        input.setText(translation.getValue());
        window.setTitle(String.format(TITLE_FORMAT, translation.getKey(), translation.getLocale()));
    }
}
