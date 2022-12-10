package Auctionhouse;

import java.io.Serializable;

public class BidRejection implements Serializable {
    private final Integer bankId;
    private final String message;
    private final String itemName;

    public BidRejection(Integer bankId, String message, String itemName) {
        this.bankId = bankId;
        this.message = message;
        this.itemName = itemName;
    }

    public Integer getBankId() {
        return bankId;
    }

    public String getMessage() {
        return message;
    }

    public String getItemName() {
        return itemName;
    }
}
