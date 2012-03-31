package cu.rst.gwt.server.graphs;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Logger;
import org.jgrapht.alg.TransitiveClosure;
import org.jgrapht.graph.SimpleDirectedGraph;

import cu.rst.gwt.server.alg.Algorithm;
import cu.rst.gwt.server.alg.EvaluationAlgorithm;
import cu.rst.gwt.server.alg.ReputationAlgorithm;
import cu.rst.gwt.server.alg.TrustAlgorithm;
import cu.rst.gwt.server.entities.Agent;
import cu.rst.gwt.server.util.Util;
import cu.rst.gwt.server.view.JGraphXView;

public class ReputationGraph extends Graph<Agent, ReputationEdge>
{

	private static final long serialVersionUID = 2768260651851459417L;
	static Logger logger = Logger.getLogger(ReputationGraph.class.getName());
	public static enum OBSERVER_TYPE{TRUST_ALGORITHM, REPUTATION_ALGORITHM, REPSYSTESTBED_ALGORITHM};  
	
	private ArrayList observers;
	private OBSERVER_TYPE observerType;
	public JGraphXView view;
	
	public ReputationGraph(ReputationEdgeFactory reputationEdgeFactory)
	{
		super(reputationEdgeFactory);
		observers = new ArrayList<Algorithm>();
		observerType = OBSERVER_TYPE.REPSYSTESTBED_ALGORITHM;
		view = new JGraphXView();
		view.m_graphModel = this;

	}
	
	@Deprecated
	public ReputationGraph(ReputationEdgeFactory reputationEdgeFactory, OBSERVER_TYPE observerType)
	{
		super(reputationEdgeFactory);
		switch(observerType)
		{
			case TRUST_ALGORITHM:
				observers = new ArrayList<TrustAlgorithm>();
				observerType = OBSERVER_TYPE.TRUST_ALGORITHM;
			case REPUTATION_ALGORITHM:
				observers = new ArrayList<ReputationAlgorithm>();
				observerType = OBSERVER_TYPE.REPUTATION_ALGORITHM;
			default: 
				observers = new ArrayList<Algorithm>();
				observerType = OBSERVER_TYPE.REPSYSTESTBED_ALGORITHM;
		}
	}
	
	/**
	 * Adds the edge if it doesn't exist already. If it exists, the edge weight is updated.
	 * @param src
	 * @param sink
	 * @param reputation
	 */
	public void addEdge(Agent src, Agent sink, double reputation)
	{
		ReputationEdge repEdge = null;
		if(super.containsEdge(src, sink))
		{
			 repEdge = (ReputationEdge) super.getEdge(src, sink);			
		}else
		{
			repEdge = new ReputationEdge(src, sink);
		}
		repEdge.setReputation(reputation);
		addEdge(src, sink);
		setEdgeWeight(repEdge, reputation);
		
	}
	

	public void addObserver(Algorithm alg) throws Exception
	{
		Util.assertNotNull(alg);
		
		if(!(alg instanceof TrustAlgorithm)  && !(alg instanceof ReputationAlgorithm) 
				&& !(alg instanceof EvaluationAlgorithm))
		{
			throw new Exception("Cannot add a algorithm that is not a trust algorithm or or a" +
					" reputation algorithm or an evaluation algorithm");
		}
		this.observers.add(alg);
		alg.setGraph2Listen(this);
		
	}
	
	public void notifyObservers(ArrayList changes) throws Exception
	{
		for(Object alg : observers)
		{
			if(alg instanceof ReputationAlgorithm || alg instanceof TrustAlgorithm 
					|| alg instanceof EvaluationAlgorithm)
			{
				((Algorithm) alg).start();
				((Algorithm) alg).update(changes);
				((Algorithm) alg).finish();
			}
			else
			{
				throw new ClassCastException("Unexpected observer in Reputation Graph.");
			}
		}
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
		
		if(addObservers)
		{
			Iterator it = observers.iterator();
			while(it.hasNext())
			{
				Algorithm alg = (Algorithm) it.next();
				try
				{
					clone.addObserver(alg);
				}
				catch(Exception e)
				{
					logger.error(e);
				}
			}
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

}
