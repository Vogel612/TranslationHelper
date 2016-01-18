package de.vogel612.helper;

import de.vogel612.helper.data.Side;
import de.vogel612.helper.ui.OverviewView;
import de.vogel612.helper.ui.OverviewModel;
import de.vogel612.helper.ui.OverviewPresenter;
import de.vogel612.helper.ui.SwingOverviewView;
import de.vogel612.helper.ui.TranslationPresenter;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    // FIXME: Don't rely on this!
    private static final String RUBBERDUCK_PATH = "RetailCoder.VBE/UI";
    // protected for testing verifications
    protected static final String ARGUMENT_MISMATCH = "Arguments do not match up. Please provide one single path to read the Rubberduck resx from";
    protected static final String ILLEGAL_FOLDER = "Rubberduck .resx files can only be found under RetailCoder.VBE/UI. Please give a path that points to a Rubberduck UI folder";

    private Main() {
    }

    public static void main(final String[] args) {
        // parsing the first argument given into a proper path to load the resx
        // from
        if (args.length != 1 && args.length != 3) {
            // don't even bother!
            System.out.println(ARGUMENT_MISMATCH);
            return;
        }
        Path resxFolder = Paths.get(args[0]);
        // normalize path to allow checking
        resxFolder = resxFolder.normalize();

        // TODO: Check if we really need this...
        if (!resxFolder.endsWith(RUBBERDUCK_PATH)) {
            System.out.println(ILLEGAL_FOLDER);
            return;
        }

        TranslationPresenter tp = new TranslationPresenter();
        OverviewModel m = new OverviewModel();
        OverviewView v = new SwingOverviewView();

        OverviewPresenter p = new OverviewPresenter(m, v, tp);
        p.initialize();
        p.loadFiles(resxFolder);
        // set the selected locales if they were specified on commandline
        // check whether they are available before that and fall back if they aren't
        if (args.length == 3) {
            final String leftLocale = args[1];
            final String rightLocale = args[2];
            if (m.getAvailableLocales().contains(leftLocale)
              && m.getAvailableLocales().contains(rightLocale)) {
                p.onLocaleRequest(leftLocale, Side.LEFT);
                p.onLocaleRequest(rightLocale, Side.RIGHT);
            }
            // "fallback"
        }
        p.show();
    }

}
