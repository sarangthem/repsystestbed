/**
 * 
 */
package cu.rst.gwt.server.alg.eg;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

import org.apache.log4j.Logger;

import cu.rst.gwt.server.alg.TrustAlgorithm;
import cu.rst.gwt.server.entities.Agent;
import cu.rst.gwt.server.graphs.Graph;
import cu.rst.gwt.server.graphs.ReputationEdge;
import cu.rst.gwt.server.graphs.Graph.Type;

/**
 * @author partheinstein
 * This is an implementation of a rank based trust algorithm.
 * @deprecated
 */
public class RankbasedTrustAlg extends TrustAlgorithm
{
	static Logger logger = Logger.getLogger(RankbasedTrustAlg.class.getName());
	
	private double ratio;

	public RankbasedTrustAlg()
	{
		
	}
	
	/**
	 * Returns true if the sink is in the top ratio of trusted agents by source. Else returns false
	 */
	@Override
	public boolean trusts(Agent src, Agent sink) throws Exception
	{
		Set<ReputationEdge> outGoingEdgesSet = super.getGraph2Listen().outgoingEdgesOf(src);
		
		ArrayList<ReputationEdge> outGoingEdgesList = new ArrayList<ReputationEdge>(); 
		for(ReputationEdge edge : outGoingEdgesSet)
		{
			outGoingEdgesList.add(edge);
		}
		//sort the edges in ascending order based on the reputation
		
	
//		logger.info("Before ranking: " + outGoingEdgesList);
		Collections.sort(outGoingEdgesList);
//		logger.info("After ranking: " + outGoingEdgesList);
		
		
		int numberOfAgentsToBeTrusted = (int)(this.ratio * outGoingEdgesList.size());
		if(numberOfAgentsToBeTrusted==0) return false;
		
		ArrayList<Agent> trustedAgents = new ArrayList<Agent>(numberOfAgentsToBeTrusted);
		
		for(int i=0;i<numberOfAgentsToBeTrusted;i++)
		{
			ReputationEdge trustedEdge = outGoingEdgesList.get(outGoingEdgesList.size() - 1 - i);
			trustedAgents.add((Agent) trustedEdge.sink);
//			logger.info("Added " + (Agent) trustedEdge.sink + "as trusted.");
		}
		
		for(Agent trustedAgent : trustedAgents)
		{
			if(trustedAgent.equals(sink))
			{
				logger.info(src + " trusts " + sink);
				return true;
			}
		}
		//logger.info(src + " does not trust " + sink);
		return false;
	}

	public void setRatio(double ratio) throws Exception
	{
		if(ratio<0 || ratio>1) 
		{
			String errMsg = "Ratio must be in [0,1]"; 
			logger.error(errMsg);
			throw new Exception(errMsg);
		}
		this.ratio = ratio;
	}

	public double getRatio()
	{
		return ratio;
	}

	@Override
	public void start() throws Exception
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void finish() throws Exception
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean assertGraph2ListenType(Graph g) throws Exception
	{
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean assertGraph2OutputType(Graph g) throws Exception
	{
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean assertVariablePrecondition(double variable) throws Exception
	{
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public Type getInputGraphType() throws Exception
	{
		return Graph.Type.RG;
	}

	@Override
	public Type getOutputGraphType() throws Exception
	{
		return Graph.Type.TG;
	}

}
