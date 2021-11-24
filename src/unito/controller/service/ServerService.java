package unito.controller.service;

import unito.ServerManager;
import unito.model.EmailBean;
import unito.model.ValidAccount;
import unito.model.ValidEmail;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Classe Runnable volta a gestire UNA RICHIESTA (che può essere di vario tipo) del Client. Effettua anche l'autenticazione del Client.
 */
public class ServerService implements Runnable {

    private Socket incoming;
    protected ServerManager serverManager;
    private ObjectInputStream inStream;
    private ObjectOutputStream outStream;
    private ValidAccount tryToConnect;
    private ClientRequestType requestType;
    private boolean authenticated;

    public ServerService(Socket incoming, ServerManager serverManager) {
        this.incoming = incoming;
        this.serverManager = serverManager;
        this.authenticated = false;
    }

    @Override
    public void run() {
        System.out.println("\n# ServerService is now running.... #");
        System.out.println("# ServerService -> Dedicated thread is now running -> " + Thread.currentThread().getName() + " started at: " + new Date(System.currentTimeMillis()));

        try {
            //aprire gli input e output stream;
            openStream();
            try {
                this.tryToConnect = ((ValidAccount) inStream.readObject());
                if (tryToConnect != null) {
                    System.out.println("# ServerService -> ValidAccount to authenticate received. Waiting for authentication... # ");
                    /* Autenticazione del Client... */
                    if (authenticateClient()) {
                        serverManager.writeOnConsole("LOG: Authentication completed. User: " + tryToConnect.getAddress());
                        /* Riconoscimento della richiesta... */
                        if (requestIdentification()) {
                            serverManager.writeOnConsole("LOG: Request Type: " + requestType);
                            switch (requestType) {
                                case HANDSHAKING -> handShaking();
                                case INVIOMESSAGGIO -> riceviEmail();
                                case RICEVIMESSAGGIO -> invioEmail();
                                case CANCELLAMESSAGGIO -> cancellaEmail();
                            }
                        } else {
                            serverManager.writeOnConsole("ERROR: Request Type is Invalid!");
                        }
                    } else {
                        serverManager.writeOnConsole("Authentication failed!");
                    }
                } else {
                    serverManager.writeOnConsole("ERROR: Account null! Aborting ...");
                }
            } catch (ClassNotFoundException e) {
                System.out.println("# ServerService -> input Stream error: received wrong object. #");
                e.printStackTrace();
            } finally {
                incoming.close();
                closeStream();
                serverManager.writeOnConsole("LOG: Connection with the client closed.\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void riceviEmail() {
        ValidEmail validEmailRecived;

        try {
            validEmailRecived = (ValidEmail) inStream.readObject();

            List<String> addressesNotFounded = new LinkedList<String>();

            if (validEmailRecived != null) {

                //System.out.println("I destinatari del messaggio sono n=" + validEmailRecived.getRecipients().length);


                for (String r : validEmailRecived.getRecipients()) {

                    boolean addressFound = false;

                    for (EmailBean toCheck : serverManager.emailBeans) {
                        if (toCheck.getEmailAccountAssociated().getAddress().equals(r)) {
                            toCheck.addEmail(validEmailRecived);
                            addressFound = true;
                            break;
                        }
                    }

                    if (!addressFound) addressesNotFounded.add(r);
                }

                outStream.writeObject(addressesNotFounded);

                serverManager.writeOnConsole("LOG: RiceviEmail completed with the client " + tryToConnect.getAddress());
            } else {
                //ho ricevuto una mail nulla, non faccio niente
                serverManager.writeOnConsole("LOG: RiceviEmail(null) FAILED with the client " + tryToConnect.getAddress());
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void invioEmail() {

        EmailBean emailBean = serverManager.getEmailBean(tryToConnect);
        List<ValidEmail> emailList = new ArrayList<>();

        try {
            if (emailBean != null) {
                emailList = emailBean.getEmailListToSend();
                outStream.writeObject(emailList);
                serverManager.writeOnConsole("InvioEmail completed with the client " + tryToConnect.getAddress() + ", " + "sended " + emailList.size());
                emailBean.setEmptyListToSend();
            } else {
                outStream.writeObject(ClientRequestResult.ERROR);
                serverManager.writeOnConsole("InvioEmail completed with the client " + tryToConnect.getAddress() + ", " + "sended " + emailList.size());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void handShaking() {

        EmailBean emailBean = serverManager.getEmailBean(tryToConnect);
        System.out.println(emailBean);
        try {
            if (emailBean != null) {
                System.out.println("HANDSHAKING il bean contiene: " + emailBean.getEmailList().size());
                outStream.writeObject(emailBean.getEmailList());
                serverManager.writeOnConsole("LOG: Handshaking completed with the client " + tryToConnect.getAddress() + ", " + "sended " + emailBean.getEmailList().size() + " email.");
            } else {
                outStream.writeObject(ClientRequestResult.ERROR);
                serverManager.writeOnConsole("Handshaking FAILED with the client " + tryToConnect.getAddress());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void cancellaEmail() {
        ValidEmail validEmailRecived;

        try {

            validEmailRecived = (ValidEmail) inStream.readObject();

            if (validEmailRecived != null) {
                for (ValidEmail email : serverManager.getEmailBean(tryToConnect).getEmailList()) {

                    if (email.equals(validEmailRecived)) {
                        boolean res = serverManager.getEmailBean(tryToConnect).getEmailList().remove(email);
                        outStream.writeObject(res);
                        serverManager.writeOnConsole("LOG: CancellaEmail completed with the client " + tryToConnect.getAddress());
                        break;
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    private boolean requestIdentification() {
        try {
            this.requestType = (ClientRequestType) inStream.readObject();

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

    private boolean authenticateClient() {
        if (tryToConnect != null) {
            System.out.println("# ServerService -> Trying to connect:\n# ServerService -> address: " + tryToConnect.getAddress() + "\n# ServerService -> password: " + tryToConnect.getPassword());
        } else {
            System.out.println("ValidAccount TryToConnect is null");
            return false;
        }

        if (serverManager.authenticateThisAccount(tryToConnect)) {
            try {
                this.outStream.writeObject(ClientRequestResult.SUCCESS);
                System.out.println("risultato spedito");
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.authenticated = true;
            return true;
        } else {
            try {
                this.outStream.writeObject(ClientRequestResult.FAILED_BY_CREDENTIALS);
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.authenticated = false;
            return false;
        }
    }

}
