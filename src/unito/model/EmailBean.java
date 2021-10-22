package unito.model;

import unito.controller.persistence.ValidAccount;
import unito.controller.persistence.ValidEmail;

import java.io.Serializable;
import java.util.*;

public class EmailBean implements Serializable {

    public ValidAccount getEmailAccountAssociated() { return emailAccountAssociated; }

    public List<ValidEmail> getEmailsListAssociated() {
        return emailsListAssociated;
    }

    private ValidAccount emailAccountAssociated;
    private List<ValidEmail> emailsListAssociated;

    public EmailBean(ValidAccount emailAccountAssociated, List<ValidEmail> emailsListAssociated) {
        this.emailAccountAssociated = emailAccountAssociated;
        this.emailsListAssociated = emailsListAssociated;
    }

    public boolean equals(ValidAccount obj) {
        return Objects.equals(obj.getPassword(), this.emailAccountAssociated.getPassword()) &&
                Objects.equals(obj.getAddress(), this.emailAccountAssociated.getAddress());
    }

    public static void printBean(EmailBean bean) {
        System.out.println(bean.toString());
        for(ValidEmail email : bean.getEmailsListAssociated()){
            System.out.println(email.toString());
        }
    }

    @Override
    public String toString() {
        return "Questo è il Bean di " + getEmailAccountAssociated().getAddress() + ", e contiene " + getEmailsListAssociated().size() + " Email.";
    }
}
