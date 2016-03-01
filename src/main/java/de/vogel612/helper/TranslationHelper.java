package de.vogel612.helper;

import de.vogel612.helper.data.OverviewModel;
import de.vogel612.helper.ui.OverviewPresenter;
import de.vogel612.helper.ui.OverviewView;
import de.vogel612.helper.ui.jfx.JFXResxChooser;
import de.vogel612.helper.ui.swing.SwingOverviewView;
import de.vogel612.helper.ui.TranslationPresenter;

import java.nio.file.Path;
import java.nio.file.Paths;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
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
        // FIXME parse argument and pass it to JFXResxChooser
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ResxChooser.fxml"));

        GridPane rcPane = loader.load();
        JFXResxChooser rc = loader.getController();
        Parameters params = getParameters();
        if (params.getUnnamed().size() != 0) { // should be 1..
            final Path resxFile = Paths.get(params.getUnnamed().get(0));
            rc.setFileset(resxFile);
        }

        TranslationPresenter tp = new TranslationPresenter();

        OverviewModel m = new OverviewModel();
        OverviewView v = new SwingOverviewView();
        OverviewPresenter p = new OverviewPresenter(m, v, tp, rc);

        p.show();
        p.fileChoosing();


        primaryStage.setTitle("FXML JFXResxChooser");
        Scene myScene = new Scene(rcPane);
        primaryStage.setScene(myScene);
        primaryStage.show();
    }
}
