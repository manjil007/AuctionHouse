package Bank;

import java.io.Serializable;

public class ClientMessage implements Serializable {
    private int agentAcctNo;
    private int aucHouseAccNo;
    private int transferAmount;

    public ClientMessage(int agentAcctNo, int aucHouseAccNo, int transferAmount){
        this.agentAcctNo = agentAcctNo;
        this.aucHouseAccNo = aucHouseAccNo;
        this.transferAmount = transferAmount;
    }

    public int getAgentAcctNo() {
        return agentAcctNo;
    }

    public int getAucHouseAccNo() {
        return aucHouseAccNo;
    }

    public int getTransferAmount() {
        return transferAmount;
    }
}
