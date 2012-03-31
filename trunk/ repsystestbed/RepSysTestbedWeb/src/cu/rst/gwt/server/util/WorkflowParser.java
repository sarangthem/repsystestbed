/**
 * 
 */
package cu.rst.gwt.server.util;

import java.io.FileInputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Properties;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import cu.rst.gwt.server.alg.Algorithm;
import cu.rst.gwt.server.alg.EvaluationAlgorithm;
import cu.rst.gwt.server.data.Feedback;
import cu.rst.gwt.server.data.Workflow;
import cu.rst.gwt.server.exceptions.WorkflowParserException;
import cu.rst.gwt.server.graphs.FeedbackHistoryEdgeFactory;
import cu.rst.gwt.server.graphs.FeedbackHistoryGraph;
import cu.rst.gwt.server.graphs.Graph;
import cu.rst.gwt.server.graphs.ReputationEdgeFactory;
import cu.rst.gwt.server.graphs.ReputationGraph;
import cu.rst.gwt.server.graphs.TrustEdgeFactory;
import cu.rst.gwt.server.graphs.TrustGraph;

/**
 * @author partheinstein
 * 
 * This class parses a workflow as a ini file. See Workflow.java for syntax.
 * 
 * Sample syntax:
 * #define workflow
 * workflow = {feedbackhistorygraph, EigenTrust, reputationgraph, RankbasedTrustAlg, trustgraph}
 * 
 * #specify the input file
 * feedbackhistorygraph.inputFile = path to arff
 * 
 * #algorithm parameters
 * EigenTrust.className = cu.repsystestbed.gwt.server.algorithms.examples.EigenTrust
 * EigenTrust.classPath = path to the class
 * EigenTrust.properties = path to the properties file
 * 
 * RankbasedTrustAlg.className = cu.repsystestbed.gwt.server.algorithms.examples.RankbasedTrustAlg
 * RankbasedTrustAlg.classPath = path to the class
 * RankbasedTrustAlg.properties = path to properties file
 * 
 * Evaluation example:
 * workflow = {feedbackhistorygraph, evalAlg1, reputationgraph, RankbasedTrustAlg, trustgraph}
 * evalAlg1.classpath = C:\\Users\\partheinstein\\RepSysTestbed\\bin\\cu\\repsystestbed\\algorithms\\examples\\TrustEvolveTest.class
 * evalAlg1.evaluate = EigenTrust
 * EigenTrust.classpath = C:\\Users\\partheinstein\\RepSysTestbed\\bin\\cu\\repsystestbed\\algorithms\\examples\\EigenTrust.class
 *
 */
public class WorkflowParser
{
	
	public static String WORKFLOW = "workflow";
	public static String FEEDBACKHISTORYGRAPH = "feedbackhistorygraph";
	public static String REPUTATIONGRAPH = "reputationgraph";
	public static String TRUSTGRAPH = "trustgraph";
	
	private String m_iniFile;
	private Properties m_properties;
	private Workflow m_workflow;
	
	static private Logger logger = Logger.getLogger(WorkflowParser.class);
	
	public WorkflowParser(String workflowConfig, boolean isIni) throws WorkflowParserException
	{
		try
		{
			Util.assertNotNull(workflowConfig);
			m_properties = new Properties();
			logger.debug("Loading " + workflowConfig + "...");
			
			if(isIni)
			{
				Util.assertFileExists(workflowConfig);
				m_iniFile = workflowConfig;
				m_properties.load(new FileInputStream(workflowConfig));
			}
			else
			{
				m_properties.load(new StringReader(workflowConfig));
			}
			
			logger.info(m_properties);
			logger.debug("Loaded " + workflowConfig + ".");
			m_workflow = new Workflow();
			
			Util.assertNotNull(m_properties.get(WORKFLOW));
			
			logger.debug("Parsing workflow...");
			parse();
			
		}
		catch(Exception e)
		{
			throw new WorkflowParserException("Error reading or parsing the ini file. Cause: ", e);
		}
		
	}
	
	
	
	private void parse() throws WorkflowParserException
	{
		
		String workflowDefn = (String) m_properties.get(WORKFLOW);
		String[] workflowElements = workflowDefn.split(",");
		
		if(workflowElements.length < 1) throw new WorkflowParserException("Workflow must have at least 1 element");
		
		for(int i=0; i< workflowElements.length; i++)
		{
			//remove the squigly brackets { and }
			if(workflowElements[i].contains("{"))
			{
				String[] temp = workflowElements[i].split("\\{");
				if(temp.length == 2)
				{
					workflowElements[i] = temp[1];
				}
				else
				{
					throw new WorkflowParserException("Could not parse workflow " + workflowDefn 
							+ ". Error in " + workflowElements[i]);
				}
			}
			else if(workflowElements[i].contains("}"))
			{
				String[] temp = workflowElements[i].split("\\}");
				if(temp.length == 1)
				{
					workflowElements[i] = temp[0].trim();
				}
				else
				{
					throw new WorkflowParserException("Could not parse workflow " + workflowDefn 
							+ ". Error in " + workflowElements[i]);
				}
			}
			
		}
		
		for(int i=0; i< workflowElements.length; i++)
		{
			if(i==0)
			{
				//first element must be graph
				if(!isGraph(workflowElements[i].trim()))
				{
					throw new WorkflowParserException("The first element in the workflow should be a graph.");
				}
					
				//inputFile is expected for the first element. This is the input to create a graph
				String graphInputFile = m_properties.getProperty(workflowElements[i].trim() + ".inputFile");
				if(graphInputFile == null)
				{
					throw new WorkflowParserException("Expected property in the ini file not found. " 
							+ m_properties.getProperty(workflowElements[i] + ".inputFile"));
				}
				
				//create and add the input graph to workflow
				if(isGraph(workflowElements[i].trim(), FEEDBACKHISTORYGRAPH))
				{
					DefaultArffFeedbackGenerator feedbackGen = new DefaultArffFeedbackGenerator();
					ArrayList<Feedback> feedbacks = new ArrayList<Feedback>();
					
					try
					{
						
						feedbacks = (ArrayList<Feedback>) feedbackGen.generateHardcoded(graphInputFile);
						FeedbackHistoryGraph feedbackHistoryGraph = (FeedbackHistoryGraph) createGraphInstance(FEEDBACKHISTORYGRAPH);
						Util.assertNotNull(feedbackHistoryGraph);
						feedbackHistoryGraph.addFeedbacks(feedbacks, false);
						m_workflow.addItem(feedbackHistoryGraph);
						
					}
					catch(Exception e)
					{
						throw new WorkflowParserException("Error creating a feedback history graph from "
									+ graphInputFile + " or error adding to workflow. Cause: ", e);
					}
		
				}
				else if(isGraph(workflowElements[i].trim(), REPUTATIONGRAPH))
				{
					try
					{
						ReputationGraph repGraph = ReputationGraphCreator.createGraph(graphInputFile);
						Util.assertNotNull(repGraph);
						m_workflow.addItem(repGraph);
						
						
					}catch(Exception e)
					{
						throw new WorkflowParserException("Error creating a reputation graph from "
								+ graphInputFile + " or error adding to workflow. Cause: ", e);				
					}
				}else if(isGraph(workflowElements[i].trim(), TRUSTGRAPH))
				{
					try
					{
						TrustGraph trustGraph = TrustGraphCreator.createGraph(graphInputFile);
						Util.assertNotNull(trustGraph);
						m_workflow.addItem(trustGraph);
						
						
					}
					catch(Exception e)
					{
						throw new WorkflowParserException("Error creating a trust graph from "
								+ graphInputFile + " or error adding to workflow. Cause: ", e);		
					}
				}
			}else
			{
			
				//add the other elements to the workflow. They can be graphs or algorithms.
				
				if(isGraph(workflowElements[i].trim()))
				{
					try
					{
						Graph graph = createGraphInstance(workflowElements[i].trim());
						Util.assertNotNull(graph);
						m_workflow.addItem(graph);
						
					}
					catch(Exception e)
					{
						throw new WorkflowParserException("Error creating a graph "
								+ workflowElements[i].trim() + " or error adding to workflow. Cause: ", e);	
					}
				}
				else
				{
					//it is an algorithm
					try
					{
						String algName = workflowElements[i];
						
						logger.debug("Loading algorithm " + algName.trim());
						String classPath = m_properties.getProperty(algName.trim() + ".classpath");
						
						try
						{
							Util.assertNotNull(classPath);
						}
						catch(Exception e)
						{
							throw new WorkflowParserException(algName.trim() + ".classpath setting not found.", e);
						}
						
						logger.debug("Algorithm class: " + classPath);
						Algorithm alg = createAlgorithmInstance(classPath);
						Util.assertNotNull(alg);
						
						String propLocation = m_properties.getProperty(algName.trim() + ".properties");
						if(propLocation != null)
						{
							
							Properties algProperties = new Properties();
							try
							{
								algProperties.load(new FileInputStream(propLocation));
								alg.setConfig(algProperties);
							}
							catch(Exception e)
							{
								logger.error("Cannot set algorithm properties.", e);
							}
							
						}
						
						
						/*
						 * If it is a evaluation algorithm, then get .classpath and .evaluate settings.
						 * Instantiate the inner algorithm and wrap it with the eval alg.
						 */
						if(alg instanceof EvaluationAlgorithm)
						{
							String innerAlgName = m_properties.getProperty(algName.trim() + ".evaluate");
							
							try
							{
								Util.assertNotNull(innerAlgName);
							}
							catch(Exception e)
							{
								throw new WorkflowParserException(algName.trim() + ".evalute setting not found. An evaluation " +
										"algorithm must wrap a reputation system testbed algorithm.", e);
							}
							
							logger.debug("Inner algorithm name: " + innerAlgName);
							
							String innerAlgClasspath = m_properties.getProperty(innerAlgName.trim() + ".classpath");
							
							try
							{
								Util.assertNotNull(innerAlgClasspath);
							}
							catch(Exception e)
							{
								throw new WorkflowParserException(".classpath setting not found for an algorithm. ", e);
							}
							
							logger.debug("Inner algorithm classpath: " + innerAlgClasspath);
							Algorithm innerAlg = createAlgorithmInstance(innerAlgClasspath);
							Util.assertNotNull(innerAlg);
							
							((EvaluationAlgorithm) alg).wrap(innerAlg);
							
						}
						
						m_workflow.addItem(alg);
						
					}
					catch(Exception e)
					{
						throw new WorkflowParserException("Error creating a algorithm "
								+ workflowElements[i].trim() + " or error adding to workflow. Cause: ", e);						
					}
				}
			}
			
		}
			
		
	}
	
	private boolean isGraph(String value)
	{
		if(!isGraph(FEEDBACKHISTORYGRAPH, value) 
				&& !isGraph(REPUTATIONGRAPH, value)
				&& !isGraph(TRUSTGRAPH, value))
		{
			return false;
		}
		return true;
	}
	
	private boolean isGraph(String graph, String value)
	{
		if(value.equalsIgnoreCase(graph)) return true;
		return false;
	}
	
	private boolean isAlgorithm(String algorithm, String value)
	{
		if(value.equalsIgnoreCase(algorithm)) return true;
		return false;
	}
	
	
	private Graph createGraphInstance(String graphType) throws WorkflowParserException
	{
		
		if(!isGraph(graphType))
		{
			throw new WorkflowParserException("The first element in the workflow must be either " +
					"a feedbackhistorygraph, reputationgraph or a trustgraph");
		}
			
		Graph graph;
		if(graphType.equalsIgnoreCase(FEEDBACKHISTORYGRAPH))
		{
			return new FeedbackHistoryGraph(new FeedbackHistoryEdgeFactory());
			
		}
		else if(graphType.equalsIgnoreCase(REPUTATIONGRAPH))
		{
			return new ReputationGraph(new ReputationEdgeFactory());
			
		}
		else if(graphType.equalsIgnoreCase(TRUSTGRAPH))
		{
			return new TrustGraph(new TrustEdgeFactory());
		}
		else
		{
			throw new WorkflowParserException("Unknown graph type.");
		}

	}
	
	public Algorithm createAlgorithmInstance(String classPath) throws WorkflowParserException
	{
		try
		{
			Algorithm alg = (Algorithm) Util.newClass(classPath);
			return alg;
			
		}
		catch(Exception e)
		{
			throw new WorkflowParserException("Could not load algorithm " + classPath, e);
		}
	}
	
	
	public String getWorkflowAsString()
	{
		return m_workflow.toString();
	}
	
	public Workflow getWorkflow()
	{
		return m_workflow;
	}
	
	public static void main(String[] args) throws Exception
	{
		BasicConfigurator.configure();
		WorkflowParser parser = new WorkflowParser("workflow.ini", true);
		System.out.println(parser.getWorkflowAsString());
		//parser.getWorkflow().getFeedbackHistoryGraph().notifyObservers(true);
		parser.getWorkflow().start(true);
		

	}

}
