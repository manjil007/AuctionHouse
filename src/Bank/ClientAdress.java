package Bank;

import java.io.Serializable;

public class ClientAdress implements Serializable {
    private final String ipAdress;
    private final int portNumber;

    public ClientAdress(String ipAdress, int portNumber){
        this.ipAdress = ipAdress;
        this.portNumber = portNumber;
    }

    public int getPortNumber(){
        return portNumber;
    }

    public String getipAdress(){
        return ipAdress;
    }
}
