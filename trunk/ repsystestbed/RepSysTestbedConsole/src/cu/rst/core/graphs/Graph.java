package cu.rst.core.graphs;

import java.util.ArrayList;

import org.jgrapht.EdgeFactory;
import org.jgrapht.graph.DirectedPseudograph;

import cu.rst.core.petrinet.PetriNetEdge;
import cu.rst.core.petrinet.PetriNet;
import cu.rst.core.petrinet.PetriNetElementIntf;
import cu.rst.core.petrinet.Place;
import cu.rst.core.petrinet.Token;

/**
 * @author partheinstein
 *
 */
public abstract class Graph<V, E> extends DirectedPseudograph implements cu.rst.core.petrinet.PetriNetElementIntf
{
	/*
	 *  FHG = Feedback History Graph
	 *  RG = Reputation Graph
	 *  TG = Trust Graph
	 *  PN = Petri Net
	 */
	public static enum Type{FHG, RG, TG, PN};
	public int m_id;
	private static int ID;
	private PetriNet workflow;
	
	ArrayList observers; 
	public Graph(EdgeFactory ef)
	{
		super(ef);
		m_id = ID++;
		observers = new ArrayList();
	}

	public abstract void deleteChanges(Token t) throws Exception;
	public abstract Graph<V, E> clone(boolean addObservers);
	public abstract Graph<V, E> getTransitiveClosureGraph();
	public void removeAllObservers()
	{
		observers.clear();
	}
	
	@Override
	public String getName()
	{
		return getClass().getSimpleName() + m_id;
	}
	
	public static void resetCounter()
	{
		ID = 0;
	}
	
	@Override
	public void setWorkflow(PetriNet net)
	{
		this.workflow = net;
	}
	
	public PetriNet getWorkflow()
	{
		return this.workflow;
	}
	
	@Override
	public boolean equals(Object o)
	{
		if(!(o instanceof Graph)) return false;
		Graph g = (Graph)o;
		if(this.m_id == g.m_id) return true;
		return false;
				
	}
	
	@Override
	public ArrayList update(ArrayList<Token> tokens, Place p) throws Exception
	{
		return update(tokens);
	}
}
