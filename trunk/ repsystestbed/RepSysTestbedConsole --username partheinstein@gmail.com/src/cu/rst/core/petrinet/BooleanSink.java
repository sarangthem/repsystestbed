/**
 * 
 */
package cu.rst.core.petrinet;

import java.util.ArrayList;

import cu.rst.util.Util;

/**
 * @author partheinstein
 *
 */
public class BooleanSink implements PetriNetElementIntf
{
	public boolean m_val;
	
	public BooleanSink()
	{
		m_val = false;
	}
	
	public boolean getVal()
	{
		return m_val;
	}
	
	public void setVal(boolean val)
	{
		m_val = val;
	}

	@Override
	public ArrayList update(ArrayList<Token> tokens) throws Exception 
	{
		Util.assertNotNull(tokens);
		if(tokens.size()==0) throw new Exception("Not enough tokens to update.");
		ArrayList<Boolean> toReturn = new ArrayList<Boolean>();
		for(Token t : tokens)
		{
			Util.assertNotNull(t);
			Util.assertNotNull(t.m_place);
			Boolean val = t.m_place instanceof Boolean ? (Boolean) t.m_place : false;
			setVal(val);
			toReturn.add(val);
		}
		
		return toReturn;
	}
	
	

	@Override
	public String getName() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setWorkflow(PetriNet net) throws Exception 
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public ArrayList update(ArrayList<Token> tokens, Place p) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

}
