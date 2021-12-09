package unito.model;

import java.io.Serializable;

/**
 * Classe Account (model)
 */
public class ValidAccount implements Serializable {

    private final String address;
    private String password;

    /*Constructor*/

    /**
     * @param address address of ValidAccount
     * @param password password of ValidAccount
     */
    public ValidAccount(String address, String password) {
        this.address = address;
        this.password = password;
    }

    /*Getter*/

    public String getAddress() {
        return address;
    }

    public String getPassword() {
        return password;
    }

    /*Used to encode and decode password*/

    public void setPassword(String password) {
        this.password = password;
    }

    /*Aux*/

    /**
     * Controlla se l'oggetto corrisponde al ValidAccount corrente
     *
     * @param obj l'oggetto da confrontare
     * @return true se sono uguali, altrimenti false
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            System.out.println("oggetto null");
            return false;
        }
        if (obj.getClass() != this.getClass()) {
            System.out.println("classe non corretta");
            return false;
        } else {
            if (this.address.equals(((ValidAccount) obj).getAddress())
                    && this.password.equals(((ValidAccount) obj).getPassword())) {
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * Crea una stringa adatta per la visualizzazione
     *
     * @return l'oggetto nella relativa rappresentazione di stringa
     */
    @Override
    public String toString() {
        return "ValidAccount address: " + address + " password: " + password;
    }

}
