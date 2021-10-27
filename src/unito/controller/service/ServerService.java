package unito.controller.service;

import unito.ServerManager;
import unito.controller.persistence.*;
import unito.model.EmailBean;

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
                serverManager.writeOnConsole("Connection with the client closed");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void riceviEmail() {
        List<ValidEmail> validEmailRecived = new ArrayList<>();
        try {
            validEmailRecived = (List<ValidEmail>) inStream.readObject();

            if (validEmailRecived != null) {

                /* Aggiorno i bean dei destinatari */
                for (EmailBean toCheck : serverManager.emailBeans) {
                    for (ValidEmail v : validEmailRecived) {
                        if (toCheck.getEmailAccountAssociated().getAddress() == v.getRecipients()) {
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

        try {
            for (EmailBean toCheck : serverManager.emailBeans) {
                if (toCheck.getEmailAccountAssociated().getAddress() == TryToConnect.getAddress()) {
                    outStream.writeObject(
                            toCheck.getNewEmail()
                    );
                    toCheck.setReadedAllMessage();
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void handShaking() {
        System.out.println("# ServerService -> " + TryToConnect.getAddress() + "Client authenticated. #");
        List<ValidEmail> emailList = ServerManager.getEmailsList(TryToConnect.getAddress());
        System.out.println("# ServerService -> " + TryToConnect.getAddress() + " have: " + emailList.size() + " email. #");

        /* Scrivo sull'outStream la lista di email email */
        try {
            this.outStream.writeObject(emailList);
        } catch (IOException e) {
            e.printStackTrace();
        }
        /**/

        System.out.println("# ServerService -> " + TryToConnect.getAddress() + "Client NOT authenticated. REJECTED #");
    }

    private boolean requestIdentification() {
        try {
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
