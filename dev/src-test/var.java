class VarTest {

	public void var()
	{
		int x = 0;
		int y = x;

		x = x + 2;
		y = x + 1;
		x = y - 1;
		x = y + y;
		x = 2 + y;

		if ( x != y )
			x = 2;
		for (int i = 0; i < 3; i++)
		{	
			x = i + 1;
		}

//		int y;
//		y = 1;
//		x = 2;
//		x = x + 2;
//		x + y = 2;
	
/*	x = y;
		x = y + 1;

		x = 2*x + 2*y;
		
		x + y = 10;
*/	
	}
}

