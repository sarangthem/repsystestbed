package cu.rst.gwt.server.graphs;

import java.util.ArrayList;
import java.util.Set;

import org.apache.log4j.Logger;
import org.jgrapht.graph.SimpleDirectedGraph;

import cu.rst.gwt.server.alg.Algorithm;
import cu.rst.gwt.server.alg.EvaluationAlgorithm;
import cu.rst.gwt.server.alg.TrustAlgorithm;
import cu.rst.gwt.server.entities.Agent;
import cu.rst.gwt.server.util.Util;
//import cu.rst.gwt.server.view.JGraphXView;

public class TrustGraph extends Graph<Agent, TrustEdge>
{

	private static final long serialVersionUID = -327490271972222723L;
	static Logger logger = Logger.getLogger(TrustGraph.class.getName());
//	public JGraphXView view;
	private ArrayList<Algorithm> observers;

	public TrustGraph(TrustEdgeFactory trustEdgeFactory)
	{
		super(trustEdgeFactory);
//		view = new JGraphXView();
//		view.m_graphModel = this;
		observers = new ArrayList<Algorithm>();
	}
	
	public void addObserver(Algorithm alg) throws Exception
	{
		Util.assertNotNull(alg);
		//TODO - we don't know what kind of algorithms will listen to a trust graph.
		observers.add(alg);
	}
	
	public void notifyObservers(ArrayList changes) throws Exception
	{
		for(Algorithm alg : observers)
		{
			if(alg instanceof TrustAlgorithm || alg instanceof EvaluationAlgorithm)
			{
				alg.start();
				alg.update(changes);
				alg.finish();
			}
			else
			{
				throw new ClassCastException("Cannot notify a non-trust algorithm in a Trust graph.");
			}
		}
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
	public TrustGraph clone(boolean addObservers)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TrustGraph getTransitiveClosureGraph()
	{
		// TODO Auto-generated method stub
		return null;
	}
	

}
