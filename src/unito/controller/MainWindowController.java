package unito.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import unito.ServerManager;
import unito.view.ViewFactory;

public class MainWindowController extends BaseController {

    @FXML
    private TextArea console;

    /**
     * @param serverManager
     * @param viewFactory   abstract view controller
     * @param fxmlName      fxml file path of this controller
     */
    public MainWindowController(ServerManager serverManager, ViewFactory viewFactory, String fxmlName) {
        super(serverManager, viewFactory, fxmlName);
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
