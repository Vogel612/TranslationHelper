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
	private static final String ILLEGAL_FILE = "Rubberduck .resx files can be found under RetailCoder.VBE/UI";

	public static void main(String[] args) {
		// parsing the first argument given into a proper path to load the resx
		// from
		if (args.length != 1) {
			// don't even bother!
			System.out.println(ARGUMENT_MISMATCH);
			System.exit(-1);
		}
		Path resxFile = Paths.get(args[0]);
		resxFile = resxFile.normalize();
		
		// FIXME: this relies on the path to be given, not the file
		// Don't judge me... I know I shouldn't rely on extensions.
		// if (!resxFile.endsWith(RUBBERDUCK_PATH)) {
		// System.out.println(ILLEGAL_FILE);
		// System.exit(-1);
		// }

		OverviewModel m = new OverviewModelImpl();
		OverviewView v = new OverviewViewImpl();

		OverviewPresenter p = new OverviewPresenterImpl(m, v);
		p.initialize();
		p.loadFile(resxFile);
		p.show();
	}

}
