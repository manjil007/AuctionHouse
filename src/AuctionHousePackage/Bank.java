package AuctionHousePackage;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class Bank {
    public static final int MAX_ACCOUNT = 10;
    public static final int MAX_AMOUNT = 10;
    public static final int INITIAL_BALANCE = 100;
    String Name;
    String[] NameList = new String[MAX_ACCOUNT];
    private BankAccount[] accounts = new BankAccount[MAX_ACCOUNT];
    private Lock bankLock;
    private Condition availableFund;

    public Bank() {
        for(int i=0; i<accounts.length; i++) {
            accounts[i] = new BankAccount(NameList[i], INITIAL_BALANCE);
        }
    }

    public void transfer ( int from, int to, int amount){
        bankLock.lock();
        try {
            while (accounts[from].getAccountBalance() < amount) {
                availableFund.await();
            }
            accounts[from].withdraw(amount);
            accounts[to].deposit(amount);

            String message = "%s transfered %d from %s to %s. Total balance: %d\n";
            String threadName = Thread.currentThread().getName();
            System.out.println(message, threadName, amount, threadName , threadName, accounts[from].getAccountBalance());
            availableFund.signalAll();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bankLock.unlock();
        }
    }
   /* public int getTotalBalance(){
        bankLock.lock();
        try{
            int total = 0;
            for(int i=0; i< account.length; i++){
                total += accounts[i].getBalance();
            }
            return total;
        }finally{
            bankLock.unlock();
        }
    }*/

}

