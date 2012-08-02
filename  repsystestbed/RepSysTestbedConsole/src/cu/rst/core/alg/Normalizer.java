/**
 * 
 */
package cu.rst.core.alg;

import java.util.ArrayList;
import java.util.Set;

import cu.rst.core.graphs.Agent;
import cu.rst.core.graphs.Graph;
import cu.rst.core.graphs.Graph.Type;
import cu.rst.core.graphs.RG;
import cu.rst.core.graphs.ReputationEdge;
import cu.rst.core.petrinet.Place;
import cu.rst.core.petrinet.Token;

/**
 * @author partheinstein
 *
 */
public class Normalizer extends Algorithm 
{

	@Override
	public boolean assertGraph2ListenType(Graph g) throws Exception 
	{
		return g instanceof RG;
	}

	@Override
	public boolean assertGraph2OutputType(Graph g) throws Exception 
	{
		return g instanceof RG;
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
		ArrayList<ReputationEdge> changes = new ArrayList<ReputationEdge>();
		Token t = null;
		if(tokens!=null && tokens.size()>0)
		{
			t = tokens.get(0);
			RG rg = (RG) ((Place)t.m_place).getGraph();
			for(Agent src : (Set<Agent>)rg.vertexSet())
			{
				//normalize over the outgoing edges
				double totalRep = 0;
				for(ReputationEdge re : (Set<ReputationEdge>)rg.outgoingEdgesOf(src))
				{
					totalRep = totalRep + rg.getEdgeWeight(re);
				}
				
				for(ReputationEdge re : (Set<ReputationEdge>)rg.outgoingEdgesOf(src))
				{
					double newWeight = (totalRep>0)? (rg.getEdgeWeight(re) / totalRep) : 0;
					changes.add(new ReputationEdge(src, (Agent) re.sink, rg.getEdgeWeight(re) / totalRep));
				}
				
				
			}
		}

		return changes;
	}

}
