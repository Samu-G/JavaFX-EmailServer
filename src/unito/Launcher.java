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

    public static final int NUM_OF_THREAD = 4;

    //init model
    private ServerManager serverManager = new ServerManager(PersistenceAccess.loadFromPersistenceEmailBean(),this);

    //init view
    private ViewFactory viewFactory = new ViewFactory(serverManager);

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        viewFactory.showMainWindow();
        serverManager.listenerServiceThread.start();
    }

    @Override
    public void stop() throws Exception {
        /* Salvataggio sul file di persistenza */
        PersistenceAccess.saveEmailBeanToPersistence(serverManager.emailBeans);

        serverManager.stopListenerServiceThread();
        serverManager.stopThreadPool();
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