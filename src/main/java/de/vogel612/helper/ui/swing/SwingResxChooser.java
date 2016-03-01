package de.vogel612.helper.ui.swing;

import static de.vogel612.helper.data.util.DataUtilities.FILENAME_PATTERN;

import de.vogel612.helper.ui.ResxChooser;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;

/**
 * View to select a set of Resx-Files and locales.
 */
public class SwingResxChooser extends ResxChooser {

    private final JFrame window = new JFrame("Translation Helper - File and Locale chooser");
    private final JFileChooser fileChooser = new JFileChooser();
    private final Set<String> localeOptionCache = new HashSet<>();

    private final JButton rightLocaleChange = new JButton("change");
    private final JLabel rightLocaleLbl = new JLabel();

    private final JButton leftLocaleChange = new JButton("change");
    private final JLabel leftLocaleLbl = new JLabel();

    private final JButton chooseFileset = new JButton("Choose");
    private final JLabel filesetLbl = new JLabel();

    private final JButton submit = new JButton("Okay");

    public SwingResxChooser() {
        window.setLayout(new GridBagLayout());
        final Dimension size = new Dimension(500, 400);
        window.setMinimumSize(size);
        window.setSize(size);

        GridBagConstraints labelConstraints = new GridBagConstraints();
        labelConstraints.fill = GridBagConstraints.BOTH;
        labelConstraints.gridx = 0;
        labelConstraints.gridy = 0;
        labelConstraints.weightx = 0.7;

        GridBagConstraints buttonConstraints = new GridBagConstraints();
        buttonConstraints.fill = GridBagConstraints.BOTH;
        buttonConstraints.gridx = 1;
        buttonConstraints.gridy = 0;
        buttonConstraints.insets = new Insets(20, 20, 20, 20);
        buttonConstraints.weightx = 0.3;
        buttonConstraints.anchor = GridBagConstraints.EAST;

        window.add(leftLocaleLbl, labelConstraints);
        window.add(leftLocaleChange, buttonConstraints);

        labelConstraints.gridy = 1;
        buttonConstraints.gridy = 1;

        window.add(rightLocaleLbl, labelConstraints);
        window.add(rightLocaleChange, buttonConstraints);

        labelConstraints.gridy = 2;
        buttonConstraints.gridy = 2;

        window.add(filesetLbl, labelConstraints);
        window.add(chooseFileset, buttonConstraints);

        buttonConstraints.gridy = 3;

        window.add(submit, buttonConstraints);

        // unit-test related
        submit.setName("submit");
        chooseFileset.setName("fileset");
        rightLocaleChange.setName("right");
        leftLocaleChange.setName("left");

        fileChooser.setFileFilter(new FileNameExtensionFilter("Resx files", "resx"));
        fileChooser.setMultiSelectionEnabled(false);
        fileChooser.setDialogTitle("Choose a resx file kind to translate");
        fileChooser.setCurrentDirectory(Paths.get(".").toFile());
        fileChooser.setMinimumSize(SwingOverviewView.MINIMUM_WINDOW_SIZE);
        fileChooser.setSize(SwingOverviewView.MINIMUM_WINDOW_SIZE);

        submit.addActionListener(e -> completeChoice());
        rightLocaleChange.addActionListener(e -> {
            right = chooseLocale();
            rightLocaleLbl.setText("RIGHT: " + right);
        });
        leftLocaleChange.addActionListener(e -> {
            left = chooseLocale();
            leftLocaleLbl.setText("LEFT: " + left);
        });
        chooseFileset.addActionListener(e -> {
            int ret = fileChooser.showOpenDialog(null);
            if (ret == JFileChooser.APPROVE_OPTION) {
                filesetBacking = fileChooser.getSelectedFile().toPath();
                onFilesetChange();
            }
        });
    }

    @Override
    protected void onFilesetChangeInternal() {
        final Matcher filesetMatcher = FILENAME_PATTERN.matcher(filesetBacking.getFileName().toString());
        if (filesetMatcher.matches()) { // should always be true
            final String filesetName = filesetMatcher.group(1);
            filesetLbl.setText(filesetName); // group is not optional
            // drop locales we cannot have anymore...
            if (!localeOptionCache.contains(left)) {
                left = null;
                leftLocaleLbl.setText("(none)");
            }
            if (!localeOptionCache.contains(right)) {
                right = null;
                rightLocaleLbl.setText("(none)");
            }
        }
    }

    private String chooseLocale() {
        final String[] localeChoices = localeOptionCache.toArray(new String[localeOptionCache.size()]);
        int result = JOptionPane.showOptionDialog(window,
          "Choose a locale",
          "",
          JOptionPane.DEFAULT_OPTION,
          JOptionPane.QUESTION_MESSAGE,
          null,
          localeChoices,
          localeChoices[0]);
        return localeChoices[result];
    }

    @Override
    public void hide() {
        window.setVisible(false);
    }

    @Override
    public void show() {
        window.setVisible(true);
    }
}
