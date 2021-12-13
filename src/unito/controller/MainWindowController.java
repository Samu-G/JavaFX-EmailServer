package unito.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import unito.ServerManager;
import unito.view.ViewManager;

public class MainWindowController extends BaseController {

    @FXML
    private TextArea console;

    /**
     * @param serverManager
     * @param viewManager   abstract view controller
     * @param fxmlName      fxml file path of this controller
     */
    public MainWindowController(ServerManager serverManager, ViewManager viewManager, String fxmlName) {
        super(serverManager, viewManager, fxmlName);
    }

    public void printOnConsole(String str) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                console.appendText("\n");
                console.appendText(str);
            }
        });
    }

}
