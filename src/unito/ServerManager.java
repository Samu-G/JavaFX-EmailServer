package unito;

import unito.controller.MainWindowController;
import unito.controller.service.ListenerService;
import unito.model.EmailBean;
import unito.model.ValidAccount;
import unito.model.ValidEmail;
import unito.view.ViewFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Classe che gestisce le funzioni principali dell'applicazione
 */
public class ServerManager {

    /* Model */
    public List<EmailBean> emailBeans;
    public static MainWindowController mainWindowController;
    public ListenerService listenerService;
    /* View */
    private ViewFactory viewFactory;
    /* Thread */
    public Thread listenerServiceThread;
    private final ExecutorService serverThreadPool;

    public ServerManager(List<EmailBean> emailBeans) {
        this.emailBeans = emailBeans;
        this.serverThreadPool = Executors.newFixedThreadPool(Launcher.NUM_OF_THREAD);
        this.listenerService = new ListenerService(serverThreadPool, this);
        this.listenerServiceThread = new Thread(listenerService);
    }

    /**
     * @param address indirizzo email dell'account
     * @return la liste di email quell'account (tutte quante)
     */
    public List<ValidEmail> getEmailsList(String address) {
        for (EmailBean i : emailBeans) {
            if (i.getEmailAccountAssociated().getAddress().equals(address)) {
                return i.getEmailList();
            } else {
                System.out.println("c'è decisamente qualcosa che non va!");
                return null;
            }
        }
        return null;
    }

    /**
     * @param address indirizzo email dell'account
     * @return la liste di email non ancora inviate al client di quell'account
     */
    /*
    public List<ValidEmail> getUnsendedEmailsList(String address) {
        for (EmailBean i : emailBeans) {
            if (i.getEmailAccountAssociated().getAddress().equals(address)) {
                return i.getEmailListAlreadyToSend();
            }
        }
        return null;

    }
    */

    /**
     * @return La lista di ValidAccount presenti sul server
     */
    public List<ValidAccount> getValidAccountList() {
        List<ValidAccount> result = new ArrayList<ValidAccount>();
        for (EmailBean i : emailBeans) {
            result.add(i.getEmailAccountAssociated());
        }
        return result;
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
     * @param toAuthenticate il ValidAccount da autenticare
     * @return true se trova l'account e le credenziali corrette, false altrimenti
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
     * @param string La stringa da scrivere sulla console prova
     */
    public void writeOnConsole(String string) {
        viewFactory.mainWindowController.printOnConsole(string);
    }

    /**
     * Ferma il thread di ListenerService che accoglie i client
     */
    public void stopListenerServiceThread() {
        if (!listenerServiceThread.isInterrupted()) {
            listenerService.closeServerSocket();
            listenerServiceThread.interrupt();
        }
    }

    /**
     * Termina il pool di Thread che gestisce le richieste dei client
     */
    public void stopThreadPool() {
        while (!serverThreadPool.isTerminated()) {
            serverThreadPool.shutdown();
        }
    }

    public void setViewFactory(ViewFactory viewFactory) {
        this.viewFactory = viewFactory;
    }
}
