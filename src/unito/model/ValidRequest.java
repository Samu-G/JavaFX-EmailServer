package unito.model;

import java.io.Serializable;

public class ValidRequest implements Serializable {

    private String address;
    private String password;

    public ValidRequest(String address, String password) {
        this.address = address;
        this.password = password;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    @Override
    public String toString() {
        return this.getAddress() + "\n"
                + this.getPassword();
    }
}
