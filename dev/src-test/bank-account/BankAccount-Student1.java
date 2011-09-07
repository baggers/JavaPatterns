
/**
 * Simple Bank Account
 * 
 * @author (Faraz Ahmed) 
 * @version (19.03.11)
 */
public class BankAccount
{
    // instance variables - replace the example below with your own
        private int balance;
    private String accountName;
     /**
     * Constructor for objects of class BankAccount
     */
    public BankAccount(String accountName, int balance) {
       this.accountName = accountName;
       this.balance = balance;
          }
    {
        // initialise instance variables
       balance = 0;
           }

        public int getBalance() {
        return balance;
    }

    public String getAccountName() {
        return accountName;
    }

        public void deposit(int amount) {
             if(amount <0 ){
           balance=balance;
           System.out.println("Error - Cannot be Negative Value");
           }
            else
           balance = balance + amount;
                              
    }

        public void withdraw(int amount) {
      if(amount <0 ){
          balance=balance;
          System.out.println("Error - Cannot be Negative Value");
           }
            else
            balance = balance - amount;
                      
    }
   
    public boolean isOverdrawn(){
      if (balance < 0){
return true;
} else {
return false;
}
}
}

