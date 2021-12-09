package unito.controller;

import unito.ServerManager;
import unito.view.ViewFactory;

/**
 * Questa è una classe astratta che esplicita la base dei controller
 */
public abstract class BaseController {

    private final ServerManager serverManager;
    private final ViewFactory viewFactory;
    private final String fxmlName;

    /**
     * @param serverManager
     * @param viewFactory abstract view controller
     * @param fxmlName fxml file path of this controller
     */
    public BaseController(ServerManager serverManager, ViewFactory viewFactory, String fxmlName) {
        this.serverManager = serverManager;
        this.viewFactory = viewFactory;
        this.fxmlName = fxmlName;
    }

    /**
     * @return il nome del file .fxml associato al controller
     */
    public String getFxmlName() {
        return fxmlName;
    }

}