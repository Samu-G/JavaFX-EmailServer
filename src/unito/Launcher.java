package unito;

import javafx.application.Application;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import unito.controller.MainWindowController;
import unito.controller.service.ListenerService;
import unito.controller.service.*;
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

    //init console reader pipe per l'input proveniente da sys.out e sys.err
    private final PipedInputStream pipeIn1 = new PipedInputStream();
    private final PipedInputStream pipeIn2 = new PipedInputStream();

    //loading from persistence all email Beans
    private PersistenceAccess persistenceAccess = new PersistenceAccess();
    protected List<EmailBean> emailBeans = persistenceAccess.loadFromPersistenceEmailBean();

    //init model
    private ServerManager serverManager = new ServerManager(emailBeans);

    //init view
    ViewFactory viewFactory = new ViewFactory(serverManager);

    //init ThreadPool
    ExecutorService serverThreadPool = Executors.newFixedThreadPool(NUM_OF_THREAD);

    //init listener service
    ListenerService listener = new ListenerService(serverThreadPool, serverManager);

    //init thread
    private Thread listenerServiceThread = new Thread(listener);
    private Thread consoleReader = new Thread();

    @Override
    public void start(Stage primaryStage) throws Exception {
        //init pipein*
        setUpPipeOutput(pipeIn1, pipeIn2);
        //TODO: Classe ReaderService da implementare
        ReaderService obj = new ReaderService(pipeIn1, pipeIn2);
        consoleReader = new Thread();
        listenerServiceThread.start();
        //show view
        ViewFactory viewFactory = new ViewFactory(serverManager);
        viewFactory.showMainWindow();
        //Thread execution for reading output stream



    }

    public static void main(String[] args ) { launch(args); }

    @Override
    public void stop() throws Exception {
        persistenceAccess.saveEmailBeanToPersistence(emailBeans);

        //stop listener
        listener.closeServerSocket();
        //stop ThreadPool
        serverThreadPool.shutdown();
        //exit(0)
    }

    void setUpPipeOutput(PipedInputStream pipeIn1, PipedInputStream pipeIn2) {
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