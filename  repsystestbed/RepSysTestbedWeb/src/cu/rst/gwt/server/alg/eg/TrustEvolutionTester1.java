/**
 * 
 */
package cu.rst.gwt.server.alg.eg;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import cu.rst.gwt.server.alg.EvaluationAlgorithm;
import cu.rst.gwt.server.data.Feedback;
import cu.rst.gwt.server.entities.Agent;
import cu.rst.gwt.server.exceptions.EvaluationException;
import cu.rst.gwt.server.graphs.FeedbackHistoryGraph;
import cu.rst.gwt.server.graphs.FeedbackHistoryGraphEdge;
import cu.rst.gwt.server.graphs.Graph;
import cu.rst.gwt.server.graphs.ReputationEdge;
import cu.rst.gwt.server.graphs.ReputationGraph;
import cu.rst.gwt.server.graphs.Graph.Type;
import cu.rst.gwt.server.util.Util;

/**
 * @author partheinstein
 * This version simply checks the following:
 * - A positive feedback results in increase in trust
 * - A negative feedback results in decrease in trust
 * If either of these 2 conditions fail, an exception is thrown. 
 *@deprecated
 */
public class TrustEvolutionTester1 extends EvaluationAlgorithm
{

	static Logger logger = Logger.getLogger(TrustEvolutionTester1.class);

	public TrustEvolutionTester1()
	{
		m_stopAtFirstFailure = true;
	}
	
	@Override
	public void evaluate(ArrayList changes) throws Exception
	{
		try
		{
			Util.assertNotNull(m_alg);
		}
		catch(Exception e)
		{
			throw new EvaluationException("The inner algorithm is not set. ", e);
		}
		
		if(!(m_alg.getGraph2Listen() instanceof FeedbackHistoryGraph))
		{
			throw new EvaluationException("This evaluation needs a FeedbackHistoryGraph as input.");
		}
		
		if(changes != null)
		{
			if(changes.size() < 1)
			{
				throw new EvaluationException("Changes is not null but there are no changes.");
			}
			
			try
			{
				for(FeedbackHistoryGraphEdge fhge : (ArrayList<FeedbackHistoryGraphEdge>) changes)
				{

					Feedback f1 = null, f0 = null;
					double ts1 = Double.MIN_VALUE, ts0 = Double.MIN_VALUE;
					
					if(fhge.feedbacks.size() >= 2)
					{
						f1 = fhge.feedbacks.get(fhge.feedbacks.size() - 1);
						f0 = fhge.feedbacks.get(fhge.feedbacks.size() - 2);
					}
					
					ReputationGraph rg = (ReputationGraph) m_alg.getGraph2Output();
					Util.assertNotNull(rg);
					
					ReputationEdge re = (ReputationEdge) rg.getEdge((Agent) fhge.src, (Agent) fhge.sink);
					Util.assertNotNull(re);
					
					/*
					 * It is ok to assume that feedback additions results in change in reputation. So we
					 * can do take the first and the last In fact,
					 * if this assumption fails, the inner alg does not comply with Marsh's trust evolution 
					 * conditions.
					 */
					
					if(re.getReputationHistory().size() >= 2)
					{
						ts1 = re.getReputationHistory().get(re.getReputationHistory().size() - 1);
						ts0 = re.getReputationHistory().get(re.getReputationHistory().size() - 2);
					}
					
					if(f1 != null && f0 != null && ts1 > Double.MIN_VALUE && ts0 > Double.MIN_VALUE )
					{
						double x = f1.value - f0.value;
						double y = ts1 - ts0;
						
						if(f1.value > f0.value)
						{
							logger.info("feedbacks[i+1] is greater than feedbacks[i]");
							
							if((ts1 - ts0) <= 0)
							{
								logger.error("trustScore[i+1] is less than trustScore[i]");
								throw new EvaluationException("Feedback change = " + (f1.value - f0.value) 
										+ ". Trust score change = " + (ts1 - ts0) + ". Expected an increase in trust score."
										+ " Src = " + fhge.src  + ",Sink = " + fhge.sink);
							}
						}
						else if(f1.value < f0.value)
						{
							logger.info("feedbacks[i+1] is less than feedbacks[i]");
							
							if((ts1 - ts0) >= 0)
							{
								logger.error("trustScore[i+1] is greater or equal to trustScore[i]");
								throw new EvaluationException("Feedback change = " + (f1.value - f0.value) 
										+ ". Trust score change = " + (ts1 - ts0) + ". Expected a decrease in trust score."
										+ " Src = " + fhge.src  + ",Sink = " + fhge.sink);
							}
						}
						else
						{
							logger.info("feedbacks[i+1] = feedbacks[i]");
						}	
					}
					
				}
			}
			catch(ClassCastException e)
			{
				throw new EvaluationException("Changes does not contain a list of FeedbackHistoryGraphEdge. ", e);
			}
			
		}
		else
		{
			logger.info("No changes passed.");
		}
		
	}
	
	private String getHashCode(Agent src, Agent sink)
	{
		return src.id + ":" + sink.id;
	}
	
	public static void main(String[] args) throws Exception
	{
		System.out.println("TrustEvolutionTester");
	}

	@Override
	public void start() throws Exception
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void finish() throws Exception
	{		
		
	}
	
	@Override
	public boolean assertGraph2ListenType(Graph g) throws Exception
	{
		if(!super.assertGraph2ListenType(g)) return false;
		if(!(g instanceof FeedbackHistoryGraph)) return false;
		return true;
	}

	@Override
	public boolean assertGraph2OutputType(Graph g) throws Exception
	{
		if(!super.assertGraph2OutputType(g)) return false;
		if(!(g instanceof ReputationGraph)) return false;
		return true;
	}

	@Override
	public Type getInputGraphType() throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Type getOutputGraphType() throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

}
