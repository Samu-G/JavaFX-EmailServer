package unito.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import unito.ServerManager;
import unito.view.ViewFactory;

/**
 * Classe del controller utilizzato per la finestra principale dove viene mostrata la console del server
 */
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

    /**
     * Scrive sulla console grafica del server il paramentro passato a str
     *
     * @param str la stringa da scrivere sulla console del Server
     */
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
