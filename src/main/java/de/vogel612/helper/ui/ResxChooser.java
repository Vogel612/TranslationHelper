package de.vogel612.helper.ui;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

/**
 * View to select a set of Resx-Files and locales.
 */
public class ResxChooser {

    private final JFrame window = new JFrame("Translation Helper - File and Locale chooser");

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

        window.add(leftLocaleLbl);
        window.add(leftLocaleChange);
        window.add(rightLocaleLbl);
        window.add(rightLocaleChange);
        window.add(filesetLbl);
        window.add(chooseFileset);
        window.add(submit);

        submit.addActionListener(e -> resxChoiceCompletionListener.forEach(c -> c.accept(new ResxChooserEvent(this))));

    }

    /**
     * An event signaling the choice of fileset and locale is completed
     */
    public static class ResxChooserEvent {
        private final Path fileset;
        private final String rightLocale;
        private final String leftLocale;

        private ResxChooserEvent(ResxChooser c) {
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
