package cu.rst.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Enumeration;

import org.apache.log4j.Logger;

import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import cu.rst.core.graphs.Agent;
import cu.rst.core.exceptions.GenericTestbedException;
import cu.rst.core.graphs.ReputationEdge;
import cu.rst.core.graphs.ReputationEdgeFactory;
import cu.rst.core.graphs.RG;

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
	
	public static RG createGraph(String arffFileName) throws Exception
	{
		RG repGraph = new RG(new ReputationEdgeFactory());
		
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
				String[] repInstance = new String[3];
				//go through each feedback line
				
				if(temp.numValues()!=3) throw new GenericTestbedException("Reputation line does not have 3 elements. This is illegal.");
				
				for(int i=0;i<temp.numValues();i++)
				{
					//number of values == 3
					repInstance[i] = String.valueOf((int)temp.value(i));				
				}
				Agent src = new Agent(new Integer(repInstance[0]));
				Agent sink = new Agent(new Integer(repInstance[1]));
				Double reputation = new Double(repInstance[2]);
				
				if(!repGraph.containsVertex(src))
				{
					repGraph.addVertex(src);
				}
				
				if(!repGraph.containsVertex(sink))
				{
					repGraph.addVertex(sink);
				}
				
				repGraph.addEdge(src, sink, (double)reputation);
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
	
	/**
	 * Input file should contain data as follows:
	 *  22605 42915 1
	 *  22605 5052 1
	 *  22605 42913 1
	 * @param fileName
	 * @return
	 */
	public static RG createGraphEpinions(String fileName) throws Exception
	{
		Util.assertNotNull(fileName);
		FileInputStream fis = new FileInputStream(fileName);
		BufferedReader br = new BufferedReader(new InputStreamReader(fis));
		
		RG rg = new RG(new ReputationEdgeFactory());
		
		String line = null;
		while((line = br.readLine())!=null)
		{
			line.trim();
			String[] splits = line.split(" ");
			if(splits!=null && splits.length==4)
			{
				Agent src = new Agent(Integer.valueOf(splits[1]));
				Agent sink = new Agent(Integer.valueOf(splits[2]));
				double rep = Double.valueOf(splits[3]);
				rg.addEdge(src, sink, rep);
			}
			else
			{
				throw new Exception("Incorrect format. ");
			}
		}
		return rg;
		
	}
	
	public static void main(String[] args) throws Exception
	{
		RG rg = ReputationGraphCreator.createGraphEpinions("input\\trust_data.txt\\trust_data.txt");
		System.out.println("Number of nodes: " + rg.vertexSet().size());
		System.out.println("Number of edges: " + rg.edgeSet().size());
		
	}
}
