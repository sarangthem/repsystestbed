package cu.rst.gwt.server.graphs;

import java.util.ArrayList;
import java.util.Set;

import org.apache.log4j.Logger;
import org.jgrapht.alg.TransitiveClosure;
import org.jgrapht.graph.SimpleDirectedGraph;

import cu.rst.gwt.server.alg.Algorithm;
import cu.rst.gwt.server.alg.EvaluationAlgorithm;
import cu.rst.gwt.server.alg.ReputationAlgorithm;
import cu.rst.gwt.server.data.Feedback;
import cu.rst.gwt.server.entities.Agent;
import cu.rst.gwt.server.util.Util;
//import cu.rst.gwt.server.view.JGraphXView;


public class FeedbackHistoryGraph extends Graph<Agent, FeedbackHistoryGraphEdge>
{
	private FeedbackHistoryGraph originalGraph;
	static Logger logger = Logger.getLogger(FeedbackHistoryGraph.class.getName());
//	public JGraphXView view;
	private ArrayList<Feedback> m_feedbacks; //to support replay
	
	/**
	 * 
	 * @param FeedbackHistoryEdgeFactory ef
	 * 
	 * The edge factory is created like this FeedbackHistoryEdgeFactory ef = new FeedbackHistoryEdgeFactory();
	 * @throws Exception 
	 */
	public FeedbackHistoryGraph(FeedbackHistoryEdgeFactory ef)
	{		
		super(ef);
		setOriginalGraph(this);
		observers = new ArrayList<Algorithm>();
//		view = new JGraphXView();
//		view.m_graphModel = (SimpleDirectedGraph) this;

	}

	@Override
	public void addObserver(Algorithm algorithm) throws Exception
	{
		Util.assertNotNull(algorithm);
		if(!algorithm.assertGraph2ListenType(this))
		{
			throw new Exception("Failed preconditions: assertGraph2ListenType().");
		}
		this.observers.add(algorithm);
		algorithm.setGraph2Listen(this); //any data mod to this graph, rep algs have that to work with.
	}
	

	public void notifyObservers(boolean oneFeedbackAtATime) throws Exception
	{
		if(oneFeedbackAtATime)
		{
			try
			{
				Util.assertNotNull(m_feedbacks);
			}
			catch(Exception e)
			{
				throw new Exception("No feedbacks in the temporary feedbacks store. Call addFeedbacks() first.");
			}
			

			for(Algorithm alg : observers)
			{
				if(alg instanceof ReputationAlgorithm || alg instanceof EvaluationAlgorithm)
				{
					alg.start();
					for(Feedback f : m_feedbacks)
					{
						
						ArrayList<FeedbackHistoryGraphEdge> changes = new ArrayList<FeedbackHistoryGraphEdge>();
						changes.add(addFeedback(f));
						alg.update(changes);
					}
					alg.finish();
				
				}
				else
				{
					throw new ClassCastException("Unexpected observer in Feedback History Graph.");
				}

			}
		}
		else
		{
			for(Algorithm alg : observers)
			{
				if(alg instanceof ReputationAlgorithm || alg instanceof EvaluationAlgorithm)
				{
					alg.start();
					alg.update(null);
					alg.finish();
				}
				else
				{
					throw new ClassCastException("Unexpected observer in Feedback History Graph.");
				}
			}
		}
		
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
	
	public void addFeedbacks(ArrayList<Feedback> feedbacks, boolean updateGraph) throws Exception
	{
		Util.assertNotNull(feedbacks);
		
		/*
		 * Add the feedbacks to a temporary store. This was to added because when you create a workflow 
		 * from a file (see WorkflowParser.java), the feedbacks need to be added without notifying the 
		 * observers.
		 */
	
		if(updateGraph)
		{
			for(Feedback feedback : feedbacks)
			{
				addFeedback(feedback);
			}
		}
		else
		{
			m_feedbacks = feedbacks;
		}
		
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
//			this.view.update(edgesToBeUpdated);
		}
		//hopefully this method returns the ptr to the edge (and not a copy)
		FeedbackHistoryGraphEdge edge = (FeedbackHistoryGraphEdge) this.getEdge(feedback.getAssesor(), feedback.getAssesee()); 
		edge.addFeedback(feedback);
		return edge;
	}



	public void setOriginalGraph(FeedbackHistoryGraph originalGraph)
	{
		this.originalGraph = originalGraph;
	}

	public FeedbackHistoryGraph getOriginalGraph()
	{
		return originalGraph;
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
		
		/*
		 * added a flag otherwise you can end up in a loop. 
		 * RepAlg2.setGraph2Listen()
		 * FHG.getTransitiveClosureGraph()
		 * clone()
		 * addObserver(RepAlg2)
		 * RepAlg2.setGraph2Listen()
		 * ...
		 * ...
		 * 
		 */
		if(addObservers)
		{
			for(Algorithm alg : this.observers)
			{
				try
				{
					clone.addObserver((ReputationAlgorithm) alg);
				} catch (Exception e1)
				{
					// TODO change this to something nice
					e1.printStackTrace();
					System.exit(1);
				}
			}
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
	
	
	
	

	/**
	 * merges the attack graph to the original graph.
	 * @param attackGraph
	 * @param attackEdges
	 * @throws Exception
	 */
	/*
	public void attack(ExperienceGraph attackGraph, ArrayList attackEdges) throws Exception
	{
		if(attackEdges==null) throw new Exception("attackEdges is null.");
		if(attackEdges!=null && attackEdges.size()==0) throw new Exception("No attackEdges. Can't attack.");
		if(attackGraph==null) throw new Exception("attackGraph is null.");
		
		Set attackAgents = attackGraph.vertexSet();
		if(attackAgents.size()==0) throw new Exception("no agents in the attack graph.");
		
		//right now, I am not saving the original graph before attacking it. you should do so.
		
		//make sure the sink in the attack edge is in the original graph
		Iterator it0 = attackEdges.iterator();
		while(it0.hasNext())
		{
			ExperienceEdge e = (ExperienceEdge)it0.next();
			if(!this.containsVertex((Agent)e.sink)) throw new Exception("the sink in the attack edge not in the original graph. this is not a attack edge");
		}
		
		//merge the attack graph to the original graph
		Iterator it = attackAgents.iterator();
		while(it.hasNext())
		{
			Agent a = (Agent) it.next();
			if(!this.containsVertex(a))
			{
				logger.info("Adding a bad node. " + a);
				this.addVertex(a); //some nodes may already be in the original graph. These are nodes attacked by the attacker.
			}
		}
		
		Iterator it1 = attackGraph.edgeSet().iterator();
		while(it1.hasNext())
		{
			ExperienceEdge e = (ExperienceEdge) it1.next();
			this.addEdge((Agent)e.src, (Agent)e.sink); //add the edge from the attack graph
			ExperienceEdge e1 = this.getEdge((Agent)e.src, (Agent)e.sink); //get the edge you just added
			e1.experiences = e.experiences; //add the experience arraylist
		}
		
		this.notifyObservers();
		

	}*/

}
