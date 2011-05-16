package checks;

import com.puppycrawl.tools.checkstyle.api.*;

/**
 * Guard Pattern Check for Checkstyle.
 * 
 * @author Alex Bagini
 * @version 0.1
 * 
 */
public class GuardCheck extends Check
{
	private String mName;

	/**
	 * Return integer array of unique Token Types to visit
	 */
	@Override
	public int[] getDefaultTokens()
	{
		return new int[]{TokenTypes.METHOD_DEF};
	}
	
	/**
	 * Visit each token identified by the default token type
	 * 
	 * For each method def AST
	 * - check if the method is one specified to be checked by the config xml (methodName property)
	 * - slist -> literal if (expr) -> { slist (expr/assignment) }
	 * 
	 * TODO
	 * - ensure that no further assignments are made to vars outside of the if statement
	 * - ensure there is no 'else' component to the if statement
	 */
	@Override
	public void visitToken(DetailAST ast)
	{		
		// Find the identity of the token being visited
		String s = ast.findFirstToken(TokenTypes.IDENT).toString();
		// Convert the identity to a readable method string
		String methodName = s.substring(0, s.indexOf('['));
		
		
		// If the method name is the specified one to check - check it
		if (this.mName.equals(methodName))
		{
			System.out.println("Visiting method: " +methodName);
			
			// Check that the method contains the guard pattern
			// First we look for a statement list
			DetailAST list = ast.findFirstToken(TokenTypes.SLIST);
			
			if (list != null)
			{
				/* Now need to check that the statement list complies with the guard pattern
				 *  - check if statement present
				 *  - check no else statement
				 *  - check variable used in the check is not used elsewhere in the method (?)
				 */
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
				System.out.println("\tNo statement list found - method is strange.");
			}		
		}
		else
		{
			// TODO report not the method we are looking for (?) 
		}
	}
	
	/**
	 * Sets the method name property
	 * @param mName the name of the method in the source Java file to perform the Guard pattern check
	 */
	public void setMethodName(String mName)
	{
		// Need to extend this idea to be able to have multiple methods
		// given as this string then manipulate into a list/array of methods to check
		System.out.println("Method to be checked: " +mName);
		this.mName = mName;
	}

}
