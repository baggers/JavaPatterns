package checks;

import com.puppycrawl.tools.checkstyle.api.*;

/**
 * Checks Java methods for use of idempotent assignments - i.e. balance = balance.
 * Logs occurence of idempotent assignments identified in the Java source code submitted.
 * @author Alex Bagini
 *
 */
public class IdempotentCheck extends Check {
	
	private int count;

	@Override
	public int[] getDefaultTokens() {
		return new int[]{TokenTypes.METHOD_DEF};
	}
	
	@Override
	public void visitToken(DetailAST a)
	{
		count = 0;
		
		// Find the method identity of the token (i.e. the method name)
		String s = a.findFirstToken(TokenTypes.IDENT).toString();
		// Convert the identity to a readable method string
		String methodName = util.StringUtil.fixName(s);
		
		// for each method DetailAST search for assignment nodes
		dfs(a, TokenTypes.ASSIGN);
		if (count != 0)
			log(a.getLineNo(), "Identified "+count+" idempotent assignments in " +methodName);
	}
	
	/**
	 * Depth first search
	 * @param a the DetailAST to be searched
	 * @param type the nodes token type to be searched for
	 */
	private void dfs(DetailAST a, int type)
	{
		if (a == null)
			return;
		
		// Found type interested in
		if (a.getType() == type)
		{
			checkAssign(a);
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
			dfs(currentTree, type);
			currentTree	= currentTree.getNextSibling();
			if (currentTree == null)
				return;
		}
	}
	
	private void checkAssign(DetailAST a)
	{
		// if the two children of an assignment node are identifiers - assume a possible idempotent assignment
		if (a.getChildCount(TokenTypes.IDENT) == 2)
		{
			// convert the identifiers to readable strings
			String identOne = util.StringUtil.fixName(a.getFirstChild().toString());
			String identTwo = util.StringUtil.fixName(a.getFirstChild().getNextSibling().toString());
			
			// compare to see if the strings match, therefore idempotent assignment identified
			if (identOne.equals(identTwo))
			{
				log(a.getFirstChild().getLineNo(), "Assignment: "+identOne+" = "+identTwo);
				count++;
			}
		}		
	}
	

}
