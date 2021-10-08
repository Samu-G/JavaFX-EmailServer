package unito.controller.service;

import unito.ServerManager;
import unito.model.Email;
import unito.controller.persistence.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Date;
import java.util.List;

public class ServerService implements Runnable {

    private Socket incoming;
    protected ServerManager serverManager;

    public ServerService(Socket incoming, ServerManager serverManager) {
        this.incoming = incoming;
        this.serverManager = serverManager;
    }

    @Override
    public void run() {
        Date timeStamp = new Date(System.currentTimeMillis());
        System.out.println("thread: " + Thread.currentThread().getName() + "## started at:" + timeStamp);
        try {
            ObjectInputStream inStream = new ObjectInputStream(incoming.getInputStream());
            ObjectOutputStream outStream = new ObjectOutputStream(incoming.getOutputStream());

            //2) ricevo le credenziali
            try {
                ValidAccount TryToConnect = ((ValidAccount) inStream.readObject());
                if (TryToConnect != null) {
                    System.out.println("Apertura di una connessione tra client e server:\n" + Thread.currentThread().getName());
                    //invio le email
                    autenticateAndSend(outStream, TryToConnect);
                } else {
                    System.out.println("ValidAccount coming from Client is null!!");
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                incoming.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void autenticateAndSend(ObjectOutputStream outStream, ValidAccount TryToConnect) throws IOException {
        if (serverManager.autenticateThisAccount(TryToConnect)) {
            System.out.println("Client autenticato.\n Credenziali Corrette. \nInvio del bean.");

            //3) invio le email
            List<ValidEmail> emailList = ServerManager.getEmailsList(TryToConnect.getAddress());
            System.out.println("il bean contiene " + emailList.size() + " email.");
            outStream.writeObject(emailList);

        } else {
            System.out.println("Credenziali ERRATE!");
            System.out.println("Trying to connect: address:" + TryToConnect.getAddress() + " password: " + TryToConnect.getPassword());
            System.out.println("Invalid request! Request is null! Closing connection.");
        }
    }

}
