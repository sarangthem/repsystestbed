/**
 * 
 */
package cu.rst.core.alg;

import java.util.ArrayList;
import java.util.Properties;

import org.apache.log4j.Logger;

import cu.rst.core.graphs.Graph;
import cu.rst.core.petrinet.PetriNet;
import cu.rst.core.petrinet.PetriNetElementIntf;
import cu.rst.core.petrinet.Place;
import cu.rst.core.petrinet.Token;
import cu.rst.core.petrinet.Transition;



/**
 * @author partheinstein
 *
 */
public abstract class Algorithm implements ConditionsCheckerIntf, PetriNetElementIntf
{
	
	static private Logger logger = Logger.getLogger(Algorithm.class);
	static private int g_id;
	private int m_id = g_id++;
	private PetriNet workflow;
	private Transition m_transition;
	
	protected Properties config;
	/**
	 * Set the algorithm configuration
	 * @param config
	 * @throws Exception
	 */
	public void setConfig(Properties config)
	{
		this.config = config;
	}
	
	public void setWorkflow(PetriNet workflow)
	{
		this.workflow = workflow;
	}
	
	public void setTransition(Transition transition)
	{
		m_transition = transition;
	}
	
	public Transition getTransition() throws Exception
	{
		if(this.m_transition==null) throw new Exception("Transition not set.");
		return m_transition;
	}
	
	public PetriNet getWorkflow() throws Exception
	{
		
		if(this.workflow==null)  throw new Exception("Workflow not set"); 
		return this.workflow;
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
	
	@Override
	public String getName()
	{
		return this.getClass().getSimpleName() + m_id;
	}
	
	public static void resetCounter()
	{
		g_id=0;
	}
	
	@Override
	public ArrayList update(ArrayList<Token> tokens, Place p) throws Exception
	{
		return update(tokens);
	}

}
