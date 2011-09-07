class GuardPattern2 {

	private int balance;
    public static void main(String[] args)
	{
        System.out.println("Guard Pattern.");
		balance = 0;
		deposit(10);
    }
	public int deposit(int amount)
	{
		if (amount > 0)
		{
			balance += amount;
		}
		else
		{
			balance = balance;
			balance = balance;
		}
		balance = 0;
	}
}
