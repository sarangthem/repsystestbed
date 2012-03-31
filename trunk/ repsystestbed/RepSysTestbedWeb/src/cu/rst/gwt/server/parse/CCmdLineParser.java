package cu.rst.gwt.server.parse;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import cu.rst.gwt.server.alg.Algorithm;
import cu.rst.gwt.server.data.Feedback;
import cu.rst.gwt.server.exceptions.WorkflowParserException;
import cu.rst.gwt.server.graphs.FeedbackHistoryEdgeFactory;
import cu.rst.gwt.server.graphs.FeedbackHistoryGraph;
import cu.rst.gwt.server.graphs.ReputationEdgeFactory;
import cu.rst.gwt.server.graphs.ReputationGraph;
import cu.rst.gwt.server.graphs.TrustEdgeFactory;
import cu.rst.gwt.server.graphs.TrustGraph;
import cu.rst.gwt.server.jcommando.Command;
import cu.rst.gwt.server.jcommando.Grouping;
import cu.rst.gwt.server.util.DefaultArffFeedbackGenerator;
import cu.rst.gwt.server.util.ReputationGraphCreator;
import cu.rst.gwt.server.util.TrustGraphCreator;
import cu.rst.gwt.server.util.Util;

public class CCmdLineParser extends CommandLineParser
{
	
	static private Logger logger = Logger.getLogger(CCmdLineParser.class);
	
	//temporary variables
	String t_algName = null;
	String t_cp = null;
	
	String t_graphName = null;
	String t_graphType = null;
	String t_graphInputFile = null;
	
	String t_workflowName = null;
	String t_workflowDefn = null;
	
	//private variables
	private Hashtable<String, Object> m_storage;
	
	public CCmdLineParser(Hashtable<String, Object> storage)
	{
		super();
		Util.assertNotNull(storage);
		m_storage = storage;
	}
	
	public Hashtable<String, Object> getStorage()
	{
		return m_storage;
	}


	@Override
	public void doCreate()
	{
		try
		{
			System.out.println("create called");
			if(t_algName !=null)
			{
				Util.assertNotNull(t_cp);
				logger.debug("Algorithm name is: " + t_algName);
				logger.debug("Algorithm cp is: " + t_cp);
							
				Algorithm alg = createAlgorithmInstance(t_cp);
				m_storage.put(new String(t_algName), alg);
			}
			else if(t_graphName != null)
			{
				Util.assertNotNull(t_graphType);
				
				logger.debug("Graph name is: " + t_graphName);
				logger.debug("Graph type is: " + t_graphType);
				logger.debug("Input file is: " + t_graphInputFile);
				
				if(t_graphType.toLowerCase().equals("fhg"))
				{
					DefaultArffFeedbackGenerator gen = new DefaultArffFeedbackGenerator();
					ArrayList<Feedback> feedbacks = (ArrayList<Feedback>) gen.generateHardcoded(t_graphInputFile);
					FeedbackHistoryGraph fhg = new FeedbackHistoryGraph(new FeedbackHistoryEdgeFactory());
					fhg.addFeedbacks(feedbacks, false);
					m_storage.put(t_graphName, fhg);
				}
				else if(t_graphType.toLowerCase().equals("rg"))
				{
					ReputationGraph rg = null;
					if(t_graphInputFile != null)
					{
						ReputationGraphCreator gen = new ReputationGraphCreator();
						rg = gen.createGraph(t_graphInputFile);
					}
					else
					{
						rg = new ReputationGraph(new ReputationEdgeFactory());
					}
					m_storage.put(t_graphName, rg);
				}
				else if(t_graphType.toLowerCase().equals("tg"))
				{
					TrustGraph tg = null;
					if(t_graphInputFile != null)
					{
						TrustGraphCreator gen = new TrustGraphCreator();
						tg = gen.createGraph(t_graphInputFile);
					}
					else
					{
						tg = new TrustGraph(new TrustEdgeFactory());
					}
					
					m_storage.put(t_graphName, tg);
				}
				else
				{
					throw new Exception("Unknown graphType.");
				}
			}
			else if(t_workflowName != null)
			{
				Util.assertNotNull(t_workflowDefn);
				
				logger.debug("Workflow definition is: " + t_workflowDefn);
				m_storage.put(t_workflowName, t_workflowDefn);
				
			}
				
		}				
		catch(Exception e)
		{
			logger.error(e.getMessage());

		}

		//reset the variables
		reset();
	}

	@Override
	public void doList()
	{
		if(m_storage!=null)
		{
			for(Object o : m_storage.keySet())
			{
				logger.info(o);
			}
		}
		reset();
	}
	
	private void reset()
	{
		//reset the variables
		//super.clearParsed(); //TODO revist this - you changed JCommandParser (see email to Jcommando folks)
		t_algName = null;
		t_cp = null;
		t_graphName = null;
		t_graphType = null;
		t_graphInputFile = null;
		t_workflowName = null;
		t_workflowDefn = null;
	}

	@Override
	public void setAlg(String algname)
	{
		logger.debug("alg called");
		t_algName = algname;
	}


	@Override
	public void setClasspath(String classpath)
	{
		logger.debug("classpath called");
		t_cp = classpath;
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
	
	public static void main(String[] args) throws Exception
	{
		BasicConfigurator.configure();

		Hashtable<String, Object> storage = new Hashtable<String, Object>();
		CCmdLineParser parser = new CCmdLineParser(storage);
		parser.printUsage();
		parser.parse(args);
		args = new String[]{"create", "--graph", "rg", "--graphtype", "rg"};
		parser.parse(args);
		args = new String[]{"create", "--alg", "et", "--classpath", "c:\\users\\partheinstein\\RepSysTestbed\\war\\WEB-INF\\classes\\cu\\repsystestbed\\algorithms\\examples\\EigenTrust.class"};
		parser.parse(args);
		args = new String[]{"create","--workflow", "wf", "--defn", "fhg>et>rg"};
		parser.parse(args);
		args = new String[]{"list"};
		parser.parse(args);
		args = new String[]{"run", "--workflow", "wf"};
		parser.parse(args);
		
		
	}

	@Override
	public void setGraph(String graphname)
	{
		logger.debug("graph called");
		t_graphName = graphname;
	}

	@Override
	public void setGraphtype(String graphtype)
	{
		logger.debug("setGraphtype called");
		t_graphType = graphtype;
		
	}

	@Override
	public void setInputfile(String inputfile)
	{
		logger.debug("setInputfile called");
		t_graphInputFile = inputfile;
		
	}

	@Override
	public void setDefn(String defn)
	{
		logger.debug("SetDefn called.");
		t_workflowDefn = defn;
		
	}

	@Override
	public void setWorkflow(String workflow)
	{
		logger.debug("SetWorkflow called.");
		t_workflowName = workflow;
		
	}

	@Override
	public void doRun()
	{
		try
		{
			if(t_workflowName!=null)
			{
				logger.info("running");
				WorkflowParser2 wfParser = new WorkflowParser2((String) m_storage.get(t_workflowName), m_storage);
				wfParser.run();
			}
		}
		catch(Exception e)
		{
			logger.error(e);
			e.printStackTrace();
		}
		
		reset();
	}




}
