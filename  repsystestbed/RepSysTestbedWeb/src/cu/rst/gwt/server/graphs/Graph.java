/**
 * 
 */
package cu.rst.gwt.server.graphs;

import java.util.ArrayList;

import org.jgrapht.EdgeFactory;
import org.jgrapht.graph.SimpleDirectedGraph;

import cu.rst.gwt.server.alg.Algorithm;
import cu.rst.gwt.server.entities.Agent;

/**
 * @author partheinstein
 *
 */
public abstract class Graph<V, E> extends SimpleDirectedGraph
{
	public static enum Type{FHG, RG, TG};
	
	ArrayList<Algorithm> observers; 
	public Graph(EdgeFactory ef)
	{
		super(ef);
		observers = new ArrayList<Algorithm>();
	}

	public abstract Graph<V, E> clone(boolean addObservers);
	public abstract Graph<V, E> getTransitiveClosureGraph();
	public abstract void addObserver(Algorithm alg)  throws Exception;
}
