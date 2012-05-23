/**
 * 
 */
package cu.rst.gwt.server.alg.eg;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import cu.rst.gwt.server.petrinet.PetriNetElementIntf;
import cu.rst.gwt.server.petrinet.Token;

/**
 * @author partheinstein
 *
 */
public class StringSink implements PetriNetElementIntf
{
	static Logger logger = Logger.getLogger(StringSink.class.getName());
	private String str;
	
	public StringSink() throws Exception
	{
	}

	@Override
	public ArrayList update(ArrayList<Token> tokens) throws Exception 
	{
		logger.debug("StringSink update() invoked.");
		if(tokens!=null && tokens.size()>0)
		{
			Token t1 = tokens.get(0);
			for(String s : ((ArrayList<String>)t1.m_changes))
			{
				logger.info(s);
			}
		}
		return null;
	}
}
