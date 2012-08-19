package cu.rst.core.petrinet;

import java.util.ArrayList;

import cu.rst.util.Util;

public class DoubleSink extends Place
{
	public double m_val;
	
	
	public DoubleSink()
	{
		m_val = Double.MIN_VALUE;
	}
	
	public double getVal()
	{
		return m_val;
	}
	
	public void setVal(double val)
	{
		m_val = val;
	}

	@Override
	public ArrayList update(ArrayList<Token> tokens) throws Exception 
	{
		Util.assertNotNull(tokens);
		if(tokens.size()==0) throw new Exception("Not enough tokens to update.");
		ArrayList<Double> toReturn = new ArrayList<Double>();
		for(Token t : tokens)
		{
			Util.assertNotNull(t);
			Util.assertNotNull(t.m_place);
			Double val = t.m_place instanceof Double ? (Double) t.m_place : Double.MIN_VALUE;
			setVal(val);
			toReturn.add(val);
		}
		
		return toReturn;
	}
	
	
	@Override
	public ArrayList update(ArrayList<Token> tokens, Place p) throws Exception
	{
		return update(tokens);
	}
}
