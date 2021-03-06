package unito;

import javafx.application.Application;
import javafx.stage.Stage;
import unito.controller.persistence.PersistenceAccess;
import unito.view.ViewManager;

/**
 * Classe usata per avviare l'applicazione
 */
public class Launcher extends Application {

    public static final int NUM_OF_THREAD = 4;

    //init application manager
    private final ServerManager serverManager = new ServerManager(PersistenceAccess.loadFromPersistenceEmailBean());

    //init view manager
    private final ViewManager viewManager = new ViewManager(serverManager);

    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Avvia l'applicazione
     *
     * @param primaryStage
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        serverManager.setViewManager(viewManager);
        serverManager.getListenerServiceThread().start();
        viewManager.showMainWindow();
    }

    /**
     * Chiude l'applicazione
     *
     * @throws Exception
     */
    @Override
    public void stop() throws Exception {
        PersistenceAccess.saveEmailBeanToPersistence(serverManager.getEmailBeans());
        serverManager.stopListenerServiceThread();
        serverManager.stopThreadPool();
    }
}