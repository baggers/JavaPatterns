package checks;

import com.puppycrawl.tools.checkstyle.api.*;

/**
 * Guard Method Pattern Check for Checkstyle.
 * 
 * @author Alex Bagini
 * @version 0.4
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
	 * 2. If it is a match, check the method has applied the Guard Method Pattern
	 * 
	 * @param ast the Method def token to be visited
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
//				System.out.println("Visiting method: " +methodName);
				
				// Call other methods to check parts of the method AST
				
				// Check if statement in method
				DetailAST ifAST = checkIf(ast, methodName);
				
				if (ifAST != null)
				{
					// Check no else
					checkElse(ifAST, methodName);
					
					// Check exprs outside if block
					checkExprs(ast, methodName);
					
					// Check for guard variable(s)
					checkGuardVar(ifAST, methodName);
				}
				
				// Message output
				if (!errMsg.equals(""))
				{
//					log(ast.getLineNo(), "Guard method pattern incorrectly implemented in ''"+methodName+"''." +errMsg);
				}
				else
				{
//					System.out.println("\tGuard method pattern implemented.\t\tPASS");
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
//				System.out.println("\tNo if statement found.\t\t\tFAIL");
				log(slist.getLineNo(), "''"+mName+"'' missing if statement");
//				errMsg +="\n\t- Missing if statement.";
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
//			System.out.println("\tMethod contains no statement list.");
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
//			System.out.println("\tElse statement found.\tFAIL");
			log(ast.getLineNo(), "''"+mName+"'' has an else statement present.");
//			errMsg+="\n\t- Else statement present.";
		}
	}
	
	/**
	 * Checks the method for expressions present outside the if statement block.
	 * 
	 * @param ast the method AST
	 */
	public void checkExprs(DetailAST ast, String mName)
	{
		if(ast.findFirstToken(TokenTypes.SLIST).getChildCount(TokenTypes.EXPR) > 0)
		{
//			System.out.println("\tExpressions outside if statement block.\t\tFAIL");
			log(ast.getLineNo(), "''"+mName+"'' has expressions outside if block.");
//			errMsg+="\n\t- Expressions present outside if block.";
		}
		
	}
	
	/**
	 * Checks the if AST for presence of the specified property guard variables (At least 1 occurrence inside if statement list)
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
		treeTraversal(ifSlist, TokenTypes.EXPR);
		
		// Check if all guard variables have been found
		for (int i = 0; i < found.length; i++)
		{
			if (!found[i])
			{
//				System.out.println("\tGuard variable '" +guardVars[i]+ "' not found.");
				log(ifSlist.getLineNo(), "''"+mName+"'' did not guard "+guardVars[i]+".");
//				errMsg +="\n\t- Guard variable ''" +guardVars[i]+ "'' not found.";
			}
		}
	}
	
	/**
	 * Helper method - Check the name of the given ident token against each of the guard variables.
	 * If the token is a guard variable set found to true.
	 * @param ident the ident token to check
	 */
	public void checkIdent(DetailAST ident)
	{
		for (int i = 0; i < guardVars.length; i++)
		{
			if (util.StringUtil.fixName(ident.toString()).equals(guardVars[i]))
			{
				found[i] = true;
			}
		}
	}
	
	/**
	 * AST traversal method. Given a starting AST 'a', traverses the tree looking for the specified 'type' token.
	 * When the token is found checkIdent is called to check the EXPR (default type searched for).
	 * 
	 * TODO Extension to make the method more viable with different token types and what to do when the type is found.
	 * @param a the AST to be traversed
	 * @param type the token type to find in 'a'
	 */
	public void treeTraversal(DetailAST a, int type)
	{
		if (a == null)
			return;
		// Check if EXPR - if it is check for guard variable
		if (a.getType() == type)
		{
			// check the expr to see if it contains the specified guard variable
			// TODO note what situations might this double getFirstChild fail for an EXPR ?
			checkIdent(a.getFirstChild().getFirstChild());
			// return now and continue with the recursive dfs which will search other siblings
			return;
		}
		// Perform the recursive search
		DetailAST currentTree 	= a.getFirstChild();
		if (currentTree == null)
		{
			return;
		}
		
		while(true)
		{
			treeTraversal(currentTree, type);
			currentTree	= currentTree.getNextSibling();
			if (currentTree == null)
				return;
		}
	}	
	
	/**
	 * Sets the method name property
	 * @param mName string array of methods to perform the Guard Pattern check for
	 */
	public void setMethodName(String[] mName)
	{
		this.mName = mName.clone();
	}
	
	/**
	 * Sets the guard variable property
	 * @param gVar string array of guard variables to ensure they appear in the guard methods
	 */
	public void setGuardVariable(String[] gVar)
	{
		this.guardVars = gVar.clone();
	}

}