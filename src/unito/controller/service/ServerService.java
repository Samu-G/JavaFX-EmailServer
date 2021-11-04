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
    private ValidAccount TryToConnect;
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
                this.TryToConnect = ((ValidAccount) inStream.readObject());
                if (TryToConnect != null) {
                    System.out.println("# ServerService -> ValidAccount to authenticate received. Waiting for authentication... # ");
                    /* Autenticazione del Client... */
                    if (authenticateClient()) {
                        if (requestIdentification()) {
                            switch (requestType) {
                                case HANDSHAKING -> handShaking();
                                case INVIOMESSAGGIO -> invioEmail();
                                case RICEVIMESSAGGIO -> riceviEmail();
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
        List<ValidEmail> validEmailRecived;

        try {
            validEmailRecived = (List<ValidEmail>) inStream.readObject();

            if (validEmailRecived != null) {
                /* Aggiorno i bean dei destinatari */
                for (EmailBean toCheck : serverManager.emailBeans) {
                    for (ValidEmail v : validEmailRecived) {
                        if (toCheck.getEmailAccountAssociated().getAddress().equals(v.getRecipients())) {
                            toCheck.addEmail(v);
                        }
                    }
                }
            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void invioEmail() {

        EmailBean emailBean = serverManager.getEmailBean(TryToConnect);
        List<ValidEmail> emailList = new ArrayList<>();

        try {
            if(emailBean != null) {
                emailList = emailBean.getEmailListAlreadyToSend();
                outStream.writeObject(emailList);
                emailBean.setReadedAllMessage();
            } else {
                outStream.writeObject(ClientRequestResult.ERROR);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        serverManager.writeOnConsole("InvioNuoveEmail completed with the client " + TryToConnect.getAddress() + ", " + "sended " + emailList.size());
    }

    private void handShaking() {

        EmailBean emailBean = serverManager.getEmailBean(TryToConnect);
        List<ValidEmail> emailList = new ArrayList<>();

        try {
            if(emailBean != null) {
                outStream.writeObject(emailBean.getEmailList());
                //emailBean.setReadedAllMessage();
            } else {
                outStream.writeObject(ClientRequestResult.ERROR);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        serverManager.writeOnConsole("Handshaking completed with the client " + TryToConnect.getAddress() + ", " + "sended " + emailList.size());
    }

    private boolean requestIdentification() {
        try {
            // trovare soluzione al cast
            this.requestType = ((ClientRequestType) inStream.readObject());

        } catch (IOException | ClassNotFoundException e) {
            //TODO: da gestire il caso in cui arriva un oggetto che non è un ClientRequestType
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
        if (TryToConnect != null) {
            System.out.println("# ServerService -> Trying to connect:\n# ServerService -> address: " + TryToConnect.getAddress() + "\n# ServerService -> password: " + TryToConnect.getPassword());
        } else {
            System.out.println("ValidAccount TryToConnect is null");
            return false;
        }

        if (serverManager.authenticateThisAccount(TryToConnect)) {
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
