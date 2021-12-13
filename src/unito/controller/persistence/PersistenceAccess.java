package unito.controller.persistence;

import unito.ServerManager;
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

    /**
     * Legge il file di persistenza salvato nella locazione indicata da VALID_ACCOUNT_LOCATION e restituisce la lista degli account letti dal file
     *
     * @return la lista di ValidAccount salvati sul file di persistenza
     */
    public static List<EmailBean> loadFromPersistenceEmailBean() {

        List<EmailBean> resultList = new ArrayList<>();

        /* Carico da File di persistenza gli emailBean */
        try {
            FileInputStream fileInputStream = new FileInputStream(VALID_EMAIL_BEANS_LOCATION);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);

            Object o = objectInputStream.readObject();

            if (o instanceof List) {
                if (!((List<?>) o).isEmpty()) {
                    if (((List<?>) o).get(0) instanceof EmailBean) {
                        resultList = (List<EmailBean>) o;
                    }
                }
            }

            /* Decripto le password di ogni account della lista */
            decodePasswords(resultList);

            for (EmailBean emailBean : resultList) {
                EmailBean.printBean(emailBean);
            }
        } catch (FileNotFoundException e) {
            /* Account salvati nel file di persistenza */
            System.out.println("File NOT FOUND! Loading demo...");
            resultList.add(new EmailBean(new ValidAccount("user1@email.com", "user1"), exampleEmailList()));
            resultList.add(new EmailBean(new ValidAccount("user2@email.com", "user2"), exampleEmailList()));
            resultList.add(new EmailBean(new ValidAccount("user3@email.com", "user3"), exampleEmailList()));
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return resultList;
    }

    /**
     * Crea una lista di ValidEmail
     *
     * @return una lista di esempio di ValidEmail
     */
    public static List<ValidEmail> exampleEmailList() {
        List<ValidEmail> emailList = new ArrayList<>();

        String testoMessaggioDiProva = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.";

        String[] destinatari1 = new String[]{"user1@email.com"};
        Email email1 = new Email("user1@email.com", destinatari1, "Mail dest. singolo", testoMessaggioDiProva);

        String[] destinatari2 = new String[]{"user1@email.com", "user2@email.com"};
        Email email2 = new Email("user2@email.com", destinatari2, "Mail a due destinatari", testoMessaggioDiProva);

        String[] destinatari3 = new String[]{"user1@email.com", "user2@email.com", "user3@email.com"};
        Email email3 = new Email("user3@email.com", destinatari3, "Mail dest. multiplo", testoMessaggioDiProva);

        emailList.add(new ValidEmail(email1));
        emailList.add(new ValidEmail(email2));
        emailList.add(new ValidEmail(email3));

        return emailList;
    }

    private static void decodePasswords(List<EmailBean> persistedList) {
        for (EmailBean e : persistedList) {
            String originalPassword = e.getEmailAccountAssociated().getPassword();
            e.getEmailAccountAssociated().setPassword(encoder.decode(originalPassword));
        }
    }

    private static void encodePasswords(List<EmailBean> persistedList) {
        for (EmailBean e : persistedList) {
            String originalPassword = e.getEmailAccountAssociated().getPassword();
            e.getEmailAccountAssociated().setPassword(encoder.encode(originalPassword));
        }
    }

    /**
     * Salva nel file di persistenza gli EmailBean dei client al momento della chiusura
     *
     * @param emailBeans la lista di EmailBean da salvare nel file di persistenza
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
