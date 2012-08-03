package cu.rst.core.alg;

import java.util.ArrayList;
import java.util.Collections;

import cu.rst.core.graphs.Agent;
import cu.rst.core.graphs.Graph;
import cu.rst.core.graphs.Graph.Type;
import cu.rst.core.graphs.RG;
import cu.rst.core.graphs.ReputationEdge;
import cu.rst.core.petrinet.Place;
import cu.rst.core.petrinet.Token;
import cu.rst.util.Util;

/**
 * @author partheinstein
 *
 */
public class SybilAttackEval extends Algorithm 
{

	@Override
	public boolean assertGraph2ListenType(Graph g) throws Exception {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean assertGraph2OutputType(Graph g) throws Exception {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean assertVariablePrecondition(double variable) throws Exception {
		// TODO Auto-generated method stub
		return true;
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
		Util.assertNotNull(tokens);
		if(tokens.size()==0) throw new Exception("Need atleast 1 token.");
		Collections.sort(tokens);
		
		RG rg = (RG) ((Place)tokens.get(0).m_place).getGraph();
		
		ReputationEdge re0 = (ReputationEdge) rg.getEdge(new Agent(0), new Agent(1));
		ReputationEdge re1 = (ReputationEdge) rg.getEdge(new Agent(0), new Agent(2));
		
		ArrayList toReturn = new ArrayList();
		
		if(re0.getReputation() > re1.getReputation())
		{
			//create a sybil
			Agent sybil = new Agent(rg.vertexSet().size());
			toReturn.add(new ReputationEdge(new Agent(2), sybil, 1));
			toReturn.add(new ReputationEdge(sybil, new Agent(1), 0)); //slander
		}
		else
		{
			toReturn.add(new Boolean(true));
		}
		
		return toReturn;
	}

}
