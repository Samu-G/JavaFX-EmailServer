package unito;

import unito.controller.service.ListenerService;
import unito.model.EmailBean;
import unito.model.ValidAccount;
import unito.view.ViewFactory;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Classe che gestisce le funzioni principali dell'applicazione
 */
public class ServerManager {

    /* Model */
    private List<EmailBean> emailBeans;
    private ListenerService listenerService;
    /* View */
    private ViewFactory viewFactory;
    /* Thread */
    private Thread listenerServiceThread;
    private final ExecutorService serverThreadPool;

    /**
     * @param emailBeans list of EmailBean
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

    /**
     * @return la lista di EmailBean
     */
    public List<EmailBean> getEmailBeans() {
        return emailBeans;
    }

    /**
     * @return il Thread del ListenerService
     */
    public Thread getListenerServiceThread() {
        return this.listenerServiceThread;
    }

    /**
     * Autentica il ValidAccount
     *
     * @param toAuthenticate il ValidAccount da autenticare
     * @return true se trova l'account e le credenziali sono corrette, false altrimenti
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
     * Scrive sulla console grafica del server il paramentro passato a string
     *
     * @param string la stringa da scrivere sulla console del Server
     */
    public void writeOnConsole(String string) {
        viewFactory.mainWindowController.printOnConsole(string);
    }

    /**
     * Ferma il thread di ListenerService che accoglie i Client
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
     * Termina il pool di Thread che gestisce le richieste dei Client
     */
    public void stopThreadPool() {
        while (!serverThreadPool.isTerminated()) {
            serverThreadPool.shutdown();
        }
    }

    /**
     * Stabilisce la ViewFactory per il ServerManager
     *
     * @param viewFactory riferimento alla ViewFactory per il ServerManager
     */
    public void setViewFactory(ViewFactory viewFactory) {
        this.viewFactory = viewFactory;
    }

}
