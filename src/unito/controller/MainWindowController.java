package unito.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import unito.ServerManager;
import unito.view.ViewManager;

/**
 * Classe del controller utilizzato per la finestra principale dove viene mostrata la console del server
 */
public class MainWindowController extends BaseController {

    @FXML
    private TextArea console;

    /**
     * @param serverManager riferimento al ServerManger dell'applicazione
     * @param viewManager   riferimento al ViewManager dell'applicazione
     * @param fxmlName      path del file .fxml
     */
    public MainWindowController(ServerManager serverManager, ViewManager viewManager, String fxmlName) {
        super(serverManager, viewManager, fxmlName);
    }

    /**
     * Scrive sulla console grafica del server il parametro passato a str
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
