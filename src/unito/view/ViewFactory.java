package unito.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import unito.ServerManager;
import unito.controller.BaseController;
import unito.controller.MainWindowController;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Abstract controller class, used to manipulate View (initialize the Window, connect the model to the View, ... )
 */
public class ViewFactory {

    private ServerManager serverManager;
    private ArrayList<Stage> activeStages;

    public ViewFactory(ServerManager serverManager) {
        this.serverManager = serverManager;
        activeStages = new ArrayList<Stage>();
    }


    /**
     * Create controller for MainWindow. Then, initialize the Main window.
     */
    public void showMainWindow(){
        System.out.println("showMainWindow() called.");
        System.out.println("initializing controller for the main window...");
        BaseController controller = new MainWindowController(serverManager, this, "MainWindow.fxml");
        initializeView(controller, "Client");
    }


    /**
     * Load fxml file, set the controller, create and show the Scene/Stage.
     * @param baseController Controller of the Stage
     */
    private void initializeView(BaseController baseController, String windowTitle){
        System.out.println("initializeView() called.\nFXMLLoader is loading the fxml file...");
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(baseController.getFxmlName()));
        fxmlLoader.setController(baseController);
        //public abstract scene...
        Parent parent;
        try {
            parent = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        Scene scene = new Scene(parent);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle(windowTitle);
        stage.show();
        System.out.println("Done! Showing the window \nadding stage to activeStages...\n\n");
        activeStages.add(stage);
    }

    public void closeStage(Stage stageToClose) {
        activeStages.remove(stageToClose);
        stageToClose.close();
    }

}
