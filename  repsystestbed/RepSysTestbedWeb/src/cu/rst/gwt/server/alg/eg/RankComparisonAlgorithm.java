/**
 * 
 */
package cu.rst.gwt.server.alg.eg;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import cu.rst.gwt.server.alg.Algorithm;
import cu.rst.gwt.server.graphs.Graph;
import cu.rst.gwt.server.graphs.Graph.Type;
import cu.rst.gwt.server.graphs.ReputationGraph;
import cu.rst.gwt.server.petrinet.Place;
import cu.rst.gwt.server.petrinet.Token;

/**
 * @author partheinstein
 *
 */
public class RankComparisonAlgorithm extends Algorithm
{
	static Logger logger = Logger.getLogger(RankComparisonAlgorithm.class.getName());
	@Override
	public boolean assertGraph2ListenType(Graph g) throws Exception 
	{
		return true;
	}

	@Override
	public boolean assertGraph2OutputType(Graph g) throws Exception 
	{
		return true;
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

	@Override
	public ArrayList update(ArrayList<Token> tokens) throws Exception 
	{
		Token t1 = null;
		Token t2 = null;
		ArrayList changes = new ArrayList();
		if(tokens!=null && tokens.size() > 1)
		{
			t1 = tokens.get(0);
			t2 = tokens.get(1);
			
			ReputationGraph rg1 = (ReputationGraph) ((Place)t1.m_place).getGraph();
			ReputationGraph rg2 = (ReputationGraph) ((Place)t2.m_place).getGraph();
			logger.debug("RG1:");
			logger.debug(rg1);
			logger.debug("RG2");
			logger.debug(rg2);
			
			changes.add(new String("Yo mama!"));
			
		}
		
		return changes;
		
	}

}
