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
        System.out.println("\n # ServerService is now running.... #");
        System.out.println("# ServerService -> Dedicated thread is now running -> " + Thread.currentThread().getName() + " started at: " + new Date(System.currentTimeMillis()));

        try {
            ObjectInputStream inStream = new ObjectInputStream(incoming.getInputStream());
            ObjectOutputStream outStream = new ObjectOutputStream(incoming.getOutputStream());

            try {
                ValidAccount TryToConnect = ((ValidAccount) inStream.readObject());
                if (TryToConnect != null) {
                    System.out.println("# ServerService -> ValidAccount to authenticate recived. Waiting for authentication... # ");
                     /* Autenticazione del Client... */
                    autenticateAndSend(outStream, TryToConnect);
                } else {
                    System.out.println("# ServerService -> ValidAccount recived is NULL. ABORTING REQUEST # ");
                }
            } catch (ClassNotFoundException e) {
                System.out.println("# ServerService -> input Stream error: recived wrong object. #");
                e.printStackTrace();
            } finally {
                incoming.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void autenticateAndSend(ObjectOutputStream outStream, ValidAccount TryToConnect) throws IOException {

        if (TryToConnect != null)
            System.out.println("# ServerService -> Trying to connect:\n# address: " + TryToConnect.getAddress() + "\n# password: " + TryToConnect.getPassword());

        if (serverManager.autenticateThisAccount(TryToConnect)) {
            System.out.println("# ServerService -> " + TryToConnect.getAddress() + "Client authenticated. #");
            List<ValidEmail> emailList = ServerManager.getEmailsList(TryToConnect.getAddress());
            System.out.println("# ServerService -> " + TryToConnect.getAddress() + "have: " + emailList.size() + " email. #");

            /* Scrivo sull'outStream la lista di email email */
            outStream.writeObject(emailList);
            /**/
        } else {
            System.out.println("# ServerService -> " + TryToConnect.getAddress() + "Client NOT authenticated. REJECTED #");
        }
    }

}
