package de.vogel612.helper;

import de.vogel612.helper.ui.*;
import de.vogel612.helper.data.OverviewModel;
import de.vogel612.helper.ui.swing.SwingOverviewView;
import de.vogel612.helper.ui.swing.SwingResxChooser;

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

        TranslationPresenter tp = new TranslationPresenter();
        ResxChooser rc = new SwingResxChooser();
        if (args.length != 0) {
            final Path resxFile = Paths.get(args[0]);
            rc.setFileset(resxFile);
        }

        OverviewModel m = new OverviewModel();
        OverviewView v = new SwingOverviewView();
        OverviewPresenter p = new OverviewPresenter(m, v, tp, rc);

        // Wire up all the crap
        v.addLanguageRequestListener(p::fileChoosing);
        v.addSaveRequestListener(p::onSaveRequest);
        v.addTranslationRequestListener(p::onTranslateRequest);
        v.addWindowClosingListener(p::onWindowCloseRequest);

        m.addParseCompletionListener(p::onParseCompletion);

        tp.addTranslationAbortListener(p::onTranslationAbort);
        tp.addTranslationSubmitListener(p::onTranslationSubmit);

        rc.addCompletionListener(p::fileChoiceCompletion);

        p.show();
        p.fileChoosing();
    }

}
