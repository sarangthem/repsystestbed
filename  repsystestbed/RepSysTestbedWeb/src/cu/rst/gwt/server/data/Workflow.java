/**
 * 
 */
package cu.rst.gwt.server.data;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.jgrapht.graph.SimpleDirectedGraph;

import cu.rst.gwt.server.alg.Algorithm;
import cu.rst.gwt.server.alg.EvaluationAlgorithm;
import cu.rst.gwt.server.alg.ReputationAlgorithm;
import cu.rst.gwt.server.alg.TrustAlgorithm;
import cu.rst.gwt.server.alg.eg.EigenTrust;
import cu.rst.gwt.server.alg.eg.RankbasedTrustAlg;
import cu.rst.gwt.server.exceptions.WorkflowException;
import cu.rst.gwt.server.graphs.FeedbackHistoryEdgeFactory;
import cu.rst.gwt.server.graphs.FeedbackHistoryGraph;
import cu.rst.gwt.server.graphs.Graph;
import cu.rst.gwt.server.graphs.ReputationEdgeFactory;
import cu.rst.gwt.server.graphs.ReputationGraph;
import cu.rst.gwt.server.graphs.TrustEdgeFactory;
import cu.rst.gwt.server.graphs.TrustGraph;
import cu.rst.gwt.server.util.DefaultArffFeedbackGenerator;
import cu.rst.gwt.server.util.Util;

/**
 * @author partheinstein
 * 
 * A work flow can be any of this:
 * 1. Feedback History Graph -> reputation alg -> reputation graph -> trust alg -> trust graph
 * 2. Feedback History Graph -> reputation alg -> reputation graph
 * 3. Reputation Graph -> reputation alg -> reputation graph
 * 4. Reputation Graph -> trust alg -> trust graph
 * 5. Reputation Graph -> reputation alg -> reputation graph -> trust alg -> trust graph
 * 6. Feedback History Graph -> reputation alg -> reputation graph -> reputation graph
 * work flow = {FRT, FR, FRR, RR, RT, RRT}
 *  
 * 
 *
 */
public class Workflow
{
	private static Logger logger = Logger.getLogger(Workflow.class); 
	//TODO should this be a list?
	public ArrayList sequence;
	
	public Workflow()
	{
		sequence = new ArrayList();
	}
	
	public void addItem(Object item) throws Exception
	{
		Util.assertNotNull(item);
		
		if(item instanceof Graph)
		{
			
			//no checks needed for the first element. 
			if(sequence.size() == 0)
			{
				logger.debug("Adding a graph");
				sequence.add(item);
			}
			else
			{
				Object lastItem = sequence.get(sequence.size() - 1);
				
				if(!(lastItem instanceof Algorithm))
				{
					throw new WorkflowException("Last item was not an algorithm.");
				}
				
				if(!((Algorithm) lastItem).assertGraph2OutputType((Graph) item))
				{
					throw new WorkflowException("Postconditions failed: assertGraph2OutputType()");
				}
				
				((Algorithm) lastItem).setGraph2Output((Graph) item);
				logger.debug("Adding a graph");
				sequence.add(item);
			}

			
		}
		else if(item instanceof Algorithm)
		{			
			if(sequence.size() < 1)
			{
				throw new WorkflowException("Sequence is empty. First add a graph before adding an algorithm.");
			}
			
			Object lastItem = sequence.get(sequence.size() - 1);			
			Util.assertNotNull(lastItem);
			
			if(!(lastItem instanceof Graph))
			{
				throw new WorkflowException("Last item was not a graph.");
			}
			
			Algorithm alg = (Algorithm) item;
			
			if(!alg.assertGraph2ListenType((Graph) lastItem))
			{
				throw new WorkflowException("Failed preconditions: assertGraph2ListenType()");
			}
			
			alg.setGraph2Listen((Graph) lastItem);
			
			((Graph) lastItem).addObserver(alg);
			
			
			if(item instanceof EvaluationAlgorithm)
			{	
				Util.assertNotNull(((EvaluationAlgorithm) alg).getWrappedAroundAlg());
			}
	
			logger.debug("Adding an algorithm.");
			sequence.add(item);
			
		}
		else
		{
			throw new WorkflowException("Unknown element. Cannot add.");
		}
		
	}
	
	/**
	 * Starts the workflow by invoking notifyObservers() on the first element (which must be a graph).
	 * @param update1AtATime Applies only if the first element in the workflow is a feedback history graph 
	 * @throws Exception
	 */
	public void start(boolean update1AtATime) throws Exception
	{
		Util.assertNotNull(sequence);
		Util.assertNotNull(sequence.get(0));
		
		if(!(sequence.get(0) instanceof SimpleDirectedGraph))
		{
			throw new WorkflowException("The first element in the sequence is not a graph.");
		}
		
		if(sequence.get(0) instanceof FeedbackHistoryGraph)
		{
			((FeedbackHistoryGraph) sequence.get(0)).notifyObservers(update1AtATime);
		}
		else if(sequence.get(0) instanceof ReputationGraph)
		{
			((ReputationGraph) sequence.get(0)).notifyObservers(null);
		}
		else if(sequence.get(0) instanceof TrustGraph)
		{
			((TrustGraph) sequence.get(0)).notifyObservers(null);
		}
		else
		{
			throw new WorkflowException("The first element in the sequence is an unknown graph,");
		}
	}
	
	/**
	 * Gets the first instance of a feeback history graph 
	 * @return FeedbackHistoryGraph if found or null if no fhg found.
	 * @throws Exception
	 */
	public FeedbackHistoryGraph getFeedbackHistoryGraph() throws Exception
	{
		for(Object o : sequence)
		{
			if(o instanceof Algorithm)
			{
				if(((Algorithm) o).getGraph2Listen() instanceof FeedbackHistoryGraph)
				{
					return (FeedbackHistoryGraph) ((Algorithm) o).getGraph2Listen();
				}
			}
		}
		
		return null;
	}
	
	/**
	 * This method returns the reputation graphs in the workflow by returning the graphs
	 * output by the reputation algorithms in the workflow. Note that this method has no
	 * knowledge on which reputation graph in the workflow will be modified. It is upto the
	 * caller to decide that. This method returns null if no reputation alg found or if there is
	 * a error getting the reputation graphs.
	 * 
	 * @return
	 * @throws Exception 
	 */
	public ArrayList<ReputationGraph> getReputationGraphs() throws Exception
	{
		ArrayList<ReputationGraph> repGraphs = null;
		
		for(Object o : sequence)
		{
			if(o instanceof ReputationAlgorithm)
			{
				ReputationAlgorithm repAlg = (ReputationAlgorithm)o;
				try
				{
					
					if(repGraphs == null) repGraphs = new ArrayList<ReputationGraph>();
					repGraphs.add((ReputationGraph) repAlg.getGraph2Output());
					
				}catch(Exception e)
				{
					logger.error(e.getMessage());
				}
			}
			
			if(o instanceof EvaluationAlgorithm)
			{
				Algorithm alg = ((EvaluationAlgorithm) o).getWrappedAroundAlg();
				Util.assertNotNull(alg);
				if(alg instanceof ReputationAlgorithm)
				{
					if(repGraphs == null) repGraphs = new ArrayList<ReputationGraph>();
					alg = (ReputationAlgorithm) alg;
					repGraphs.add((ReputationGraph) alg.getGraph2Output());
				}
						
			}
		}
		
		return repGraphs;
		
	}
	
	/**
	 * Gets the first instance of a trust graph
	 * @return
	 * @throws Exception
	 */
	public TrustGraph getTrustGraph() throws Exception
	{
		for(Object o : sequence)
		{
			if(o instanceof TrustAlgorithm)
			{
				TrustAlgorithm trustAlg = (TrustAlgorithm) o;
				return (TrustGraph) trustAlg.getGraph2Output();
			}
			
			//TODO get rid of this evaluation algorithm design
			if(o instanceof EvaluationAlgorithm)
			{
				Algorithm alg = ((EvaluationAlgorithm) o).getWrappedAroundAlg();
				if(alg instanceof TrustAlgorithm)
				{
					alg = (TrustAlgorithm) alg;
					return (TrustGraph) alg.getGraph2Output();
				}
				
			}
		}
		
		return null;
	}
	
	
	//TODO Methods to get the algorithms in the workflow.
	
	/**
	 * Given a particular class name of an element in the workflow sequence, this method returns the instance of the class
	 * in the workflow.
	 * @param sequence workflow
	 * @param itemType fully qualified class name
	 * @return
	 * @throws WorkflowException
	 */
	public static Object assertAndGetInstance(ArrayList sequence, String itemType) throws WorkflowException
	{
		if(sequence == null) throw new WorkflowException(" Workflow sequence cannot be null.");
		
		boolean found = false;
		
		for(Object o : sequence)
		{
			if(o.getClass().getCanonicalName().toLowerCase().contains(itemType.toLowerCase()))
			{
				return o;
			}
		}
		
		if(!found) throw new WorkflowException("An instance of " + itemType 
				+ " was not found in the workflow sequence.");
		
		//should never come here.
		return null;
		
	}
	
	@Override
	public String toString()
	{
		String begin = "{";
		String end = "}";
		String delimiter = ",";
		
		String string2return = begin;
		Iterator it = sequence.iterator();
		while(it.hasNext())
		{
			Object o = it.next();
			boolean addDelimiter = false;
			if(it.hasNext()) addDelimiter = true;
			
			if(o instanceof FeedbackHistoryGraph)
			{
				string2return += "FeedbackHistoryGraph";
				
			}else if(o instanceof ReputationGraph)
			{
				string2return += "ReputationGraph";
			}else if(o instanceof TrustGraph)
			{
				string2return += "TrustGraph";
			}else if(o instanceof ReputationAlgorithm)
			{
				string2return += "ReputationAlgorithm";
			}else if(o instanceof TrustAlgorithm)
			{
				string2return += "TrustAlgorithm";
			}else if(o instanceof TrustGraph)
			{
				string2return += "TrustGraph";
			}else if(o instanceof EvaluationAlgorithm)
			{
				string2return += "EvaluationAlgorithm";
			}
			else
			{
				string2return += "Unknown";
			}
			
			if(addDelimiter) string2return += delimiter + " ";
		}
		string2return += end;
		return string2return;
	}
	
	public static void main(String[] args) throws Exception
	{
		BasicConfigurator.configure();
		
		// create the graphs
		FeedbackHistoryGraph feedbackHistoryGraph = new FeedbackHistoryGraph(new FeedbackHistoryEdgeFactory());
		ReputationGraph repGraph = new ReputationGraph(new ReputationEdgeFactory());
		TrustGraph trustGraph = new TrustGraph(new TrustEdgeFactory());
		
		// populate the feedback history graph by parsing the feedbacks from a arff file
		DefaultArffFeedbackGenerator feedbackGen = new DefaultArffFeedbackGenerator();
		ArrayList<Feedback> feedbacks = (ArrayList<Feedback>) feedbackGen.generateHardcoded("feedbacks.arff");
		
		// add the feedbacks to the feedback history graph
		feedbackHistoryGraph.addFeedbacks(feedbacks, true);
		
		// create the algorithms
		EigenTrust repAlg = (EigenTrust) ReputationAlgorithm.getInstance("cu.repsystestbed.gwt.server.algorithms.examples.EigenTrust");
		RankbasedTrustAlg trustAlg = (RankbasedTrustAlg) TrustAlgorithm.getInstance("cu.repsystestbed.gwt.server.algorithms.examples.RankbasedTrustAlg");
		
		// create the work flow and add the items
		Workflow workflow = new Workflow();
		workflow.addItem(feedbackHistoryGraph);
		workflow.addItem(repAlg);
		workflow.addItem(repGraph);
		workflow.addItem(trustAlg);
		workflow.addItem(trustGraph);
		
		// notify the listener of the feedback history graph but in reality it should be the listeners of the first
		// graph in the workflow are notified. 
		workflow.getFeedbackHistoryGraph().notifyObservers(false);
		
		System.out.println(workflow);
		
		

	}

}
