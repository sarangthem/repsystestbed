package cu.rst.gwt.server.graphs;

import java.util.ArrayList;
import java.util.Set;

import org.apache.log4j.Logger;
import org.jgrapht.alg.TransitiveClosure;

import cu.rst.gwt.server.entities.Agent;
import cu.rst.gwt.server.petrinet.Token;
import cu.rst.gwt.server.util.Util;

public class ReputationGraph extends Graph<Agent, ReputationEdge>
{

	private static final long serialVersionUID = 2768260651851459417L;
	static Logger logger = Logger.getLogger(ReputationGraph.class.getName());
		
	public ReputationGraph(ReputationEdgeFactory reputationEdgeFactory)
	{
		super(reputationEdgeFactory);
	}
	
	
	/**
	 * Adds the edge if it doesn't exist already. If it exists, the edge weight is updated.
	 * @param src
	 * @param sink
	 * @param reputation
	 */
	public void addEdge(Agent src, Agent sink, double reputation)
	{
		if(!this.containsVertex(src)) this.addVertex(src);
		if(!this.containsVertex(sink)) this.addVertex(sink);
			
		ReputationEdge repEdge = null;
		if(super.containsEdge(src, sink))
		{
			 repEdge = (ReputationEdge) super.getEdge(src, sink);			
		}else
		{
			repEdge = new ReputationEdge(src, sink);
		}
		repEdge.setReputation(reputation);
		addEdge(src, sink, repEdge);
		setEdgeWeight(repEdge, reputation);
		
	}
	

	@Override
	public ReputationGraph clone(boolean addObservers)
	{
		ReputationGraph clone = new ReputationGraph(new ReputationEdgeFactory());
		Set<ReputationEdge> edges = this.edgeSet();
		Set<Agent> agents = this.vertexSet();
		
		for(Agent a : agents)
		{
			clone.addVertex(a); //not copying the agent
		}
		for(ReputationEdge e : edges)
		{
			clone.addEdge((Agent)e.src, (Agent)e.sink);
		}
		
		return clone;
	}
	
	@Override
	public ReputationGraph getTransitiveClosureGraph()
	{
		ReputationGraph temp = this.clone(false);
		TransitiveClosure.INSTANCE.closeSimpleDirectedGraph(temp);
		return temp;
	}
	
	public ReputationGraph getTransitiveClosureGraph(FeedbackHistoryGraph f)
	{
		ReputationGraph temp = new ReputationGraph(new ReputationEdgeFactory());
		Set<FeedbackHistoryGraphEdge> edges = f.edgeSet();
		for(FeedbackHistoryGraphEdge edge : edges)
		{
			Agent src = (Agent)edge.src;
			Agent sink = (Agent)edge.sink;
			temp.addVertex(src);
			temp.addVertex(sink);
			temp.addEdge(src, sink);
		}
		TransitiveClosure.INSTANCE.closeSimpleDirectedGraph(temp);
		return temp;
	}
	
	@Override
	public String toString()
	{
		String temp = null;
		temp = "Reputation Graph" + System.getProperty("line.separator");
		temp += "Vertices:" + System.getProperty("line.separator");
		for(Agent a : (Set<Agent>) super.vertexSet())
		{
			temp += a + ",";
		}
		temp += System.getProperty("line.separator") + "Edges:" + System.getProperty("line.separator");
		for(ReputationEdge e : (Set<ReputationEdge>) super.edgeSet())
		{
			temp += e.toString() + " ,";
		}	
		return System.getProperty("line.separator") + temp;
	}


	@Override
	public ArrayList update(ArrayList<Token> tokens) throws Exception 
	{
		Util.assertNotNull(tokens);
		ArrayList<ReputationEdge> changes = new ArrayList<ReputationEdge>();
		for(Token t : tokens)
		{
			for(ReputationEdge e : (ArrayList<ReputationEdge>) t.m_changes)
			{
				changes.add(e);
				addEdge((Agent)e.src, (Agent)e.sink, e.getReputation());
			}
		}
		return changes;
	}

}
