package checks;

import com.puppycrawl.tools.checkstyle.api.*;

/**
 * Constructor Pattern custom check for Checkstyle.
 * @author Alex Bagini
 * @version 0.1
 *
 */
public class ConstructorPattern extends Check {

	/**
	 * @return integer array containing the types of tokens/nodes for the constructor pattern check
	 * Interested in methods and the instance variable definitions
	 */
	@Override
	public int[] getDefaultTokens() 
	{
		return new int[]{TokenTypes.METHOD_DEF, TokenTypes.VARIABLE_DEF};
	}

	/**
	 * Visit each token and ensure that at least once all the instance variables have been given intial values
	 * 
	 * @param ast the token/node to be visited
	 */
	@Override
	public void visitToken(DetailAST ast)
	{
		
	}
	
}
