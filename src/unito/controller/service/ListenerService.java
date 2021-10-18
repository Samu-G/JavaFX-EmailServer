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
public class ListenerService implements Runnable, Initializable {

    private ServerSocket listener;
    private ExecutorService serverThreadPool;
    private ServerManager serverManager;

    public ListenerService( ExecutorService serverThreadPool, ServerManager serverManager) {
        this.serverThreadPool = serverThreadPool;
        this.serverManager = serverManager;
    }

    private ServerSocket openServerSocket() {
        System.out.println("openServerSocket() called.");
        try {
            ServerSocket serverSocket = new ServerSocket(8189);
            return serverSocket;
        } catch (IOException e) {
            System.out.println("Errore nell'apertura del server socket.");
            return null;
        }
    }

    @Override
    public void run() {
        for(;;) {

            System.out.println("\n # ListenerService is now running.... #");
            Socket incoming = null; //pending for new connection
            try {
                System.out.println("# ListenerService -> Listening to 8189... #");
                incoming = listener.accept();
                System.out.println("Connessione in entrata accettata; autenticazione del client presa in carico dal ThreadPool.");
            } catch (IOException e) {
                System.out.println("Errore nell'apertura del Socket in entrata.");
                e.printStackTrace();
            }

            //init dedicated socket service
            Runnable task = new ServerService(incoming, serverManager);

            //starting runnable
            serverThreadPool.execute(task);
        }

    }

    public void closeServerSocket() {
        //closing ServerSocket
        if(listener != null) {
            try {
                listener.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        listener = openServerSocket();
    }
}
