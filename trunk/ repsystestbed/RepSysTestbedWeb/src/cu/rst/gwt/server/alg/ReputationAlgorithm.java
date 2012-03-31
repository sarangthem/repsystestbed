package cu.rst.gwt.server.alg;

import java.util.ArrayList;
import java.util.Set;

import org.apache.log4j.Logger;

import cu.rst.gwt.server.entities.Agent;
import cu.rst.gwt.server.exceptions.GenericTestbedException;
import cu.rst.gwt.server.graphs.Graph;
import cu.rst.gwt.server.graphs.ReputationEdge;
import cu.rst.gwt.server.graphs.ReputationGraph;
import cu.rst.gwt.server.graphs.TestbedEdge;
import cu.rst.gwt.server.util.Util;

public abstract class ReputationAlgorithm extends Algorithm
{
	static Logger logger = Logger.getLogger(ReputationAlgorithm.class.getName());
	
	private static ReputationAlgorithm algorithm;
	protected  enum OutputGraphType{COMPLETE_GRAPH, TRANSITIVE_GRAPH, COPY_INPUT_GRAPH};
	protected OutputGraphType outputGraphType;

	/**
	 * Given the class name of a reputation algorithm, this method returns an instance of it.
	 * @param className
	 * @return ReputationAlgorithm
	 * @throws GenericTestbedException
	 */
	@SuppressWarnings("unchecked")
	public static ReputationAlgorithm getInstance(String className) throws GenericTestbedException
	{
		try
		{
			Class<?>cls = (Class<ReputationAlgorithm>) Class.forName(className);
			algorithm = (ReputationAlgorithm) cls.newInstance();
			
		}catch(Exception e)
		{
			String msg = "Error loading reputation algorithm with name " + className;
			logger.error(msg);
			throw new GenericTestbedException(msg, e);
		}
		return algorithm;
		
	}
	
	@Override
	public Graph getGraph2Output() throws Exception
	{
		if(this.m_graph2Output.edgeSet().size()<1) throw new GenericTestbedException("No edges in reputation graph."); 
		return (ReputationGraph) m_graph2Output;
	}
	
	@Override
	public void setGraph2Output(Graph graphToBeSet) throws Exception
	{
		Util.assertNotNull(graphToBeSet);
		if(!assertGraph2OutputType(graphToBeSet))
		{
			throw new Exception("Failed postconditions: assertGraph2OutputType()");
		}
		m_graph2Output = (ReputationGraph) graphToBeSet;
		
		try
		{
			Util.assertNotNull(m_graph2Listen);
		}
		catch(Exception e)
		{
			throw new GenericTestbedException("Call setGraph2Listen() first.", e);
		}
		//initialize the reputation graph
		if(outputGraphType == OutputGraphType.COMPLETE_GRAPH)
		{
			/*
			 * For every src and sink in the input graph, create an edge in the output graph
			 */
			Graph graphTransitive = ((Graph)this.m_graph2Listen).getTransitiveClosureGraph();
			for(Agent src: (Set<Agent>) graphTransitive.vertexSet())
			{
				for(Agent sink: (Set<Agent>) graphTransitive.vertexSet())
				{
					if(!src.equals(sink))
					{
						m_graph2Output.addVertex(src);
						m_graph2Output.addVertex(sink);
						m_graph2Output.addEdge(src, sink);
					}
					
				}
			}
		}
		else if(outputGraphType == OutputGraphType.TRANSITIVE_GRAPH)
		{
			/*
			 * Find the transitive closure edges of the input graph and add them to the output grph
			 */
			Graph graphTransitive = ((Graph)this.m_graph2Listen).getTransitiveClosureGraph();
			for(TestbedEdge edge : (Set<TestbedEdge>)graphTransitive.edgeSet())
			{
				Agent src = (Agent)edge.src;
				Agent sink = (Agent)edge.sink;
				m_graph2Output.addVertex(src);
				m_graph2Output.addVertex(sink);
				m_graph2Output.addEdge(src, sink);
			}
		}
		else if(outputGraphType == OutputGraphType.COPY_INPUT_GRAPH)
		{
			/*
			 * Copy the edges in the input graph to the output graph
			 */
			Set<TestbedEdge> edges = ((Graph)this.m_graph2Listen).edgeSet();
			for(TestbedEdge edge : edges)
			{
				Agent src = (Agent)edge.src;
				Agent sink = (Agent)edge.sink;
				m_graph2Output.addVertex(src);
				m_graph2Output.addVertex(sink);
				m_graph2Output.addEdge(src, sink);
			}
		}
		else
		{
			throw new GenericTestbedException("Unknown OutputGraphType.");
		}
	}

	@Override
	public void update(ArrayList changes) throws Exception
	{
		Util.assertNotNull(this.m_graph2Listen);
		Util.assertNotNull(this.m_graph2Output);
		update();
		//TODO - for now calling update() which doesn't make use of the changes.
	}
	
	/**
	 * To be called by FeedbackHistoryGraph.notifyObservers() only
	 * Everytime a feedback is added, this method is called.
	 */
	public void update() throws Exception
	{
		/*
		 * update the reputation graph
		 * for every src and sink node in the input graph, 
		 * weight = calculateTrustGraph (src, sink)
		 * observer.setEdgeWeight(weight) 
		 */
	
		
		Set<Agent> agents = m_graph2Listen.vertexSet();
		ArrayList<TestbedEdge> edgesTobeUpdated = new ArrayList<TestbedEdge>();
		
		if(outputGraphType == OutputGraphType.COMPLETE_GRAPH)
		{
			/**
			 * PeerTrust, EigenTrust and the likes
			 */
			for(Agent src : agents)
			{
				for(Agent sink : agents)
				{
					if(!src.equals(sink))
					{
						double trustScore = calculateTrustScore(src, sink);
						//TODO - move this to preconditions and postconditions
						//ensure the trust score is in [0,1].
						if(!assertVariablePrecondition(trustScore))
						{
							throw new GenericTestbedException("Failed postconditions: assertVariablePrecondition()");
						}
						
						if(m_graph2Output.getEdge(src, sink) != null &&
								!(m_graph2Output.getEdge(src, sink) instanceof ReputationEdge))
						{
							throw new GenericTestbedException("Expected edge type is ReputationEdge.");
						}
						
						ReputationEdge repEdge = (ReputationEdge) m_graph2Output.getEdge(src, sink);
						
						//repEdge may be null. all edges in the graph this alg listens to may have been added but that is not
						//reflected in the reputation graph edge. So reputationGraph.getEdge(src, sink) may return null.
						
						if(repEdge==null)
						{
							if(!m_graph2Output.containsVertex(src)) m_graph2Output.addVertex(src);
							if(!m_graph2Output.containsVertex(sink)) m_graph2Output.addVertex(sink);
							m_graph2Output.addEdge(src, sink);	
							repEdge = (ReputationEdge) m_graph2Output.getEdge(src, sink);	
						}
						
						repEdge.setReputation(trustScore);
						m_graph2Output.setEdgeWeight(repEdge, trustScore);
						edgesTobeUpdated.add(repEdge);
					}
				}
			}
		}else if(outputGraphType == OutputGraphType.TRANSITIVE_GRAPH)
		{
			
			/**
			 * Calculate trust scores of sink agents that are reachable from the source.
			 * that is determine the transitive closure of the graph that this alg listens to 
			 * and set the weights of the edges.
			 */
			
			Graph transitiveGraph = m_graph2Listen.getTransitiveClosureGraph();
			 
			for(Agent src : agents)
			{
				Set<TestbedEdge> outgoingEdges = transitiveGraph.outgoingEdgesOf(src);
				for(TestbedEdge e : outgoingEdges)
				{
					double trustScore = calculateTrustScore(src, (Agent)e.sink);
					if(!assertVariablePrecondition(trustScore))
					{
						throw new GenericTestbedException("Failed postconditions: assertVariablePrecondition()");
					}
					
					if(!((ReputationEdge) m_graph2Output.getEdge(src, (Agent)e.sink) instanceof ReputationEdge))
					{
						throw new GenericTestbedException("Expected edge type is ReputationEdge.");
					}
					
					ReputationEdge repEdge = (ReputationEdge) m_graph2Output.getEdge(src, (Agent)e.sink);
					
					//repEdge may be null. see setFeedbackHistoryGraph(). a feedback history graph edge may have been added but that is not
					//reflected in the reputation graph edge. So reputationGraph.getEdge(src, sink) may return null.
					
					if(repEdge==null)
					{
						if(!m_graph2Output.containsVertex(src)) m_graph2Output.addVertex(src);
						if(!m_graph2Output.containsVertex((Agent)e.sink)) m_graph2Output.addVertex((Agent)e.sink);
						m_graph2Output.addEdge(src, (Agent)e.sink);
						repEdge = (ReputationEdge) m_graph2Output.getEdge(src, (Agent)e.sink);
						repEdge.setReputation(trustScore);
					}
					m_graph2Output.setEdgeWeight(repEdge, trustScore);
					edgesTobeUpdated.add(repEdge);
					
				}
			}
		}
		else
		{
			for(Agent src : agents)
			{
				Set<TestbedEdge> outgoingEdges = super.m_graph2Listen.outgoingEdgesOf(src);
				for(TestbedEdge e : outgoingEdges)
				{
					double trustScore = calculateTrustScore(src, (Agent)e.sink);
					if(!assertVariablePrecondition(trustScore))
					{
						throw new GenericTestbedException("Failed postconditions: assertVariablePrecondition()");
					}
					
					if(!((ReputationEdge) m_graph2Output.getEdge(src, (Agent)e.sink) instanceof ReputationEdge))
					{
						throw new GenericTestbedException("Expected edge type is ReputationEdge.");
					}
					
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
					edgesTobeUpdated.add(repEdge);
				}
			}
		}
		
		//need to let the trust algorithms know that something has changed
		//TODO - no changes mentioned.
		((ReputationGraph) m_graph2Output).notifyObservers(null);
		//now update the view
		((ReputationGraph) m_graph2Output).view.update(edgesTobeUpdated);
		
	}
	
	/**
	 * Implement this method. 
	 * Given a graph, trustor and trustee, calculate the trust score
	 * @param src Trustor
	 * @param sink Trustee
	 * @return
	 * @throws Exception
	 */
	public abstract double calculateTrustScore(Agent src, Agent sink) throws Exception;
	
	/**
	 * 
	 * @param type OutputGraphType
	 */
	public void setOutputGraphType(OutputGraphType type)
	{
		outputGraphType = type;
	}
	
	/**
	 * 
	 * @return OutputGraphType
	 */
	public OutputGraphType OutputGraphType()
	{
		return outputGraphType;
	}
	
	public Graph getGraph2Listen()
	{
		return m_graph2Listen;
	}

	@Override
	public void setGraph2Listen(Graph g) throws Exception
	{
		if(!assertGraph2ListenType(g))
		{
			throw new Exception("assertGraph2ListenType() failed.");
		}
		this.m_graph2Listen = g;
	}
	
	

}
