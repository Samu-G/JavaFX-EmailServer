package unito;

import javafx.application.Application;
import javafx.stage.Stage;
import unito.controller.persistence.PersistenceAccess;
import unito.view.ViewFactory;

public class Launcher extends Application {

    public static final int NUM_OF_THREAD = 4;

    //init model
    private final ServerManager serverManager = new ServerManager(PersistenceAccess.loadFromPersistenceEmailBean());

    //init view
    private final ViewFactory viewFactory = new ViewFactory(serverManager);

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        serverManager.setViewFactory(viewFactory);
        viewFactory.showMainWindow();
        serverManager.listenerServiceThread.start();
    }

    @Override
    public void stop() throws Exception {
        PersistenceAccess.saveEmailBeanToPersistence(serverManager.emailBeans);
        serverManager.stopListenerServiceThread();
        serverManager.stopThreadPool();
        System.exit(0);
    }
}