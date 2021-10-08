package unito.controller.service;

import unito.ServerManager;
import unito.controller.BaseController;
import unito.model.*;
import unito.view.ViewFactory;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.List;

public class ClientRequestService extends BaseController implements Runnable {

    private ValidRequest clientAccount;

    /**
     * @param serverManager
     * @param viewFactory   abstract view controller
     * @param fxmlName      fxml file path of this controller
     */
    public ClientRequestService(ServerManager serverManager, ViewFactory viewFactory, String fxmlName) {
        super(serverManager, viewFactory, fxmlName);
    }

    @Override
    public void run() {
        Date timeStamp = new Date(System.currentTimeMillis());

        try {
            // establish server socket
            ServerSocket s = new ServerSocket(8189);

            System.out.println(Thread.currentThread().getName() + " started at " + timeStamp.toString());

            // wait for client connection (1 sola connessione)
            Socket incoming = s.accept( );
            try {
                ObjectInputStream inStream =
                        new ObjectInputStream(incoming.getInputStream());
                ObjectOutputStream outStream =
                        new ObjectOutputStream(incoming.getOutputStream());

                PrintWriter outPrinter = new PrintWriter(outStream, true /* autoFlush */);

                outPrinter.println( "Hello! Waiting for data." );

                // echo client input
                try {
                    ValidRequest TryToConnect = ((ValidRequest)inStream.readObject());
                    if(TryToConnect != null) {

                        System.out.println("Trying to connect:\n" + TryToConnect.toString() );
                        EmailAccount temp = new EmailAccount(TryToConnect.getAddress(), TryToConnect.getPassword());

                        if(serverManager.getValidAccountList().contains(temp))
                        {
                            System.out.println("Connessione ACCETTATA");

                            List<Email> emailList = ServerManager.getEmailsList();

                            outStream.writeObject(emailList);


                        }
                        else
                            {
                            System.out.println("Credenziali ERRATE!");
                            System.out.println("Trying to connect: adress:" + TryToConnect.getAddress() + "password: " + TryToConnect.getPassword() );
                            System.out.println("Invalid request! Request is null! Closing connection.");
                            s.close();
                        }

                        //dai email!
                    }
                    else {
                    }
                    outPrinter.println("Echo: " + TryToConnect.toString());

                } catch (ClassNotFoundException e) {System.out.println(e.getMessage());}
            }
            finally {
                incoming.close();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }        }
    }
