package de.vogel612.helper;

import de.vogel612.helper.ui.*;
import de.vogel612.helper.data.OverviewModel;
import de.vogel612.helper.ui.swing.SwingDialog;
import de.vogel612.helper.ui.swing.SwingOverviewView;
import de.vogel612.helper.ui.swing.SwingResxChooser;
import de.vogel612.helper.ui.swing.SwingTranslationView;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    // protected for testing verifications
    static final String ARGUMENT_MISMATCH = "Arguments do not match up. Please provide no more than a Path to the intended fileset";

    private Main() {
    }

    public static void main(final String[] args) {
        // parsing the first argument given into a proper path to load the resx
        // from
        if (args.length > 1) {
            // don't even bother!
            System.out.println(ARGUMENT_MISMATCH);
            return;
        }

        TranslationView tv = new SwingTranslationView();
        ResxChooser rc = new SwingResxChooser();
        if (args.length != 0) {
            final Path resxFile = Paths.get(args[0]);
            rc.setFileset(resxFile);
        }

        OverviewModel m = new OverviewModel();
        OverviewView v = new SwingOverviewView();
        Dialog d = new SwingDialog();
        OverviewPresenter p = new OverviewPresenter(m, v, tv, rc, d);

        DependencyRoot.inject(m, v, p, tv, rc);

        p.show();
        p.fileChoosing();
    }

}
