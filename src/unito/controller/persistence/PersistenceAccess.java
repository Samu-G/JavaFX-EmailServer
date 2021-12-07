package unito.controller.persistence;


import unito.model.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe non generica usata per leggere e scrivere sul file di persistenza gli account salvati nel server al momento dell'appertura / chiusura della applicazione
 */
public class PersistenceAccess {

    private static final String VALID_EMAIL_BEANS_LOCATION = "src/unito/controller/persistence/emailBean.ser";
    private static final Encoder encoder = new Encoder();

    public static List<EmailBean> loadFromPersistenceEmailBean() {

        List<EmailBean> resultList = new ArrayList<>();

        /* Carico da File di persistenza gli emailBean */
        try {
            FileInputStream fileInputStream = new FileInputStream(VALID_EMAIL_BEANS_LOCATION);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);

            Object o = objectInputStream.readObject();

            if(o instanceof List) {
                if(!((List<?>) o).isEmpty()) {
                    if(((List<?>) o).get(0) instanceof EmailBean) {
                        resultList = (List<EmailBean>)o;
                    }
                }
            }

            /* Decripto le password di ogni account della lista */
            decodePasswords(resultList);

            for (EmailBean emailBean : resultList) {
                EmailBean.printBean(emailBean);
            }
        } catch (FileNotFoundException e) {
            /* * Account salvati nel file di persistenza */
            System.out.println("File NOT FOUND! Loading demo...");
            resultList.add(new EmailBean(new ValidAccount("user1@email.com", "user1"), exampleEmailList()));
            resultList.add(new EmailBean(new ValidAccount("user2@email.com", "user2"), exampleEmailList()));
            resultList.add(new EmailBean(new ValidAccount("user3@email.com", "user3"), exampleEmailList()));
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return resultList;
    }

    public static List<ValidEmail> exampleEmailList() {
        List<ValidEmail> emailList = new ArrayList<>();

        String[] destinatari = new String[]{"Destinatario1", "Destinatario2"};

        Email email = new Email("Mittente", destinatari, "Oggetto", "Testo del messaggio");

        emailList.add(new ValidEmail(email));

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
     * Salva nel file di persistenza gli EmailBean dei client al momento della chiusura
     *
     * @param emailBeans la lista di EmailBean da salvare nel file di pesistenza
     */
    public static void saveEmailBeanToPersistence(List<EmailBean> emailBeans) {
        System.out.print("\nSaving emailBean....");
        try {
            File file = new File(VALID_EMAIL_BEANS_LOCATION);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            encodePasswords(emailBeans);
            objectOutputStream.writeObject(emailBeans);
            objectOutputStream.close();
            fileOutputStream.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        System.out.print("DONE!");
    }


}
