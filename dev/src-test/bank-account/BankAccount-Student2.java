
/**
 * Write a description of class BankAccount here.
 * 
 * @author (Nigel Ang) 
 * @version (100311)
 */
public class BankAccount
{
    private int balance;
    private String accountName;
    private int valueDeposits;
    private int valueWithdrawals;
    private int maximumBalance;
    private int minimumBalance;



    /**
     * Constructor for objects of class BankAccount
     */

   public BankAccount(String accountName, int balance)
    {
        this.accountName = accountName;
        this.balance = balance;
        
    }

    
    public int getBalance()
    {
        return balance;
    }
    
    public String getAccountName() 
    {
        return accountName;
    }
    
    public void deposit(int amount)
    {
        if (amount >= 0){
        balance = balance + amount;
    }
    }
    
    public void withdraw(int amount)
    {
        if (amount >=0){
        balance = balance - amount;
    }
    }
      
    public boolean isOverdrawn()
    {
        if (balance >= 0) 
        {
        return false;
        }
        return true;
    }
    
   public int getValueDeposits() {
        return valueDeposits;
    }

    public int getValueWithdrawals() {
        return valueWithdrawals;
    }

    public int getMinimumBalance() {
        return minimumBalance;
    }

    public int getMaximumBalance() {
        return maximumBalance;
    }
 
}
    
