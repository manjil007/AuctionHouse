package AuctionHousePackage;

import java.io.FileNotFoundException;

public class BankAccount implements Runnable{
    private int accountNum;
    private String customerName;
    private double balance;
    private static int noOfAccounts=0;


    public BankAccount(String abc, double xyz){
        customerName=abc;
        balance= xyz;
        noOfAccounts++;
        accountNum = noOfAccounts;
    }
    public String getAccountInfo(){
        return "Account number: "+ accountNum +"\nCustomer Name:" + customerName + "\nBalance:" + balance +"\n";
    }
    public int getAccountNum(){
        return accountNum;
    }
    public double getAccountBalance(){
        return balance;
    }
    public String getAccountName(){
        return customerName;
    }
    public void deposit(double amount){
        if(amount <= 0){
            System.out.println("Amount to be deposited should be positive");
        }else{
            balance = balance + amount;
        }
    }
    public void withdraw(double amount){
        if(amount <= 0){
            System.out.println("Amount to be withdraw should be positive");
        }else{
            balance = balance - amount;
        }
    }
    @Override
    public void run(){
        System.out.println(Thread.currentThread().getName()
                + ", executing run() method!");
        try{
            throw new FileNotFoundException();
        }catch(FileNotFoundException e){
            System.out.println("Must catch here!");
            e.printStackTrace();
        }
        int r = 1/0;
    }

}
