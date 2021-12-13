package unito.controller.service;

import unito.ServerManager;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

/**
 * Classe Runnable, con un socket sempre in ascolto accetta le richieste di connessione da parte dei client
 * Poi delega a ServerService il compito di fare il riconoscimento delle credenziali
 */
public class ListenerService implements Runnable {

    private ServerSocket listener;
    private final ExecutorService serverThreadPool;
    private final ServerManager serverManager;
    private boolean loop;

    /**
     * @param serverThreadPool pool of Thread
     * @param serverManager    riferimento al ServerManager dell'app
     */
    public ListenerService(ExecutorService serverThreadPool, ServerManager serverManager) {
        this.serverThreadPool = serverThreadPool;
        this.serverManager = serverManager;
        this.loop = true;
    }

    private void openServerSocket() {
        System.out.println("openServerSocket() called.");
        try {
            listener = new ServerSocket(8189);
            serverManager.writeOnConsole("LOG: Opened ServerSocket on port: " + listener.getLocalPort());
        } catch (IOException e) {
            serverManager.writeOnConsole("ERR: Opened ServerSocket failed!");
        }
    }

    /**
     * Il ServerSocket si mette in ascolto sulla porta 8189 e accoglie le richieste del client creando
     * dei Runnable dedicati per ciascuna richiesta
     */
    @Override
    public void run() {

        openServerSocket();

        if (listener != null) {
            do {
                serverManager.writeOnConsole("LOG: ListenerService is now running on Port " + listener.getLocalPort());

                Socket incoming; //pending for new connection

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
            } while (loop);
        }

        closeServerSocket();
    }

    private void closeServerSocket() {
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
            serverManager.writeOnConsole("ERR: ServerSocket is null!");
        }
    }

    /**
     * Setta il valore loop che interrompe il ciclo di ascolto del server
     *
     * @param value
     */
    public void setLoop(boolean value) {
        this.loop = value;
    }
}
