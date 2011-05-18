package checks;

import antlr.collections.ASTEnumeration;
import java.util.*;

import com.puppycrawl.tools.checkstyle.api.*;

/**
 * Guard Pattern Check for Checkstyle.
 * 
 * @author Alex Bagini
 * @version 0.2
 * 
 */
public class GuardMethodCheck extends Check
{
	private String[]	mName;
	private String[] 	guardVars;
	
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
					
					// Check guard variable
					checkGuardVar(ast, ifAST, methodName);
				}
				
				// Message output
				if (!errMsg.isEmpty())
				{
					log(ast.getLineNo(), "Guard method pattern incorrectly implemented in ''"+methodName+"''." +errMsg);
				}
				else
				{
					System.out.println("\tGuard method pattern\t\tPASS");
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
				System.out.println("\tNo if statement found.\tFAIL");
				errMsg +="\n- Missing if statement.";
			}
			else
			{
//**			TODO Check for more than 1 if statement - fail if it has more than 1
				int numIfs 	= slist.getChildCount(TokenTypes.LITERAL_IF);
				System.out.println("\t"+numIfs+" if statement(s) in method.");
				
				
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
			errMsg+="\n- Else statement present.";
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
			System.out.println("\tExpressions outside if statement block.\tFAIL");
			errMsg+="\n- Expressions present outside if block.";
		}
		
	}
	
	/**
	 * Checks the if AST for presence of the specified guard variables (At least 1 inside if statement)
	 * 
	 * @param ast the method AST
	 * @param ifAST the if AST
	 */
	public void checkGuardVar(DetailAST ast, DetailAST ifAST, String mName)
	{
		//*TESTING THIS PART - FIND IDENT IN IF SLIST TO CHECK NOT USED OUTSIDE IF STATEMENT********************
		
		// if statement list
		DetailAST ifSlist 	= ifAST.findFirstToken(TokenTypes.SLIST);
			
		// Know this is how many expressions to check for the guard variable
		int numIfExprs		= ifSlist.getChildCount(TokenTypes.EXPR);
		if (numIfExprs == 0) System.out.println("Shit has hit the fan, there is no exprs in the if block.");
		
		
		/*
		 * Here have the if statement list - need to count at least one instance of the guard var inside here
		 * Then need to check that there is no instance of it outside the if statement (i.e. count inside = count outside)
		 */
		
		// Need to check each EXPR in the ifSlist to find a guard var as the first token
		// from EXPR we know next will be some sort of assign (child) then we look for first IDENT token and compare
		
		
		// See how many expressions in the SLIST so we know how many EXPRs to check for the guard var
		
		// getChildCount only does direct children - need to find ALL
		
		
//		ifAST.findAll(TokenTypes.EXPR);
//		System.out.println("\tnumber of expr in if: " +numIfExpr);
//		
//		int numExpr		= ast.getChildCount(TokenTypes.EXPR);
//		System.out.println("\tnumber of expr: " +numExpr);
//		
//		if (numIfExpr != numExpr)
//		{
//			System.out.println("\tExpressions outside if statement present.\tFAIL");
//			log(ast.getLineNo(), "Guard pattern incorrectly implemented in ''" +mName+ "''. Expressions present outside if statement.");
//		}
		
		
		
		
/*		//Bad assumption that first ident come across is the actual guard var
		//TODO find out how to traverse to OTHER exprs in the tree
		DetailAST ifIdent = ifSlist.findFirstToken(TokenTypes.EXPR).getFirstChild().findFirstToken(TokenTypes.IDENT);
								
		// This is the ident token found in the expr
		// Need to ensure this is not used elsewhere in the method def token
		String var = util.StringUtil.fixName(ifIdent.toString());
		System.out.println("\tInside if statement found ident token: "+var+"\n\tMust check number of occurences of this ident in method.");
		
		ASTEnumeration varTree = ast.findAll(ifIdent);
		int countTotal 	= 0;
		int count		= 0;
		
		// count the instances of the var in the whole method
		while (varTree.hasMoreNodes())
		{
			countTotal++;
			varTree.nextNode();
		}
		
		System.out.println("\tTotal count of "+var+" is "+countTotal);
		
		// count instances of the var in the if statement list
		// NOTE: not working as intended - does not just traverse ifList ast
		ASTEnumeration test = ifSlist.findAll(ifIdent);
		while (test.hasMoreNodes())
		{
			count++;
			test.nextNode();
		}
		
		System.out.println("\tCount of "+var+" in if block is "+count);
		
		// Check that only 1 instance of the variable is used in the method - WRONG ASSUMPTION
		// TODO correct assumption for the use of the var inside the if statement (limit or how to check 'outside' the if statement)
		if (count != countTotal)
		{
			System.out.println("\tTotal count of " +var+" does not equal the count in the if statement list." +
					"\n\tThere should be no other occurences.");
		}

//****************************************************************************************************/
		
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
			System.out.println("Guard var: " +s);
		}
		
	}

}
