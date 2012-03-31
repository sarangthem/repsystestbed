package cu.rst.gwt.server.exceptions;

public class WorkflowParserException extends Exception
{

	private static final long serialVersionUID = -4551321984948089994L;

	public WorkflowParserException(String errMsg)
	{
		super(errMsg);
	}
	
	public WorkflowParserException(String errMsg, Throwable e)
	{
		super(errMsg, e);
	}

}
