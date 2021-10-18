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
    public List<EmailBean> loadFromPersistenceEmailBean() {

        //example
        //emailBeans = exampleEmailBean();
        List<EmailBean> resultList = new ArrayList<>();

        /* Carico da File di persistenza gli emailBean */
        try {
            /* Apro un FileInputStream e leggo il file .emailBeans.ser */
            FileInputStream fileInputStream = new FileInputStream(VALID_EMAIL_BEANS_LOCATION);
            /* Il file è composto da stringhe di testo che descrivono gli oggetti "emailBean" */
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            /* Leggo gli oggetti dallo stream con readObject() */
            try {
                List<EmailBean> persistedList = (List<EmailBean>) objectInputStream.readObject();

                for (int i = 0; i < persistedList.size(); i++) {
                    EmailBean.printBean(persistedList.get(i));
                }

                /* Decripto le password di ogni account della lista */
                // decodePasswords(persistedList);

                /* Aggiungo alla lista */
                resultList.addAll(persistedList);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        /* Ritorno la lista di account salvati nel file di oggetti .EmailBean.ser */

        return emailBeans;
    }

    public static List<EmailBean> exampleEmailBean() {
        List<EmailBean> emailBeans = new ArrayList<>();

        //creo 10 email bean
        //ogni email bean contiene 10 email
        //example
        for (int i = 0; i < 10; i++) {
            emailBeans.add(new EmailBean(
                    new ValidAccount("prova@gmail.com" + i, "pinco" + i),
                    exampleEmailList()
            ));
        }

        return emailBeans;
    }

    public static List<ValidEmail> exampleEmailList() {
        List<ValidEmail> emailList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            emailList.add(new ValidEmail("sender" + i, "recipients" + i, "subject" + i, "size" + i, "date" + i, "textMessage" + i));
        }
        return emailList;
    }


    public void saveEmailBeanToPersistence(List<EmailBean> emailBeans) {
        System.out.print("\nSaving emailBean to emailbean....");
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
        System.out.print("DONE!");
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
