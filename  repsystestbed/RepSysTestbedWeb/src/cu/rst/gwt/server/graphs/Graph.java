/**
 * 
 */
package cu.rst.gwt.server.graphs;

import java.util.ArrayList;
import java.util.Iterator;

import org.jgrapht.EdgeFactory;
import org.jgrapht.graph.SimpleDirectedGraph;

import cu.rst.gwt.server.alg.Algorithm;
import cu.rst.gwt.server.entities.Agent;
import cu.rst.gwt.server.petrinet.PetriNetElementIntf;

/**
 * @author partheinstein
 *
 */
public abstract class Graph<V, E> extends SimpleDirectedGraph implements PetriNetElementIntf
{
	/*
	 *  FHG = Feedback History Graph
	 *  RG = Reputation Graph
	 *  TG = Trust Graph
	 *  PN = Petri Net
	 */
	public static enum Type{FHG, RG, TG, PN};
	
	ArrayList observers; 
	public Graph(EdgeFactory ef)
	{
		super(ef);
		observers = new ArrayList();
	}

	public abstract Graph<V, E> clone(boolean addObservers);
	public abstract Graph<V, E> getTransitiveClosureGraph();
	public void removeAllObservers()
	{
		observers.clear();
	}
}
