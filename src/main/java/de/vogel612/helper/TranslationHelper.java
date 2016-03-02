package de.vogel612.helper;

import de.vogel612.helper.data.OverviewModel;
import de.vogel612.helper.ui.*;
import de.vogel612.helper.ui.jfx.JFXOverviewView;
import de.vogel612.helper.ui.jfx.JFXResxChooserView;
import de.vogel612.helper.ui.jfx.JFXTranslationView;
import de.vogel612.helper.ui.swing.SwingTranslationView;

import java.nio.file.Path;
import java.nio.file.Paths;

import javafx.application.Application;
import javafx.stage.Stage;

public class TranslationHelper extends Application {

    static final String ARGUMENT_MISMATCH = "Arguments do not match up. Please provide no more than a Path to the intended fileset";


    public static void main(String[] args) {

        if (args.length > 1) {
            // don't even bother!
            System.out.println(ARGUMENT_MISMATCH);
            return;
        }
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        ResxChooser rc = new JFXResxChooserView(primaryStage, getClass().getResource("/ResxChooser.fxml"));
        Parameters params = getParameters();
        if (params.getUnnamed().size() != 0) { // should be 1..
            final Path resxFile = Paths.get(params.getUnnamed().get(0));
            rc.setFileset(resxFile);
        }
        TranslationView tv = new JFXTranslationView(primaryStage, getClass().getResource("/TranslationView.fxml"));
        OverviewView v = new JFXOverviewView(primaryStage, getClass().getResource("/OverviewView.fxml"));

        OverviewModel m = new OverviewModel();
        OverviewPresenter p = new OverviewPresenter(m, v, tv, rc); // just to stop the compiler from screaming at me

        // Wire up all the crap
        DependencyRoot.inject(m, v, p, tv, rc);

        p.show();
        p.fileChoosing();
    }
}
