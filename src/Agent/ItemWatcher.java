package Agent;

import Auctionhouse.AuctionBidManager;
import Auctionhouse.BidRejection;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.Arrays;
import java.util.HashMap;

public class ItemWatcher implements Runnable {
    private final Socket socket;
    private final int itemNo;
    private final int accountNumber;
    private final HashMap<String, AuctionBidManager> itemsForAuction = new HashMap<>();
    private final HashMap<String, String> auctionUpdate = new HashMap<>();
    private ObjectOutputStream outMessage;
    private ObjectInputStream inMessage;
    private double amountReturn;
    private boolean newItemsRegistered, returnApproved;

    public ItemWatcher(Socket socket, int itemNo, int accountNumber) {
        this.socket = socket;
        this.itemNo = itemNo;
        this.accountNumber = accountNumber;
    }

    @Override
    public void run() {
        try {
            inMessage = new ObjectInputStream(socket.getInputStream());
            outMessage = new ObjectOutputStream(socket.getOutputStream());
            outMessage.writeObject(accountNumber);

            while (socket.isConnected()) {
                Object object = inMessage.readObject();
                if (object instanceof AuctionBidManager item) {
                    updateItemList(item);
                    auctionUpdate.put(item.getAuctionItemName(), item.getMessage(accountNumber));
                    if (!item.isBidOver()) {
                        amountReturn += item.refundBid(accountNumber);
                        if (amountReturn == 0) {
                            returnApproved = false;
                        } else {
                            returnApproved = true;
                        }
                    }
                } else {
                    BidRejection rejection = (BidRejection) object;
                    updateAuctionUpdate(rejection.getItemName(), rejection.getMessage());
                }
            }
        }catch(SocketException | EOFException e) {
            System.out.println("AuctionHouse Closed");
        }catch (IOException | ClassNotFoundException e){
            e.printStackTrace();
        }
    }

    public void updateAuctionUpdate (String auctionItem, String message) {
        auctionUpdate.put(auctionItem,message);
    }
    private void updateItemList(AuctionBidManager item) {
        if (itemsForAuction.containsKey(item.getAuctionItemName())){
            itemsForAuction.remove(item.getAuctionItemName());
            itemsForAuction.put(item.getAuctionItemName(), item);
        } else {
            itemsForAuction.put(item.getAuctionItemName(), item);
        }
        newItemsRegistered = true;
    }

    public void closeAuction(){
        for (AuctionBidManager items : itemsForAuction.values()){
            if (!items.getMessage(accountNumber).equals("Auction won.") &&
                    !items.getMessage(accountNumber).equals("Auction lost.")) {
                auctionUpdate.put(items.getAuctionItemName(), "Auction closed");
                items.setBidOver(true);
                if (items.getCurrentBidderID() == accountNumber) {
                    amountReturn += items.getCurrentBid();
                    returnApproved = true;
                }
            }
        }
    }
    public String getStatusMessage(String item) {
        return auctionUpdate.get(item);
    }

    public double getItemBid(String item) {
        return itemsForAuction.get(item).getCurrentBid();
    }

    public boolean getAuctionIsOtherBid(String item, int accountNumber) {
        return itemsForAuction.get(item).isOtherBidder(accountNumber);
    }

    public String[] getItems() {
        String[] itemList = itemsForAuction.keySet().toArray(new String[0]);
        Arrays.sort(itemList);
        return itemList;
    }

    public void makeABid(String item, int amount, int accountNumber) throws IOException {
        outMessage.writeObject(new AuctionBidManager(itemsForAuction.get(item), amount, accountNumber, false));
    }

    public double getAmountReturn() {
        if (returnApproved) {
            double temp = amountReturn;
            amountReturn = 0;
            returnApproved = false;
            return temp;
        }
        return 0;
    }

    public int getItemNo() {return itemNo;}

    public boolean isNewItemsRegistered(){
        return newItemsRegistered;
    }

    public void resetItems(){
        newItemsRegistered = false;
    }

    public HashMap<String, AuctionBidManager> getAuctionItems(){
        return itemsForAuction;
    }

    public boolean isItemActive(String item) {
        return !itemsForAuction.get(item).isBidOver();
    }
}
