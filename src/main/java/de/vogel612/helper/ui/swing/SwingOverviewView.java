package de.vogel612.helper.ui.swing;

import static de.vogel612.helper.ui.util.UiBuilder.addToGridBag;
import static java.awt.GridBagConstraints.BOTH;
import static java.awt.event.KeyEvent.VK_ENTER;
import static javax.swing.JOptionPane.*;

import de.vogel612.helper.data.Translation;
import de.vogel612.helper.data.TranslationTable;
import de.vogel612.helper.data.TranslationTableRenderer;
import de.vogel612.helper.data.TranslationTableSelectionModel;
import de.vogel612.helper.ui.common.OverviewViewCommon;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class SwingOverviewView extends OverviewViewCommon {

    static final Dimension MINIMUM_WINDOW_SIZE = new Dimension(800, 500);
    private static final Dimension DEFAULT_WINDOW_SIZE = new Dimension(1000, 700);

    private static final Dimension MENU_BAR_DIMENSION = new Dimension(800, 100);
    private static final Dimension BUTTON_DIMENSION = new Dimension(100, 40);

    private final JFrame window;
    private final JTable translationContainer;
    private final JPanel menuBar;
    private final JButton saveButton;
    private final JButton chooseLang;

    public SwingOverviewView() {
        window = new JFrame("Rubberduck Translation Helper");
        window.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        translationContainer = new JTable();
        translationContainer.setModel(new TranslationTable());
        menuBar = new JPanel();
        saveButton = new JButton("save");
        chooseLang = new JButton("choose sides");

        saveButton.setName("save");
        chooseLang.setName("chooseLang");

        saveButton.addActionListener(event -> saveRequestListeners.forEach(Runnable::run));
        chooseLang.addActionListener(event -> langChoiceRequestListeners.forEach(Runnable::run));

        window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                windowCloseListeners.forEach(Runnable::run);
            }
        });
    }

    @Override
    public void initialize() {
        window.setLayout(new GridBagLayout());
        window.setSize(DEFAULT_WINDOW_SIZE);
        window.setMinimumSize(MINIMUM_WINDOW_SIZE);
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

        translationContainer.setDefaultRenderer(Object.class, new TranslationTableRenderer());
        translationContainer.setSelectionModel(new TranslationTableSelectionModel());
        translationContainer.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        translationContainer.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent keyEvent) {
                switch (keyEvent.getKeyCode()) {
                    case VK_ENTER:
                        translateRow(translationContainer.getSelectedRow());
                        keyEvent.consume();
                        break;
                    default:
                        super.keyPressed(keyEvent);
                }
            }
        });
    }

    private void bindEventListener() {
        translationContainer.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(final MouseEvent event) {
                if (event.getClickCount() != 2) { // only react to doubleclicks!
                    return;
                }
                final int row = translationContainer.rowAtPoint(event.getPoint());
                translateRow(row);
            }
        });
    }

    private void translateRow(int row) {
        final String key = ((TranslationTable) translationContainer
          .getModel()).getKeyAt(row);
        translationRequestListeners.forEach(c -> c.accept(key));
    }

    private void addMenuBar() {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(15, 15, 15, 15);
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 1.0;
        constraints.weighty = 0.0;
        constraints.fill = BOTH;
        menuBar.setLayout(new GridBagLayout());
        menuBar.setBackground(new Color(0.4f, 0.2f, 0.4f, 0.2f));

        addToGridBag(menuBar, window, MENU_BAR_DIMENSION, constraints);

        GridBagConstraints buttonConstraints = (GridBagConstraints) constraints.clone();
        buttonConstraints.gridx = GridBagConstraints.RELATIVE;
        addToGridBag(chooseLang, menuBar, BUTTON_DIMENSION, buttonConstraints);
        addToGridBag(saveButton, menuBar, BUTTON_DIMENSION, buttonConstraints);
    }

    @Override
    public void rebuildWith(final List<Translation> left, final List<Translation> right) {
        translationContainer.setModel(new TranslationTable(left, right));
    }

    @Override
    public void displayError(final String title, final String errorMessage) {
        JOptionPane.showMessageDialog(window, errorMessage, title, ERROR_MESSAGE);
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
    public void showPrompt(String title, String promptText, Runnable okCallback) {
        int choice = JOptionPane.showConfirmDialog(window, promptText, title, YES_NO_OPTION);
        if (choice == YES_OPTION) { okCallback.run(); }
    }
}

