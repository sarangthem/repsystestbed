/**
 * 
 */
package cu.rst.gwt.server.alg;

import java.util.ArrayList;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.jgrapht.graph.SimpleDirectedGraph;

import cu.rst.gwt.server.exceptions.GenericTestbedException;
import cu.rst.gwt.server.graphs.Graph;
import cu.rst.gwt.server.parse.WorkflowParser2;



/**
 * @author partheinstein
 *
 */
public abstract class Algorithm implements ConditionsCheckerIntf
{
	
	static private Logger logger = Logger.getLogger(Algorithm.class);
	
	protected Properties config;
	protected Graph m_graph2Listen;
	protected Graph m_graph2Output;
	/**
	 * Set the algorithm configuration
	 * @param config
	 * @throws Exception
	 */
	public void setConfig(Properties config)
	{
		this.config = config;
	}
	
	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	public Properties getConfig() throws Exception
	{
		return this.config;
	}
	
	public abstract void start() throws Exception;
	public abstract void update(ArrayList changes) throws Exception;
	public abstract void finish() throws Exception;
	
	public abstract void setGraph2Listen(Graph graph) throws Exception;
	public abstract void setGraph2Output(Graph graph) throws Exception;
	
	public Graph getGraph2Listen() throws Exception
	{
		return m_graph2Listen;
	}
	
	public Graph getGraph2Output() throws Exception
	{
		return m_graph2Output;
	}



}
