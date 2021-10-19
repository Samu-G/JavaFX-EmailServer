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
import unito.ServerManager;
import unito.view.ViewFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.ResourceBundle;

public class MainWindowController extends BaseController implements Initializable {

    @FXML
    private TextArea console;

    @FXML
    private Button startButton;

    @FXML
    private Button stopButton;

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

    @FXML
    void getConnectedClientAction(ActionEvent event) {

    }

    @FXML
    void startServerAction(ActionEvent event) {

        // these will be redirected to textArea on GUI
        /*System.err.println("@@@@ERROR: This is error");
        System.out.println("####OUTPUT : THIS IS ERROR");*/

        printConsole("@@@@ERROR: This is error" +
                "\n####OUTPUT : THIS IS ERROR");
    }

    @FXML
    void stopServerAction(ActionEvent event) {

    }

    @FXML
    void printConsole(String str) {
        System.out.println(str);
        console.appendText(str);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        staticTxtArea = console;
    }

}
