package cu.rst.core.alg;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Set;

import org.apache.commons.math.stat.regression.SimpleRegression;
import org.apache.log4j.Logger;

import cu.rst.core.exceptions.EvaluationException;
import cu.rst.core.graphs.Agent;
import cu.rst.core.graphs.FHG;
import cu.rst.core.graphs.Feedback;
import cu.rst.core.graphs.FeedbackHistoryGraphEdge;
import cu.rst.core.graphs.Graph;
import cu.rst.core.graphs.Graph.Type;
import cu.rst.core.graphs.RG;
import cu.rst.core.graphs.ReputationEdge;
import cu.rst.core.petrinet.PetriNetEdge;
import cu.rst.core.petrinet.Place;
import cu.rst.core.petrinet.Token;
import cu.rst.core.petrinet.Transition;
import cu.rst.util.Util;

public class EvalDP extends Algorithm 
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
	static Logger logger = Logger.getLogger(EvalDP.class);
	private Hashtable<String, EvolData> dataPoints;
	
	public EvalDP()
	{
		dataPoints = new Hashtable<String, EvolData>();
	}
	@Override
	public boolean assertGraph2ListenType(Graph g) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean assertGraph2OutputType(Graph g) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean assertVariablePrecondition(double variable) throws Exception {
		// TODO Auto-generated method stub
		return false;
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

	@Override
	public ArrayList update(ArrayList<Token> tokens) throws Exception 
	{
		Util.assertNotNull(tokens);
		if(tokens.size()!=4) throw new Exception("Need 4 tokens to execute this evaluation algorithm.");
	
		/*
		 * Order of the tokens is important because thats what determine the input order
		 * evaldp(fhg0, rg0, fhg1, rg1). But the incoming edges of a transition doesn't guarantee that order.
		 * So I am using a heuristic to associate fhg0 with rg0 and fhg1 with rg1 as navigating through the workflow
		 * fhg0 -> transition -> rg0, fhg1 -> transition -> rg1  
		 */
		ArrayList<Place> rgs = getRGs(tokens);
		ArrayList<Place> fhgs = getFHGs(tokens);
		
		//match fhg0 and rg0
		FHG fhg0 = (FHG) fhgs.get(0).getGraph();
		int fhg0rg0_match = -1;
		for(PetriNetEdge e : this.getWorkflow().outgoingEdgesOf(fhgs.get(0)))
		{
			for(PetriNetEdge e1 : this.getWorkflow().outgoingEdgesOf(((Transition)e.sink)))
			{
				for(int i = 0; i<rgs.size(); i++)
				{
					RG rg = (RG) rgs.get(i).getGraph();
					try
					{
						if(((Place)e1.sink).getGraph().equals(rg))
						{
							fhg0rg0_match = i; 
							break;
						}
					}catch(Exception ex)
					{
						//not a graph, continue on
					}
				}
				if(fhg0rg0_match != -1) break;
			}
		}

		//match fhg1 and rg1
		FHG fhg1 = (FHG) fhgs.get(1).getGraph();
		int fhg1rg1_match = -1;
		for(PetriNetEdge e : this.getWorkflow().outgoingEdgesOf(fhgs.get(1)))
		{
			for(PetriNetEdge e1 : this.getWorkflow().outgoingEdgesOf(((Transition)e.sink)))
			{
				for(int i = 0; i<rgs.size(); i++)
				{
					RG rg = (RG) rgs.get(i).getGraph();
					try
					{
						if(((Place)e1.sink).getGraph().equals(rg))
						{
							fhg1rg1_match = i; 
							break;
						}
					}catch(Exception ex)
					{
						//not a graph, continue on
					}
				}
				if(fhg1rg1_match != -1) break;
			}
		}

		if(fhg0rg0_match == fhg1rg1_match)
		{
			throw new Exception("Oops. Multiple matches found when matching fhg0 with rg0 and fhg1 with rg1");
		}
		
		RG rg0 = (RG) rgs.get(fhg0rg0_match).getGraph();
		RG rg1 = (RG) rgs.get(fhg1rg1_match).getGraph();
		
		int i = fhg0.m_feedbacks.size();
		if(fhg1.m_feedbacks.size() > i)
		{
			//assuming only one feedback added
			Feedback f1 = (Feedback) fhg1.m_feedbacks.get(i);
			Feedback f0 = getLastFeedback(fhg0.m_feedbacks, f1.getAssesee());
//			Feedback f0 = (Feedback) fhg0.m_feedbacks.get(i-1);
//			Feedback f1 = (Feedback) fhg1.m_feedbacks.get(i);
			
			if(f0 != null)
			{
				//at this point, we are guaranteed to have f0.assessee = f1.assessee
				
				Set<FeedbackHistoryGraphEdge> fhges = fhg0.incomingEdgesOf(f0.getAssesee());
				
				//fhg0 must contain the new feedback's assessee but that's guaranteed because f0 != null. so
				//commenting the following if.
				
				//if(fhges!=null && fhges.size()>0 && fhg0.containsVertex(f1.getAssesee())) 
				//{
					
					//global rep graph - can use any incoming edges because they all have the same weights
					//note that you can't use f1.getAssessor because it may not be present in fhg1.
					FeedbackHistoryGraphEdge fhge = (FeedbackHistoryGraphEdge) fhges.toArray()[0];
					
					EvolData data = dataPoints.remove(getHashCode(f1.getAssesee()));
					if(data == null)
					{
						data = new EvolData(new ArrayList<Double>(), new ArrayList<Double>(),
								new ArrayList<Double>(), new ArrayList<Double>());
					}
					
					ReputationEdge e0 = ((ReputationEdge)rg0.getEdge(fhge.src, f1.getAssesee()));
					ReputationEdge e1 = ((ReputationEdge)rg1.getEdge(fhge.src, f1.getAssesee()));
					
					double rep0 = e0.getReputation();
					double rep1 = e1.getReputation();
					
					double x = f1.value - f0.value;
					double y = rep1 - rep0;
					logger.debug("f(i+1)=" + f1.value + ", r(i+1)=" + rep1 + ", f(i)=" + f0.value + ", r(i)" + rep0);
					
					if(f1.value > f0.value)
					{
						logger.info("feedbacks[i+1] is greater than feedbacks[i]. delta(f)=" + x + ", delta(r)=" + y);
						data.m_positiveXPoints.add(x);
						data.m_positiveYPoints.add(y);
						
						if((rep1 - rep0) <= 0)
						{
							logger.error("trustScore[i+1] is less than trustScore[i]");
							throw new EvaluationException("Feedback change = " + (f1.value - f0.value) 
									+ ". Trust score change = " + (rep1 - rep0) + ". Expected an increase in trust score."
									+ " Src = " + fhge.src  + ",Sink = " + f1.getAssesee());
						}
					}
					else if(f1.value < f0.value)
					{
						logger.info("feedbacks[i+1] is less than feedbacks[i]. delta(f)=" + x + ", delta(r)=" + y);
						data.m_negativeXPoints.add(x);
						data.m_negativeYPoints.add(y);
						
						if((rep1 - rep0) >= 0)
						{
							logger.error("trustScore[i+1] is greater or equal to trustScore[i]");
							throw new EvaluationException("Feedback change = " + (f1.value - f0.value) 
									+ ". Trust score change = " + (rep1 - rep0) + ". Expected a decrease in trust score."
									+ " Src = " + fhge.src  + ",Sink = " + f1.getAssesee());
						}
					}
					else
					{
						logger.info("feedbacks[i+1] = feedbacks[i]. feedbacks[i+1]=" + f1 + ". feedbacks[i]=" + f0 + ". delta(f)=" + x + ", delta(r)=" + y);
					}	
					
					dataPoints.put(getHashCode((Agent)fhge.sink), data);
				//}
				
				for(EvolData evolData : dataPoints.values())
				{
					if(evolData == null)
					{
						continue;
					}
					
					SimpleRegression positive = new SimpleRegression();
					SimpleRegression negative = new SimpleRegression();
					
					for(i=0; i<evolData.m_positiveXPoints.size(); i++)
					{
						positive.addData(evolData.m_positiveXPoints.get(i), evolData.m_positiveYPoints.get(i));
					}
					
					for(i=0; i<evolData.m_negativeXPoints.size(); i++)
					{
						negative.addData(evolData.m_negativeXPoints.get(i),evolData. m_negativeYPoints.get(i));
					}
					
					logger.debug("Number of negative points: " + evolData.m_negativeXPoints.size());
					logger.debug("Number of positive points: " + evolData.m_positiveXPoints.size());
					
					double nSlope = negative.getSlope();
					double pSlope = positive.getSlope();
					
					if(evolData.m_positiveXPoints.size() < 2 || evolData.m_positiveYPoints.size() < 2)
					{
						//throw new EvaluationException(" Not enough points. Need atleast 2 points to draw a line. ");
						logger.debug(" Not enough points. Need atleast 2 points to draw a line. ");
						continue;
					}
					
					logger.info("nSlope: n(x)=" + nSlope + "*x + " + negative.getIntercept());
					logger.info("pSlope: n(x)=" + pSlope + "*x + " + positive.getIntercept());
							
					if(Math.abs(nSlope) <= Math.abs(pSlope))
					{
//						throw new EvaluationException(" Negative regression line slope (" + nSlope
//								+ ") is less than positive regression line slope (" + pSlope + ").");
						
						logger.info(" Negative regression line slope (" + nSlope
								+ ") is less than positive regression line slope (" + pSlope + ").");
					}
							
					logger.debug("Evaluation passed. Negative regression line slope=" + negative.getSlope() 
							+ ". Positive regression line slope=" + positive.getSlope());
				}
			}
			
		}
		
		return null;
	}
	
	private ArrayList<Place> getRGs(ArrayList<Token> tokens) throws Exception
	{
		Util.assertNotNull(tokens);
		ArrayList<Place> rgs = new ArrayList<Place>();
		for(Token t : tokens)
		{
			if(t.m_place instanceof Place && ((Place)t.m_place).getGraph() instanceof RG)
			{
				rgs.add((Place)t.m_place);
			}
		}
		return rgs;
	}
	
	private ArrayList<Place> getFHGs(ArrayList<Token> tokens) throws Exception
	{
		Util.assertNotNull(tokens);
		ArrayList<Place> fhgs = new ArrayList<Place>();
		for(Token t : tokens)
		{
			if(t.m_place instanceof Place && ((Place)t.m_place).getGraph() instanceof FHG)
			{
				fhgs.add((Place)t.m_place);
			}
		}
		return fhgs;
	}
	
	private Feedback getLastFeedback(ArrayList<Feedback> feedbacks, Agent assessee) throws Exception
	{
		Util.assertNotNull(feedbacks);
		Util.assertNotNull(assessee);
		
		for(int i=feedbacks.size()-1; i>=0; i--)
		{
			if(feedbacks.get(i).getAssesee().equals(assessee)) return feedbacks.get(i);
		}
		return null;
		
	}
	
	private String getHashCode(Agent sink)
	{
		return (new Integer(sink.id)).toString();
	}

}
