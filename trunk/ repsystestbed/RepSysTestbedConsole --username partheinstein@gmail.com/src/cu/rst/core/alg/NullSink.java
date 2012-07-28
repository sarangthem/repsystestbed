/**
 * 
 */
package cu.rst.core.alg;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import cu.rst.core.graphs.Graph;
import cu.rst.core.graphs.Graph.Type;
import cu.rst.core.petrinet.Token;

/**
 * @author partheinstein
 *
 */
public class NullSink extends Algorithm
{
	static Logger logger = Logger.getLogger(NullSink.class.getName());

	public NullSink() throws Exception
	{
	}

	@Override
	public ArrayList update(ArrayList<Token> tokens) throws Exception 
	{
		logger.debug("StringSink update() invoked.");
		if(tokens!=null && tokens.size()>0)
		{
			Token t1 = tokens.get(0);
			for(String s : ((ArrayList<String>)t1.m_changes))
			{
				logger.info(s);
			}
		}
		return null;
	}

	@Override
	public boolean assertGraph2ListenType(Graph g) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean assertGraph2OutputType(Graph g) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean assertVariablePrecondition(double variable) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Type getInputGraphType() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Type getOutputGraphType() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
