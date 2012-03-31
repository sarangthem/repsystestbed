/**
 * 
 */
package cu.rst.gwt.server.alg;

import org.jgrapht.graph.SimpleDirectedGraph;

import cu.rst.gwt.server.graphs.Graph;

/**
 * @author partheinstein
 *
 */
public interface ConditionsCheckerIntf
{
	public boolean assertGraph2ListenType(Graph g) throws Exception;
	public boolean assertGraph2OutputType(Graph g) throws Exception;
	public boolean assertVariablePrecondition(double variable) throws Exception;
	
	public Graph.Type getInputGraphType() throws Exception;
	public Graph.Type getOutputGraphType() throws Exception;
}
