package checks;

import com.puppycrawl.tools.checkstyle.api.*;

/**
 * Guard Pattern Check for Checkstyle.
 * 
 * @author Alex Bagini
 * @version 0.2
 * 
 */
public class GuardCheck extends Check
{
	private String[] mName;

	/**
	 * Return integer array of unique Token Types to visit
	 */
	@Override
	public int[] getDefaultTokens()
	{
		return new int[]{TokenTypes.METHOD_DEF};
	}
	
	/**
	 * Visit each Method Def token
	 * 
	 * At each Method Def token:
	 * 1. Compare the method name with the list of methods to be checked (Set through the XML configuration file)
	 * 2. If it is a match, check the method has applied the Guard Pattern 
	 * 
	 * TODO
	 * - ensure that no further assignments are made to vars outside of the if statement
	 * - add check for the if condition (should fail on if(true) etc)
	 */
	@Override
	public void visitToken(DetailAST ast)
	{
		// Sanity check for no methods specified in xml config to check for Guard Pattern
		if (mName == null)
		{
			System.out.println("No methods specified to be checked.");
			return;
		}
		
		// Find the identity of the token being visited
		String s = ast.findFirstToken(TokenTypes.IDENT).toString();
		// Convert the identity to a readable method string
		String methodName = s.substring(0, s.indexOf('['));
		
		
		// If the method name is the specified to be check - check it
		for(String m: this.mName)
		{
			if (m.equals(methodName))
			{
				System.out.println("Visiting method: " +methodName);
				
				// Check that the method contains the guard pattern
				// First we look for a statement list
				DetailAST list = ast.findFirstToken(TokenTypes.SLIST);
				
				/* Now need to check that the statement list complies with the guard pattern
				 *  - check if statement present
				 *  - check no else statement
				 *  - check variable used in the check is not used elsewhere in the method (?)
				 */
				if (list != null)
				{
					if (!list.branchContains(TokenTypes.LITERAL_IF))
					{
						System.out.println("\tExpected if statement NOT found.\tFAIL");
						log(list.getLineNo(), "Guard pattern not present in '" +methodName+ "' method.");
					}
					else
					{
						// Check no else component
						if (!list.findFirstToken(TokenTypes.LITERAL_IF).branchContains(TokenTypes.LITERAL_ELSE))
						{
							System.out.println("\tGuard pattern found.\t\t\tPASS");
						}
						else
						{
							// Else found which should not be present in guard pattern
							log(list.getLineNo(), "Guard pattern incorrectly implemented in ''" +methodName+ "''. Else statement present.");
						}
					}
				}
				else
				{
					System.out.println("\tNo statement list found - method is strange =S.");
				}		
			}
		}
	}
	
	/**
	 * Sets the method name property
	 * @param mName string array of methods to perform the Guard Pattern check for
	 */
	public void setMethodName(String[] mName)
	{
		this.mName = mName.clone();
		// Verbose message to be removed later
		for(String s: this.mName)
		{
			System.out.println("Method to be checked: " +s);
		}
	}

}
