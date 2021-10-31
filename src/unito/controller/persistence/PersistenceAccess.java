package unito.controller.persistence;


import unito.model.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class PersistenceAccess {

    private static final String VALID_EMAIL_BEANS_LOCATION = "src/unito/controller/persistence/emailBean.ser";
    private static Encoder encoder = new Encoder();

    public static List<EmailBean> loadFromPersistenceEmailBean() {

        List<EmailBean> resultList = new ArrayList<>();

        resultList.add(new EmailBean(new ValidAccount("user1@email.com", "user1"), exampleEmailList()));
        resultList.add(new EmailBean(new ValidAccount("user2@email.com", "user2"), exampleEmailList()));
        resultList.add(new EmailBean(new ValidAccount("user3@email.com", "user3"), exampleEmailList()));

        /* Carico da File di persistenza gli emailBean */
        try {
            FileInputStream fileInputStream = new FileInputStream(VALID_EMAIL_BEANS_LOCATION);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            resultList = (List<EmailBean>) objectInputStream.readObject();

            /* Decripto le password di ogni account della lista */
            decodePasswords(resultList);

            for (int i = 0; i < resultList.size(); i++) {
                EmailBean.printBean(resultList.get(i));
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return resultList;
    }


    public static List<ValidEmail> exampleEmailList() {
        List<ValidEmail> emailList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            emailList.add(new ValidEmail("sender" + i, new String[]{"recipients" + i, "recipients" + (i+1)} , "subject" + i, "size" + i, "date" + i, "textMessage" + i));
        }
        return emailList;
    }

    /**
     * Prende una lista di "EmailBean" e ne decripta le password
     *
     * @param persistedList la lista con le EmailBean da decriptare
     */
    private static void decodePasswords(List<EmailBean> persistedList) {
        for (EmailBean e : persistedList) {
            String originalPassword = e.getEmailAccountAssociated().getPassword();
            e.getEmailAccountAssociated().setPassword(encoder.decode(originalPassword));
        }
    }

    /**
     * Prende una lista di "EmailBean" e ne cripta le password
     *
     * @param persistedList la lista con le EmailBean da criptare
     */
    private static void encodePasswords(List<EmailBean> persistedList) {
        for (EmailBean e : persistedList) {
            String originalPassword = e.getEmailAccountAssociated().getPassword();
            e.getEmailAccountAssociated().setPassword(encoder.encode(originalPassword));
        }
    }

    /**
     * Salva nel file di persistenza gliEmailBean dei client al momento della chiusura
     *
     * @param emailBeans la lista di EmailBean da salvare nel file di pesistenza
     */
    public static void saveEmailBeanToPersistence(List<EmailBean> emailBeans) {
        System.out.print("\nSaving emailBean to emailbean....");
        try {
            File file = new File(VALID_EMAIL_BEANS_LOCATION);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            encodePasswords(emailBeans);
            objectOutputStream.writeObject(emailBeans);
            objectOutputStream.close();
            fileOutputStream.close();
        } catch (Exception e) {
            e.getMessage();
        }
        System.out.print("DONE!");
    }


}
