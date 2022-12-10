package Agent;

import Bank.ClientAdress;

import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

public class Agent implements Runnable{
    private Socket socket = null;
    private ObjectInputStream inMessage = null;
    private ObjectOutputStream outMessage = null;
    private int accountId;
    private String name;
    private double bankBalance;
    private double availableBalance;
    private final String bankAddress;
    private final int bankPort;
    private final HashMap<Integer, ItemWatcher> ah = new HashMap<>();
    private int i = 0;
    private double bidAmount;

    public boolean hasBankId = false;

    public Agent(String bankAddress, int bankPort){
        this.bankAddress = bankAddress;
        this.bankPort = bankPort;
    }

    @Override
    public void run() {
        try{
            socket = new Socket(bankAddress, bankPort);
            System.out.println("Connected to Bank");
            inMessage = new ObjectInputStream(socket.getInputStream());
            outMessage = new ObjectOutputStream(socket.getOutputStream());

            outMessage.writeObject("Agent");
            outMessage.writeObject(bankBalance);
            accountId = (int)inMessage.readObject();
            System.out.println("Account number: " + accountId);

            while (socket.isConnected()){
                Object object = inMessage.readObject();
                if (object instanceof ClientAdress client){
                    Socket auctionSocket = new Socket(client.getipAdress(), client.getPortNumber());
                    System.out.println("Connected to Auction House" + (i + 1) + ".");
                    ItemWatcher itemWatcher = new ItemWatcher(auctionSocket, i, accountId);
                    i++;
                    ah.put(itemWatcher.getItemNo(), itemWatcher);
                    (new Thread(itemWatcher)).start();
                }else{
                    setTotalBalance((double) object);
                }
            }
        }catch (SocketException | EOFException e){
            System.out.println("Connection Closed");
            System.exit(0);
        }catch (IOException | ClassNotFoundException e){
            e.printStackTrace();
        }
    }

    public void sendBid(String item, int i) throws IOException{
        availableBalance -= bidAmount;
        ah.get(i).makeABid(item, (int) bidAmount, accountId);
    }

    public boolean checkBidOwner(int i, String item){
        return ah.get(i).getAuctionIsOtherBid(item, accountId);
    }

    public double getBidStatus(int index, String item){
        return ah.get(index).getItemBid(item);
    }

    public String receiveMessage(int i, String item){
        return ah.get(i).getStatusMessage(item);
    }

    public void setMessage(int i, String item){
        ah.get(i).updateAuctionUpdate(item, BidMessage.REJECTED.getMessage());
    }

    public Set<String> getAuctionItemKeys(int i){
        return ah.get(i).getAuctionItems().keySet();
    }

    public String[] getListOfItem(int i){
        return ah.get(i).getItems();
    }

    public Integer[] getIndex(){
        Integer[] itemArray = ah.keySet().toArray(new Integer[0]);
        Arrays.sort(itemArray);
        return itemArray;
    }

    public void setTotalBalance(double bankBalance){
        this.bankBalance = bankBalance;
    }

    public double getTotalBalance(){
        return bankBalance;
    }

    public void setAvailableBalance(double availableBalance){
        this.availableBalance = availableBalance;
    }

    public double getAvailableBalance(){
        return availableBalance;
    }

    public void setBidAmount(double bid){
        this.bidAmount = bid;
    }

    public double getBidAmount(){
        return bidAmount;
    }

    public int getAccountId(){
        return accountId;
    }

    public boolean checkActiveBids(){
        return availableBalance != bankBalance;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public boolean isItemActive(String item, int i){
        return ah.get(i).isItemActive(item);
    }

    public void closeSocket(){
        try{
            if (socket != null){
                inMessage.close();
                outMessage.close();
                socket.close();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public boolean getAHUpdate(){
        boolean check = false;
        for (ItemWatcher aucHo : ah.values()){
            if (aucHo.isNewItemsRegistered()){
                aucHo.resetItems();
                check = true;
            }
        }
        return check;
    }

    public void returnRejectedBid(){
        for (ItemWatcher itemWatcher : ah.values()){
            double amount = itemWatcher.getAmountReturn();
            availableBalance += amount;
        }
    }

    public void clearAccountId(){
        hasBankId = false;
    }

}
