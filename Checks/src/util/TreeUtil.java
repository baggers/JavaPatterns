package util;

import com.puppycrawl.tools.checkstyle.api.*;

public class TreeUtil {
	
	/* 	int x = 0		variable def -> x equals -> int & expr -> 0 			WRITE X
	 *  int x = y + 2	variable def -> x equals -> expr -> add -> y 2			WRITE X, READ Y
		int y			variable def -> y										?
		y = 0			expr -> equals (operator) -> y 0						WRITE
		x = y			expr -> equals -> x y									WRITE X, READ Y
		x = y + 1		expr -> equals -> x add -> y 1							WRITE X, READ Y
	
		read 	= contained within the right subtree after a variable def or expr-operator
		write	= immediate left child of variable def or expr-operator
		
		NOTE: Context is crucial for the way these methods have been coded.
		
		For example searching for an expression contained within a variable definition does not currently work
		
		Current known working cases:
		1. Search a variable declaration to see if a var has been written to (i.e. initialised)
		2. Search an expression to see if a var has been written to (i.e. assignment of some sort - left side of =)
		3. Search an expression to see if a var has been read  (i.e. right side of = )
		
		
			
		TODO At present - not breaking when variable is found in dfs - redo flag found
	*/
	
	private static String varName;
	private static boolean found;

	/**
	 * Given a tree determine if the var is read within this tree
	 * @param tree the variable definition or an expression tree
	 * @param var the variable name we are interested in finding
	 * @return true if the variable is read, false if it is not
	 */
	public static boolean varRead(DetailAST tree, String var)
	{
		// Tested successfully against simple variable declarations and expressions
		
		varName = var;
		found = false;
		
		//NOTE: Can we say that we will only be checking for a variable being read in an expression? Yes for now.

/*		// A variable declaration may contain an expression with a variable that is read
		if (tree.getType() == TokenTypes.VARIABLE_DEF)
		{
			// do nothing
			//System.out.println("R: variable def - come back to.");
			return false;
		}
*/
		// Expression tree - checking if var has been read
		System.out.println();
		System.out.println(tree.toString());
		
		
		// Case 1: Operator -> Expression -> Single child (var or int)
		if (tree.getFirstChild().getChildCount() == 0)
		{
			System.out.println("R: Case 1");
			if (tree.getFirstChild().getType() == TokenTypes.IDENT)
			{
				System.out.println("R: Checking for expression with single var");
				checkVar(tree.getFirstChild());
				System.out.println("R: Finished checking single var - found = "+found);
				return found;
			}
			else
			{
				System.out.println("R: Invalid case 1 - expression with single non-var");
				return false;				
			}
		}
		
		
		// Case 2: Standard expression of form expression -> operator -> operands
		
		// Get first child of the expression = our operator
		DetailAST op = tree.getFirstChild();
		
		// if the operators first child (left child) is of type ident we have an expression of the form x = _____________
		// TODO may need child count check for greater robustness! or handle any other variations
		
		// we are then able to proceed and check the right subtree of the operator for the presence of 'var'
		if (op.getFirstChild().getType() == TokenTypes.IDENT)
		{
			// run dfs on right subtree of the operator
			System.out.println("R: Calling dfs on operator right subtree");
			dfs(op.getLastChild(), TokenTypes.IDENT, 0);
	
			// found is set if the variable was present in the operator right child subtree
			System.out.println("R: Finished dfs - found = " + found);
			return found;
		}
		else
		{
			System.out.println("R: Expression contains a more complex left side of the operator - fix/check.");
		}
		
		System.out.println("R: Something strange");
		
		return false;
	}
	
	public static boolean varWritten(DetailAST tree, String var)
	{
		varName = var;
		System.out.println("var is: " + var);
		found = false;
	
		
		// WORKING
		if (tree.getType() == TokenTypes.VARIABLE_DEF)
		{
			// writing to the variable requires vardef -> x operator -> _______
			// other variation is vardef -> x which is not considered writing to the variable(?) CHECK
			
			// check the vardef has two children then check the first child matches
			
			DetailAST id = tree.findFirstToken(TokenTypes.IDENT);
			if (id != null)
			{
				// Looking good
				System.out.println("W: Checking the first child of the variable def");
				checkVar(id);
				System.out.println("W: Returning found = " + found);
				return found;
			}
			else
			{
				System.out.println("R: Variable def tree has no identifier");
			}
		}
		// TODO Fails some expression trees with only 1 child - check
		else
		{
			// have an expression - all we need to do is check if the left child of the op is an ident and then matches 'var'
			
			// Note
			// Check for the 2 found cases:
			// 1. Expression without an operator (ONLY in variable defs)	e.g. int x = y; Where y is read and has the tree (= -> expr -> y) with no intermediate operator
			// 2. Expression with an operator (ALL other expr trees)	e.g. x = y + 2; Where y is read and has the tree (= -> expr -> y , 2)
			//															e.g. x = y;     Where y is read and has the tree (expr -> = -> x, y)
			
			// For writing we only care about Case 2. So any expression that looks like Case 1 is disregarded - handle this below.
		
			DetailAST op = tree.getFirstChild();
			
			// Case 1 - NO
			if (tree.getFirstChild().getType() == TokenTypes.IDENT)
			{
				System.out.println("W: Found Case 1 and disregarded");
				return false;
			}
			else
			{
			// Case 2 - GO
				if (op.getFirstChild().getType() == TokenTypes.IDENT)
				{
					System.out.println("W: Checking ops first child which is an ident to see if it is 'var'");
					checkVar(op.getFirstChild());
					System.out.println("W: Returning if var found = " +found);
					return found;
				}
			}
		}
		System.out.println("Something weird has occurred");
		
		
		return false;
	}
	
	/**
	 * depth first search
	 * @param a the tree to be searched
	 * @param type the token type to be searched for
	 * @param method what method to call when a token of the particular type is found
	 */
	private static void dfs(DetailAST a, int type, int method)
	{
		if (a == null)
		{
			return;
		}
		
		if (a.getType() == type)
		{
			// Determines the method to apply to the found AST match
			switch (method) {
				case(0): checkVar(a); break;
			}
			return;
		}
		// Perform the recursive search
		DetailAST currentTree 	= a.getFirstChild();
		if (currentTree == null)
		{
			return;
		}
		
		// TODO CHECK THIS - CHANGED FROM TRUE TO !FOUND
		while(!found)
		{
			dfs(currentTree, type, method);
			currentTree	= currentTree.getNextSibling();
			if (currentTree == null)
				return;
		}
	}
	
	// Check if the ident matches the var we are interested in
	public static void checkVar(DetailAST ident)
	{
		String iName = ident.toString();
		// if the ident matches the variable we are searching for - flag it
		System.out.println("Ident : " + util.StringUtil.fixName(iName));
		if (util.StringUtil.fixName(iName).equals(varName))
			found = true;
	}
	 
}
