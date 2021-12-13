package unito.controller;

import unito.ServerManager;
import unito.view.ViewManager;

/**
 * Questa è una classe astratta che esplicita la base dei controller
 */
public abstract class BaseController {

    protected final ServerManager serverManager;
    protected ViewManager viewManager;
    private final String fxmlName;

    /**
     * @param serverManager riferimento al ServerManger dell'applicazione
     * @param viewManager riferimento al ViewManager dell'applicazione
     * @param fxmlName path del file .fxml
     */
    public BaseController(ServerManager serverManager, ViewManager viewManager, String fxmlName) {
        this.serverManager = serverManager;
        this.viewManager = viewManager;
        this.fxmlName = fxmlName;
    }

    /**
     * @return il nome del file .fxml associato al controller
     */
    public String getFxmlName() {
        return fxmlName;
    }

}