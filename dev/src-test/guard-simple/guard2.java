class GuardPattern2 {

	private int balance;
    public static void main(String[] args)
	{
        System.out.println("Guard Pattern.");
		balance = 0;
		balance;
		deposit(10);
    }
	public int deposit(int amount)
	{
		balance += amount;
	}

	public void test(){}
}
