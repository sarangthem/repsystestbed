package cu.rst.core.alg;

import java.util.ArrayList;
import java.util.Collections;

import org.apache.log4j.Logger;

import cu.rst.core.graphs.Agent;
import cu.rst.core.graphs.FHG;
import cu.rst.core.graphs.Feedback;
import cu.rst.core.graphs.Graph;
import cu.rst.core.graphs.Graph.Type;
import cu.rst.core.graphs.RG;
import cu.rst.core.graphs.ReputationEdge;
import cu.rst.core.petrinet.Place;
import cu.rst.core.petrinet.Token;
import cu.rst.util.GNUPlotDataDumper;
import cu.rst.util.Util;

public class EvalDPv2 extends Algorithm 
{

	static Logger logger = Logger.getLogger(EvalDP.class);
	
	private int xCounter = 0; //this is for plotting (x,f) and (x,r)
	private int xDecCounter = 0;
	GNUPlotDataDumper dumper;
	Agent agentToEvaluate;
	
	public EvalDPv2() throws Exception
	{
	    dumper = new GNUPlotDataDumper(5);
	    agentToEvaluate = new Agent(1); // agent b
	}
	
	@Override
	public boolean assertGraph2ListenType(Graph g) throws Exception {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean assertGraph2OutputType(Graph g) throws Exception {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean assertVariablePrecondition(double variable) throws Exception {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public Type getInputGraphType() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Type getOutputGraphType() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * The tokens can be unsorted and this method will attempt to sort it first based on the
	 * token id. Once sorted, token t(i+1) maps to t(i)
	 * Assumptions:
	 * 1. There are even number of tokens
	 * 2. 1 fhg token corresponds to 1 rg token (yes I am saying that tokens need to be colour coded)
	 * 3. The first 2 tokens always corresponds to the token in rg0 and fhg0. The others correspond to 
	 *    rg1 and fhg1.
	 * 4. Reputation graph is a global reputation graph
	 * 5. Only 1 change occurs at a time
	 */
	@Override
	public ArrayList update(ArrayList<Token> tokens) throws Exception 
	{
		Util.assertNotNull(tokens);
		if(tokens.size()>0 && tokens.size()%2 != 0) 
		{
			throw new Exception("Number of tokens <=0 or odd number of tokens. " +
					"This violates the preconditions for this evaluation.");
		}
		
		Collections.sort(tokens);
		ArrayList<Graph> graphs = new ArrayList<Graph>();
		
		for(int i=0; i<tokens.size();i=i+2)
		{
			Token t1 = tokens.get(i);
			Token t2 = tokens.get(i+1);
			if(((Place)t1.m_place).getGraph() instanceof RG 
					&& ((Place)t2.m_place).getGraph() instanceof FHG)
			{
				graphs.add(((Place)t1.m_place).getGraph());
				graphs.add(((Place)t2.m_place).getGraph());
				
			}
			else
			{
				throw new Exception("Expected t1 to correspond to RG and t2 to correspond to FHG");
			}			
		}
		
		//now we have tuples of graphs (rg, fhg0, rg1, fhg1, rg2, fhg2,...)
		int window = graphs.size()/2;
		Feedback[] feedbacks = new Feedback[window];
		double[] reputations = new double[window];
		int i,j;
		for(i=0, j=0;i<graphs.size();i=i+2, j++)
		{
			RG rg = (RG)graphs.get(i);
			FHG fhg = (FHG)graphs.get(i+1);
			
			//get the last feedback
			Feedback f = fhg.m_feedbacks.get(fhg.m_feedbacks.size()-1);
			feedbacks[j] = f;
			
			//assuming global reputation, we don't care about the src
			//hardcoded sink
			ReputationEdge re = (ReputationEdge)rg.getEdge(f.getAssesor(), agentToEvaluate);
			//if the src or sink doesn't exist in the graph, set rep = 0 meaning it is not trusted.
			reputations[j] = (re!=null)? re.getReputation() : 0; 
		}
		
		int numFeedbacks=0;
		for(i=0;i<feedbacks.length;i++) if(feedbacks[i]!=null) numFeedbacks++;
		
		if(numFeedbacks != window)
		{
			logger.info("Number of the last " + window + " feedbacks were not the same. Needed " 
						+ window + " feedbacks but got only " + numFeedbacks);
		}
		
		int x = 0;

		for(i=0;i<feedbacks.length;i++, xCounter++)
		{
			x = xCounter-xDecCounter;

			logger.info("Pts f and r: (" + x + ", " + feedbacks[i].value + ")" + ", (" + x + ", " + reputations[i] + ") ");
			
			if(x == 0)
			{
				double r0 = (double)1/(double)graphs.get(1).vertexSet().size(); //assuming fhg0.vertex
				dumper.addColumnData(x);
			    dumper.addColumnData(feedbacks[i].value);
			    dumper.addColumnData(reputations[i]);
			    dumper.addColumnData(feedbacks[i].value - r0);
			    dumper.addColumnData(reputations[i] - r0);
			}
			if(i == feedbacks.length - 1)
			{
			    dumper.addColumnData(x);
			    dumper.addColumnData(feedbacks[i].value);
			    dumper.addColumnData(reputations[i]);
			    dumper.addColumnData(feedbacks[i].value - reputations[i-1]);
			    dumper.addColumnData(reputations[i] - reputations[i-1]);
			}
			
		}
		xDecCounter = xDecCounter + 1;
		dumper.flush();
        
        return null;
	}
	

}
