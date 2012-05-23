package cu.rst.gwt.server.graphs;

import java.util.ArrayList;
import java.util.Set;

import org.apache.log4j.Logger;
import org.jgrapht.alg.TransitiveClosure;

import cu.rst.gwt.server.data.Feedback;
import cu.rst.gwt.server.entities.Agent;
import cu.rst.gwt.server.petrinet.Token;
import cu.rst.gwt.server.util.Util;


public class FeedbackHistoryGraph extends Graph<Agent, FeedbackHistoryGraphEdge>
{
	static Logger logger = Logger.getLogger(FeedbackHistoryGraph.class.getName());
	
	public FeedbackHistoryGraph(FeedbackHistoryEdgeFactory ef)
	{		
		super(ef);
	}
	
	
	@Override
	public String toString()
	{
		String temp = null;
		temp = "Feedback History Graph" + System.getProperty("line.separator");
		temp += "Vertices:" + System.getProperty("line.separator");
		for(Agent a : (Set<Agent>)super.vertexSet())
		{
			temp += a + ",";
		}
		temp += System.getProperty("line.separator") + "Edges:" + System.getProperty("line.separator");
		for(FeedbackHistoryGraphEdge e : (Set<FeedbackHistoryGraphEdge>) super.edgeSet())
		{
			temp += e.toString2() + " ,";
		}
		temp += System.getProperty("line.separator") + "Feedbacks:" + System.getProperty("line.separator");
		for(FeedbackHistoryGraphEdge e : (Set<FeedbackHistoryGraphEdge>) super.edgeSet())
		{
			temp += e.toString() + ":" + System.getProperty("line.separator");
			for(Feedback f : e.feedbacks)
			{
				temp += "{" + f.getAssesor().id + ", " + f.getAssesee().id + ", " + f.value + "}, ";
			}
			temp += System.getProperty("line.separator");
		}
		
		return System.getProperty("line.separator") + temp;
	}
	
	public ArrayList<FeedbackHistoryGraphEdge> addFeedbacks(ArrayList<Feedback> feedbacks) throws Exception
	{
		Util.assertNotNull(feedbacks);
		ArrayList<FeedbackHistoryGraphEdge> changes = new ArrayList<FeedbackHistoryGraphEdge>();
		for(Feedback feedback : feedbacks)
		{
			changes.add(addFeedback(feedback));
		}
		return changes;
	}
	
	
	/**
	 * 
	 * @param feedback feedback to add to the graph
	 */
	public FeedbackHistoryGraphEdge addFeedback(Feedback feedback) throws Exception
	{
		/*
		 * Add the source and destination nodes of the feedback and the edge to the 
		 * feedback history graph
		 */
		if(!this.containsVertex(feedback.getAssesor())) this.addVertex(feedback.getAssesor());
		if(!this.containsVertex(feedback.getAssesee())) this.addVertex(feedback.getAssesee());
		if(!this.containsEdge(feedback.getAssesor(), feedback.getAssesee()))
		{
			FeedbackHistoryGraphEdge edge = new FeedbackHistoryGraphEdge(feedback.getAssesor(), feedback.getAssesee());
			this.addEdge((Agent)edge.src, (Agent)edge.sink);
			//update the view
			ArrayList<TestbedEdge> edgesToBeUpdated = new ArrayList<TestbedEdge>();
			edgesToBeUpdated.add(edge);
		}
		//hopefully this method returns the ptr to the edge (and not a copy)
		FeedbackHistoryGraphEdge edge = (FeedbackHistoryGraphEdge) this.getEdge(feedback.getAssesor(), feedback.getAssesee()); 
		edge.addFeedback(feedback);
		return edge;
	}


	@Override
	public FeedbackHistoryGraph clone(boolean addObservers)
	{
		Set<FeedbackHistoryGraphEdge> edges = this.edgeSet();
		Set<Agent> agents = this.vertexSet();
		FeedbackHistoryGraph clone = new FeedbackHistoryGraph(new FeedbackHistoryEdgeFactory());
		for(Agent a : agents)
		{
			clone.addVertex(a); //not copying the agent
		}
		for(FeedbackHistoryGraphEdge e : edges)
		{
			clone.addEdge((Agent)e.src, (Agent)e.sink);
		}
		
		//not copying the feedbacks in each edge.
		return clone;
	}
	
	@Override
	public FeedbackHistoryGraph getTransitiveClosureGraph()
	{
		FeedbackHistoryGraph temp = this.clone(false);
		TransitiveClosure.INSTANCE.closeSimpleDirectedGraph(temp);
		return temp;
	}


	@Override
	public ArrayList update(ArrayList<Token> tokens) throws Exception 
	{
		Util.assertNotNull(tokens);
		ArrayList<FeedbackHistoryGraphEdge> changes = new ArrayList<FeedbackHistoryGraphEdge>();
		for(Token t : tokens)
		{
			//assume that the changes will always be feedbacks
			changes.addAll(this.addFeedbacks((ArrayList<Feedback>) t.m_changes));
		}
		
		return changes;
	}


}
