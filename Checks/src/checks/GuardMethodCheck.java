package checks;

import com.puppycrawl.tools.checkstyle.api.*;

/**
 * Guard Method Pattern Check for Checkstyle.
 * 
 * @author Alex Bagini
 * @version 0.3
 * 
 */
public class GuardMethodCheck extends Check
{
	private String[]	mName;
	private String[] 	guardVars;
	private boolean[]	found;
	
	private String		errMsg;

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
			System.out.println("No methods specified to be checked in XML configuration file.");
			return;
		}
		
		errMsg = "";
		
		// Find the identity of the token being visited
		String s = ast.findFirstToken(TokenTypes.IDENT).toString();
		// Convert the identity to a readable method string
		String methodName = util.StringUtil.fixName(s);
		
		// If the method name is the specified to be checked -> perform the 3 checks
		for(String m: this.mName)
		{
			if (m.equals(methodName))
			{
				System.out.println("Visiting method: " +methodName);
				
				// Call other methods to check parts of the method AST
				
				// Check if statement in method
				DetailAST ifAST = checkIf(ast, methodName);
				
				if (ifAST != null)
				{
					// Check no else
					checkElse(ifAST, methodName);
					
					// Check exprs outside if block
					checkExprs(ast);
					
					// Check for guard variable(s)
					checkGuardVar(ifAST, methodName);
				}
				
				// Message output
				if (!errMsg.equals(""))
				{
					log(ast.getLineNo(), "Guard method pattern incorrectly implemented in ''"+methodName+"''." +errMsg);
				}
				else
				{
					System.out.println("\tGuard method pattern implemented.\t\tPASS");
				}				
			}
		}

	}
	
	/**
	 * Checks the method AST for presence of an if statement.
	 * Returns the if AST for use in further checks.
	 * 
	 * @param ast method AST to be checked
	 * @param mName method AST name
	 * @return the if AST if it exists, null otherwise
	 */
	public DetailAST checkIf(DetailAST ast, String mName)
	{
		
		
		DetailAST slist	= ast.findFirstToken(TokenTypes.SLIST);
		
		if (slist != null)
		{
			
			if (!slist.branchContains(TokenTypes.LITERAL_IF))
			{
				System.out.println("\tNo if statement found.\t\t\tFAIL");
				errMsg +="\n\t- Missing if statement.";
			}
			else
			{
//**			TODO Check for more than 1 if statement - fail if it has more than 1
//				int numIfs 	= slist.getChildCount(TokenTypes.LITERAL_IF);
//				System.out.println("\t"+numIfs+" if statement(s) in method.");
				
				
				// if statement present - return the if AST
				return slist.findFirstToken(TokenTypes.LITERAL_IF); 
			}
		}
		else
		{
			System.out.println("\tMethod contains no statement list.");
		}
		return null;
	}
	
	/**
	 * Checks the method AST for presence of an else statement.
	 * 
	 * @param ast the method AST to be checked
	 * @param mName the method AST name
	 */
	public void checkElse(DetailAST ast, String mName)
	{
		if(ast.branchContains(TokenTypes.LITERAL_ELSE))
		{
			System.out.println("\tElse statement found.\tFAIL");
			errMsg+="\n\t- Else statement present.";
		}
		else
		{
			// Else statement not present = good
			// TODO output for no else statement if necessary
		}
		
	}
	
	/**
	 * Checks the method for expressions present outside the if statement block.
	 * 
	 * @param ast the method AST
	 */
	public void checkExprs(DetailAST ast)
	{
		if(ast.findFirstToken(TokenTypes.SLIST).getChildCount(TokenTypes.EXPR) > 0)
		{
			System.out.println("\tExpressions outside if statement block.\t\tFAIL");
			errMsg+="\n\t- Expressions present outside if block.";
		}
		
	}
	
	/**
	 * TODO Re-Implement using DFS/BFS to check for ALL expression tokens in the ifAST
	 * Checks the if AST for presence of the specified guard variables (At least 1 inside if statement)
	 * 
	 * @param ast the method AST
	 * @param ifAST the if AST
	 */
	public void checkGuardVar(DetailAST ifAST, String mName)
	{
		/*
		 *  Concept:
		 *  Traverse if slist tree
		 *  Find all exprs (At present this only searches the immediate children of if slist for exprs)
		 *  Find first child of each expr - flag in found array if matches a guard var
		 *  Output if all guard vars were found
		 */
		
		// Flag found each guard variable - if any remain false at the end of this method, the pattern has not been implemented correctly
		found 	= new boolean[guardVars.length];
			
		// if statement list
		DetailAST ifSlist 	= ifAST.findFirstToken(TokenTypes.SLIST);
		
		// Know this is how many expressions to check for the guard variable(s)
		int numIfExprs	= ifSlist.getChildCount(TokenTypes.EXPR);
		//System.out.println("Num exprs" +numIfExprs);
		
		// Check that there are expressions present
		if (numIfExprs == 0)
		{
			System.out.println("Guarded variables not present in if block.");
			errMsg +="\n\t- Guarded variables not present in if block.";
			return;
		}
		
		DetailAST currentExpr	= ifSlist.findFirstToken(TokenTypes.EXPR);
	//	System.out.println("Intial expr "+currentExpr);
		
		// Using getFirstChild twice skips the intermediate token and provides the variable x: from the expr x = y
		// Other variations of expressions may exist and will simply be ignored at this stage.
		// Need to check x against the specified guard variables
		checkIdent(currentExpr.getFirstChild().getFirstChild());

		// Move to next sibling to avoid infinite looping
		currentExpr = currentExpr.getNextSibling();
	//	System.out.println("next expr after first "+currentExpr);
		
		// Loop through the immediate children of the if statement list looking for the numIfExprs expr tokens
		// Check each expr tokens first var against the expected guard variables
		
		for (int i = 0; i < numIfExprs - 1; i++)
		{
			// loop until found the next expr - should be replaced with PROPER DFS/BFS methodology - simply searches the siblings no further depth-wise
			while(currentExpr.getType() != TokenTypes.EXPR)
			{
				currentExpr = currentExpr.getNextSibling();
	//			System.out.println("cur expr "+currentExpr.toString());
			}
			checkIdent(currentExpr.getFirstChild().getFirstChild());			
		}
		
		// Check if all guard variables have been found
		for (int i = 0; i < found.length; i++)
		{
			if (!found[i])
			{
				System.out.println("\tGuard variable '" +guardVars[i]+ "' not found.");
				errMsg +="\n\t- Guard variable ''" +guardVars[i]+ "'' not found.";
			}
		}
	}
	
	/**
	 * Helper method - Check the name of the given ident token against each of the guard variables.
	 * If the token is a guard variable set found to true.
	 * @param ident the ident token to check
	 */
	private void checkIdent(DetailAST ident)
	{
		for (int i = 0; i < guardVars.length; i++)
		{
			if (util.StringUtil.fixName(ident.toString()).equals(guardVars[i]))
			{
	//			System.out.println("\tFound "+guardVars[i]);
				found[i] = true;
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
	
	public void setGuardVariable(String[] gVar)
	{
		this.guardVars = gVar.clone();
		for(String s: this.guardVars)
		{
			System.out.println("Guard variable to be checked: " +s);
		}
		
	}

}