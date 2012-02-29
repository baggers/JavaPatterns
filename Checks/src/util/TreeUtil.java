package util;

import com.puppycrawl.tools.checkstyle.api.*;

public class TreeUtil {
	
	/* 	int x = 0		variable def -> x equals -> int & expr -> 0 			WRITE X
	 *  int x = y + 2	variable def -> x equals -> expr -> add -> y 2			WRITE X, READ Y
		int y			variable def -> y										?
		y = 0			expr -> equals/assign (operator) -> y 0						WRITE
		x = y			expr -> equals/assign -> x y									WRITE X, READ Y
		x = y + 1		expr -> equals/assign -> x add -> y 1							WRITE X, READ Y
	
		read 	= contained within the right subtree after a variable def or expr-operator
		write	= immediate left child of variable def or expr-operator
		
		NOTE: Context is crucial for the way these methods have been coded.
		
		For example searching for an expression contained within a variable definition does not currently work
		
		Current known working cases:
		1. Search a variable declaration to see if a var has been written to (i.e. initialised)
		2. Search an expression to see if a var has been written to (i.e. assignment of some sort - left side of =)
		3. Search an expression to see if a var has been read  (i.e. right side of = )
	*/
	
	private static String varName;
	private static boolean found;

	/**
	 * Given a tree determine if the var is read within this tree of token type expression or variable definition
	 * @param tree the variable definition or an expression tree
	 * @param var the variable name we are interested in finding
	 * @return true, if the variable is read. false if it is not
	 */
	public static boolean varRead(DetailAST tree, String var)
	{
		// Tested successfully against simple variable declarations and expressions
		
		varName = var;
		found = false;
		
		//NOTE: Can we say that we will only be checking for a variable being read in an expression? Yes for now.

		// Expression tree - checking if var has been read
		System.out.println("varRead checking: " +tree.toString());

		// TODO Variable being read is checked in the return statement
		// Works when passed the return statement tree rather than an explicit expr
		if (tree.getType() != TokenTypes.EXPR && tree.getType() != TokenTypes.VARIABLE_DEF)
		{
			System.out.println("Checking a non expr/var def tree - check this");
		}
		
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
		
		// Here we have the situation where it is a complex statement for example an expression that also contains an expression inside.
		// When this occurs, we cannot assume that the local variable WILL be only on the right subtree.
		
		
		System.out.println("R: Testing complex case for read");
		// TODO test this thoroughly - ensure also works with old examples
		// Working with simple test!
		dfs(op, TokenTypes.IDENT, 0);
		
		return found;
	}
	
	
	/**
	 * Identifies if the particular variable var is written to (assigned) within the given tree
	 * @param tree an expression or variable definition AST
	 * @param var the name of the variable to be searched for
	 * @return true, if the variable is found to be written to. false if it is not
	 */
	public static boolean varWritten(DetailAST tree, String var)
	{
		varName = var;		
		System.out.println("varWritten checking: " +tree.toString());
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
				System.out.println("W: Checking the first child of the variable def to see if the var we are looking for");
				checkVar(id);
				System.out.println("W: Returning found = " + found);
				return found;
			}
			else
			{
				System.out.println("R: Variable def tree has no identifier");
			}
		}
		// Handle expression tree
		if (tree.getType() == TokenTypes.EXPR)
		{
			// have an expression - all we need to do is check if the left child of the op is an ident and then matches 'var'
			
			/* Note
			 	Check for the 2 found cases:
			 	1. Expression without an operator (ONLY in variable defs)	e.g. int x = y; Variable def checks for write to x, y appears as an expression with 1 child.
			 	We want to ignore any expressions of this form as y is not being written to, instead it is read. See above in varRead for further details.
			
			 	2. Expression with an operator (ALL other expr trees)		e.g. x = y + 2;
			*/
			
			// For writing we only care about Case 2. So any expression that looks like Case 1 is disregarded - handle this below.
		
			DetailAST op = tree.getFirstChild();
			
			// Case 1 - NO
			if (tree.getFirstChild().getChildCount() == 0)
			{
					System.out.println("W: Found Case 1 with expression with single child => disregard");
					return false;				
			}
			else
			{
			// Case 2 - GO
				if (op.getFirstChild().getType() == TokenTypes.IDENT)
				{
					System.out.println("W: Checking ops first child which is an ident to see if it is '" +var+"'");
					checkVar(op.getFirstChild());
					System.out.println("W: Returning if var found = " +found);
					return found;
				}
			}
		}
		
		// We have been passed an AST that is neither a variable definition or expression
		System.out.println("Tree argument is not the correct token type - expected either expression or variable definition");
		return false;
	}
	
	/**
	 * Depth first search
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
	
	/**
	 * Check a given identity token to see if it matches our required variable name. If it matches, sets global flag found to true.
	 * @param ident the identity tree
	 */
	public static void checkVar(DetailAST ident)
	{
		String iName = ident.toString();
		// if the ident matches the variable we are searching for - flag it
		//System.out.println("Ident : " + util.StringUtil.fixName(iName));
		if (util.StringUtil.fixName(iName).equals(varName))
			found = true;
	}
	 
}
