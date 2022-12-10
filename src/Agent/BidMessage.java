package Agent;

public enum BidMessage {
    ACCEPTED("Bid accepted."),
    REJECTED("Bid rejected."),
    WINNER("Auction won."),
    LOSER("Auction lost."),
    OUTBID("Outbid.");

    final String message;

    BidMessage(String message) {
        this.message = message;
    }

    public String getMessage() { return message; }
}