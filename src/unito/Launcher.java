package unito;

import javafx.application.Application;
import javafx.stage.Stage;
import unito.model.EmailBean;
import unito.controller.persistence.PersistenceAccess;
import unito.view.ViewFactory;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Launcher extends Application {

    private static final int NUM_OF_THREAD = 4;

    //loading from persistence all email Beans
    private PersistenceAccess persistenceAccess = new PersistenceAccess();
    //protected List<EmailBean> emailBeans = persistenceAccess.loadFromPersistenceEmailBean();
    protected List<EmailBean> emailBeans = PersistenceAccess.exampleEmailBean();

    //init ThreadPool
    private ExecutorService serverThreadPool = Executors.newFixedThreadPool(NUM_OF_THREAD);

    //init model
    private ServerManager serverManager = new ServerManager(emailBeans, serverThreadPool, this);

    @Override
    public void start(Stage primaryStage) throws Exception {
        //init view
        ViewFactory viewFactory = new ViewFactory(serverManager);
        viewFactory.showMainWindow();

    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void stop() throws Exception {
        //Salvataggio del file di persistenza
        persistenceAccess.saveEmailBeanToPersistence(emailBeans);
        //stop listenerService
        serverManager.listenerService.closeServerSocket();
        //stop ThreadPool
        while (!serverThreadPool.isTerminated()) {
            serverThreadPool.shutdown();
        }
        System.exit(0);
    }

    static void setUpPipeOutput(PipedInputStream pipeIn1, PipedInputStream pipeIn2) {
        try {
            PipedOutputStream pout = new PipedOutputStream(pipeIn1);
            System.setOut(new PrintStream(pout, true));
        } catch (IOException | SecurityException e) {
            e.printStackTrace();
        }

        try {
            PipedOutputStream pout2 = new PipedOutputStream(pipeIn2);
            System.setErr(new PrintStream(pout2, true));
        } catch (IOException | SecurityException e) {
            e.printStackTrace();
        }

        //ReaderThread obj = new ReaderThread(pipeIn, pipeIn2, errorThrower, reader, reader2, quit, txtArea);

    }


}