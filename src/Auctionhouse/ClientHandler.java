package Auctionhouse;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;

public class ClientHandler implements Runnable{
    private final Socket socket;
    private final Salesman salesman;
    private final Client client;
    private final ObjectInputStream reader;
    private final HashMap<Integer, ObjectOutputStream> agents;
    private final int bankId;

    public ClientHandler(Socket socket, Salesman salesman, Client client,
                         ObjectInputStream reader, HashMap<Integer, ObjectOutputStream> agents, int bankId){
        this.socket = socket;
        this.salesman = salesman;
        this.client = client;
        this.reader = reader;
        this.agents = agents;
        this.bankId = bankId;
    }

    @Override
    public void run() {
        try{
            while(true){
                AuctionBidManager newItem = (AuctionBidManager) reader.readObject();
                client.add(newItem);
                System.out.println("Received bid from agent");

            }
        }catch (SocketException | EOFException e){
            agents.remove(bankId);
        }catch (IOException | ClassNotFoundException e){
            e.printStackTrace();
        }
    }
}
