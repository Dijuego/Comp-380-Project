/**
 * Card.java
 * November 21, 2023
 * @author Diego Hernandez
 *
 * The Card Class is designed to represent a Card that is going to be used in Purchase.
 * Has 4 fields cardNum, experationDate, cvc, cardAddress.
 */
package debuggerenjoyers.airlinereservation;
import java.util.Date;
public class Card {

    private String cardNum;
    private String expirationDate;
    private int cvc;
    private String cardAddress;

    /**
     * Card Constructor with cardNum, expiration Date, Cvc, and Card Address as input parameters.
     * @param cardNum
     * @param expirationDate
     * @param cvc
     * @param cardAddress
     */
    public Card(String cardNum, String expirationDate, int cvc, String cardAddress){
        this.cardNum = cardNum;
        this.expirationDate = expirationDate;
        this.cvc = cvc;
        this.cardAddress = cardAddress;
    }

    /**
     * Default Card Constructor
     */
    public Card(){
        this.cardNum = null;
        this.expirationDate = null;
        this.cvc = -1;
        this.cardAddress = null;
    }

    /**
     * Access method for Card cardNum. Returns Integer
     * @return cardNum
     */
    public String getCardNum(){
        return cardNum;
    }

    /**
     * Access method for Card expirationDate. Returns Date
     * @return expirationDate
     */
    public String getExpirationDate(){
        return expirationDate;
    }

    /**
     * Access method for Card cvc. Returns Integer
     * @return cvc
     */
    public int getCvc(){
        return cvc;
    }

    /**
     * Access method for Card cardAddress. Returns String
     * @return cardAddress
     */
    public String getCardAddress(){
        return cardAddress;
    }
}
