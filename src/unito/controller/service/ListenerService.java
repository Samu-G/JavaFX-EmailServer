package unito.controller.service;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.Initializable;
import unito.ServerManager;
import unito.controller.BaseController;
import unito.view.ViewFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;

/**
 * Classe Runnable, con un socket sempre in ascolto accetta le richieste di connessione da parte dei client
 * Poi DELEGA A SERVERSERVICE il compito di fare il riconoscimento delle credenziali
 */
public class ListenerService implements Runnable {

    private ServerSocket listener;
    private final ExecutorService serverThreadPool;
    private final ServerManager serverManager;

    public ListenerService(ExecutorService serverThreadPool, ServerManager serverManager) {
        this.serverThreadPool = serverThreadPool;
        this.serverManager = serverManager;
    }

    private ServerSocket openServerSocket() {
        System.out.println("openServerSocket() called.");
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(8189);
            serverManager.writeOnConsole("LOG: Opened ServerSocket on port: " + serverSocket.getLocalPort());
        } catch (IOException e) {
            serverManager.writeOnConsole("ERROR: Opened ServerSocket failed!");
        }
        return serverSocket;
    }

    @Override
    public void run() {
        listener = openServerSocket();

        if (listener != null) {
            serverManager.writeOnConsole("LOG: ListenerService is now running on Port " + listener.getLocalPort());
            for (; ; ) {
                Socket incoming = null; //pending for new connection
                try {
                    incoming = listener.accept();

                    serverManager.writeOnConsole("LOG: Connection accepted with the client." + incoming.getPort());

                    //init dedicated socket service
                    Runnable task = new ServerService(incoming, serverManager);

                    //starting runnable
                    serverThreadPool.execute(task);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void closeServerSocket() {
        System.out.println("\ncloseServerSocket() called.");
        if (listener != null) {
            if (listener.isClosed()) {
                try {
                    listener.close();
                    serverManager.writeOnConsole("LOG: Closed ServerSocket on port: " + listener.getLocalPort());

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            serverManager.writeOnConsole("ERROR: ServerSocket is null!");
        }
    }

}
