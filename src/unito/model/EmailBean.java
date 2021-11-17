package unito.model;

import java.io.Serializable;
import java.util.*;

public class EmailBean implements Serializable {

    private final ValidAccount emailAccountAssociated;
    private final List<ValidEmail> emailList;
    private List<ValidEmail> emailListToSend;

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

    public static void printBean(EmailBean bean) {
        System.out.println(bean.toString());
    }

    synchronized public void addEmail(ValidEmail email) {
        if (email != null) {
            this.emailList.add(email);
            this.emailListToSend.add(email);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ValidAccount) {
            return Objects.equals(((ValidAccount) obj).getPassword(), this.emailAccountAssociated.getPassword()) &&
                    Objects.equals(((ValidAccount) obj).getAddress(), this.emailAccountAssociated.getAddress());
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "Questo è il Bean di " + getEmailAccountAssociated().getAddress() + "\n" +
                "EmailList size: " + getEmailList().size() + "\n\n";
    }

    synchronized public void setEmptyListToSend() {
        this.emailListToSend.clear();
    }
}
