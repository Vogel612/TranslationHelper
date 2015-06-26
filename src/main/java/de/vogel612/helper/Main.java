package de.vogel612.helper;

import java.nio.file.Path;
import java.nio.file.Paths;

import de.vogel612.helper.ui.OverviewModel;
import de.vogel612.helper.ui.OverviewPresenter;
import de.vogel612.helper.ui.OverviewView;
import de.vogel612.helper.ui.TranslationPresenter;
import de.vogel612.helper.ui.impl.OverviewModelImpl;
import de.vogel612.helper.ui.impl.OverviewPresenterImpl;
import de.vogel612.helper.ui.impl.OverviewViewImpl;
import de.vogel612.helper.ui.impl.TranslationPresenterImpl;

public class Main {
	public static final String RUBBERDUCK_PATH = "RetailCoder.VBE/UI";
	public static final String ARGUMENT_MISMATCH = "Arguments do not match up. Please provide one single path to read the Rubberduck resx from";
	public static final String ILLEGAL_FOLDER = "Rubberduck .resx files can only be found under RetailCoder.VBE/UI. Please give a path that points to a Rubberduck UI folder";

	public static void main(final String[] args) {
		// parsing the first argument given into a proper path to load the resx
		// from
		if (args.length != 1) {
			// don't even bother!
			System.out.println(ARGUMENT_MISMATCH);
			return;
		}
		Path resxFolder = Paths.get(args[0]);
		// normalize path to allow checking
		resxFolder = resxFolder.normalize();

		if (!resxFolder.endsWith(RUBBERDUCK_PATH)) {
			System.out.println(ILLEGAL_FOLDER);
			return;
		}

		TranslationPresenter tp = new TranslationPresenterImpl();
		OverviewModel m = new OverviewModelImpl();
		OverviewView v = new OverviewViewImpl();

		OverviewPresenter p = new OverviewPresenterImpl(m, v, tp);
		p.initialize();
		p.loadFiles(resxFolder, OverviewPresenter.DEFAULT_ROOT_LOCALE,
				OverviewPresenter.DEFAULT_TARGET_LOCALE);
		p.show();
	}

}
