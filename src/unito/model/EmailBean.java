package unito.model;

import unito.controller.persistence.ValidAccount;
import unito.controller.persistence.ValidEmail;

import java.io.Serializable;
import java.util.*;

public class EmailBean implements Serializable {


    private ValidAccount emailAccountAssociated;
    private List<ValidEmail> emailListSended;
    private List<ValidEmail> emailListAlreadyToSend;

    public EmailBean(ValidAccount emailAccountAssociated, List<ValidEmail> emailsListAssociated) {
        this.emailAccountAssociated = emailAccountAssociated;
        this.emailListSended = emailsListAssociated;
    }

    public ValidAccount getEmailAccountAssociated() {
        return emailAccountAssociated;
    }

    public List<ValidEmail> getEmailListSended() {
        return emailListSended;
    }

    public boolean equals(ValidAccount obj) {
        return Objects.equals(obj.getPassword(), this.emailAccountAssociated.getPassword()) &&
                Objects.equals(obj.getAddress(), this.emailAccountAssociated.getAddress());
    }

    public static void printBean(EmailBean bean) {
        System.out.println(bean.toString());
        for (ValidEmail email : bean.getEmailListSended()) {
            System.out.println(email.toString());
        }
    }

    public void addEmail(ValidEmail email) {
        if (email != null) {
            emailListAlreadyToSend.add(email);
        }
    }

    public List<ValidEmail> getNewEmail() {
        return emailListAlreadyToSend;
    }

    public void setReadedAllMessage() {
        for(ValidEmail email : emailListAlreadyToSend) {
            emailListSended.add(email);
        }

        emailListAlreadyToSend.clear();
    }

    @Override
    public String toString() {
        return "Questo è il Bean di " + getEmailAccountAssociated().getAddress() + ", e contiene " + getEmailListSended().size() + " Email.";
    }
}
