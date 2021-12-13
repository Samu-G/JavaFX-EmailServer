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
     * @param serverManager
     */
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

    /**
     * Apre la socket e si mette in ascolto per richieste da parte dei Client
     */
    @Override
    public void run() {
        do {
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
        } while (loop);

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
            serverManager.writeOnConsole("ERROR: ServerSocket is null!");
        }
    }

    /**
     * Setta il valore per la variabile booleana
     *
     * @param b true continua il ciclo, false lo interrompe
     */
    public void setLoop(boolean b) {
        this.loop = b;
    }
}
