/**
 * 
 */
package cu.rst.gwt.server.alg;

import java.util.ArrayList;
import java.util.Properties;

import org.apache.log4j.Logger;

import cu.rst.gwt.server.graphs.Graph;
import cu.rst.gwt.server.petrinet.PetriNetElementIntf;
import cu.rst.gwt.server.petrinet.Token;
import cu.rst.gwt.server.petrinet.Transition;



/**
 * @author partheinstein
 *
 */
public abstract class Algorithm implements ConditionsCheckerIntf, PetriNetElementIntf
{
	
	static private Logger logger = Logger.getLogger(Algorithm.class);
	
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
	
	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	public Properties getConfig() throws Exception
	{
		return this.config;
	}
	

}
