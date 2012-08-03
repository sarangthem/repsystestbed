package cu.rst.core.graphs;

import java.util.ArrayList;
import java.util.Set;

import org.apache.log4j.Logger;

import cu.rst.core.petrinet.Token;
import cu.rst.util.Util;

/**
 * This class models a Trust graph.
 * @author pchandra
 *
 */
public class TG extends Graph<Agent, TrustEdge>
{

	private static final long serialVersionUID = -327490271972222723L;
	static Logger logger = Logger.getLogger(TG.class.getName());

	public TG(TrustEdgeFactory trustEdgeFactory)
	{
		super(trustEdgeFactory);
	}

	@Override
	public String toString()
	{
		String temp = null;
		temp = "Trust Graph" + System.getProperty("line.separator");
		temp += "Vertices:" + System.getProperty("line.separator");
		for(Agent a : (Set<Agent>) super.vertexSet())
		{
			temp += a + ",";
		}
		temp += System.getProperty("line.separator") + "Edges:" + System.getProperty("line.separator");
		for(TrustEdge e : (Set<TrustEdge>) super.edgeSet())
		{
			temp += e.toString() + " ,";
		}	
		return System.getProperty("line.separator") + temp;
	}

	@Override
	public TG clone(boolean addObservers)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TG getTransitiveClosureGraph()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public void addEdge(Agent src, Agent sink)
	{
		if(!this.containsVertex(src)) this.addVertex(src);
		if(!this.containsVertex(sink)) this.addVertex(sink);
		addEdge((Object)src, (Object)sink);
	}

	@Override
	public ArrayList update(ArrayList<Token> tokens) throws Exception 
	{
		Util.assertNotNull(tokens);
		
		for(Token t : tokens)
		{
			for(TrustEdge e : (ArrayList<TrustEdge>) t.m_changes)
			{
				addEdge(e.src, e.sink);
			}
		}
		
		return null;
	}

	@Override
	public void deleteChanges(Token t) throws Exception 
	{
		for(TrustEdge e : (ArrayList<TrustEdge>) t.m_changes)
		{
			removeEdge(e.src, e.sink);
		}
	}
	

}
