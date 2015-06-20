package de.vogel612.helper;

import java.nio.file.Path;
import java.nio.file.Paths;

import de.vogel612.helper.ui.OverviewModel;
import de.vogel612.helper.ui.OverviewPresenter;
import de.vogel612.helper.ui.OverviewView;
import de.vogel612.helper.ui.impl.OverviewModelImpl;
import de.vogel612.helper.ui.impl.OverviewPresenterImpl;
import de.vogel612.helper.ui.impl.OverviewViewImpl;

public class Main {
	private static final String RUBBERDUCK_PATH = "RetailCoder.VBE/UI";
	private static final String ARGUMENT_MISMATCH = "Arguments do not match up. Please provide one single path to read the Rubberduck resx from";
	private static final String ILLEGAL_FOLDER = "Rubberduck .resx files can only be found under RetailCoder.VBE/UI. Please give a path that points to a Rubberduck UI folder";

	public static void main(final String[] args) {
		// parsing the first argument given into a proper path to load the resx
		// from
		if (args.length != 1) {
			// don't even bother!
			System.out.println(ARGUMENT_MISMATCH);
			return;
		}
		Path resxFolder = Paths.get(args[0]);
		resxFolder = resxFolder.normalize();

		if (!resxFolder.endsWith(RUBBERDUCK_PATH)) {
			System.out.println(ILLEGAL_FOLDER);
			return;
		}

		OverviewModel m = new OverviewModelImpl();
		OverviewView v = new OverviewViewImpl();

		OverviewPresenter p = new OverviewPresenterImpl(m, v);
		p.initialize();
		p.loadFiles(resxFolder);
		p.show();
	}

}
