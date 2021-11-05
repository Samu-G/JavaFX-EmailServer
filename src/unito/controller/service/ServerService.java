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
import java.util.List;

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
                        if (requestIdentification()) {
                            switch (requestType) {
                                case HANDSHAKING -> handShaking();
                                case INVIOMESSAGGIO -> riceviEmail();
                                case RICEVIMESSAGGIO -> invioEmail();
                            }
                        } else {
                            System.out.println("# ServerService -> ClientRequestType IS INVALID. ABORTING REQUEST # ");
                        }
                    } else {
                        //non mi sono autenticato
                        System.out.println("# ServerService -> Authentication failed. ABORTING REQUEST # ");
                    }
                } else {
                    System.out.println("# ServerService -> ValidAccount received is NULL. ABORTING REQUEST # ");
                }
            } catch (ClassNotFoundException e) {
                System.out.println("# ServerService -> input Stream error: received wrong object. #");
                e.printStackTrace();
            } finally {
                incoming.close();
                closeStream();
                serverManager.writeOnConsole("Connection with the client closed.\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void riceviEmail() {
        ValidEmail validEmailRecived;

        try {
            validEmailRecived = (ValidEmail) inStream.readObject();

            if (validEmailRecived != null) {

                //System.out.println("I destinatari del messaggio sono n=" + validEmailRecived.getRecipients().length);

                for (EmailBean toCheck : serverManager.emailBeans) {

                    for (String r : validEmailRecived.getRecipients()) {
                        if (toCheck.getEmailAccountAssociated().getAddress().equals(r)) {
                            System.out.println("\nPRIMA: " + toCheck);
                            toCheck.addEmail(validEmailRecived);
                            System.out.println("Ho aggiunto una mail al bean " + toCheck.getEmailAccountAssociated().getAddress());
                            System.out.println("\nDOPO: " + toCheck);
                        }
                    }

                }
                serverManager.writeOnConsole("RiceviEmail completed with the client " + tryToConnect.getAddress());
            } else {
                //ho ricevuto una mail nulla, non faccio niente
                serverManager.writeOnConsole("RiceviEmail FAILED with the client " + tryToConnect.getAddress());
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
                emailList = emailBean.getEmailListAlreadyToSend();
                outStream.writeObject(emailList);
                emailBean.setReadedAllMessage();
                serverManager.writeOnConsole("InvioEmail completed with the client " + tryToConnect.getAddress() + ", " + "sended " + emailList.size());
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
                //emailBean.setReadedAllMessage();
                serverManager.writeOnConsole("Handshaking completed with the client " + tryToConnect.getAddress() + ", " + "sended " + emailBean.getEmailList().size());
            } else {
                outStream.writeObject(ClientRequestResult.ERROR);
                serverManager.writeOnConsole("Handshaking FAILED with the client " + tryToConnect.getAddress());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private boolean requestIdentification() {
        try {
            this.requestType = ((ClientRequestType) inStream.readObject());

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        serverManager.writeOnConsole("Kind of request needed for " + tryToConnect.getAddress() + " is " + this.requestType);
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
