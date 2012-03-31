/**
 * 
 */
package cu.rst.gwt.server.exceptions;

/**
 * @author partheinstein
 *
 */
public class WorkflowException extends Exception
{
	public WorkflowException(String errMsg)
	{
		super(errMsg);
	}
	
	public WorkflowException(String errMsg, Throwable e)
	{
		super(errMsg, e);
	}
}
