package de.vogel612.helper;

import de.vogel612.helper.data.FilesetOverviewModel;
import de.vogel612.helper.ui.*;
import de.vogel612.helper.ui.jfx.*;

import java.nio.file.Path;
import java.nio.file.Paths;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

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
        Stage rcStage = new Stage(StageStyle.UTILITY);
        rcStage.initOwner(primaryStage);
        LocaleChooser rc = new JFXLocaleChooserView(rcStage, getClass().getResource("/LocaleChooser.fxml"));
        Parameters params = getParameters();
        if (params.getUnnamed().size() != 0) { // should be 1..
            final Path resxFile = Paths.get(params.getUnnamed().get(0));
            // FIXME show project or resx overview depending on argument
//            rc.setFileset(resxFile);
        }

        Stage translationStage = new Stage(StageStyle.UTILITY);
        translationStage.initOwner(primaryStage);
        TranslationView tv = new JFXTranslationView(translationStage, getClass().getResource("/TranslationView.fxml"));

        OverviewView v = new JFXFilesetOverviewView(primaryStage, getClass().getResource("/FilesetOverviewView.fxml"));
        FilesetOverviewModel m = new FilesetOverviewModel();
        OverviewPresenter p = new OverviewPresenter(m, v, tv, rc);

        ProjectView pv = new JFXProjectView(primaryStage, getClass().getResource("/ProjectOverview.fxml"));
        // Wire up all the crap
        DependencyRoot.inject(m, v, p, tv, rc);

        Platform.runLater(p::show);
        Platform.runLater(p::fileChoosing);
    }
}
