package checks;

import com.puppycrawl.tools.checkstyle.api.*;

/**
 * Class used to verify the varRead and varWrite TreeUtil class methods
 * @author Alex Bagini
 * @version 0.1
 * @date Feb 2012
 *
 */
public class TestVarCheck extends Check {
	
	@Override
	public int[] getDefaultTokens() {
		return new int[]{TokenTypes.EXPR};
	}
	
	
	// For each expression or variable def we want to invoke both varRead and varWrite from the TreeUtil class
	// For testing purposes the variable names used were x and y - note these need to be identified in another manner for REAL variables in tested Java submissions
	@Override
	public void visitToken(DetailAST a)
	{
	//	util.TreeUtil.varRead(a, "x");
		util.TreeUtil.varRead(a, "y");
		
		/*
		DetailAST body = a.getFirstChild();
		System.out.println("Get first child "+ body.toString());
			
		// check the immediate children of the statement list
		// should contain some variable defs and expressions based on the var.java test file
		System.out.println("While");
		while(body.getNextSibling() != null)
		{
			System.out.println(body.toString());
			
			if (body.getType() == TokenTypes.EXPR)
			{
				System.out.println("expr check");
//				util.TreeUtil.varWritten(body, "x");
				util.TreeUtil.varRead(body,  "x");
			}
			
			body = body.getNextSibling();
		}
		*/
		
	}

}
