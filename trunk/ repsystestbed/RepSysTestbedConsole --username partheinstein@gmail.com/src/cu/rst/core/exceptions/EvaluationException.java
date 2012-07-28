/**
 * 
 */
package cu.rst.core.exceptions;

/**
 * @author partheinstein
 *
 */
public class EvaluationException extends Exception
{

	public EvaluationException(String msg, Throwable e)
	{
		super(msg, e);
	}

	public EvaluationException(String string)
	{
		super(string);
	}
}
