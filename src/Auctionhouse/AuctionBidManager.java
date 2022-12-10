package Auctionhouse;

import Agent.BidMessage;

import java.io.Serial;
import java.io.Serializable;

public class AuctionBidManager implements Serializable {

    @Serial
    private static final long serialVersionUID = 2L;
    private final String auctionItemName;
    private Integer currentBid;
    private Integer previousBid;
    private Integer currentBidderID;
    private Integer previousBidderId;
    private transient int remainingTime;
    private transient boolean timeStarted;
    private boolean bidOver;


    public AuctionBidManager(String auctionItem, int startingBid) {
        this.auctionItemName = auctionItem;
        currentBid = startingBid;
        currentBidderID = 0;
        previousBid = 0;
        previousBidderId = 0;
        bidOver = false;
        remainingTime = 30;
    }

    public AuctionBidManager(AuctionBidManager pastBid, int currentBid,
                              int currentBidderID, boolean bidOver) {
        auctionItemName = pastBid.getAuctionItemName();
        previousBid = (int)pastBid.getCurrentBid();
        previousBidderId = pastBid.getCurrentBidderID();
        this.currentBid = currentBid;
        this.currentBidderID = currentBidderID;
        this.bidOver = bidOver;
        remainingTime = 30;
    }

    public AuctionBidManager(AuctionBidManager pastBid, boolean bidOver) {
        auctionItemName = pastBid.getAuctionItemName();
        previousBid = (int) pastBid.getPreviousBid();
        previousBidderId = pastBid.getPreviousBidderId();
        this.currentBid = (int) pastBid.getCurrentBid();
        this.currentBidderID = pastBid.getCurrentBidderID();
        this.bidOver = bidOver;
        remainingTime = 30;
    }

    public String getMessage(int agentId) {
        if (currentBidderID.equals(0)) return "No bid.";
        if (bidOver) {
            if (currentBidderID.equals(agentId)) {
                return BidMessage.WINNER.getMessage();
            } else {
                return BidMessage.LOSER.getMessage();
            }
        } else {
            if (currentBidderID.equals(agentId)) {
                return BidMessage.ACCEPTED.getMessage();
            } else if (previousBidderId.equals(agentId)) {
                return BidMessage.OUTBID.getMessage();
            }
        }
        return "New bid.";
    }

    public void updateBid(Integer currentBidderID, Integer currentBid) {
        previousBidderId = this.currentBidderID;
        previousBid = this.currentBid;
        this.currentBidderID = currentBidderID;
        this.currentBid = currentBid;
        remainingTime = 30;
        timeStarted = true;
    }

    public double refundBid(int agentID) {
        if (currentBidderID.equals(agentID) && bidOver) {
            return 0;
        }
        if (previousBidderId.equals(agentID)) {
            return previousBid;
        }
        return 0;
    }

    public String getAuctionItemName() {
        return auctionItemName;
    }

    public double getCurrentBid() {
        return currentBid;
    }

    public double getPreviousBid() {
        return previousBid;
    }

    public Integer getCurrentBidderID() {
        return currentBidderID;
    }

    public Integer getPreviousBidderId() {
        return previousBidderId;
    }

    public int getRemainingTime() {
        return remainingTime;
    }

    public boolean isTimeStarted() {
        return timeStarted;
    }

    public boolean isOtherBidder(int accountNumber) {
        return currentBidderID.equals(accountNumber);
    }

    public boolean isBidOver() {
        return bidOver;
    }

    public void setBidOver(boolean bidOver) {
        this.bidOver = bidOver;
    }

    public synchronized void timeOver() {
        if (!timeStarted) return;
        --remainingTime;
    }
}
