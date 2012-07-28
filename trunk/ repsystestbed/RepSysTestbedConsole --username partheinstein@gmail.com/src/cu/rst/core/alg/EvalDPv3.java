package cu.rst.core.alg;

import java.util.ArrayList;
import java.util.Collections;

import cu.rst.core.graphs.FHG;
import cu.rst.core.graphs.Feedback;
import cu.rst.core.graphs.Graph;
import cu.rst.core.graphs.Graph.Type;
import cu.rst.core.graphs.RG;
import cu.rst.core.graphs.ReputationEdge;
import cu.rst.core.petrinet.Place;
import cu.rst.core.petrinet.Token;
import cu.rst.util.Util;

public class EvalDPv3 extends Algorithm 
{

	@Override
	public boolean assertGraph2ListenType(Graph g) throws Exception
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean assertGraph2OutputType(Graph g) throws Exception
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean assertVariablePrecondition(double variable) throws Exception
	{
		// TODO Auto-generated method stub
		return false;
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

	/**
	 * 
	 */
	@Override
	public ArrayList update(ArrayList<Token> tokens) throws Exception
	{
		Util.assertNotNull(tokens);
		if(tokens.size() != 3) throw new Exception("Must have only 3 tokens.");
		
		Collections.sort(tokens);
		
		ArrayList<Graph> graphs = new ArrayList<Graph>();
		graphs.add(((Place)tokens.get(0).m_place).getGraph());
		graphs.add(((Place)tokens.get(1).m_place).getGraph());
		graphs.add(((Place)tokens.get(2).m_place).getGraph());
		
		if(!(graphs.get(0) instanceof RG)) throw new Exception("Expected the first token to contain RG");
		if(!(graphs.get(1) instanceof RG)) throw new Exception("Expected the second token to contain FHG");
		if(!(graphs.get(2) instanceof FHG)) throw new Exception("Expected the third token to contain FHG");
		
		ArrayList toReturn = new ArrayList();
		
		RG rg0 = (RG)graphs.get(0);
		RG rg1 = (RG)graphs.get(1);
		FHG fhg1 = (FHG)graphs.get(2);
		
		Feedback f = fhg1.m_feedbacks.get(fhg1.m_feedbacks.size()-1); //last feedback
		double rep0 = ((ReputationEdge)rg0.getEdge(f.getAssesor(), f.getAssesee())).getReputation();
		double rep1 = ((ReputationEdge)rg1.getEdge(f.getAssesor(), f.getAssesee())).getReputation();
		
		double metric = (f.value - rep0)/(rep1 - rep0);
		System.out.println("Metric=" + metric);
		if(metric >= 0) 
			toReturn.add(new Boolean(true));
		else 
			toReturn.add(new Boolean(false));
		
		return toReturn;
	}

}
