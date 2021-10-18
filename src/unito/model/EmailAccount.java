package unito.model;

import javafx.beans.property.SimpleStringProperty;

import java.util.Properties;

public class EmailAccount {

    private SimpleStringProperty address;
    private SimpleStringProperty password;
    private Properties properties;

    public String getAddress() {
        return address.get();
    }

    public String getPassword() {
        return password.get();
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    @Override
    public String toString() {
        System.out.println( "Creato un nuovo EmailAccount\nADRESS: " + address.get() + "\nPASSWORD: " + password.get() );
        return address.get();
    }


    public EmailAccount(String address, String password) {
        this.address = new SimpleStringProperty(address);
        this.password = new SimpleStringProperty(password);

        /*
        properties = new Properties();
        properties.put("incomingHost", "imap.gmail.com");
        properties.put("mail.store.protocol", "imaps");

        properties.put("mail.transport.protocol", "smtps");
        properties.put("mail.smtps.host", "smtp.gmail.com");
        properties.put("mail.smtps.auth", "true");
        properties.put("outgoingHost", "smtp.gmail.com");

         */
    }
}
