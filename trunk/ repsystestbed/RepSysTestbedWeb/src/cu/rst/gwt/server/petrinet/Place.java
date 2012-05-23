package cu.rst.gwt.server.petrinet;

import java.util.ArrayList;
import java.util.HashMap;

import cu.rst.gwt.server.graphs.Graph;
import cu.rst.gwt.server.util.Util;

/**
 * A place embeds a graph/file or other data sources. Tokens on a place means its conditions have been satisfied.
 * @author partheinstein
 *
 */
public class Place implements PetriNetElementIntf 
{
	private ArrayList<Token> m_tokens;
	protected int m_id;
	
	private PetriNetElementIntf m_container;
	
	public Place(PetriNetElementIntf graph)
	{
		m_container = graph;
		m_id = PetriNet.globalCounter++;
	}
	
	public Graph getGraph() throws Exception
	{
		if(m_container instanceof Graph) return (Graph) m_container;
		else throw new Exception("Not a graph");
	}
	
	public ArrayList<Token> getTokens()
	{
		return m_tokens;
	}
	
	public int numTokens()
	{
		return (m_tokens!=null)? m_tokens.size() : 0;
	}
	
	public void putToken(Token t)
	{
		if(m_tokens == null) m_tokens = new ArrayList<Token>();
		m_tokens.add(t);
	}
	
	
	public void putTokens(ArrayList<Token> tokens) throws Exception
	{
		Util.assertNotNull(tokens);
		for(Token t : tokens)
		{
			putToken(t);
		}
	}
	
	/**
	 * Clears all tokens of all types
	 */
	public void deleteAllTokens()
	{
		if(m_tokens != null)
		{
			m_tokens.clear();
		}
	}
	
	public void deleteTokens(int n)
	{
		if(m_tokens != null && m_tokens.size() >= n)
		{
			for(int i=0; i<n; i++)
			{
				m_tokens.remove(i);
			}
		}
		
	}
	
	@Override
	public boolean equals(Object o)
	{
		if(o instanceof Place)
		{
			Place p = (Place)o;
			return (p.m_id == this.m_id)? true : false;
		}
		else return false;
	}

	@Override
	public ArrayList update(ArrayList<Token> tokens) throws Exception 
	{
		return m_container.update(tokens);
	}
	
	@Override
	public String toString()
	{
		return "Place: " + m_container.getClass().getCanonicalName() + ", Number of tokens: " + numTokens();
	}
	
}
