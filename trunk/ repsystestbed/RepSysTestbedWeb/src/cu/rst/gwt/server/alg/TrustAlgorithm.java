package cu.rst.gwt.server.alg;

import java.util.ArrayList;
import java.util.Set;

import org.apache.log4j.Logger;
import org.jgrapht.graph.SimpleDirectedGraph;

import cu.rst.gwt.server.entities.Agent;
import cu.rst.gwt.server.exceptions.GenericTestbedException;
import cu.rst.gwt.server.graphs.FeedbackHistoryGraphEdge;
import cu.rst.gwt.server.graphs.Graph;
import cu.rst.gwt.server.graphs.ReputationEdge;
import cu.rst.gwt.server.graphs.ReputationEdgeFactory;
import cu.rst.gwt.server.graphs.ReputationGraph;
import cu.rst.gwt.server.graphs.TestbedEdge;
import cu.rst.gwt.server.graphs.TrustEdge;
import cu.rst.gwt.server.graphs.TrustEdgeFactory;
import cu.rst.gwt.server.graphs.TrustGraph;
import cu.rst.gwt.server.util.Util;

public abstract class TrustAlgorithm extends Algorithm
{
	private static TrustAlgorithm algorithm;
	static Logger logger = Logger.getLogger(TrustAlgorithm.class.getName());
	
	@SuppressWarnings("unchecked")
	public static TrustAlgorithm getInstance(String className) throws GenericTestbedException
	{
		try
		{
			Class<?>cls = (Class<TrustAlgorithm>) Class.forName(className);
			algorithm = (TrustAlgorithm) cls.newInstance();
		}catch(Exception e)
		{
			String msg = "Error loading trust algorithm with name " + className;
			logger.error(msg);
			throw new GenericTestbedException(msg, e);

		}
		return algorithm;
		
	}

	@Override
	public void update(ArrayList changes) throws Exception
	{
		update();
		//TODO - not making use of the changes
	}
	
	public void update() throws Exception
	{
		ArrayList<TestbedEdge> edgesToBeUpdated = new ArrayList<TestbedEdge>();
		//go through each agent and find out the agents it trusts
		//create an edge if an agent trusts another
		Set<Agent> agents = m_graph2Listen.vertexSet();
		for(Agent src : agents)
		{
			for(Agent sink : agents)
			{
				if(!src.equals(sink))
				{
					if(trusts(src, sink))
					{
						if(!m_graph2Output.containsVertex(src)) m_graph2Output.addVertex(src);
						if(!m_graph2Output.containsVertex(sink)) m_graph2Output.addVertex(sink);
						//TODO you are not checking. you are simply adding a Trust edge but its probably ok.
						TrustEdge te = new TrustEdge(src, sink);
						m_graph2Output.addEdge((Agent)te.src, (Agent)te.sink);
						edgesToBeUpdated.add(te);
					}
				}
			}
			
		}
		//update the view
//		((TrustGraph)m_graph2Output).view.update(edgesToBeUpdated);
		
	}

	@Override
	public void setGraph2Listen(Graph reputationGraph) throws Exception
	{
		if(!(reputationGraph instanceof ReputationGraph))
		{
			throw new Exception("A trust algorithm must listen to a reputation graph");
		}
		m_graph2Listen = (ReputationGraph) reputationGraph;
		
	}
	
	@Override
	public void setGraph2Output(Graph g) throws Exception
	{
		Util.assertNotNull(g);
		if(!(assertGraph2OutputType(g)))
		{
			throw new Exception("A trust algorithm can only output a trust graph.");
		}
		m_graph2Output = g;
		
		try
		{
			Util.assertNotNull(m_graph2Listen);
		}
		catch(Exception e)
		{
			throw new GenericTestbedException("Call setGraph2Listen() first.", e);
		}
		
		m_graph2Output = new TrustGraph(new TrustEdgeFactory());
		//an edge in the trust graph means src trusts sink. So don't copy the edges from the rep graph. just copy the agents
		for(Agent agent : (Set<Agent>) m_graph2Listen.vertexSet())
		{
			m_graph2Output.addVertex(agent);
		}
	}
	
	
	public abstract boolean trusts(Agent src, Agent sink) throws Exception;
	
	

}


