/**
 * 
 */
package cu.rst.util;

import java.util.Enumeration;

import org.apache.log4j.Logger;

import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import cu.rst.core.graphs.Agent;
import cu.rst.core.exceptions.GenericTestbedException;
import cu.rst.core.graphs.TrustEdgeFactory;
import cu.rst.core.graphs.TG;


/**
 * @author partheinstein
 * This class is a utility class to generate a trust graph given a arff file.
 * The syntax of the arff file shall be:
 * 
 * @relaton trust
 * @attribute srcAgent  NUMERIC
 * @attribute sinkAgent NUMERIC
 * 
 * @data
 * 0, 2
 * 1, 10
 * 
 *
 */
public class TrustGraphCreator
{
	static private Logger logger = Logger.getLogger(TrustGraphCreator.class);
	
	public static TG createGraph(String arffFileName) throws Exception
	{
		TG trustGraph = new TG(new TrustEdgeFactory());
		
		Util.assertNotNull(arffFileName);
		Util.assertFileExists(arffFileName);
		
		DataSource source;
		try
		{
			source = new DataSource(arffFileName);
			Instances instances = source.getDataSet();
			logger.debug("Number of instances in arff file is " + instances.numInstances());
			
			Enumeration enu = instances.enumerateInstances();
			//get all the feedback lines
			
			while(enu.hasMoreElements())
			{
				Instance temp = (Instance)enu.nextElement();
				logger.info("Parsing " + temp);
				String[] feedbackInstance = new String[3];
				//go through each feedback line
				
				if(temp.numValues()!=2) throw new GenericTestbedException("Trust line does not have 2 elements. This is illegal.");
				
				for(int i=0;i<temp.numValues();i++)
				{
					//number of values == 2
					feedbackInstance[i] = temp.stringValue(i);					
				}
				Agent src = new Agent(new Integer(feedbackInstance[0]));
				Agent sink = new Agent(new Integer(feedbackInstance[1]));
				
				if(!trustGraph.containsVertex(src))
				{
					trustGraph.addVertex(src);
				}
				
				if(!trustGraph.containsVertex(sink))
				{
					trustGraph.addVertex(sink);
				}
				
				trustGraph.addEdge(src, sink);
				
			}

		} catch (Exception e)
		{
			logger.info("Error parsing arff file '" + arffFileName +"'.");
			logger.info(e.getStackTrace());
			throw new GenericTestbedException("Error parsing arff file.", e);
		}
		
		return trustGraph;
		
	}
}
