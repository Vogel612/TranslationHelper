package de.vogel612.helper.ui;

import static de.vogel612.helper.data.util.DataUtilities.FILENAME_PATTERN;

import de.vogel612.helper.data.util.DataUtilities;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * View to select a set of Resx-Files and locales.
 */
public class ResxChooser {

    private final JFrame window = new JFrame("Translation Helper - File and Locale chooser");
    private final JFileChooser fileChooser = new JFileChooser();
    private final Set<String> localeOptionCache = new HashSet<>();

    private final JButton rightLocaleChange = new JButton("change");
    private final JLabel rightLocaleLbl = new JLabel();
    private String rightLocale;

    private final JButton leftLocaleChange = new JButton("change");
    private final JLabel leftLocaleLbl = new JLabel();
    private String leftLocale;

    private final JButton chooseFileset = new JButton("Choose");
    private final JLabel filesetLbl = new JLabel();
    private Path fileset;

    private final JButton submit = new JButton("Okay");
    private final Set<Consumer<ResxChooserEvent>> resxChoiceCompletionListener = new HashSet<>();


    public ResxChooser() {
        window.setLayout(new GridLayout(4, 2, 10, 10));
        final Dimension size = new Dimension(500, 400);
        window.setMinimumSize(size);
        window.setSize(size);

        window.add(leftLocaleLbl);
        window.add(leftLocaleChange);
        window.add(rightLocaleLbl);
        window.add(rightLocaleChange);
        window.add(filesetLbl);
        window.add(chooseFileset);
        window.add(submit);

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

        submit.addActionListener(e -> {
            if (fileset != null && leftLocale != null && rightLocale != null) {
                final ResxChooserEvent event = new ResxChooserEvent(this);
                resxChoiceCompletionListener.forEach(c -> c.accept(event));
            }
        });
        rightLocaleChange.addActionListener(e -> {
            rightLocale = chooseLocale();
            rightLocaleLbl.setText("RIGHT: " + rightLocale);
        });
        leftLocaleChange.addActionListener(e -> {
            leftLocale = chooseLocale();
            leftLocaleLbl.setText("LEFT: " + leftLocale);
        });
        chooseFileset.addActionListener(e -> {
            int ret = fileChooser.showOpenDialog(null);
            if (ret == JFileChooser.APPROVE_OPTION) {
                fileset = fileChooser.getSelectedFile().toPath();
                onFilesetChange();
            }
        });
    }

    public void setFileset(Path fileset) {
        if (fileset == null) {
            throw new IllegalArgumentException();
        }
        this.fileset = fileset;
        onFilesetChange();
    }

    private void onFilesetChange() {
        final Matcher filesetMatcher = FILENAME_PATTERN.matcher(fileset.getFileName().toString());
        if (filesetMatcher.matches()) { // should always be true
            final String filesetName = filesetMatcher.group(1);
            filesetLbl.setText(filesetName); // group is not optional

            try (final Stream<Path> filesetFiles = DataUtilities.streamFileset(fileset.getParent(), filesetName)) {
                localeOptionCache.clear();
                localeOptionCache.addAll(filesetFiles.map(
                  DataUtilities::parseLocale).collect(
                  Collectors.toSet()));
                // drop locales we cannot have anymore...
                if (!localeOptionCache.contains(leftLocale)) {
                    leftLocale = null;
                    leftLocaleLbl.setText("(none)");
                }
                if (!localeOptionCache.contains(rightLocale)) {
                    rightLocale = null;
                    rightLocaleLbl.setText("(none)");
                }
            } catch (IOException e1) {
                // FIXME handle
                e1.printStackTrace();
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

    public void hide() {
        window.setVisible(false);
    }

    public void show() {
        window.setVisible(true);
    }

    public void addCompletionListener(Consumer<ResxChooserEvent> listener) {
        resxChoiceCompletionListener.add(listener);
    }

    /**
     * An event signaling the choice of fileset and locale is completed
     */
    public static class ResxChooserEvent {
        private final Path fileset;
        private final String rightLocale;
        private final String leftLocale;

        public ResxChooserEvent(ResxChooser c) {
            this.leftLocale = c.leftLocale;
            this.rightLocale = c.rightLocale;
            this.fileset = c.fileset;
        }

        public String getLeftLocale() {
            return leftLocale;
        }

        public String getRightLocale() {
            return rightLocale;
        }

        public Path getFileset() {
            return fileset;
        }
    }
}
