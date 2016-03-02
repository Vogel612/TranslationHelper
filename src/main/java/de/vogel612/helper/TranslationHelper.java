package de.vogel612.helper;

import de.vogel612.helper.data.OverviewModel;
import de.vogel612.helper.ui.OverviewPresenter;
import de.vogel612.helper.ui.OverviewView;
import de.vogel612.helper.ui.ResxChooser;
import de.vogel612.helper.ui.jfx.JFXOverviewView;
import de.vogel612.helper.ui.jfx.JFXResxChooserController;
import de.vogel612.helper.ui.TranslationPresenter;
import de.vogel612.helper.ui.jfx.JFXResxChooserView;

import java.nio.file.Path;
import java.nio.file.Paths;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
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
        ResxChooser rc = new JFXResxChooserView(primaryStage, getClass().getResource("/ResxChooser.fxml"));
        Parameters params = getParameters();
        if (params.getUnnamed().size() != 0) { // should be 1..
            final Path resxFile = Paths.get(params.getUnnamed().get(0));
            rc.setFileset(resxFile);
        }

        // That's a view though.. duh.
        TranslationPresenter tp = new TranslationPresenter();
        OverviewView v = new JFXOverviewView(primaryStage, getClass().getResource("/OverviewView.fxml"));

        OverviewModel m = new OverviewModel();
        OverviewPresenter p = new OverviewPresenter(m, v, tp, rc); // just to stop the compiler from screaming at me

        // Wire up all the crap
        v.addLanguageRequestListener(p::fileChoosing);
        v.addSaveRequestListener(p::onSaveRequest);
        v.addTranslationRequestListener(p::onTranslateRequest);
        v.addWindowClosingListener(p::onWindowCloseRequest);

        m.addParseCompletionListener(p::onParseCompletion);

        tp.addTranslationAbortListener(p::onTranslationAbort);
        tp.addTranslationSubmitListener(p::onTranslationSubmit);

        rc.addCompletionListener(p::fileChoiceCompletion);

        p.show();
        p.fileChoosing();
    }
}
