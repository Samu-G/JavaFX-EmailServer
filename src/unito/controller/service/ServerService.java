package unito.controller.service;

import unito.ServerManager;
import unito.model.EmailBean;
import unito.model.ValidAccount;
import unito.model.ValidEmail;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.*;

/**
 * Classe Runnable volta a gestire una richiesta (che può essere di vario tipo) del Client.
 * Effettua anche l'autenticazione del Client.
 */
public class ServerService implements Runnable {

    private final Socket incoming;
    private final ServerManager serverManager;
    private ObjectInputStream inStream;
    private ObjectOutputStream outStream;
    private ValidAccount tryToConnect;
    private ClientRequestType requestType;

    /**
     * @param incoming      connessione Client-Server
     * @param serverManager riferimento al ServerManager dell'app
     */
    public ServerService(Socket incoming, ServerManager serverManager) {
        this.incoming = incoming;
        this.serverManager = serverManager;
    }

    /**
     * Autentica il Client e identifica la richiesta gestendola a sua volta
     */
    @Override
    public void run() {
        System.out.println("\nSRV: ServerService is now running....");
        System.out.println("SRV: ServerService -> Dedicated thread is now running -> " + Thread.currentThread().getName() + " started at: " + new Date(System.currentTimeMillis()));

        try {
            openStream();
            try {
                Object o = inStream.readObject();

                if (o instanceof ValidAccount) {
                    this.tryToConnect = (ValidAccount) o;

                    serverManager.writeOnConsole("LOG: ServerService. ValidAccount to authenticate received. Waiting for authentication... ");
                    System.out.println("SRV: ServerService -> Trying to connect:\n\t - address: " + tryToConnect.getAddress() + "\n\t - password: " + tryToConnect.getPassword());

                    /* Autenticazione del Client... */
                    if (authenticateClient()) {
                        serverManager.writeOnConsole("LOG: Authentication completed. User: " + tryToConnect.getAddress());

                        /* Riconoscimento della richiesta... */
                        if (requestIdentification()) {
                            System.out.println("SRV: Request Type: " + requestType);
                            serverManager.writeOnConsole("LOG: Request Type: " + requestType);
                            switch (requestType) {
                                case HANDSHAKING -> handShaking();
                                case INVIOMESSAGGIO -> riceviEmail();
                                case RICEVIMESSAGGIO -> invioEmail();
                                case CANCELLAMESSAGGIO -> cancellaEmail();
                            }
                        } else {
                            serverManager.writeOnConsole("ERR: Request Type is Invalid!");
                        }
                    } else {
                        serverManager.writeOnConsole("ERR: Authentication failed!");
                    }
                } else {
                    System.out.println("SRV: ServerService -> input Stream error: received wrong object.");
                }

            } catch (ClassNotFoundException e) {
                System.out.println("SRV: ServerService -> input Stream error");
                e.printStackTrace();
            } finally {
                incoming.close();
                closeStream();
                System.out.println("SRV: Connection with the client closed.");
                System.out.println("SRV: ServerService -> Dedicated thread is stopped -> " + Thread.currentThread().getName() + " ended at: " + new Date(System.currentTimeMillis()));
                serverManager.writeOnConsole("LOG: Connection with the client closed.\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Riceve le email dal Client prelevandole dal suo EmailBean
     */
    private void riceviEmail() {

        ValidEmail validEmailReceived;

        try {

            Object o = inStream.readObject();

            if (o instanceof ValidEmail) {
                validEmailReceived = (ValidEmail) o;

                List<String> addressesNotFounded = new LinkedList<>();

                List<String> addressesFounded = new LinkedList<>();

                for (String r : validEmailReceived.getRecipients()) {

                    boolean addressFound = false;

                    List<EmailBean> l = serverManager.getEmailBeans();

                    for (EmailBean emailBean : l) {
                        if (emailBean.getEmailAccountAssociated().getAddress().equals(r)) {
                            addressFound = true;
                            emailBean.addEmail(validEmailReceived);
                            break;
                        }
                    }

                    if (addressFound) {
                        addressesFounded.add(r);
                    } else {
                        addressesNotFounded.add(r);
                    }
                }

                outStream.writeObject(addressesNotFounded);

                serverManager.writeOnConsole("LOG: RiceviEmail address not founded: " + addressesNotFounded.size());

                serverManager.writeOnConsole("LOG: RiceviEmail address founded: " + addressesFounded.size());

                serverManager.writeOnConsole("LOG: RiceviEmail completed with the client " + tryToConnect.getAddress());

            } else {
                serverManager.writeOnConsole("ERR: RiceviEmail(null) failed with the client " + tryToConnect.getAddress());
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Invia le email al Client prelevandole dal sul EmailBean
     */
    private void invioEmail() {

        EmailBean emailBean = serverManager.getEmailBean(tryToConnect);
        List<ValidEmail> emailList;

        try {
            if (emailBean != null) {
                emailList = emailBean.getEmailListToSend();
                outStream.writeObject(emailList);
                serverManager.writeOnConsole("LOG: InvioEmail completed with the client " + tryToConnect.getAddress() + " " + "sended " + emailList.size());
                emailBean.setEmptyListToSend();
            } else {
                outStream.writeObject(ClientRequestResult.ERROR);
                serverManager.writeOnConsole("ERR: InvioEmail failed with the client " + tryToConnect.getAddress() + " " + "sended " + 0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Gestisce l'handshaking (prima connessione) con il Client
     */
    private void handShaking() {

        EmailBean emailBean = serverManager.getEmailBean(tryToConnect);
        System.out.println(emailBean);

        try {
            if (emailBean != null) {
                System.out.println("SRV: Handshaking, client have: " + emailBean.getEmailList().size() + " emails.");
                outStream.writeObject(emailBean.getEmailList());
                serverManager.writeOnConsole("LOG: Handshaking completed with the client " + tryToConnect.getAddress() + ", " + "sended " + emailBean.getEmailList().size() + " email.");
            } else {
                outStream.writeObject(ClientRequestResult.ERROR);
                serverManager.writeOnConsole("ERR: Handshaking failed with the client " + tryToConnect.getAddress());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Rimuove l'email dalla lista dell'EmailBean e da la conferma della cancellazione al Client
     */
    private void cancellaEmail() {

        ValidEmail validEmailReceived;

        try {

            Object o = inStream.readObject();

            if (o instanceof ValidEmail) {

                validEmailReceived = (ValidEmail) o;

                for (ValidEmail email : serverManager.getEmailBean(tryToConnect).getEmailList()) {

                    if (email.equals(validEmailReceived)) {
                        boolean res = serverManager.getEmailBean(tryToConnect).getEmailList().remove(email);
                        outStream.writeObject(res);
                        serverManager.writeOnConsole("LOG: CancellaEmail completed with the client " + tryToConnect.getAddress());
                        break;
                    }
                }

            } else {
                outStream.writeObject(false);
                serverManager.writeOnConsole("ERR: CancellaEmail failed with the client " + tryToConnect.getAddress());
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    /**
     * Identifica la richiesta del Client
     *
     * @return true se è una ClientRequestType, altrimenti false
     */
    private boolean requestIdentification() {
        try {

            Object o = inStream.readObject();

            if (o instanceof ClientRequestType) {
                this.requestType = (ClientRequestType) o;
            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private void openStream() {
        try {
            this.inStream = new ObjectInputStream(this.incoming.getInputStream());
            this.outStream = new ObjectOutputStream(this.incoming.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void closeStream() {
        try {
            this.inStream.close();
            this.outStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Autentica il Client che cerca di connettersi
     *
     * @return true se è registrato presso il Server, altrimenti false
     */
    private boolean authenticateClient() {
        if (tryToConnect == null) {
            System.out.println("SRV: ValidAccount TryToConnect is null");
            return false;
        }

        if (serverManager.authenticateThisAccount(tryToConnect)) {
            try {
                this.outStream.writeObject(ClientRequestResult.SUCCESS);
                System.out.println("SRV: ServerService -> Authentication completed with the client.");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        } else {
            try {
                this.outStream.writeObject(ClientRequestResult.FAILED_BY_CREDENTIALS);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }
    }

}