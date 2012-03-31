/**
 * 
 */
package cu.rst.gwt.server.alg.eg;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import cu.rst.gwt.server.alg.ReputationAlgorithm;
import cu.rst.gwt.server.data.Feedback;
import cu.rst.gwt.server.entities.Agent;
import cu.rst.gwt.server.graphs.FeedbackHistoryGraph;
import cu.rst.gwt.server.graphs.FeedbackHistoryGraphEdge;
import cu.rst.gwt.server.graphs.Graph;
import cu.rst.gwt.server.graphs.ReputationGraph;
import cu.rst.gwt.server.graphs.Graph.Type;

/**
 * @author partheinstein
 *
 */
public class EigenTrust1a extends ReputationAlgorithm
{
	private static String THRESHOLD2SATISFY = "Threshold2Satisfy";
	private static String LOG4JPPROPERTIES = "Log4jPropLocation";
	private static double MINIMUM_TRUST_SCORE = 0.0;
	private static double MAXIMUM_TRUST_SCORE = 1.0;
	
	private final double DEFAULT_THRESHOLD2SATISFY = 0.7;
	
	private double threshold2Satisfy;
	private double[][] cijMatrix;
	private Hashtable<Integer, Integer> agentIdmapping;
	
	static Logger logger = Logger.getLogger(EigenTrust1a.class.getName());
	
	public EigenTrust1a()
	{
		super.setOutputGraphType(OutputGraphType.COPY_INPUT_GRAPH);
		this.threshold2Satisfy = DEFAULT_THRESHOLD2SATISFY;
		this.agentIdmapping = new Hashtable<Integer, Integer>();
	}

	@Override
	public void setConfig(Properties config)
	{
		if(config!=null)
		{
			try
			{
				Double threshold2Satisfy = new Double(config.getProperty(EigenTrust1a.THRESHOLD2SATISFY));
				if(threshold2Satisfy>=0) this.threshold2Satisfy = threshold2Satisfy;
				else this.threshold2Satisfy = DEFAULT_THRESHOLD2SATISFY;
			}catch(Exception e)
			{
				this.threshold2Satisfy = DEFAULT_THRESHOLD2SATISFY;
			}
		}else
		{
			this.threshold2Satisfy = DEFAULT_THRESHOLD2SATISFY;
		}
		if(config.getProperty(this.LOG4JPPROPERTIES)==null)
			BasicConfigurator.configure();
		else
			PropertyConfigurator.configure(config.getProperty(this.LOG4JPPROPERTIES));
		
	}
	
	@Override
	public double calculateTrustScore(Agent src, Agent sink) throws Exception
	{
		if(super.m_graph2Listen==null || super.m_graph2Listen.vertexSet().size()==0 
				|| super.m_graph2Listen.edgeSet().size()==0)
		{
			throw new Exception("Feedback History graph not initialized");
		}
		
		int numVertices = super.m_graph2Listen.vertexSet().size();
		cijMatrix = new double[numVertices][numVertices];
		
		Set<Agent> agents = super.m_graph2Listen.vertexSet();
		
		int internalId=0;
		this.agentIdmapping.clear();
		for(Agent a : agents)
		{
			this.agentIdmapping.put(a.id, internalId);
			internalId++;
		}

		
		for(Agent source : agents)
		{
			Set<FeedbackHistoryGraphEdge> allOutgoingEdges = super.m_graph2Listen.outgoingEdgesOf(source); 
			
			
			//fill up cij matrix
			for(FeedbackHistoryGraphEdge edge : allOutgoingEdges)
			{
				ArrayList<Feedback> feedbacks = edge.feedbacks;
				double sij=0;
				for(Feedback feedback : feedbacks)
				{
					if(feedback.value >= threshold2Satisfy) sij++;
					else sij--;
				}
				
				if(sij<1) sij=0;
				Agent sinkTemp = (Agent)edge.sink;
				if(this.agentIdmapping.get(source.id) > cijMatrix.length || this.agentIdmapping.get(sinkTemp.id) > cijMatrix.length)
					throw new Exception("Array out of bounds exception will occur. Problem with internal id mapping.");
				cijMatrix[this.agentIdmapping.get(source.id)][this.agentIdmapping.get(sinkTemp.id)] = sij;				
			}
		}
		
		logger.info("cijMatrix before normalization = " + this.printMatrix(cijMatrix));
		
		//normalize cij matrix
		for(int i=0;i<numVertices;i++)
		{
			//row by row normalization
			double total = 0;
			for(int j=0;j<numVertices;j++)
			{
				total = total + cijMatrix[i][j];
			}
			for(int j=0;j<numVertices;j++)
			{
				if(total>0) cijMatrix[i][j] = cijMatrix[i][j] / total;
				//else cijMatrix[i][j]=0; //don't divide by 0
				
				//agent i doesnt trust anyone. make it trust everyone equally.
				else cijMatrix[i][j]=1.0/(double)numVertices;
			}
		}
	
		logger.info("cijMatrix after normalization = " +  this.printMatrix(cijMatrix));
		
		return cijMatrix[this.agentIdmapping.get(src.id)][this.agentIdmapping.get(sink.id)];
	}
	
	public String printMatrix(double[][] mat)
	{
		String output = "\n";
		output += "Internal id mapping:";
		Set<Entry<Integer, Integer>> temp = this.agentIdmapping.entrySet();
		for(Entry<Integer, Integer> e : temp)
		{
			output += "Agent.id: " + e.getKey() + ", Internal id: " + e.getValue() + "\n"; 
		}
		for(int i=0;i<mat.length;i++)
		{
			for(int j=0;j<mat[i].length;j++)
			{
				output = output + (mat[i][j] + " ");
			}
			output = output + "\n";
		}
		return output;

	}


	@Override
	public void start() throws Exception
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void finish() throws Exception
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean assertGraph2ListenType(Graph g)
			throws Exception
	{
		if(!(g instanceof FeedbackHistoryGraph)) return false;
		return true;
	}

	@Override
	public boolean assertGraph2OutputType(Graph g)
			throws Exception
	{
		if(!(g instanceof ReputationGraph)) return false;
		return true;
	}

	@Override
	public boolean assertVariablePrecondition(double variable) throws Exception
	{
		if(variable < this.MINIMUM_TRUST_SCORE || variable > this.MAXIMUM_TRUST_SCORE)
		{
			return false;
		}
		return false;
	}

	@Override
	public Type getInputGraphType() throws Exception
	{
		return Graph.Type.FHG;
	}

	@Override
	public Type getOutputGraphType() throws Exception
	{
		return Graph.Type.RG;
	}


}
