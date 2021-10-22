package unito;

import unito.controller.MainWindowController;
import unito.model.EmailBean;
import unito.controller.persistence.*;

import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

public class ServerManager {

    private static List<EmailBean> emailBeans;
    private ServerSocket serverSocket;
    private static boolean quit;
    private MainWindowController mainWindowController;

    public ServerManager(List<EmailBean> emailBeans) {
        this.emailBeans = emailBeans;
    }

    /**
     * Ritorna una lista di ValidEmail associata all'indirizzo passato come parametro
     * @param address indirizzo email
     * @return Lista di ValidEmail
     */
    public static List<ValidEmail> getEmailsList(String address) {
        for(EmailBean i : emailBeans) {
            if(i.getEmailAccountAssociated().getAddress().equals(address)){
                return i.getEmailsListAssociated();
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

    public static boolean autenticateThisAccount(ValidAccount toAutenticate) {
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

}
