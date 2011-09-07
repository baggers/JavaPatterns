class forlooptest
{

    public static void main(String[] args)
	{
		int[] a = new int[2];
		a[0] = 1;
		a[1] = 2;
		System.out.println("Result:" +sumloop(a));

    }
	public int maximum(int[] a)
	{
		int max = a[0];
		for (int i = 0; i < a.length; i++)
		{
			if (max < a[i])
				max = a[i];
		}
		return max;

	}


}
