/**
 * 
 */
package cu.rst.gwt.server.alg;

import java.util.ArrayList;
import java.util.Set;

import org.jgrapht.graph.SimpleDirectedGraph;

import cu.rst.gwt.server.entities.Agent;
import cu.rst.gwt.server.exceptions.GenericTestbedException;
import cu.rst.gwt.server.graphs.FeedbackHistoryGraph;
import cu.rst.gwt.server.graphs.ReputationEdge;
import cu.rst.gwt.server.graphs.ReputationEdgeFactory;
import cu.rst.gwt.server.graphs.ReputationGraph;
import cu.rst.gwt.server.graphs.TestbedEdge;

/**
 * @author partheinstein
 * This type of reputation algorithm simple calculates the trust score of agents that are connected
 * in the feedback history graph. The output graph which is a reputation graph is not a fully connected 
 * graph or a transitive closure graph of the feedback history graph. An example is eigentrust which first
 * calculates the one-to=one direct trust scores of agents before becoming ReputaitonAlgorithmType2.
 */
public abstract class ReputationAlgorithmType1a extends ReputationAlgorithm
{
	@Override
	public void setGraph2Listen(SimpleDirectedGraph g) throws Exception
	{
		if(!(g instanceof FeedbackHistoryGraph))
			throw new Exception("Can only listen to a feedback history graph.");
		
		super.m_graph2Listen = (FeedbackHistoryGraph)g;
		if(m_graph2Output == null) m_graph2Output = new ReputationGraph(new ReputationEdgeFactory());
		super.produceCompleteGraph = false;
		
		//initialize the reputation graph
		Set<TestbedEdge> edges = super.m_graph2Listen.edgeSet();
		for(TestbedEdge e : edges)
		{
			m_graph2Output.addVertex((Agent)e.sink);
			m_graph2Output.addVertex((Agent)e.src);
			m_graph2Output.addEdge((Agent)e.src, (Agent)e.sink);
		}
		
	}
	
	@Override
	public void update(ArrayList changes) throws Exception
	{
		update();
		//TODO - not making use of the changes
	}
	
	@Override
	public void update() throws Exception
	{
		ArrayList<TestbedEdge> edgesToBeUpdated = new ArrayList<TestbedEdge>();
		Set<Agent> agents = m_graph2Listen.vertexSet();
		for(Agent src : agents)
		{
			Set<TestbedEdge> outgoingEdges = super.m_graph2Listen.outgoingEdgesOf(src);
			for(TestbedEdge e : outgoingEdges)
			{
				double trustScore = calculateTrustScore(src, (Agent)e.sink);
				/*
				if(trustScore < this.MINIMUM_TRUST_SCORE || trustScore > this.MAXIMUM_TRUST_SCORE)
				{
					throw new GenericTestbedException("Algorithm returned a trust score (" 
							+ trustScore + ")that is not within [0,1]. ");
				}*/
				ReputationEdge repEdge = (ReputationEdge) m_graph2Output.getEdge(src, (Agent)e.sink);
				
				if(repEdge==null)
				{
					if(!m_graph2Output.containsVertex(src)) m_graph2Output.addVertex(src);
					if(!m_graph2Output.containsVertex((Agent)e.sink)) m_graph2Output.addVertex((Agent)e.sink);
					m_graph2Output.addEdge(src, (Agent)e.sink);
					repEdge = (ReputationEdge) m_graph2Output.getEdge(src, (Agent)e.sink);
					repEdge.setReputation(trustScore);
				}
				m_graph2Output.setEdgeWeight(repEdge, trustScore);
				edgesToBeUpdated.add(repEdge);
			}
		}
		
		//need to let the trust algorithms know that something has changed
		//TODO - no changes mentioned.
		((ReputationGraph) m_graph2Output).notifyObservers(null);
		//update the view
		((ReputationGraph) m_graph2Output).view.update(edgesToBeUpdated);
	}
	
	
	
	
}
