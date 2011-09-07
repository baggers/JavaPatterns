class GuardPattern {
	private int balance;
    public static void main(String[] args) {
        System.out.println("Guard Pattern.");
		guard(10);
    }
	public void deposit(int amount)
	{
		if (amount > 0)
		{
			balance += amount;
			balance +1;
		}
		balance+1;
	}
}
