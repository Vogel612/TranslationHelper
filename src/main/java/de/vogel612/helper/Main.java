package de.vogel612.helper;

import java.nio.file.Path;
import java.nio.file.Paths;

import de.vogel612.helper.ui.OverviewModel;
import de.vogel612.helper.ui.OverviewPresenter;
import de.vogel612.helper.ui.OverviewView;
import de.vogel612.helper.ui.impl.OverviewPresenterImpl;

public class Main {
	private static final String FILE_ENDING = "resx";
	private static final String ARGUMENT_MISMATCH = "Arguments do not match up. Please provide one single path to read the Rubberduck resx from";
	private static final String ILLEGAL_FILE = "The file provided is not a .resx File. I doubt this is a Rubberduck translation";

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
		
		// Don't judge me... I know I shouldn't rely on extensions.
		if (!resxFile.endsWith(FILE_ENDING)) {
			System.out.println(ILLEGAL_FILE);
			System.exit(-1);
		}

		OverviewModel m = null;
		OverviewView v = null;

		OverviewPresenter p = new OverviewPresenterImpl(m, v);
		p.loadFile(resxFile);
		p.show();
	}

}
