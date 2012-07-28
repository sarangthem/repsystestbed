package cu.rst.core.graphs;

import java.util.ArrayList;
import java.util.Set;

import org.apache.log4j.Logger;

import cu.rst.core.petrinet.Token;
import cu.rst.util.Util;


public class FHG extends Graph<Agent, FeedbackHistoryGraphEdge>
{
	static Logger logger = Logger.getLogger(FHG.class.getName());
	public ArrayList<Feedback> m_feedbacks;
	
	public FHG(FeedbackHistoryEdgeFactory ef)
	{		
		super(ef);
		m_feedbacks = new ArrayList<Feedback>();
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
		m_feedbacks.add(feedback);
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
	public FHG clone(boolean addObservers)
	{
		Set<FeedbackHistoryGraphEdge> edges = this.edgeSet();
		Set<Agent> agents = this.vertexSet();
		FHG clone = new FHG(new FeedbackHistoryEdgeFactory());
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
	public FHG getTransitiveClosureGraph()
	{
//		FeedbackHistoryGraph temp = this.clone(false);
//		TransitiveClosure.INSTANCE.closeSimpleDirectedGraph(temp);
//		return temp;
		return null;
	}


	@Override
	public ArrayList update(ArrayList<Token> tokens) throws Exception 
	{
		Util.assertNotNull(tokens);
		ArrayList<FeedbackHistoryGraphEdge> changes = new ArrayList<FeedbackHistoryGraphEdge>();
		for(Token t : tokens)
		{
			//update the graph only if the token contains feedbacks as changes
			if(t.m_changes!=null && t.m_changes.size()>0 
					&& t.m_changes.get(0)!=null && t.m_changes.get(0) instanceof Feedback)
			{
				changes.addAll(this.addFeedbacks((ArrayList<Feedback>) t.m_changes));
			}
			
		}
		
		return changes;
	}


	@Override
	public void deleteChanges(Token t) throws Exception 
	{
		ArrayList<Feedback> changes = (ArrayList<Feedback>) t.m_changes;
		for(Feedback f : changes)
		{
			FeedbackHistoryGraphEdge e = (FeedbackHistoryGraphEdge) this.getEdge(f.getAssesor(), f.getAssesee());
			if(e!=null && e.feedbacks!=null)
			{
				e.feedbacks.clear();
			}
		}
		
	}


}
