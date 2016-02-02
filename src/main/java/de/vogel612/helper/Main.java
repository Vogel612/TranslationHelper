package de.vogel612.helper;

import static de.vogel612.helper.ui.OverviewPresenter.DEFAULT_ROOT_LOCALE;
import static de.vogel612.helper.ui.OverviewPresenter.DEFAULT_TARGET_LOCALE;

import de.vogel612.helper.data.Side;
import de.vogel612.helper.ui.*;
import de.vogel612.helper.data.OverviewModel;
import de.vogel612.helper.ui.ResxChooser.ResxChooserEvent;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Predicate;

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

        TranslationPresenter tp = new TranslationPresenter();
        ResxChooser rc = new ResxChooser();
        if (args.length != 0) {
            final Path resxFile = Paths.get(args[0]);
            rc.setFileset(resxFile);
        }

        OverviewModel m = new OverviewModel();
        OverviewView v = new SwingOverviewView();
        OverviewPresenter p = new OverviewPresenter(m, v, tp, rc);

        p.show();
        p.fileChoosing();
    }

}
