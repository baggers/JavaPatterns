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
	private String		aName, initLocalVar;
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
				// Flags for the single for loop checks
				boolean f = false, fc = false, fi = false, fif = false, fr = false, lv = false; // add more
				
				// Determine the statement list AST (contains the code for the method)
				DetailAST slist = ast.findFirstToken(TokenTypes.SLIST);
				
				// The method is very very wrong - it has nothing in it!?
				if (slist == null)
				{
					System.out.println("No statement list found in " +methodName);
					// Add log output?
					continue;
				}
			
				// check statement list AST for local variable max/min = aName[0]
				lv = checkLocalVariable(slist);
//				System.out.println("lvar "+lv);
								
				DetailAST forAST = checkFor(slist);
				// check for AST conditions:
				// 1. for exists
				// 2. for condition uses aName.length
				// 3. for init begins at 1 rather than 0
				// 4. if statement present to update local min/max
				if (forAST != null)
				{
					f 	= true;
					fc 	= checkForCondition(forAST);
//					System.out.println("condition "+fc);
					fi	= checkForInit(forAST);
//					System.out.println("init "+fi);
					//fif	= checkIf(forAST);
//					System.out.println("if "+fif);
				}
				
				// if a local variable has been initialised inside the method - check it is returned correctly
				if (lv)
				{
					fr	= checkReturn(slist);
//					System.out.println("return "+fr);
				}
				reportLog(reportStyle, ast, methodName, f, fc, fi, fif, fr, lv);
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
	 * @param fi boolean for loop initialisation starts at 1
	 * @param fif boolean for loop contains if statement for updating local variables
	 * @param lv boolean local variables present outside for loop (e.g. local max/min)
	 */
	public void reportLog(int style, DetailAST a, String m, boolean f, boolean fc, boolean fi, boolean fif, boolean fr, boolean lv)
	{
		switch(style) {
		case 0: System.out.println("Lecturer output summary style - TBD"); break;
		case 1:
			if (lv)
				log(a.getLineNo(), "Suc_SFL_LocalVar ''"+m+"'' uses a local variable for calculation of the "+m);
			else
				log(a.getLineNo(), "Err_SFL_LocalVar ''"+m+"'' does not uuses a local variable for calculation of the "+m);
		
			if (f)
				log(a.getLineNo(), "Suc_SFL_For ''"+m+"'' uses a for loop");
			else
				log(a.getLineNo(), "Err_SFL_For ''"+m+"'' does not use a for loop");
			
			if (fc)
				log(a.getLineNo(), "Suc_SFL_ForCond ''"+m+"'' uses length of ''"+aName+"'' as looping condition");
			else
				log(a.getLineNo(), "Err_SFL_ForCond ''"+m+"'' does not use length of ''"+aName+"'' as looping condition");
			
			if (fi)
				log(a.getLineNo(), "Suc_SFL_ForInit ''"+m+"'' initialises for loop init with 2nd element in ''"+aName+"''");
			else
				log(a.getLineNo(), "Err_SFL_ForInit ''"+m+"'' does not initiliase for loop init with 2nd element in ''"+aName+"''");
			
			if (fif)
				log(a.getLineNo(), "Suc_SFL_ForIf ''"+m+"'' uses an if statement inside the for loop");
			else
				log(a.getLineNo(), "Err_SFL_ForIf ''"+m+"'' does not use an if statement inside the for loop or includes a redundant else statement");
			
			if (fr)
				log(a.getLineNo(), "Suc_SFL_Return ''"+m+"'' returns the local variable "+m);
			else
				log(a.getLineNo(), "Err_SFL_Return ''"+m+"'' does not return the local variable "+m);
			
			// NOTE: removed fif from this
			if (f && fc && fi && lv && fr)
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
	 * Check the method slist AST for presence of a required for loop
	 * @param a the method's slist AST
	 * @param m the method name
	 * @return the DetailAST of the found for loop, else null
	 */
	private DetailAST checkFor(DetailAST a)
	{	
		return a.findFirstToken(TokenTypes.LITERAL_FOR);
	}
	
	/**
	 * Check the literal for AST has a looping condition based on the length of the array
	 * @param a the literal For AST
	 * @return true if the looping condition is based on length, false otherwise. False implies the use of a hard coded value.
	 */
	private boolean checkForCondition(DetailAST a)
	{
		// When an array name is not specified - simply check the existence of a dot type
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
			// TODO Neater way of doing this would be good - manual tree traversal always necessary???
			DetailAST dot = forCondition.getFirstChild().getFirstChild().findFirstToken(TokenTypes.DOT);
			if  (dot != null)
			{
				//dot AST's first child is the Ident variable aName
				return util.StringUtil.fixName(dot.getFirstChild().toString()).equals(aName);
			}
		}
		return false;
	}
	
	/**
	 * For min/max methods check use of local max/min variable and that it is initialised to the first element of the array
	 * @param a the method statement list AST
	 * @return true, if one of the local variables defined in the method contain an initialisation to the first element of the array
	 */
	private boolean checkLocalVariable(DetailAST a)
	{
		treeTraversal(a, TokenTypes.INDEX_OP);
		return !initLocalVar.equals(null);
	}
	
	/**
	 * When min/max is performed, the temporary variable should be the 0th element of the array. Therefore, the for loop should
	 * loop on all array elements from 1 to array.length-1.
	 * @param a the for literal AST
	 * @return true, if the for loop initial looping value is 1. false otherwise.
	 */
	private boolean checkForInit(DetailAST a)
	{	
		DetailAST init = a.findFirstToken(TokenTypes.FOR_INIT);
		if (init != null)
		{
			// Obtain the for_init -> var_def -> assign -> expr -> num int
			String iValue = init.getFirstChild().findFirstToken(TokenTypes.ASSIGN).getFirstChild().getFirstChild().toString();
			// Compare the value held as the num int to 1
//			System.out.println(iValue+" for init i=1");
			return util.StringUtil.fixName(iValue).equals("1");
		}
		// Something is strange - should be a for loop with a for init..
		return false;
	}

	/**
	 * To update the min/max variable using an if statement comparing the current min/max and array element
	 * @param a the for literal AST
	 * @return true, if the for loop contains an if statement to perform an update on the local variable min/max etc. False otherwise.
	 */
	private boolean checkIf(DetailAST a)
	{
		DetailAST fslist = a.findFirstToken(TokenTypes.SLIST);
		if (fslist != null)
		{
//			System.out.println("Found if statement inside for loop");
			//TODO extend this to check for the local variables when decided upon how to implement

			DetailAST litif = fslist.findFirstToken(TokenTypes.LITERAL_IF);
			if (litif != null)
			{
//				System.out.println(!litif.branchContains(TokenTypes.LITERAL_ELSE)+" else statement not in for loop");
				// check when there is an if statement that there is no else statement - should not be similar to the guard method.
				return !litif.branchContains(TokenTypes.LITERAL_ELSE);
			}
		}
		return false;
	}
	
	/**
	 * Check correct return of the initLocalVar value outside the if statement and for loop.
	 * @param a
	 * @return
	 */
	private boolean checkReturn(DetailAST a)
	{
		String returnVar = a.findFirstToken(TokenTypes.LITERAL_RETURN).getFirstChild().getFirstChild().toString();
//		System.out.println(util.StringUtil.fixName(returnVar).equals(initLocalVar)+" for return");
		return util.StringUtil.fixName(returnVar).equals(initLocalVar);
	}
	
	
	/**
	 * AST traversal method. Given a starting AST 'a', traverses the tree looking for the specified 'type' token.
	 * When the token is found checkIdentAssign is called to check the EXPR (default type searched for).
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
			System.out.println("checking indexop " +a);
			checkIndexOp(a);
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
//			System.out.println("Local var set to " +initLocalVar);
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
}
