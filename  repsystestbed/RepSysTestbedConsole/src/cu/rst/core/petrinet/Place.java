package cu.rst.core.petrinet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cu.rst.core.graphs.Graph;
import cu.rst.util.Util;

/**
 * A place embeds a graph/file or other data sources. Tokens on a place means its conditions have been satisfied.
 * @author partheinstein
 *
 */
public class Place implements PetriNetElementIntf 
{
	private ArrayList<Token> m_tokens;
	protected int m_id;
	private PetriNet workflow;
	
	private PetriNetElementIntf m_container;
	
	public Place(PetriNetElementIntf graph)
	{
		m_container = graph;
		m_id = PetriNet.globalCounter++;
	}
	
	/**
	 * Returns the containted graph. If it is not a graph, throws an exception.
	 */
	public Graph getGraph() throws Exception
	{
		if(m_container instanceof Graph) return (Graph) m_container;
		else throw new Exception("Not a graph");
	}
	
	public ArrayList<Token> getTokens()
	{
		return m_tokens;
	}
	
	public List<Token> getTokens(int n)
	{
		return (m_tokens!=null)? (List<Token>)m_tokens.subList(0, n) : new ArrayList<Token>();
	}
	
	public int numTokens()
	{
		return (m_tokens!=null)? m_tokens.size() : 0;
	}
	
	/**
	 * Puts a token on the place. Each place maintains a collection of tokens. The given token is added to this collection
	 * @param t token to be put
	 */
	public void putToken(Token t)
	{
		if(m_tokens == null) m_tokens = new ArrayList<Token>();
		m_tokens.add(t);
	}
	
	/**
	 * Puts a token on the place. Each place maintains a collection of tokens. The given token is added to this collection
	 * @param t token to be put
	 * @param update If true, the underlying object (graph, boolean variable, etc) is updated. 
	 * @throws Exception
	 */
	public void putToken(Token t, boolean update) throws Exception
	{
		if(m_tokens == null) m_tokens = new ArrayList<Token>();
		
		m_tokens.add(t);
		if(update && m_container!=null)
		{
			ArrayList changes = m_container.update(m_tokens);
			//if no changes, remove the token
//				if(changes !=null && changes.size()==0) m_tokens.remove(t); 
		}
	
		
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
	 * Clears all tokens 
	 */
	public void deleteAllTokens()
	{
		if(m_tokens != null)
		{
			m_tokens.clear();
		}
	}
	
	/**
	 * Delete specified number of tokens. Note that this method will delete the first n number of tokens in the place.
	 * @param n number of tokens to be deleted.
	 */
	public void deleteTokens(int n) throws Exception
	{
		if(m_tokens != null && m_tokens.size() >= n)
		{
			for(int i=0; i<n; i++)
			{
				Token t = m_tokens.remove(i);
				//delete the changes from the graph
				//this.getGraph().deleteChanges(t);
				
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
	
	@Override
	public String getName() throws Exception
	{
		return (this.m_container!=null)? this.m_container.getName(): null;
	}

	@Override
	public void setWorkflow(PetriNet net) throws Exception 
	{
		this.workflow = net;
		this.m_container.setWorkflow(net);
		
	}
	
	public PetriNetElementIntf getContainedElement()
	{
		return m_container;
	}
	
	public ArrayList update(ArrayList<Token> tokens, Place p) throws Exception
	{
		return update(tokens);
	}
	
}
