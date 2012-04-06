package cu.rst.gwt.server.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import org.apache.commons.math.random.GaussianRandomGenerator;
import org.apache.commons.math.random.JDKRandomGenerator;
import org.apache.commons.math.random.RandomData;
import org.apache.commons.math.random.RandomDataImpl;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import cu.rst.gwt.server.data.Feedback;
import cu.rst.gwt.server.data.Strategy;
import cu.rst.gwt.server.entities.Agent;
import cu.rst.gwt.server.entities.Group;
import cu.rst.gwt.server.entities.Group.TargetGpStrAssignment;

import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

/**
 * This is a utility class that reads a arff file and outputs feedbacks.
 * 
 * @author partheinstein
 *
 */
public class DefaultArffFeedbackGenerator extends FeedbackGenerator
{

	private static String strategiesArffFileName = "strategiesArffFileName";
	private static String groupArffFileName = "groupArffFileName";
	private static String groupstrategiesAssignmentFileName = "groupstrategiesAssignmentFileName";
	
	static Logger logger = Logger.getLogger(DefaultArffFeedbackGenerator.class.getName());
	
	public DefaultArffFeedbackGenerator()
	{
		
	}
	
	/*
	 * Syntax 1:
	 * ---------
	 * @relaton strategy
	 * @attribute strategyId NUMERIC
	 * @attribute numberOfFeedbacks NUMERIC
	 * @attribute feedbackMean NUMERIC
	 * @attribute feedbackStdDev NUMERIC
	 * 
	 * @data
	 * 0, 10, 0.8, 0.1
	 * 1, 10, 0.3, 0.1
	 * 
	 * @relation group
	 * @attribute groupdId NUMERIC
	 * @attribute numberAgents NUMERIC
	 * 
	 * @data
	 * 0, 5
	 * 1, 5
	 * 
	 * @relation strategyassignments
	 * @attribute groupId NUMERIC
	 * @attribute targetGroupId NUMERIC
	 * @attribute strategyId NUMERIC
	 * 
	 * @data
	 * 0, 1, 1
	 * 0, 0, 0
	 * 1, 0, 1
	 * 
	 * 
	 * We could have created something like this but it gets confusing...
	 * 
	 * Syntax 2
	 * --------
	 * @relation mydataset
	 * @attribute group relational
	 *  @attribute groupdId NUMERIC
	 *  @attribute numberAgents NUMERIC
	 * @end group
	 * @attribute strategy relational
	 *  @attribute numberOfFeedbacks NUMERIC
	 *  @attribute feedbackMean NUMERIC
	 *  @attribute feedbackStdDev NUMERIC
	 *  @attribute targetGroupId NUMERIC
	 * @end strategy
	 *  
	 * @data
	 * {0,5},{10, 0.8, 0.1, 0}
	 * {0,5},{10, 0.3, 0.1, 1} //group 1 doesnt exist still. problem
	 * {1,5},{10, 0.3, 0.1, 0} //repeated strategy
	 * 
	 * >> So decided to follow Syntax 1. 
	 * 
	 * The input file is expected to be properties file containing 3 lines:
	 * strategiesArffFileName
	 * groupArffFileName
	 * groupstrategiesAssignmentFileName
	 *
	 */
	@Override
	public List<Feedback> generate(String propertiesFileName) throws Exception
	{
		ArrayList<Strategy> strategies = new ArrayList<Strategy>();
		ArrayList<Group> groups = new ArrayList<Group>();
		ArrayList<Feedback> feedbacks = new ArrayList<Feedback>();
		
		Properties properties = new Properties();
		properties.load(new FileInputStream(propertiesFileName));
		
		String strFileName = properties.getProperty(DefaultArffFeedbackGenerator.strategiesArffFileName);
		String grpFileName = properties.getProperty(DefaultArffFeedbackGenerator.groupArffFileName);
		String strGrpFileName = properties.getProperty(DefaultArffFeedbackGenerator.groupstrategiesAssignmentFileName);
		
		if(strFileName==null || grpFileName==null || strGrpFileName==null)
		{
			throw new Exception("Incomplete properties file. Check documentation.");
		}
		
		//parse and create strategies
		DataSource source = new DataSource(strFileName);
		Instances instances = source.getDataSet();
		Enumeration enu = instances.enumerateInstances();
		while(enu.hasMoreElements())
		{
			Instance temp = (Instance)enu.nextElement();
			logger.info("Parsing " + temp);
			if(temp.numValues()!=4) throw new Exception("Strategy line does not have 3 elements. This is illegal.");
			Double[] strategyInstance = new Double[4];
			for(int i=0;i<temp.numValues();i++)
			{
				//number of values == 4
				strategyInstance[i] = temp.value(i);			
				if(strategyInstance[i]==null) throw new Exception("A parameter in strategy line is null.");
			}
			/*
			 * @relaton strategy
			 * @attribute strategyId NUMERIC
			 * @attribute numberOfFeedbacks NUMERIC
			 * @attribute feedbackMean NUMERIC
			 * @attribute feedbackStdDev NUMERIC
			 */
			
			//TODO do checks to make sure feedbackMean and feedbackStdDev are in [0,1].
			Strategy s = new Strategy(strategyInstance[0].intValue(),
					strategyInstance[1].intValue(), strategyInstance[2], strategyInstance[3]);
			
			strategies.add(s);
		}
		
		//parse and create groups
		source = new DataSource(grpFileName);
		instances = source.getDataSet();
		enu = instances.enumerateInstances();
		while(enu.hasMoreElements())
		{
			Instance temp = (Instance)enu.nextElement();
			logger.info("Parsing " + temp);
			if(temp.numValues()!=2) throw new Exception("Group line does not have 2 elements. This is illegal.");
			Double[] groupInstance = new Double[2];
			for(int i=0;i<temp.numValues();i++)
			{
				//number of values == 2
				groupInstance[i] = temp.value(i);		
				if(groupInstance[i]==null) throw new Exception("A parameter in group line is null.");
			}
			/*
			 * @relation group
			 * @attribute groupdId NUMERIC
			 * @attribute numberAgents NUMERIC
			 */
			Group g = new Group(groupInstance[0].intValue(), groupInstance[1].intValue());
			groups.add(g);
		}
		
		//parse and create group-strategy assignments
		source = new DataSource(strGrpFileName);
		instances = source.getDataSet();
		enu = instances.enumerateInstances();
		while(enu.hasMoreElements())
		{
			Instance temp = (Instance)enu.nextElement();
			logger.info("Parsing " + temp);
			if(temp.numValues()!=3) throw new Exception("Group-Strategy assignment line does not have 3 elements. This is illegal.");
			Double[] strGrpInstance = new Double[3];
			for(int i=0;i<temp.numValues();i++)
			{
				//number of values == 3
				strGrpInstance[i] = temp.value(i);
				if(strGrpInstance[i]==null) throw new Exception("A parameter in group-strategy line is null.");
			}
			/*
			 * @attribute groupId NUMERIC
			 * @attribute targetGroupId NUMERIC
			 * @attribute strategyId NUMERIC
			 */
			System.out.println("g1: " + strGrpInstance[0].intValue());
			System.out.println("g2: " + strGrpInstance[1].intValue());
			Group g1 = getGroup(strGrpInstance[0].intValue(), groups);
			if(g1==null) throw new Exception("Group not found in the parsed groups.");
			Group g2 = getGroup(strGrpInstance[1].intValue(), groups);
			if(g2==null) throw new Exception("Group not found in the parsed groups.");
			Strategy s = getStrategy(strGrpInstance[2].intValue(), strategies);
			if(s==null) throw new Exception("Strategy not found in the parsed strategies.");
			g1.assignTargetGpStrategy(g2, s);
		}
		
		//we have all the groups and the strategies associated with them. Now generate the feedbacks
		//create the agents first
		for(Group g : groups)
		{
			for(int i=0;i<g.getMaxNumOfAgents();i++) g.joinGroup(new Agent());
		}
		//create the feedbacks based on the strategies
		for(Group sourceGroup : groups)
		{
			for(TargetGpStrAssignment assignment : sourceGroup.getTargetGpStrategyAssignments())
			{
				Group targetGroup = assignment.m_targetGroup;
				ArrayList<Agent> sourceGroupMembers = sourceGroup.getMembers();
				ArrayList<Agent> targetGroupMembers = targetGroup.getMembers();
				for (Agent a : sourceGroupMembers)
				{
					for(Agent b : targetGroupMembers)
					{
						for(int i=0;i<assignment.m_strategy.getNumberOfFeedbacks();i++)
						{
							if(!a.equals(b))
							{
								Feedback f = new Feedback(a, b, getRandom(assignment.m_strategy.getFeedbackMean(),
										assignment.m_strategy.getStdDev()));
								feedbacks.add(f);
							}
						}
					}
				}
			}
		}
		
		return feedbacks;
	}
	
	public static Group getGroup(int groupID, ArrayList<Group> groups)
	{
		for(Group g : groups)
		{
			if(g.getGroupID() == groupID) return g;
		}
		return null;
	}
	
	public static double getRandom(double mean, double stdDev)
	{
		RandomDataImpl randomDataImpl = new RandomDataImpl();
		randomDataImpl.reSeedSecure(1000);
		return randomDataImpl.nextGaussian(mean, stdDev);
	}
	
	public static Strategy getStrategy(int strategyID, ArrayList<Strategy> strategies)
	{
		for(Strategy s : strategies)
		{
			if(s.getStrategyID() == strategyID) return s;
		}
		return null;
		
	}
	
	public static void writeToArff(ArrayList<Feedback> feedbacks, String fileName) throws Exception
	{
		//header
		FastVector attributes = new FastVector();
		Attribute assessorID = new Attribute("assessorID");
		Attribute assesseeID = new Attribute("assesseeID");
		Attribute feedbackValue = new Attribute("feedbackValue");
		attributes.addElement(assessorID);
		attributes.addElement(assesseeID);
		attributes.addElement(feedbackValue);
		
		Instances data = new Instances("mydataset", attributes, 0);
		for(Feedback f : feedbacks)
		{
			//instances
			double[] values = new double[data.numAttributes()];
			values[0] = f.getAssesor().id;
			values[1] = f.getAssesee().id;
			values[2] = f.value;
			data.add(new Instance(1.0, values));
		}
		System.out.println(data);
//		FileWriter writer = new FileWriter(fileName);
//		writer.write(data.toString());
//		writer.close();
	}
	
	/*
	 * 
	 * Expected ARFF file syntax as follows:
	 * @relation mydataset
	 * 
	 * @attribute assessorID string
	 * @attribute assesseeID string
	 * @attribute feedbackValue string
	 * 
	 * @data
	 * 0,1,0.8
	 * 0,1,0.9
	 * 1,0,0.9
	 * 1,0,0.8
	 * 1,2,0.5
	 * 1,2,0.4
	 * 1,2,0.2
	 * 3,2,0.7
	 * 3,2,0.8
	 * 3,2,0.8
	 */
	@Override
	public List<Feedback> generateHardcoded(String arffFileName) throws Exception
	{
		ArrayList<Feedback> feedbacks = null;
		
		/**
		 * 
		 * @attribute assessorID string
		 * @attribute assesseeID string
		 * @attribute feedbackValue string
		 */
		
		DataSource source;
		try
		{
			source = new DataSource(arffFileName);
			Instances instances = source.getDataSet();
			feedbacks = new ArrayList<Feedback>();
			logger.debug("Number of instances in arff file is " + instances.numInstances());
			
			Enumeration enu = instances.enumerateInstances();
			//get all the feedback lines
			
			while(enu.hasMoreElements())
			{
				Instance temp = (Instance)enu.nextElement();
				logger.info("Parsing " + temp);
				String[] feedbackInstance = new String[3];
				//go through each feedback line
				
				if(temp.numValues()!=3) throw new Exception("Feedback line does not have 3 elements. This is illegal.");
				
				for(int i=0;i<temp.numValues();i++)
				{
					//number of values == 3
					feedbackInstance[i] = temp.stringValue(i);					
				}
				Agent assessor = new Agent(new Integer(feedbackInstance[0]));
				Agent assessee = new Agent(new Integer(feedbackInstance[1]));
				Double value = new Double(feedbackInstance[2]);
				
				Feedback f = new Feedback(assessor, assessee, value);
				feedbacks.add(f);
				logger.info("Added " + f );
				
			}

		} catch (Exception e)
		{
			logger.info("Error parsing arff file '" + arffFileName +"'.");
			logger.info(e.getStackTrace());
			throw e;
		}
		
		return feedbacks;
				
	}
	
	
	public List<Feedback> generateHardcoded(byte[] arff) throws Exception
	{
		ArrayList<Feedback> feedbacks = null;
		
		/**
		 * 
		 * @attribute assessorID string
		 * @attribute assesseeID string
		 * @attribute feedbackValue string
		 */
		
		try
		{
			Instances instances = new Instances(new BufferedReader(new InputStreamReader(new ByteArrayInputStream(arff))));
			feedbacks = new ArrayList<Feedback>();
			logger.debug("Number of instances in arff file is " + instances.numInstances());
			
			Enumeration enu = instances.enumerateInstances();
			//get all the feedback lines
			
			while(enu.hasMoreElements())
			{
				Instance temp = (Instance)enu.nextElement();
				logger.info("Parsing " + temp);
				String[] feedbackInstance = new String[3];
				//go through each feedback line
				
				if(temp.numValues()!=3) throw new Exception("Feedback line does not have 3 elements. This is illegal.");
				
				for(int i=0;i<temp.numValues();i++)
				{
					//number of values == 3
					feedbackInstance[i] = temp.stringValue(i);					
				}
				Agent assessor = new Agent(new Integer(feedbackInstance[0]));
				Agent assessee = new Agent(new Integer(feedbackInstance[1]));
				Double value = new Double(feedbackInstance[2]);
				
				Feedback f = new Feedback(assessor, assessee, value);
				feedbacks.add(f);
				logger.info("Added " + f );
				
			}

		} catch (Exception e)
		{
			logger.info("Error parsing arff bytes '");
			logger.info(e.getStackTrace());
			throw e;
		}
		
		return feedbacks;
				
	}

}
