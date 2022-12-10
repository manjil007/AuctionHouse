package Auctionhouse;

import Bank.ClientMessage;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class Client extends LinkedBlockingQueue<AuctionBidManager> implements Runnable {
    private final HashMap<Integer, ObjectOutputStream> agents;
    private final ObjectOutputStream writer;
    private final int bankId;
    private Salesman salesman;

    public Client(HashMap<Integer, ObjectOutputStream> agents, ObjectOutputStream writer, int bankId){
        this.agents = agents;
        this.writer = writer;
        this.bankId = bankId;
    }

    public void setSalesman(Salesman salesman){
        this.salesman = salesman;
    }
    @Override
    public void run() {
        try{
            while (true){
                AuctionBidManager itemsForBid = this.take();
                String itemName = itemsForBid.getAuctionItemName();
                AuctionBidManager existingItem = salesman.get(itemName);
                double prevBidPrice = existingItem.getCurrentBid();
                double newBidPrice = itemsForBid.getCurrentBid();


                if (itemsForBid.isBidOver()){
                    AuctionBidManager newItems = new AuctionBidManager(existingItem, true);
                    salesman.remove(itemName);

                    AuctionBidManager boughtItem = null;

                    if (salesman.itemLeft()){
                        boughtItem = salesman.getSelleableItems();
                    }

                    for (ObjectOutputStream out : agents.values()){
                        out.writeObject(existingItem);
                        System.out.println("Bid ended");

                        if (boughtItem != null){
                            out.writeObject(boughtItem);
                        }
                    }
                    ClientMessage msg = new ClientMessage(newItems.getCurrentBidderID(), bankId, (int) newBidPrice);
                    writer.writeObject(msg);
                }else if (salesman.containsKey(itemName) || newBidPrice > prevBidPrice){
                    existingItem.updateBid(itemsForBid.getCurrentBidderID(), (int) newBidPrice);

                    for (ObjectOutputStream out : agents.values()){
                        out.writeObject(itemsForBid);
                        System.out.println("Send new bid to someone");
                    }
                }else{
                    agents.get(itemsForBid.getCurrentBidderID()).writeObject(
                            new BidRejection(itemsForBid.getCurrentBidderID(), "rejection", itemName));
                    System.out.println("Sent rejection to " + itemsForBid.getCurrentBidderID());
                }
            }
        }catch (InterruptedException | IOException e){
            e.printStackTrace();
        }
    }
}
