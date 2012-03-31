/**
 * 
 */
package cu.rst.gwt.server.alg;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.jgrapht.graph.SimpleDirectedGraph;

import cu.rst.gwt.server.exceptions.EvaluationException;
import cu.rst.gwt.server.graphs.Graph;
import cu.rst.gwt.server.util.Util;

/**
 * @author partheinstein
 * 
 * An evaluation algorithm is one that takes a 3-tuple consisting of an input graph, an algorithm and an output graph in a trust workflow 
 * as input and outputs results of the evaluation. Examples:
 * - evalAlg1{Feedback History Graph,  reputation alg, reputation graph}
 * - evalAlg2{reputationgraph, trust alg, trust graph}
 * - evalAlg3{Feedback History Graph, reputation alg, reputation graph}
 * 
 * Implementation:
 * An evaluation algorithm wraps around a RepSysTestbedAlgorithm, which can be a reputation or trust algorithm. Since these algorithms have 
 * access to the graphs, an evaluation algorithm also has access to them.  
 *
 */
public abstract class EvaluationAlgorithm extends Algorithm
{	
	protected static enum InvokeOrder{BEFORE_INNER_ALG, AFTER_INNER_ALG, BEFORE_AFTER_INNER_ALG};
	static Logger logger = Logger.getLogger(EvaluationAlgorithm.class);
	
	protected Algorithm m_alg;
	protected boolean m_stopAtFirstFailure;
	protected InvokeOrder m_invokeOrder;
	
	public void setStopAtFirstFailure(boolean value)
	{
		m_stopAtFirstFailure = value;
	}
	
	public boolean isSetToStopAtFirstFailure()
	{
		return m_stopAtFirstFailure;
	}
	
	
	public void wrap(Algorithm alg) throws Exception
	{
		Util.assertNotNull(alg);
		m_alg = alg;
	}
	
	public Algorithm getWrappedAroundAlg()
	{
		return m_alg;
	}

	@Override
	public boolean assertGraph2ListenType(Graph g) throws Exception
	{
		Util.assertNotNull(m_alg);
		return m_alg.assertGraph2ListenType(g);
	}

	@Override
	public boolean assertGraph2OutputType(Graph g) throws Exception
	{
		Util.assertNotNull(m_alg);
		return m_alg.assertGraph2OutputType(g);
	}
	

	@Override
	public boolean assertVariablePrecondition(double variable) throws Exception
	{
		Util.assertNotNull(m_alg);
		return m_alg.assertVariablePrecondition(variable);
	}
	
	@Override
	public void setGraph2Listen(Graph graph) throws Exception
	{
		Util.assertNotNull(m_alg);
		m_alg.setGraph2Listen(graph);
	}
	
	@Override
	public void setGraph2Output(Graph graph) throws Exception
	{
		Util.assertNotNull(graph);
		Util.assertNotNull(m_alg);
		m_alg.setGraph2Output(graph);
		
	}
	
	@Override
	public Graph getGraph2Listen() throws Exception
	{
		
		Util.assertNotNull(m_alg);
		Util.assertNotNull(m_alg.m_graph2Listen);
		return m_alg.m_graph2Listen;
		
	}
	
	@Override
	public Graph getGraph2Output() throws Exception
	{
		
		Util.assertNotNull(m_alg);
		Util.assertNotNull(m_alg.m_graph2Output);
		return m_alg.m_graph2Output;
		
	}
	
	

	
	/**
	 * Invokes the wrapped around RepSysTestbedAlgorithm to update and if the evaluation alg is set to do run time evaluation,
	 * this method also invokes evaluate().
	 */
	@Override
	public void update(ArrayList changes) throws Exception
	{
		Util.assertNotNull(m_alg);
		m_alg.update(changes);
		try
		{
			evaluate(changes);
		}
		catch(EvaluationException e)
		{
			logger.error(e);
			if(m_stopAtFirstFailure) throw e;
		}
	}
	
	/**
	 * Override this method to do the actual evaluation. This method is automatically invoked in update() if this algorithm is set
	 * to do runtime evaluation. 
	 * @throws Exception
	 */
	public abstract void evaluate(ArrayList changes) throws Exception;

}
