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
	private String[]	mName, guardVars;
	private boolean[]	found;
	private int			reportStyle;

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
	 * Visit each Method_Def token
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
		
		// Find the method identity of the token (i.e. the method name)
		String s = ast.findFirstToken(TokenTypes.IDENT).toString();
		
		// Convert the identity to a readable method string
		String methodName = util.StringUtil.fixName(s);
		
		// If the method name is the specified to be checked -> perform the 3 checks
		for(String m: this.mName)
		{
			if (m.equals(methodName))
			{				
				// Flags for each condition of success
				boolean i = false, e = false, oi = false, gv = false;
				
				// Check the method contains a statement list
				DetailAST slist	= ast.findFirstToken(TokenTypes.SLIST);
				
				if (slist == null)
				{
					System.out.println("No slist found in " +methodName);
					continue;
				}
				
				// Check if statement in method
				DetailAST ifAST = checkIf(slist);
				
				// If the method contains the minimal required if statement check the following
				if (ifAST != null)
				{
					i	= true;
					e	= checkElse(ifAST);
					oi	= checkOutsideIf(ast);
					gv	= checkGuardVar(ifAST, methodName);
				}
				
				reportLog(reportStyle, ast, methodName, i, e, oi, gv);
			}
		}
	}
	
	/**
	 * Outputs Checkstyle log report on methods implementation of the Guard Method Check
	 */
	private void reportLog(int style, DetailAST a, String m, boolean i, boolean e, boolean oi, boolean gv)
	{
		switch (style) {
		case 0: System.out.println("Lecturer output summary style - TBD"); break;
		case 1:
			if (i)
				log(a.getLineNo(), "Suc_GM_If ''"+m+"'' uses if statement");
			else
				log(a.getLineNo(), "Err_GM_If ''"+m+"'' does not use if statement");
			
			if (e)
				log(a.getLineNo(), "Suc_GM_Else ''"+m+"'' does not have a redundant else");
			else
				log(a.getLineNo(), "Err_GM_Else ''"+m+"'' uses a redundant else");
			
			if (oi)
				log(a.getLineNo(), "Suc_GM_Exprs ''"+m+"'' does not use expressions outside if block");
			else
				log(a.getLineNo(), "Err_GM_Exprs ''"+m+"'' uses expressions outside if block");
			
			if (gv)
				log(a.getLineNo(), "Suc_GM_GuardVar ''"+m+"'' guards specified variables");
			else
				log(a.getLineNo(), "Err_GM_GuardVar ''"+m+"'' does not guard specified variables");
				
			if (i && e && oi && gv)
				log(a.getLineNo(), "Complete_Pass ''"+m+"'' correctly implements Guard Method Pattern");
			else
				log(a.getLineNo(), "Incomplete_Pass ''"+m+"'' incorrectly implements Guard Method Pattern");
			break;
			default: System.out.println("Style undefined - please specify a reportStyle in the XML configuration file.\n" +
					"0 - Lecturer summary output [Codes only]\n" +
					"1 - Student verbose feedback"); break;
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
	private DetailAST checkIf(DetailAST ast)
	{		
		return ast.findFirstToken(TokenTypes.LITERAL_IF);
	}
	
	/**
	 * Checks the method AST for presence of an else statement.
	 * 
	 * @param ast the method AST to be checked
	 * @return true if the branch does not contain an else statement, false otherwise
	 */
	private boolean checkElse(DetailAST ast)
	{	
		return !ast.branchContains(TokenTypes.LITERAL_ELSE);
	}
	
	/**
	 * Checks the method for expressions present outside the if statement block.
	 * 
	 * @param ast the method AST
	 * @return true if there are no expressions present outside the if statement, false otherwise
	 */
	private boolean checkOutsideIf(DetailAST ast)
	{
		return ast.findFirstToken(TokenTypes.SLIST).getChildCount(TokenTypes.EXPR) == 0;
	}
	
	/**
	 * Checks the if AST for presence of the specified property guard variables (At least 1 occurrence inside if statement list)
	 * 
	 * @param ast the method AST
	 * @param ifAST the if AST
	 * @return true if all the guard variables have been found, false otherwise
	 */
	private boolean checkGuardVar(DetailAST ifAST, String m)
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
		
		boolean foundAllGV	= true;
		
		// Check if all guard variables have been found
		for (int i = 0; i < found.length; i++)
		{
			if (!found[i])
			{
				log(ifSlist.getLineNo(), "Err_GM_GVar ''"+m+"'' did not guard "+guardVars[i]);
				foundAllGV = false;
			}
		}
		return foundAllGV;
	}
	
	/**
	 * Helper method - Check the name of the given ident token against each of the guard variables.
	 * If the token is a guard variable set found to true.
	 * @param ident the ident token to check
	 */
	private void checkIdent(DetailAST ident)
	{
		String exprLeftVar = util.StringUtil.fixName(ident.toString());
		for (int i = 0; i < guardVars.length; i++)
		{
			// Check if a guard variable or a this.guard variable
			// E.g. guard variable balance and this.balance
			if (exprLeftVar.equals(guardVars[i]) || exprLeftVar.equals("this."+guardVars[i]));
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
	private void treeTraversal(DetailAST a, int type)
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
	
	public void setReportStyle(int a)
	{
		reportStyle = a;
	}
}