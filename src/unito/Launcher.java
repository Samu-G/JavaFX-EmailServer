package unito;

import javafx.application.Application;
import javafx.stage.Stage;
import unito.controller.persistence.PersistenceAccess;
import unito.view.ViewManager;

public class Launcher extends Application {

    public static final int NUM_OF_THREAD = 4;

    //init application manager
    private final ServerManager serverManager = new ServerManager(PersistenceAccess.loadFromPersistenceEmailBean());

    //init view manager
    private final ViewManager viewManager = new ViewManager(serverManager);

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        serverManager.setViewFactory(viewManager);
        viewManager.showMainWindow();
        serverManager.getListenerServiceThread().start();
    }

    @Override
    public void stop() throws Exception {
        PersistenceAccess.saveEmailBeanToPersistence(serverManager.getEmailBeans());
        serverManager.stopListenerServiceThread();
        serverManager.stopThreadPool();
        System.exit(0);
    }
}