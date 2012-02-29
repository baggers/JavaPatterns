package checks;

import com.puppycrawl.tools.checkstyle.api.*;

/**
 * Single For Loop Pattern for Checkstyle.
 * 
 * @author Alex Bagini
 * @version 1.1
 * 
 * Updated 28th Feb 2012
 *
 */
public class SingleForLoopCheck extends Check {
	
	private String[]	mName;
	private String		aName, initLocalVar, localVariable;
	private int			reportStyle;
	private boolean		checkWrite, checkRead;
	private DetailAST	forAST;
	
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
//				System.out.println("Entering a method");
				
				// Flags for the single for loop checks
				boolean f = false, fc = false, fi = false, fif = false, fr = false, lv = false;
				initLocalVar = null;
				
				// Determine the method statement list AST (contains the code for the method)
				DetailAST methodSlist = ast.findFirstToken(TokenTypes.SLIST);
				
				// The method is very very wrong - it has nothing in it!?
				if (methodSlist == null)
				{
					System.out.println("No statement list found in " +methodName);
					// Add log output?
					continue;
				}
								
				// check use of for loop in method statement list
				f = checkFor(methodSlist);
				
				// For instances where the methd may make calls to outside helper functions
				// this check avoids any exceptions being thrown.
				if (forAST == null)
				{
					System.out.println("No literal for found - strange code.");
					reportLog(reportStyle, ast, methodName, f, fc, lv);
					return;
				}
				
				// local slist containing the for loop (and potentially other code)
				// NOTE This was required for students who may have encapsulated their method in an if statement - to check for an empty array
				// it is out of scope for novices, however, this implementation remains functional for both instances.
				DetailAST localSlist = forAST.getParent();
				
				// check the forAST conditions:
				// 1. for condition uses aName.length
				// 2. for init begins at 1 rather than 0
				// 3. if statement present to update local min/max
				
				//System.out.println("check for loop params");
				if (f)
				{
					fc 	= checkForCondition(forAST);
//					System.out.println("condition "+fc);
					
					// Old check
//					fi	= checkForInit(forAST);
//					System.out.println("init "+fi);
				}
				
				// check existence of a local variable in the local slist
				lv = checkLocalVariable(localSlist);
				
				/* OLD STUFF FOR CHECKING LV
				// if a local variable has been initialised inside the method
				// - check the local variable is updated/assigned inside the for loop
				// - check the local variable is returned
				if (lv)
				{
					fr	= checkReturn(localSlist);
//					System.out.println("return "+fr);
					fif	= checkLVAssignment(forAST);
//					System.out.println("if "+fif);

				}
				*/
				
				// Output report log for results of the full check
				reportLog(reportStyle, ast, methodName, f, fc, lv);
			}
		}
	}
	
	/**
	 * Report output messages to output file or stdout. Here variations in the single for loop can adjusted based on the pattern requirements.
	 * Simply remove or exclude parameter output messages depending on the check conditions required for the assessable methods.
	 * @param a the particular method's complete AST
	 * @param m method name
	 * 
	 * Checks performed on the method:
	 * @param f boolean for loop present
	 * @param fc boolean for loop condition uses aName.length
	 * @param lv boolean local variable present within the method that is written to within for loop and read within the return statement
	 */
	public void reportLog(int style, DetailAST a, String m, boolean f, boolean fc, boolean lv)
	{
		switch(style) {
		case 0: System.out.println("Lecturer output summary style - TBD"); break;
		case 1:
			if (f)
				log(a.getLineNo(), "Suc_SFL_For ''"+m+"'' uses a for loop");
			else
				log(a.getLineNo(), "Err_SFL_For ''"+m+"'' does not use a for loop");
			
			if (fc)
				log(a.getLineNo(), "Suc_SFL_ForCond ''"+m+"'' uses length of ''"+aName+"'' as looping condition");
			else
				log(a.getLineNo(), "Err_SFL_ForCond ''"+m+"'' does not use length of ''"+aName+"'' as looping condition");

			if (lv)
				log(a.getLineNo(), "Suc_SFL_LocalVar ''"+m+"'' uses a local variable that is written within the for loop and read within the return statement");
			else
				log(a.getLineNo(), "Err_SFL_LocalVar ''"+m+"'' does not use a local variable that is written within the for loop and read within the return statement");
		
			
			
			// Old reports
/*			if (fi)
				log(a.getLineNo(), "Suc_SFL_ForInit ''"+m+"'' initialises for loop init with 2nd element in ''"+aName+"''");
			else
				log(a.getLineNo(), "Err_SFL_ForInit ''"+m+"'' does not initiliase for loop init with 2nd element in ''"+aName+"''");

			if (flv)
				log(a.getLineNo(), "Suc_SFL_ForLV ''"+m+"'' assigns/updates the local variable value inside the for loop");
			else
				log(a.getLineNo(), "Err_SFL_ForLV ''"+m+"'' does not assign/update the local variable value inside the for loop");
			
			if (fr)
				log(a.getLineNo(), "Suc_SFL_Return ''"+m+"'' returns the local variable "+m);
			else
				log(a.getLineNo(), "Err_SFL_Return ''"+m+"'' does not return the local variable "+m);
*/			

			// Pass
			if (f && fc && lv)
				log(a.getLineNo(), " Complete_Pass ''"+m+"'' correctly implements the Single For Loop pattern");
			else
				log(a.getLineNo(), " Incomplete_Pass ''"+m+"'' incorrectly implements the Single For Loop pattern");
			break;
			
		default: System.out.println("Style undefined - please specify a reportStyle in the XML configuration file.\n" +
				"0 - Lecturer summary output [Codes only]\n" +
				"1 - Student verbose feedback"); break;
		}
	}
	

	/**
	 * Check for the instance of a for loop within the method statement list
	 * @param a the method statement list
	 * @return
	 */
	private boolean checkFor(DetailAST a)
	{	
		forAST = null;
//		System.out.println(a);
		if(a.branchContains(TokenTypes.LITERAL_FOR))
		{
//			System.out.println("contains for");
			
			// Search the method statement list to a literal for
			// Set this for loop to the forAST global variable for use in other functions
			dfs(a, TokenTypes.LITERAL_FOR, 2);
//			System.out.println("tree traversal done");
			return forAST != null;
		}
		return false;
	}
	
	/**
	 * Check the literal for AST has a looping condition based on the length of the array
	 * @param a the literal For AST
	 * @return true if the looping condition is based on length, false otherwise. False implies the use of a hard coded value.
	 */
	private boolean checkForCondition(DetailAST a)
	{
		// When an array name is not specified - simply check the existence of a dot type for the array
		if (aName == null)
		{
			return a.findFirstToken(TokenTypes.FOR_CONDITION).branchContains(TokenTypes.DOT);
		}
		// Grab the for loop condition AST i.e. for ( a ; b ; c ) - the b portion
		// If the for condition exists and contains the dot type representing some array.length
		// check that the specified array variable matches the expected aName
		DetailAST forCondition = a.findFirstToken(TokenTypes.FOR_CONDITION);
		
		if (forCondition != null && forCondition.branchContains(TokenTypes.DOT))
		{
			// Iterate down to the dot level of the for condition AST. For condition -> expr -> operator (i < x.length)
			DetailAST dot = forCondition.getFirstChild().getFirstChild().findFirstToken(TokenTypes.DOT);
			if (dot != null)
			{
				//dot AST's first child is the Ident variable aName
				return util.StringUtil.fixName(dot.getFirstChild().toString()).equals(aName);
			}
		}
		return false;
	}

	
	/**
	 * Checks a method that uses a for loop for the appropriate use of a local variable
	 * For example using max/min to track the highest/lowest value in an array
	 * This check involves a 3 step check
	 * 1. Check that a local variable has been defined - find this variable name
	 * 2. Check that this local variable is written to within the for loop
	 * 3. Check that this local variable is read within the return statement
	 * @param a the methods statement list
	 * @return true if at least one variable within the method satisfies the above three conditions, false if NONE satisfy
	 */
	private boolean checkLocalVariable(DetailAST a)
	{
		/*	taking a which is the method statement list
		 * 	look for declared variables - first they should be children of the statement list
		 * 	once found - proceed to perform the other two checks
		 * 	if satisfies both checks return true
		 */
		
		// First we assume that local variables will be contained within the immediate children of the method tree
		// Note: This should be extended at some stage to include any variable definition
		DetailAST slistChildren = a.getFirstChild();
		if (slistChildren == null)
		{
			System.out.println("No children for the method statement list - weird.");
		}
		// Loop over the immediate children of the statement list looking for the variable definitions
		System.out.println("Starting while loop");
		while (slistChildren.getNextSibling() != null)
		{
			System.out.println("Checking child " + slistChildren.toString());
			if (slistChildren.getType() == TokenTypes.VARIABLE_DEF)
			{
				System.out.println("Found variable definition.");
				localVariable = util.StringUtil.fixName(slistChildren.findFirstToken(TokenTypes.IDENT).toString());
				System.out.println("Local variable to check: " + localVariable);
				
				System.out.println("Setting checkWrite/Read to false");
				checkWrite 	= false;
				checkRead 	= false;
				
				if (a.findFirstToken(TokenTypes.LITERAL_FOR) == null)
				{
					System.out.println("No FOR found in immediate children - check");
					continue;
				}
				
				if (a.findFirstToken(TokenTypes.LITERAL_RETURN) == null)
				{
					System.out.println("No RETURN found in immediate children - check");
					continue;
				}
				
				// Here we need to check for any expressions or variable definitions within the for loop and/or return statementS
				
				System.out.println("Setting for slist");
				DetailAST forSlist = a.findFirstToken(TokenTypes.LITERAL_FOR).findFirstToken(TokenTypes.SLIST);
				
				// Use dfs to search the for statement list for expressions and variable definitions
				System.out.println("Calling dfs for exprs on for slist");
				dfs(forSlist, TokenTypes.EXPR, 3);
				System.out.println("Calling dfs for var defs on for slist");
				dfs(forSlist, TokenTypes.VARIABLE_DEF, 3);
				
				// dfs will set checkWrite to true if the current localVariable is written to within the for loop
				
				// The return statements expression to check that the localVariable is read
				// TODO test against any variations of return statements - assumed tree looks like "LITERAL RETURN -> EXPR -> ________"
				DetailAST returnStatement = a.findFirstToken(TokenTypes.LITERAL_RETURN);
				
				System.out.println("Calling dfs on exprs for return statement");
				dfs(returnStatement, TokenTypes.EXPR, 4);
				
				// As soon as a local variable passes both checks - we have a suitable use of local variable for the method
				if (checkWrite && checkRead)
				{
					System.out.println("Local variable " +localVariable + " is written and read correclty");
					return true;
				}
			}
			slistChildren = slistChildren.getNextSibling();
		}
	
		// Unable to find a local variable that satisfied the requirements of the check
		return false;
	}	
	
	/**
	 * Method that checks a given DetailAST (From the dfs method this will be an EXPR or VAR_DEF) to see if it contains the current
	 * local variable being checked for proper use within the method.
	 * Calls the TreeUtil method varWritten to identify if the localVariable is written to within the EXPR or VAR_DEF AST
	 * @param a A DetailAST EXPR or VAR_DEF
	 */
	private void checkLVWrite(DetailAST a)
	{
		// Called when either an EXPR or VAR DEF is found within the for loop
		System.out.println("CheckLVWrite on " +a.toString());
		checkWrite 	= util.TreeUtil.varWritten(a, localVariable);
		
	}
	
	private void checkLVRead(DetailAST a)
	{
		// Called when an EXPR is found within the return statement to check if the current lv is read
		System.out.println("CheckLVRead on " +a.toString());
		checkRead = util.TreeUtil.varRead(a, localVariable);
		System.out.println("CheckRead = " +checkRead);
	}
	
	/**
	 * AST traversal method. Given a starting AST 'a', traverses the tree looking for the specified 'type' token.
	 * When the token is found checkIdentAssign is called to check the EXPR (default type searched for).
	 * @param a the AST to be traversed
	 * @param type the token type to find in 'a'
	 */
	private void dfs(DetailAST a, int type, int method)
	{
		// halt dfs if we have already confirmed a write on the local variable
		if (method == 3 && checkWrite)
		{
			System.out.println("Halting DFS as checkWrite already true");
			return;
		}
		
		if (method == 4 && checkRead)
		{
			System.out.println("Halting DFS as checkRead already true");
			return;
		}
		
		if (a == null)
		{
			return;
		}
		// Check if EXPR - if it is check for guard variable
		if (a.getType() == type)
		{
			// Determines the method to apply to the found AST match
			switch (method) {
			case 0: checkIndexOp(a); break;
//			case 1: checkLV(a); break; // old
			case 2: forAST = a; break;
			
			// Found EXPR or VAR DEF which may contain the local variable
			case 3: checkLVWrite(a); break;
			
			// Found EXPR to be checked for read on local variable in return statement
			case 4: checkLVRead(a); break;
			}
			return;
		}
		
//		System.out.println("Just switched on token: " +a.toString());
		
		// Perform the recursive search
		DetailAST currentTree 	= a.getFirstChild();
		
		
		if (currentTree == null)
		{
//			System.out.println(a.toString()+" cannot get first child");
			return;
		}
		
//		System.out.println("Child of a is currentTree = " +currentTree.toString());
	
		while(true)
		{
			dfs(currentTree, type, method);
			currentTree	= currentTree.getNextSibling();
			if (currentTree == null)
				return;
//			else
//				System.out.println("currentTree now is = " +currentTree.toString());
		}
	}
	

	/**
	 * 
	 * @param indOp
	 */
	private void checkIndexOp(DetailAST indOp)
	{
		// some dummy vars
		String arrName="", val="", temp="";
		
		// check the correct index op on the array we are interested in
		DetailAST array = indOp.findFirstToken(TokenTypes.IDENT);
		if (array != null)
		{
//			System.out.println("check array");
			arrName = util.StringUtil.fixName(array.toString());
		}
		
		// check the value being assigned in the index op - preferably 0 but may be changed to suit or even removed(?)
		DetailAST value = indOp.findFirstToken(TokenTypes.EXPR).getFirstChild();
		if (value != null)
		{
//			System.out.println("check val");
			val = util.StringUtil.fixName(value.toString());
		}
		
		// check that the array name and value are valid to progress to finding the local variable name
		if (!arrName.equals(aName) || !val.equals("0"))
			return;
		
		// local variable shoudl exist, determine what form of variable declaration used
		int pType = indOp.getParent().getType();

		// determine the local variable identity, if possible
		if (pType == TokenTypes.ASSIGN)
		{
//			System.out.println("Found assign styled variable index op");
			temp = indOp.getParent().findFirstToken(TokenTypes.IDENT).toString();
			initLocalVar = util.StringUtil.fixName(temp);
			System.out.println("Local var set to " +initLocalVar);
		}
		else if (pType == TokenTypes.EXPR)
		{
//			System.out.println("Found expr styled variable index op");
			int check = indOp.getParent().getParent().getPreviousSibling().getType();
//			System.out.println("got check type");
			if (check == TokenTypes.IDENT)
			{
				initLocalVar = util.StringUtil.fixName(indOp.getParent().getParent().getPreviousSibling().toString());
//				System.out.println("Local var set to " +initLocalVar);
			}
		}
		else
		{
//			System.out.println("Something strange has occurred!");
		}
	}

	
	/**
	 * Sets the method name property
	 * @param mName string array of methods to perform the 1D For Loop Pattern check for
	 */
	public void setMethodName(String[] mName)
	{
		this.mName = mName.clone();
	}
	
	/**
	 * Sets the array name property
	 * @param aName string of the array name that is being processed
	 */
	public void setArrayName(String aName)
	{
		this.aName = aName;
	}
	
	public void setReportStyle(int a)
	{
		reportStyle = a;
	}
	
	
	
	
	
	
	
	/* PREVIOUS VERSION CODE -------------------------------------------------------------------------------------------------
	 * 
	 * 	
	/**-------------------------------------------------------------------------------
	 * OLD - REMOVE LATER
	 * For min/max methods check use of local max/min variable and that it is initialised to the first element of the array
	 * @param a the method statement list AST
	 * @return true, if one of the local variables defined in the method contain an initialisation to the first element of the array
 
  	private boolean checkLocalVariable(DetailAST a)
	{
		//System.out.println("checkLV method");
		// searching for an index op for assignment of LV e.g. max = obs[0] - not guaranteed.
		dfs(a, TokenTypes.INDEX_OP, 0);
		return initLocalVar != null;
	}
	

	private void checkLV(DetailAST i)
	{
		String iName = i.toString();
		if (util.StringUtil.fixName(iName).equals(initLocalVar))
		{
			LVassigned = checkParentofLV(i);
		}
	}
	
	/**
	 * Checks the parent of the identity token - this is used to check how the local variable is updated in the for loop
	 * @param i the identity AST
	 * @return true, if the Local variable parent matches at least one of the expected update types (i.e. <, >, =). false otherwise.
	 
	private boolean checkParentofLV(DetailAST i)
	{
		boolean result = false;
		// get parent of the identity
		int parent = i.getParent().getType();
		
		// the types to check over
		// idea to make this flexible as possible - simply change these types and it will check them against the parent
		int[] types = {TokenTypes.ASSIGN, TokenTypes.GT, TokenTypes.LT};
		
		// OR all the types against the parent type - if there is at least one it will be set to true
		for(int t: types)
		{
			result = result || (parent == t);
		}
		return result;	
	}
	
	//******** OLD LOCAL VARIABLE CHECK CODE ******************************
	 * 
	 * 
	 * 
	 * Checks variables in the method to see if a local variable suitable to the problem has been used correctly
	 * Correct use involves:
	 * 1. Declaring and assigning it inside the method
	 * 2. Writing (updating) its value somewhere within the for loop
	 * 3. Returning an expression that contains the variable
	 * 
	 * Example: Using a max/min local variable to determine smallest/largest value in array
	 * @param a The method AST
	 * @return true, if a variable was found to contain the 3 conditions. false, if no variables matched all conditions
	 
		private boolean checkVariable(DetailAST a)
		{
			boolean pass = false;
			// Assume that all considered variables declared will have a corresponding assign.
			// Two forms:
			// 1. int x; x = 0; [variable def and expression with assign]
			// 2. int x = 0;	[variable def with assign]
			// Both will have an assignment token
			dfs(a, TokenTypes.ASSIGN, 4);
			return pass;
		}
		
//		/**
//		 * When min/max is performed, the temporary variable should be the 0th element of the array. Therefore, the for loop should
//		 * loop on all array elements from 1 to array.length-1.
//		 * @param a the for literal AST
//		 * @return true, if the for loop initial looping value is 1. false otherwise.
//		 */
//		private boolean checkForInit(DetailAST a)
//		{	
//			DetailAST init = a.findFirstToken(TokenTypes.FOR_INIT);
//			if (init != null)
//			{
//				// Obtain the for_init -> var_def -> assign -> expr -> num int
//				String iValue = init.getFirstChild().findFirstToken(TokenTypes.ASSIGN).getFirstChild().getFirstChild().toString();
//				// Compare the value held as the num int to 1
////				System.out.println(iValue+" for init i=1");
//				return util.StringUtil.fixName(iValue).equals("1");
//			}
//			// Something is strange - should be a for loop with a for init..
//			return false;
//		}
	//
//		/**
//		 * To update the min/max variable using an assignment inside the for loop
//		 * @param a the for literal AST
//		 * @return true, if the for loop contains an if statement to perform an update on the local variable min/max etc. False otherwise.
//		 */
//		private boolean checkLVAssignment(DetailAST a)
//		{
//			LVassigned = false;
//			DetailAST fslist = a.findFirstToken(TokenTypes.SLIST);
//			if (fslist != null)
//			{
//				//check if the LV is being accessed/updated inside the for loop
//				dfs(a, TokenTypes.IDENT, 1);
//				return LVassigned;
//			}
//			return false;
//		}
	//	
//		/**
//		 * Check correct return of the initLocalVar value outside the if statement and for loop.
//		 * @param a the local statement list AST
//		 * @return true if the return statement, returns the discovered local variable
//		 */
//		private boolean checkReturn(DetailAST a)
//		{
//			// Manual traveral of the statement list to find the return statement and check what is being returned - should be the local variable
//			String returnVar = a.findFirstToken(TokenTypes.LITERAL_RETURN).getFirstChild().getFirstChild().toString();
////			System.out.println(util.StringUtil.fixName(returnVar).equals(initLocalVar)+" for return");
//			return util.StringUtil.fixName(returnVar).equals(initLocalVar);
//		}
	
}
