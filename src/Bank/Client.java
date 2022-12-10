package Bank;

import Agent.Agent;
import Auctionhouse.AuctionHouse;

public class Client {
    AuctionHouse auctionHouse;
    Agent agent;

    public Client(AuctionHouse auctionHouse){
        this.auctionHouse = auctionHouse;
    }

    public Client(Agent agent){
        this.agent = agent;
    }
}
