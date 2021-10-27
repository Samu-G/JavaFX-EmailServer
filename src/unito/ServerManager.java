package unito;

import unito.controller.MainWindowController;
import unito.controller.service.ListenerService;
import unito.model.EmailBean;
import unito.controller.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class ServerManager {

    public static List<EmailBean> emailBeans;
    private MainWindowController mainWindowController;
    public ListenerService listenerService;
    public Thread listenerServiceThread;
    public Launcher launcher;

    public ServerManager(List<EmailBean> emailBeans, ExecutorService serverThreadPool, Launcher launcher) {
        this.emailBeans = emailBeans;
        this.listenerService = new ListenerService(serverThreadPool, this);
        this.listenerServiceThread = new Thread(listenerService);
        this.launcher = launcher;
    }

    /**
     * Ritorna una lista di ValidEmail associata all'indirizzo passato come parametro
     * @param address indirizzo email
     * @return Lista di ValidEmail
     */
    public static List<ValidEmail> getEmailsList(String address) {
        for(EmailBean i : emailBeans) {
            if(i.getEmailAccountAssociated().getAddress().equals(address)){
                return i.getEmailListSended();
            } else {
                System.out.println("c'è decisamente qualcosa che non va!");
                return null;
            }
        }
        return null;
    }

    public static List<ValidAccount> getValidAccountList() {
        List<ValidAccount> result = new ArrayList<ValidAccount>();
        for(EmailBean i : emailBeans) {
            result.add(i.getEmailAccountAssociated());
        }
        return result;
    }

    public static boolean authenticateThisAccount(ValidAccount toAutenticate) {
        //TODO
        for(EmailBean i : emailBeans) {
            //System.out.println(i);

            if(i.getEmailAccountAssociated().equals(toAutenticate)) {
                return true;
            }
        }
        return false;
    }

    public void setMainWindowController(MainWindowController controller) {
        this.mainWindowController = controller;
    }

    public void writeOnConsole(String string) {
        this.mainWindowController.printOnConsole(string);
    }

    public void stopListenerServiceThread() {
        if(!listenerServiceThread.isInterrupted()) {
            listenerService.closeServerSocket();
            listenerServiceThread.interrupt();
            mainWindowController.printOnConsole("ListenerService interrupted()");
        } else {
            mainWindowController.printOnConsole("ListenerService already interrupted.");
        }
    }

}
