class forlooptest
{

    public static void main(String[] args)
	{
		int[] a = new int[2];
		a[0] = 1;
		a[1] = 2;
		System.out.println("Result:" +sumloop(a));

    }
	public int sumloop(int[] a)
	{
		int sum = 0;
		for (int i = 0; i < 2; i++)
		{
			sum += a[i];
		}
		return sum;

	}


}
