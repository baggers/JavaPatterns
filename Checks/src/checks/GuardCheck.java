package checks;

import com.puppycrawl.tools.checkstyle.api.*;

public class GuardCheck extends Check
{
	private String mName;

	/**
	 * Return the tokens this check is interested in visiting
	 */
	@Override
	public int[] getDefaultTokens()
	{
		System.out.println("getDefTokens: Guard check (method_def).");
		int[] result = new int[]{TokenTypes.METHOD_DEF};
		System.out.println("Number of token types: " + result.length); // 1 for method def
		return result;
	}
	
	/**
	 * Called for each default token
	 * 
	 * For each method AST we want to see if it is a method that is required to be checked
	 * as per the configuration file property methodName
	 * 
	 * If the method is to be checked, it needs to match the guard pattern AST
	 * Slist 	-> literal_if ( expr) { slist }
	 * expr 	-> operator (ident, value)
	 * 
	 * TODO 
	 * - establish if just checking for an 'if' statement is satisfactory or how to extend this...
	 * - learn how to log information correctly 
	 */
	@Override
	public void visitToken(DetailAST ast)
	{
		// Find the identity of the token being visited
		String s = ast.findFirstToken(TokenTypes.IDENT).toString();
		// Convert the identity to a readable method string
		String methodName = s.substring(0, s.indexOf('['));
		
		System.out.println("Method: " +methodName);
		
		// If the method name is the specified one to check - check it
		if (this.mName.equals(methodName))
		{	
			// Check that the method contains the guard pattern
			// First we look for a statement list
			DetailAST list = ast.findFirstToken(TokenTypes.SLIST);
			
			if (list != null)
			{
				// If the statement list contains an if we have a guard pattern present (?)
				if (!list.branchContains(TokenTypes.LITERAL_IF))
				{
					System.out.println("\tExpected if statement NOT found in " +methodName);
					log(list.getLineNo(), "Guard pattern not present in '" +methodName+ "' method.");
				}
				else
				{
					System.out.println("\tIf statement found in " +methodName+ "method.");
				}
			}
			else
			{
				System.out.println("\tNo statement list found - method is strange.");
			}
				
		}
		else
		{
			// TODO fix this message
			System.out.println("Method named " +methodName+ "did not match the method we are looking for.");
		}
	}
	
	/**
	 * Sets the method name property
	 * @param mName the name of the method to perform the check upon in the source java file
	 */
	public void setMethodName(String mName)
	{
		// Need to extend this idea to be able to have multiple methods
		// given as this string then manipulate into a list/array of methods to check
		System.out.println("Config value for method name " +mName);
		this.mName = mName;
	}

}
