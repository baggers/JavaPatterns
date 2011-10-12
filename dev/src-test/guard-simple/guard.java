class GuardPattern {
	private int balance;
    public static void main(String[] args) 
	{
		balance = 0;
		deposit(10);
    }
	
	public void deposit(int amount)
	{
		if (amount > 0)
		{
			balance += amount;
		}
	}
}
