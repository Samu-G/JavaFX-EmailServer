package unito.controller;

/**
 * Sample Skeleton for 'MainWindow.fxml' Controller Class
 */

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import unito.Launcher;
import unito.ServerManager;
import unito.view.ViewFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class MainWindowController extends BaseController implements Initializable {

    @FXML
    private TextArea console;

    @FXML
    private Button getConnectedClientButton;

    public static TextArea staticTxtArea;

    /**
     * @param serverManager
     * @param viewFactory   abstract view controller
     * @param fxmlName      fxml file path of this controller
     */
    public MainWindowController(ServerManager serverManager, ViewFactory viewFactory, String fxmlName) {
        super(serverManager, viewFactory, fxmlName);
    }

    public void printOnConsole(String str) {
        //System.out.println(str);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                console.appendText("\n");
                console.appendText(str);
            }
        });
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        staticTxtArea = console;
    }

}
