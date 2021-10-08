package unito.controller.persistence;


import unito.model.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class PersistenceAccess {


    //private final String VALID_ACCOUNTS_LOCATION = ".validAccounts.ser";
    //private final String VALID_EMAIL_LOCATION = ".email.ser";
    private final String VALID_EMAIL_BEANS_LOCATION = ".emailbean.ser";

    //private Encoder encoder = new Encoder();
    private List<ValidAccount> accountList;
    private List<Email> emailList;
    private List<EmailBean> emailBeans;

    //TODO: da sistemare
    public List<EmailBean> loadFromPersistenceEmailBean(){

        //example
        emailBeans = exampleEmailBean();

        /*
        try {
            FileInputStream fileInputStream = new FileInputStream(VALID_EMAIL_LOCATION);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            List<Email> persistedList = (List<Email>) objectInputStream.readObject();
            emailList.addAll(persistedList);
        } catch ( Exception e){
            e.printStackTrace();
        }
        System.out.println("fetch email completata");

        try {
            FileInputStream fileInputStream = new FileInputStream(VALID_ACCOUNTS_LOCATION);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            List<EmailAccount> persistedList = (List<EmailAccount>) objectInputStream.readObject();
            decodePasswords(persistedList);
            accountList.addAll(persistedList);
        } catch ( Exception e){
            e.printStackTrace();
        }
        System.out.println("fetch account completata");
        */

        return emailBeans;
    }

    //TODO: da scambiare con una funzione che legge il file emailbean.ser
    public List<EmailBean> exampleEmailBean() {
        List<EmailBean> emailBeans = new ArrayList<>();

        //creo 10 email bean
        //ogni email bean contiene 10 email
        //example
        for(int i=0; i<10; i++){
            emailBeans.add(new EmailBean(
                    new ValidAccount("prova@gmail.com"+i, "pinco"+i ),
                    exampleEmailList()
            ));
        }

        return emailBeans;
    }

    public List<ValidEmail> exampleEmailList(){
        List<ValidEmail> emailList = new ArrayList<>();
        for(int i=0; i<10; i++){
            emailList.add(new ValidEmail("sender"+i, "recipients"+i, "subject"+i, "size"+i, "date"+i, "textMessage"+i));
        }
        return emailList;
    }


    public void saveEmailBeanToPersistence(List<EmailBean> emailBeans) {
        System.out.println("Saving emailBean to emailbean.ser");
        try {
            File file = new File(VALID_EMAIL_BEANS_LOCATION);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(emailBeans);
            objectOutputStream.close();
            fileOutputStream.close();
        } catch (Exception e) {
            e.getMessage();
        }
        System.out.println("DONE!");
    }



    /*
    private void decodePasswords(List<EmailAccount> persistedList) {
        for (EmailAccount validAccount: persistedList){
            String originalPassword = validAccount.getPassword();
            //validAccount.setPassword(encoder.decode(originalPassword));
        }
    }

    private void encodePasswords(List<EmailAccount> persistedList) {
        for (EmailAccount validAccount: persistedList){
            String originalPassword = validAccount.getPassword();
            //validAccount.setPassword(encoder.encode(originalPassword));
        }
    }
    */

    /*
    public List<ValidAccount> exampleAccount(){
        List<ValidAccount> accountList = new ArrayList<>();
        for(int i=0; i<10; i++) {
            accountList.add(new ValidAccount("prova@gmail.com" + i, "pinco" + i));
        }
        return accountList;
    }

    public void saveAccountToPersistence(List<EmailAccount> validAccounts){
        System.out.println("Saving account to validAccounts.ser");
        try {
            File file = new File(VALID_ACCOUNTS_LOCATION);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            encodePasswords(validAccounts);
            objectOutputStream.writeObject(validAccounts);
            objectOutputStream.close();
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("DONE!");
    }

    public void saveEmailToPersistence(List<Email> validAccounts){
        System.out.println("Saving email to email.ser");
        try {
            File file = new File(VALID_EMAIL_LOCATION);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(validAccounts);
            objectOutputStream.close();
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("DONE!");
    }
    */
}
