package checks;

import com.puppycrawl.tools.checkstyle.api.*;

public class MethodLimitCheck extends Check
{
    private static final int DEFAULT_MAX = 30;
    private int max = DEFAULT_MAX;

    @Override
    public int[] getDefaultTokens()
    {
        return new int[]{TokenTypes.CLASS_DEF, TokenTypes.INTERFACE_DEF};
//		for (int i = 0; i < test.length; i++)
//		{
//			System.out.println("DefaultToken[" + i +"] = " + test[i]);
//		}
//		return test; 	
    }

    @Override
    public void visitToken(DetailAST ast)
    {
        // find the OBJBLOCK node below the CLASS_DEF/INTERFACE_DEF
        DetailAST objBlock = ast.findFirstToken(TokenTypes.OBJBLOCK);
        // count the number of direct children of the OBJBLOCK
        // that are METHOD_DEFS
        int methodDefs = objBlock.getChildCount(TokenTypes.METHOD_DEF);
        System.out.println("method def = " +methodDefs);
        // report error if limit is reached
        if (methodDefs > this.max) {
            log(ast.getLineNo(),
                "Too many methods, only " + this.max + " are allowed. You had " +methodDefs+ " methods.");
        }
	}
	
	public void setMax(int limit)
	{
		max = limit;
	}
}
