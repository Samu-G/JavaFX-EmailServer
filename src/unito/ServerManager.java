package unito;

import unito.controller.service.ListenerService;
import unito.model.EmailBean;
import unito.model.ValidAccount;
import unito.view.ViewManager;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Classe che gestisce le funzioni principali dell'applicazione
 */
public class ServerManager {

    /* Model */
    private final List<EmailBean> emailBeans;
    private final ListenerService listenerService;
    /* View */
    private ViewManager viewManager;
    /* Thread */
    private final Thread listenerServiceThread;
    private final ExecutorService serverThreadPool;

    /**
     * @param emailBeans la lista delle EmailBean
     */
    public ServerManager(List<EmailBean> emailBeans) {
        this.emailBeans = emailBeans;
        this.serverThreadPool = Executors.newFixedThreadPool(Launcher.NUM_OF_THREAD);
        this.listenerService = new ListenerService(serverThreadPool, this);
        this.listenerServiceThread = new Thread(listenerService);
    }

    /**
     * @param account Il ValidAccount associato a quel EmailBean
     * @return il suo EmailBean
     */
    public EmailBean getEmailBean(ValidAccount account) {
        for (EmailBean e : emailBeans) {
            if (e.equals(account)) {
                return e;
            }
        }
        return null;
    }

    public List<EmailBean> getEmailBeans() {
        return emailBeans;
    }

    public Thread getListenerServiceThread() {
        return this.listenerServiceThread;
    }


    public void setViewManager(ViewManager viewManager) {
        this.viewManager = viewManager;
    }

    /**
     * Restituisce true se l'account è presente nel database del server, false altrimenti
     *
     * @param toAuthenticate il ValidAccount da autenticare
     * @return risultato autenticazione
     */
    public boolean authenticateThisAccount(ValidAccount toAuthenticate) {
        for (EmailBean i : emailBeans) {
            if (i.getEmailAccountAssociated().equals(toAuthenticate)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Scrive sulla console grafica del server il parametro passato a string
     *
     * @param string La stringa da scrivere sulla console prova
     */
    public void writeOnConsole(String string) {
        viewManager.mainWindowController.printOnConsole(string);
    }

    /**
     * Soft stop del thread ListenerService che accoglie i client
     */
    public void stopListenerServiceThread() {
        listenerService.setLoop(false);
        try {
            listenerServiceThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Termina il pool di Thread che gestisce le richieste del client
     */
    public void stopThreadPool() {
        while (!serverThreadPool.isTerminated()) {
            serverThreadPool.shutdown();
        }
    }

}
