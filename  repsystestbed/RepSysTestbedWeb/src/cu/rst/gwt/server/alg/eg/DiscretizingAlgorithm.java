/**
 * 
 */
package cu.rst.gwt.server.alg.eg;

import java.util.ArrayList;

import cu.rst.gwt.server.alg.Algorithm;
import cu.rst.gwt.server.data.Feedback;
import cu.rst.gwt.server.graphs.Graph;
import cu.rst.gwt.server.graphs.FeedbackHistoryGraph;
import cu.rst.gwt.server.graphs.Graph.Type;
import cu.rst.gwt.server.petrinet.Token;

/**
 * @author partheinstein
 *
 */
public class DiscretizingAlgorithm extends Algorithm 
{

	private final double DEFAULT_THRESHOLD2SATISFY = 0.7;
	private double threshold = DEFAULT_THRESHOLD2SATISFY;
	
	@Override
	public boolean assertGraph2ListenType(Graph g) throws Exception 
	{
		return g instanceof FeedbackHistoryGraph;
	}

	@Override
	public boolean assertGraph2OutputType(Graph g) throws Exception 
	{
		return g instanceof FeedbackHistoryGraph;
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
		ArrayList<Feedback> changes = new ArrayList<Feedback>();
		//expecting a list of feedbacks
		for(Token t : tokens)
		{
			for(Feedback f : (ArrayList<Feedback>) t.m_changes)
			{
				if(f.value >= threshold)
				{
					changes.add(new Feedback(f.getAssesor(), f.getAssesee(), (double) 1));
				}
				else
				{
					changes.add(new Feedback(f.getAssesor(), f.getAssesee(), (double) 0));
				}
			}
		}
		
		return changes;
	}

}
