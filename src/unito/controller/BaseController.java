package unito.controller;

import unito.ServerManager;
import unito.view.ViewManager;

/**
 * Questa è una classe astratta che esplicita la base dei controller
 */
public abstract class BaseController {

    private final ServerManager serverManager;
    private final ViewManager viewManager;
    private final String fxmlName;

    /**
     * @param serverManager
     * @param viewManager abstract view controller
     * @param fxmlName fxml file path of this controller
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