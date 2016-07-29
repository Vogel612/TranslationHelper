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
        ResxChooser rc = new JFXResxChooserView(rcStage, getClass().getResource("/ResxChooser.fxml"));
        Parameters params = getParameters();
        if (params.getUnnamed().size() != 0) { // should be 1..
            final Path resxFile = Paths.get(params.getUnnamed().get(0));
            rc.setFileset(resxFile);
        }
        Stage translationStage = new Stage(StageStyle.UTILITY);
        translationStage.initOwner(primaryStage);
        TranslationView tv = new JFXTranslationView(translationStage, getClass().getResource("/TranslationView.fxml"));
        OverviewView v = new JFXFilesetOverviewView(primaryStage, getClass().getResource("/FilesetOverviewView.fxml"));
        ProjectView pv = new JFXProjectView(primaryStage, getClass().getResource("/ProjectOverview.fxml"));
        FilesetOverviewModel m = new FilesetOverviewModel();
        Dialog d = new JFXDialog();
        OverviewPresenter p = new OverviewPresenter(m, v, tv, rc, d, pv);
        // Wire up all the crap
        DependencyRoot.inject(m, v, p, tv, rc, pv);

        Platform.runLater(p::show);
        Platform.runLater(p::fileChoosing);
    }
}
