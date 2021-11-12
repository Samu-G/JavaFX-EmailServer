package unito.model;

import java.io.Serializable;
import java.util.*;

public class EmailBean implements Serializable {

    private ValidAccount emailAccountAssociated;
    private List<ValidEmail> emailList;
    private List<ValidEmail> emailListSended;
    private List<ValidEmail> emailListAlreadyToSend;

    public EmailBean(ValidAccount emailAccountAssociated, List<ValidEmail> emailsListAssociated) {
        this.emailAccountAssociated = emailAccountAssociated;
        this.emailList = emailsListAssociated;
        this.emailListSended = new ArrayList<>();
        this.emailListAlreadyToSend = new ArrayList<>();
    }

    public ValidAccount getEmailAccountAssociated() {
        return emailAccountAssociated;
    }

    public List<ValidEmail> getEmailList() {
        return emailList;
    }

    public List<ValidEmail> getEmailListAlreadyToSend() {
        return emailListAlreadyToSend;
    }

    public static void printBean(EmailBean bean) {
        System.out.println(bean.toString());
    }

    public void addEmail(ValidEmail email) {
        if (email != null) {
            emailList.add(email);
            //emailListAlreadyToSend.add(email);
        }
    }

    public void setReadedAllMessage() {
        for(ValidEmail email : emailListAlreadyToSend) {
            //TODO(MB): Cosa sta facendo esattamente questo metodo? Perchè è chiamato "Sended"? Stessa cosa per "AlreadyToSend"
            emailListSended.add(email);
        }
        emailListAlreadyToSend.clear();
    }


    @Override
    public boolean equals(Object obj) {
        if(obj instanceof ValidAccount) {
            return Objects.equals( ((ValidAccount)obj).getPassword(), this.emailAccountAssociated.getPassword()) &&
                    Objects.equals(((ValidAccount)obj).getAddress(), this.emailAccountAssociated.getAddress());
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "Questo è il Bean di " + getEmailAccountAssociated().getAddress() + "\n" +
                "emailList size: " + getEmailList().size() + "\n" +
                "emailListSended size: " + emailListSended.size() + "\n" +
                "emailListAlreadyToSend size: " + emailListAlreadyToSend.size();
    }
}
