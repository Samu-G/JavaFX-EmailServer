package unito.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import unito.ServerManager;
import unito.controller.BaseController;
import unito.controller.MainWindowController;
import java.util.ArrayList;

/**
 * Classe usata per manipolare la view (inizializzare le Window, connettere il controller alla View, ... )
 */
public class ViewManager {

    private final ServerManager serverManager;
    private final ArrayList<Stage> activeStages;
    public MainWindowController mainWindowController;

    /**
     * @param serverManager
     */
    public ViewManager(ServerManager serverManager) {
        this.serverManager = serverManager;
        activeStages = new ArrayList<Stage>();
    }

    /**
     * Crea il controller per la MainWindow. Poi, inizializza la finestra principale
     */
    public void showMainWindow() {
        System.out.println("showMainWindow() called.");
        mainWindowController = new MainWindowController(serverManager, this, "MainWindow.fxml");
        initializeView(mainWindowController, "Server");
    }

    private void initializeView(BaseController baseController, String windowTitle) {
        System.out.println("initializeView() called.");
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(baseController.getFxmlName()));
        fxmlLoader.setController(baseController);
        Parent parent;
        try {
            parent = fxmlLoader.load();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        Scene scene = new Scene(parent);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle(windowTitle);
        stage.show();
        activeStages.add(stage);
    }

    /**
     * Chiude una finestra rimuovendola dalla lista activeStages
     *
     * @param stageToClose finestra da chiudere
     */
    public void closeStage(Stage stageToClose) {
        activeStages.remove(stageToClose);
        stageToClose.close();
    }

    /**
     * Crea un alert per informare il Client
     *
     * @param title titolo dell'alert
     * @param contentText contenuto dell'alert
     */
    public static void viewAlert(String title, String contentText) {
        System.out.println("viewAlert() called.");
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(contentText);
        alert.showAndWait();
    }

}
