package de.vogel612.helper;

import de.vogel612.helper.ui.ProjectView;
import de.vogel612.helper.ui.jfx.JFXProjectView;
import javafx.application.Application;
import javafx.stage.Stage;

import java.nio.file.Paths;

/**
 * Created by vogel612 on 29.07.16.
 */
public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        ProjectView check = new JFXProjectView(stage, getClass().getResource("/ProjectOverview.fxml"));
        check.loadProject(Paths.get(getParameters().getRaw().get(0)));
        check.show();
    }
}
