package cu.rst.core.alg;

import java.util.ArrayList;

import cu.rst.core.graphs.FHG;
import cu.rst.core.graphs.Feedback;
import cu.rst.core.graphs.Graph;
import cu.rst.core.graphs.Graph.Type;
import cu.rst.core.petrinet.Token;

/**
 * This is a discretization algorithm that takes FHG0 where the edge values are continuous as input and 
 * outputs FHG1 where the edge vales are either -1 or 1. For e.g., if f(a,b) in FHG0 is greater than some threshold, then f'(a,b) in 
 * FHG1 is 1, otherwise -1.
 * @author partheinstein
 *
 *
 */
public class Discretizer extends Algorithm 
{

	private final double DEFAULT_THRESHOLD2SATISFY = 0.7;
	private double threshold = DEFAULT_THRESHOLD2SATISFY;
	
	@Override
	public boolean assertGraph2ListenType(Graph g) throws Exception 
	{
		return g instanceof FHG;
	}

	@Override
	public boolean assertGraph2OutputType(Graph g) throws Exception 
	{
		return g instanceof FHG;
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
