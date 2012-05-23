/**
 * 
 */
package cu.rst.gwt.server.parse;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Random;
import java.util.regex.Pattern;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import cu.rst.gwt.server.alg.Algorithm;
import cu.rst.gwt.server.alg.eg.Appleseed;
import cu.rst.gwt.server.alg.eg.EigenTrust;
import cu.rst.gwt.server.alg.eg.RankbasedTrustAlg;
import cu.rst.gwt.server.data.Workflow;
import cu.rst.gwt.server.exceptions.WorkflowParserException;
import cu.rst.gwt.server.graphs.FeedbackHistoryEdgeFactory;
import cu.rst.gwt.server.graphs.FeedbackHistoryGraph;
import cu.rst.gwt.server.graphs.Graph;
import cu.rst.gwt.server.graphs.ReputationEdgeFactory;
import cu.rst.gwt.server.graphs.ReputationGraph;
import cu.rst.gwt.server.graphs.TrustEdgeFactory;
import cu.rst.gwt.server.graphs.TrustGraph;
import cu.rst.gwt.server.util.Util;

/**
 * @author pchandra
 *
 */
public class WorkflowParser2
{
	//static variables
	static private Logger logger = Logger.getLogger(WorkflowParser2.class);
	
	//private variables
	private Workflow m_workflow;
	private ArrayList<String> m_parsedStrings;
	//public variables
	
	public WorkflowParser2(String args, Hashtable<String, Object> storage) throws Exception
	{
		Util.assertNotNull(args);
		Util.assertNotNull(storage);
		m_workflow = new Workflow();
		m_parsedStrings = new ArrayList<String>();
		parse(args, storage);
	}
	
	private void parse(String args, Hashtable<String, Object> storage) throws Exception
	{
		Util.assertNotNull(args);
		Util.assertNotNull(storage);
		/*
		 * Samples:
		 * FHG>ET|RK>TG
		 * RG>AS>RG
		 */
		
		String[] temp = null;
		Object o = null;
		temp = args.split(">");
		Util.assertNotNull(temp);
		for(String t : temp)
		{
			if(t.contains("|"))
			{
				String[] temp2 = t.split(Pattern.quote("|"));
				for(String s : temp2)
				{
					m_parsedStrings.add(s);		
				}
			}
			else
			{
				m_parsedStrings.add(t);
			}
			
		}
		
		Random r = new Random();
		
		for(int i = 0; i < m_parsedStrings.size(); i++)
		{
			System.out.println(m_workflow);
			o = storage.get(m_parsedStrings.get(i));
			Util.assertNotNull(o);
			if(o instanceof Algorithm)
			{
				if(i > 0 && !(m_workflow.sequence.get(i-1) instanceof Graph))
				{
					switch(((Algorithm) o).getInputGraphType())
					{
					case FHG:
						FeedbackHistoryGraph fhg = new FeedbackHistoryGraph(new FeedbackHistoryEdgeFactory());
						m_workflow.addItem(fhg);
						storage.put("int_fhg_" + r.nextInt(1000), fhg);
						break;
					case RG:
						ReputationGraph rg = new ReputationGraph(new ReputationEdgeFactory());
						m_workflow.addItem(rg);
						storage.put("int_rg_" + r.nextInt(1000), rg);
						break;
					case TG:
						TrustGraph tg = new TrustGraph(new TrustEdgeFactory());
						m_workflow.addItem(tg);
						storage.put("int_tg_" + r.nextInt(1000), tg);
						break;
					default:
						throw new WorkflowParserException("Unknown input graph type for algorithm" + o);
					}
				}
			}
			m_workflow.addItem(o);
		}
		
		System.out.println(m_workflow);
	}
	
	public void run() throws Exception
	{	
		m_workflow.start(false);
	}
	
	public void clearAllObservers() throws Exception
	{
		ArrayList<Graph> graphs = m_workflow.getAllGraphs();
		Util.assertNotNull(graphs);
		for(Graph g : graphs)
		{
			g.removeAllObservers();
		}
		
		for(int i=0; i< m_workflow.sequence.size(); i++)
		{
			m_workflow.sequence.remove(i);
		}
	}
	
	@Override
	public String toString()
	{
		String temp = new String();
		for(String t : m_parsedStrings)
		{
			temp = temp + t + ",";
		}
		return temp;
	}
	
	public static void main(String[] args) throws Exception
	{
		BasicConfigurator.configure();
		
		Hashtable<String, Object> storage = new Hashtable<String, Object>();
		storage.put("fhg", new FeedbackHistoryGraph(new FeedbackHistoryEdgeFactory()));
		storage.put("et", new EigenTrust());
		storage.put("rg", new ReputationGraph(new ReputationEdgeFactory()));
		storage.put("as", new Appleseed());
		storage.put("rk", new RankbasedTrustAlg());
		storage.put("tg", new TrustGraph(new TrustEdgeFactory()));
		
		WorkflowParser2 parser = new WorkflowParser2("rg>as>rg", storage);
		System.out.println(parser);

	}

}
