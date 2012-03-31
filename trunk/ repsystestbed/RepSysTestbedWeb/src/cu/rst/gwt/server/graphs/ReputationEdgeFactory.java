package cu.rst.gwt.server.graphs;

import org.jgrapht.EdgeFactory;

import cu.rst.gwt.server.entities.Agent;

public class ReputationEdgeFactory implements EdgeFactory<Agent, ReputationEdge>
{

	@Override
	public ReputationEdge createEdge(Agent src, Agent sink)
	{
		
		return new ReputationEdge(src, sink);
	}

}
