package checks;

import com.puppycrawl.tools.checkstyle.api.*;

/**
 * Single For Loop Pattern for Checkstyle.
 * 
 * @author Alex Bagini
 * @version 0.1
 *
 */
public class SingleForLoopCheck extends Check {
	
	private String[]	mName;
	
	/**
	 * Return integer array of unique Token Types to visit
	 * For GM Check we consider only Method_Def tokens.
	 */
	@Override
	public int[] getDefaultTokens()
	{
		return new int[]{TokenTypes.METHOD_DEF};
	}
	
	/**
	 * Visit each method def token
	 * 
	 * For each method def token:
	 * 1. Compare the method name with the list of methods to be checked (Set through the XML configuration file)
	 * 2. If it is a match, check the method has applied the Guard Method Pattern
	 * 
	 * @param ast the Method def token to be visited
	 */
	@Override
	public void visitToken(DetailAST ast)
	{
		// Check at least 1 method has been specified to perform the check on
		if (mName == null)
		{
			System.out.println("No methods specified to be checked in XML configuration file.");
			return;
		}
		
		// Find the method identity of the token (i.e. the method name)
		String s = ast.findFirstToken(TokenTypes.IDENT).toString();
		
		// Convert the identity to a readable method string
		String methodName = util.StringUtil.fixName(s);
		
		for (String m: this.mName)
		{
			if (m.equals(methodName))
			{
				// Flags for conditions being met
				boolean f = false, fc = false; // add more
				
				// Determine the slist ast (contains the code for the method)
				DetailAST slist = ast.findFirstToken(TokenTypes.SLIST);
				
				// The method is very very wrong.
				if (slist == null)
				{
					System.out.println("No statement list found in " +methodName);
					// Add log output?
					continue;
				}
								
				DetailAST forAST = checkFor(slist);
				
				if (forAST != null)
				{
					f 	= true;
					fc 	= checkForCondition(forAST);
					
					// Perform other checks now with boolean returns
				}
				
				reportLog(ast, methodName, f, fc);
			}
		}
	}
	
	public void reportLog(DetailAST a, String m, boolean f, boolean fc)
	{
		if (f)
			log(a.getLineNo(), "Suc_SFL_For ''"+m+"'' uses for loop");
		else
			log(a.getLineNo(), "Err_SFL_For ''"+m+"'' does not use for loop");
		
		if (fc)
			log(a.getLineNo(), "Suc_SFL_For ''"+m+"'' uses length of array as looping condition");
		else
			log(a.getLineNo(), "Err_SFL_For ''"+m+"'' does not use length of array as looping condition");
		
		
		// TODO add other booleans in as conditions are created
		if (f && fc)
			log(a.getLineNo(), "Suc_SFL_Pass ''"+m+"'' correctly implements the Single For Loop pattern");
		else
			log(a.getLineNo(), "Err_SFL_Fail ''"+m+"'' incorrectly implements the Single For Loop pattern");
	}
	
	/**
	 * Check the method slist AST for presence of a required for loop
	 * @param a the method's slist AST
	 * @param m the method name
	 * @return the DetailAST of the found for loop, else null
	 */
	public DetailAST checkFor(DetailAST a)
	{	
		return a.findFirstToken(TokenTypes.LITERAL_FOR);
	}
	
	/**
	 * Check the literal for AST has a looping condition based on the length of the array
	 * @param a the literal for AST
	 * @return true if the looping condition is based on length, false otherwise
	 */
	public boolean checkForCondition(DetailAST a)
	{
		return a.branchContains(TokenTypes.DOT);
	}

	/**
	 * Sets the method name property
	 * @param mName string array of methods to perform the 1D For Loop Pattern check for
	 */
	public void setMethodName(String[] mName)
	{
		this.mName = mName.clone();
	}
}
