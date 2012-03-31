/**
 * 
 */
package cu.rst.gwt.server.alg.eg;

import java.util.ArrayList;
import java.util.Hashtable;

import org.apache.commons.math.stat.regression.SimpleRegression;
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
 * This algorithm looks trust evolution:
 * Suppose r(a, b) âˆˆ R be the trust that a has in b following a feedback t(a, b) âˆˆ R , r (a, b) be the trust 
 * following a feedback t (a, b), and âˆ†r and âˆ†t be the change in trust values and feedback values respectively. 
 * Then, the rate of change of trust due to feedbacks can be determined as âˆ†r . A âˆ†r versus âˆ†t graph is plotted 
 * and âˆ†t then the slopes of the trust gain and trust loss lines are determined separately. If the slope 
 * of the trust gain line is less than the slope of the trust loss line, then we can conï¬�rm that 
 * the trust is indeed gained slower than it is lost. 
 *
 */
public class TrustEvolutionTester2 extends EvaluationAlgorithm
{

	class EvolData
	{
		public ArrayList<Double> m_positiveXPoints;
		public ArrayList<Double> m_positiveYPoints;
		public ArrayList<Double> m_negativeXPoints;
		public ArrayList<Double> m_negativeYPoints;
		
		
		public EvolData(ArrayList<Double> positiveXPoints, ArrayList<Double> positiveYPoints,
				ArrayList<Double> negativeXPoints, ArrayList<Double> negativeYPoints) throws Exception
		{
			Util.assertNotNull(positiveXPoints);
			Util.assertNotNull(positiveYPoints);
			Util.assertNotNull(negativeXPoints);
			Util.assertNotNull(negativeYPoints);
			
			m_positiveXPoints = positiveXPoints;
			m_positiveYPoints = positiveYPoints;
			m_negativeXPoints = negativeXPoints;
			m_negativeYPoints = negativeYPoints;
			
		}
		
		@Override
		public String toString()
		{
			String string2Return = "\nPositive coordinates:";
			for(int i=0; i<m_positiveYPoints.size(); i++)
			{
				string2Return += "(" + m_positiveXPoints.get(i) + ", " + m_positiveYPoints.get(i) + ")";
			}
			
			string2Return += "\nNegative coordinates:";
			
			for(int i=0; i<m_negativeYPoints.size(); i++)
			{
				string2Return += "(" + m_negativeXPoints.get(i) + ", " + m_negativeYPoints.get(i) + ")";
			}
			
			string2Return += "\n";
			
			return string2Return;
		}
		
	}
	
	static Logger logger = Logger.getLogger(TrustEvolutionTester1.class);
	private Hashtable<String, EvolData> dataPoints;
	
	public TrustEvolutionTester2()
	{
		dataPoints = new Hashtable<String, EvolData>();
		m_stopAtFirstFailure = false;
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
					EvolData data = dataPoints.remove(getHashCode((Agent)fhge.src, (Agent)fhge.sink));
					if(data == null)
					{
						data = new EvolData(new ArrayList<Double>(), new ArrayList<Double>(),
								new ArrayList<Double>(), new ArrayList<Double>());
					}
					
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
							data.m_positiveXPoints.add(x);
							data.m_positiveYPoints.add(y);
						}
						else if(f1.value < f0.value)
						{
							logger.info("feedbacks[i+1] is less than feedbacks[i]");
							data.m_negativeXPoints.add(x);
							data.m_negativeYPoints.add(y);
						}
						else
						{
							logger.info("feedbacks[i+1] = feedbacks[i]");
						}	
					}
					
					dataPoints.put(getHashCode((Agent)fhge.src, (Agent)fhge.sink), data);
					logger.debug("Data point added. Key=" + getHashCode((Agent)fhge.src, (Agent)fhge.sink) + "Data: " + data);
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
		for(EvolData evolData : dataPoints.values())
		{
			if(evolData == null)
			{
				continue;
			}
			
			SimpleRegression positive = new SimpleRegression();
			SimpleRegression negative = new SimpleRegression();
			
			for(int i=0; i<evolData.m_positiveXPoints.size(); i++)
			{
				positive.addData(evolData.m_positiveXPoints.get(i), evolData.m_positiveYPoints.get(i));
			}
			
			for(int i=0; i<evolData.m_negativeXPoints.size(); i++)
			{
				negative.addData(evolData.m_negativeXPoints.get(i),evolData. m_negativeYPoints.get(i));
			}
			
			logger.debug("Number of negative points: " + evolData.m_negativeXPoints.size());
			logger.debug("Number of positive points: " + evolData.m_positiveXPoints.size());
			
			double nSlope = negative.getSlope();
			double pSlope = positive.getSlope();
			
			if(evolData.m_positiveXPoints.size() < 2 || evolData.m_positiveXPoints.size() < 2)
			{
				//throw new EvaluationException(" Not enough points. Need atleast 2 points to draw a line. ");
				logger.error(" Not enough points. Need atleast 2 points to draw a line. ");
				continue;
			}
					
			if(Math.abs(nSlope) <= Math.abs(pSlope))
			{
				throw new EvaluationException(" Negative regression line slope (" + nSlope
						+ ") is less than positive regression line slope (" + pSlope + ").");
			}
					
			logger.debug("Evaluation passed. Negative regression line slope=" + negative.getSlope() 
					+ ". Positive regression line slope=" + positive.getSlope());

		}
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
