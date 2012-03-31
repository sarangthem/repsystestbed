package cu.rst.gwt.server.util;

import java.util.ArrayList;
import java.util.Enumeration;

import org.apache.log4j.Logger;

import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import cu.rst.gwt.server.entities.Agent;
import cu.rst.gwt.server.exceptions.GenericTestbedException;
import cu.rst.gwt.server.graphs.ReputationEdge;
import cu.rst.gwt.server.graphs.ReputationEdgeFactory;
import cu.rst.gwt.server.graphs.ReputationGraph;

/**
 * @author partheinstein
 * 
 * This class is a utility class to generate a reputation graph given a arff file.
 * The syntax of the arff file shall be:
 * 
 * @relaton reputation
 * @attribute srcAgent  NUMERIC
 * @attribute sinkAgent NUMERIC
 * @attribute reputation NUMERIC
 * 
 * @data
 * 0, 2, 0.8
 * 1, 10, 0.3, 0.1
 * 
 */
public class ReputationGraphCreator
{
	static private Logger logger = Logger.getLogger(ReputationGraphCreator.class);
	
	public static ReputationGraph createGraph(String arffFileName) throws Exception
	{
		ReputationGraph repGraph = new ReputationGraph(new ReputationEdgeFactory());
		
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
				
				if(temp.numValues()!=3) throw new GenericTestbedException("Reputation line does not have 3 elements. This is illegal.");
				
				for(int i=0;i<temp.numValues();i++)
				{
					//number of values == 3
					feedbackInstance[i] = temp.stringValue(i);					
				}
				Agent src = new Agent(new Integer(feedbackInstance[0]));
				Agent sink = new Agent(new Integer(feedbackInstance[1]));
				Double reputation = new Double(feedbackInstance[2]);
				
				if(!repGraph.containsVertex(src))
				{
					repGraph.addVertex(src);
				}
				
				if(!repGraph.containsVertex(sink))
				{
					repGraph.addVertex(sink);
				}
				
				repGraph.addEdge(src, sink);
				ReputationEdge repEdge = (ReputationEdge) repGraph.getEdge(src, sink);
				repEdge.setReputation(reputation);
				System.out.println(repGraph);
			}
			
			return repGraph;

		} catch (Exception e)
		{
			logger.error("Error parsing arff file '" + arffFileName +"'.");
			logger.error(e);
			throw new GenericTestbedException("Error parsing arff file.", e);
		}
		
		
		
	}
}
