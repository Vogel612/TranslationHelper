package de.vogel612.helper;

import de.vogel612.helper.data.FilesetModel;
import de.vogel612.helper.ui.*;
import de.vogel612.helper.ui.jfx.*;

import java.io.IOException;
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
            System.err.println(ARGUMENT_MISMATCH);
            return;
        }
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        // FIXME show project or resx overview depending on argument
        // Parameters params = getParameters();
        // if (params.getUnnamed().size() != 0) { // must be 1..
        //     final Path resxFile = Paths.get(params.getUnnamed().get(0));
        //     rc.setFileset(resxFile);
        // }

        Stage overviewStage = new Stage(StageStyle.DECORATED);
        overviewStage.initOwner(primaryStage);

        TranslationView tv = prepareTranslationView(overviewStage);
        LocaleChooser rc = prepareLocaleChooser(overviewStage);

        OverviewView v = new JFXTranslationOverviewView(rc, new FilesetModel(), tv, overviewStage, getClass().getResource("/TranslationOverviewView.fxml"));
        ProjectView pv = new JFXProjectView(primaryStage, getClass().getResource("/ProjectOverview.fxml"));
        OverviewPresenter p = new OverviewPresenter(v, pv);

        Platform.runLater(p::show);
        Platform.runLater(p::fileChoosing);
    }

    private LocaleChooser prepareLocaleChooser(Stage overviewStage) throws IOException {
        Stage rcStage = new Stage(StageStyle.UTILITY);
        rcStage.initOwner(overviewStage);
        return new JFXLocaleChooserView(rcStage, getClass().getResource("/LocaleChooser.fxml"));
    }

    private TranslationView prepareTranslationView(Stage overviewStage) throws IOException {
        Stage translationStage = new Stage(StageStyle.UTILITY);
        translationStage.initOwner(overviewStage);
        return new JFXTranslationView(translationStage, getClass().getResource("/TranslationView.fxml"));
    }
}
