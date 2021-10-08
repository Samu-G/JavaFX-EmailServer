package unito;

import unito.model.EmailBean;
import unito.controller.persistence.*;

import java.net.ServerSocket;
import java.util.List;

public class ServerManager {

    private static List<EmailBean> emailBeans;
    private ServerSocket serverSocket;
    private static boolean quit;

    public ServerManager(List<EmailBean> emailBeans) {
        this.emailBeans = emailBeans;
    }

    /**
     * Ritorna una lista di ValidEmail associata all'indirizzo passato come parametro
     * @param address indirizzo email
     * @return Lista di ValidEmail
     */
    public static List<ValidEmail> getEmailsList(String address) {
        System.out.println("getEmailsList() called.");
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

    public boolean autenticateThisAccount(ValidAccount toAutenticate) {
        //TODO
        System.out.println("autenticateThisAccount() called.");
        for(EmailBean i : emailBeans) {
            System.out.println(i);

            if(i.getEmailAccountAssociated().equals(toAutenticate)) {
                return true;
            }
        }
        return false;
    }


}
