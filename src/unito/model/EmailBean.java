package unito.model;

import java.io.Serializable;
import java.util.*;

/**
 * Classe EmailBean (serializzabile) volta a gestire un Account e la sua relativa lista di Email
 */
public class EmailBean implements Serializable {

    private final ValidAccount emailAccountAssociated;
    private final List<ValidEmail> emailList;
    private List<ValidEmail> emailListToSend;

    /**
     * @param emailAccountAssociated ValidAccount
     * @param emailsListAssociated list of ValidEmail
     */
    public EmailBean(ValidAccount emailAccountAssociated, List<ValidEmail> emailsListAssociated) {
        this.emailAccountAssociated = emailAccountAssociated;
        this.emailList = emailsListAssociated;
        this.emailListToSend = new LinkedList<>();
    }

    public ValidAccount getEmailAccountAssociated() {
        return emailAccountAssociated;
    }

    synchronized public List<ValidEmail> getEmailList() {
        return emailList;
    }

    synchronized public List<ValidEmail> getEmailListToSend() {
        return emailListToSend;
    }

    /**
     * Stampa a video l'EmailBean
     *
     * @param bean l'EmailBean da stampare
     */
    public static void printBean(EmailBean bean) {
        System.out.println(bean.toString());
    }

    /**
     * Aggiunge la ValidEmail alla emailList e alla emailListToSend
     *
     * @param email ValidEmail da aggiungere alle liste
     */
    synchronized public void addEmail(ValidEmail email) {
        if (email != null) {
            this.emailList.add(email);
            this.emailListToSend.add(email);
        }
    }

    /**
     * Controlla se l'oggetto corrisponde all'EmailBean corrente
     *
     * @param obj l'oggetto da confrontare
     * @return true se sono uguali, altrimenti false
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ValidAccount) {
            return Objects.equals(((ValidAccount) obj).getPassword(), this.emailAccountAssociated.getPassword()) &&
                    Objects.equals(((ValidAccount) obj).getAddress(), this.emailAccountAssociated.getAddress());
        } else {
            return false;
        }
    }

    /**
     * Crea una stringa adatta per la visualizzazione
     *
     * @return l'oggetto nella relativa rappresentazione di stringa
     */
    @Override
    public String toString() {
        return "Questo è il Bean di " + getEmailAccountAssociated().getAddress() +
                ", numero di email: " + getEmailList().size();
    }

    /**
     * Svuota la lista dalle ValidEmail
     */
    synchronized public void setEmptyListToSend() {
        this.emailListToSend.clear();
    }
}
