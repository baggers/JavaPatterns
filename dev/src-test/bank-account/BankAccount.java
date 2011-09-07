/**
 * Class BankAccount - a simple model of a bank account
 * designed to illustrate the concepts of classes, objects, and methods
 * @author Rachel Cardell-Oliver based on version by Gordon Royle
 * @version Feb 2011
 */

public class BankAccount {

    private int balance;
    private String accountName;
    private int valueDeposits;
    private int valueWithdrawals;
    private int maximumBalance;
    private int minimumBalance;

    /**
     * Construct a bank account with given initial balance
     * @param accountName unique identifying string
     * @param balance opening balance of account, assumed non-negative
     */
    public BankAccount(String accountName, int balance) {
        this.accountName   = accountName;
        this.balance       = balance;
        this.valueDeposits = balance;
        this.valueWithdrawals = 0;
        this.maximumBalance = balance;
        this.minimumBalance = balance;
    }
    
    /**
     * Deposit amount by adding to balance
     * @param amount must be non-negative
     */
    public void deposit (int amount) {
        if (amount >= 0) {
            balance = balance + amount;
            valueDeposits = valueDeposits + amount;
            if (balance > maximumBalance) {
                maximumBalance = balance;
            }
        }
    }

    /**
     * Withdraw amount by decreasing balance
     * @param amount must be non-degative
     */
    public void withdraw (int amount) {
        if (amount >= 0) {
            balance = balance - amount;
            valueWithdrawals = valueWithdrawals + amount;
            if (balance < minimumBalance){
                minimumBalance = balance;
            }
        }
    }

    /**
     *  Is the account balance negative?
     *  @return boolean true if the acccount balance is strictly less than 0 and false otherwise
     */
    public boolean isOverdrawn() {
        return balance < 0;
    }

    /**
     * Calculate and update interest
     * Interest should be credited if balance is positive and
     * debited if the balance is negative
     * @param rate must be double greater than 0
     */
    public void applyInterest(double rate) {
        if ((rate > 0) && (balance < 0)) {
            withdraw((int) (-balance*rate));
        } 
        if ((rate > 0) && (balance > 0)) {
            deposit((int) (balance*rate));
        }
    }

    /* GETTER METHODS for accessing field values */
    
    public int getBalance() {
        return balance;
    }

    public String getAccountName() {
        return accountName;
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

