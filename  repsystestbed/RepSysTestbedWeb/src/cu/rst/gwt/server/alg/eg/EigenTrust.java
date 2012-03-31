package cu.rst.gwt.server.alg.eg;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import Jama.Matrix;
import cu.rst.gwt.server.alg.ReputationAlgorithm;
import cu.rst.gwt.server.data.Feedback;
import cu.rst.gwt.server.entities.Agent;
import cu.rst.gwt.server.graphs.FeedbackHistoryGraph;
import cu.rst.gwt.server.graphs.FeedbackHistoryGraphEdge;
import cu.rst.gwt.server.graphs.Graph;
import cu.rst.gwt.server.graphs.ReputationGraph;
import cu.rst.gwt.server.graphs.Graph.Type;

public class EigenTrust extends ReputationAlgorithm
{

	//config parameters that are used by the algorithm
	private static String THRESHOLD2SATISFY = "Threshold2Satisfy";
	private static String NUMITERATIONS = "NumberOfIterations";
	private static String LOG4JPPROPERTIES = "Log4jPropLocation";
	
	private final int DEFAULT_ITERATIONS = 20;
	private final double DEFAULT_THRESHOLD2SATISFY = 0.7;

	private int iterations;
	private Matrix trustScores;
	private double threshold2Satisfy;
	private double[][] cijMatrix;
	private Hashtable<Integer, Integer> agentIdmapping;
	private static double MINIMUM_TRUST_SCORE = 0.0;
	private static double MAXIMUM_TRUST_SCORE = 1.0;
	
	
	static Logger logger = Logger.getLogger(EigenTrust.class.getName());
	
	
	
	public EigenTrust()
	{
		super.setOutputGraphType(OutputGraphType.COMPLETE_GRAPH);
		this.iterations = DEFAULT_ITERATIONS;
		this.threshold2Satisfy = DEFAULT_THRESHOLD2SATISFY;
		this.agentIdmapping = new Hashtable<Integer, Integer>();
		/*
		if(super.config!=null)
		{
			try
			{
				this.iterations = config.getIntegerValue(EigenTrust.NUMITERATIONS);
				this.threshold2Satisfy = config.getDoubleValue(EigenTrust.THRESHOLD2SATISFY);
			}catch(Exception e)
			{
				this.iterations = DEFAULT_ITERATIONS;
				this.threshold2Satisfy = DEFAULT_THRESHOLD2SATISFY;	
			}
		}else
		{
			this.iterations = DEFAULT_ITERATIONS;
			this.threshold2Satisfy = DEFAULT_THRESHOLD2SATISFY;
		}*/
	}
	
	@Override
	public void setConfig(Properties config)
	{
		if(config != null)
		{
			try
			{
				
				Integer iterations = new Integer(config.getProperty(EigenTrust.NUMITERATIONS));
				if(iterations>0) this.iterations = iterations;
				else this.iterations = DEFAULT_ITERATIONS;
				
				Double threshold2Satisfy = new Double(config.getProperty(EigenTrust.THRESHOLD2SATISFY));
				if(threshold2Satisfy>=0) this.threshold2Satisfy = threshold2Satisfy;
				else this.threshold2Satisfy = DEFAULT_THRESHOLD2SATISFY;
				
			}
			catch(Exception e)
			{
				this.iterations = DEFAULT_ITERATIONS;
				this.threshold2Satisfy = DEFAULT_THRESHOLD2SATISFY;
			}
		}
		else
		{
			this.iterations = DEFAULT_ITERATIONS;
			this.threshold2Satisfy = DEFAULT_THRESHOLD2SATISFY;
		}
		if(config.getProperty(this.LOG4JPPROPERTIES)==null)
			BasicConfigurator.configure();
		else
			PropertyConfigurator.configure(config.getProperty(this.LOG4JPPROPERTIES));
		
	}
	
	/**
	 * @param iterations
	 * @param threshold2Satisfy
	 */
	/*
	public EigenTrust(int iterations, double threshold2Satisfy)
	{
		this.iterations = iterations;
		this.threshold2Satisfy = threshold2Satisfy;
		super.setGlobal(true);
	}
	*/
	
	/*
	public EigenTrust(String algConfigFile) throws Exception
	{
		this();
		try
		{
			config = new AlgConfig(algConfigFile);
			this.iterations = config.getIntegerValue(EigenTrust.NUMITERATIONS);
			this.threshold2Satisfy = config.getDoubleValue(EigenTrust.THRESHOLD2SATISFY);
			
		}catch(Exception e)
		{
			throw new Exception("Cannot instantiate EigenTrust algorithm", e); 
		}
		
	}
	*/
	
	@Override
	public double calculateTrustScore(Agent src, Agent sink) throws Exception
	{
		
		if(super.m_graph2Listen==null || super.m_graph2Listen.vertexSet().size()==0 
				|| super.m_graph2Listen.edgeSet().size()==0)
		{
			throw new Exception("Feedback History graph not initialized");
		}
		
		if(this.iterations<1) throw new Exception("Number of iterations is less than 1");

		/**
		 * first time this method is invoked, it actually calculates the trust scores for all nodes 
		 * but returns the trust score only for sink.
		 * subsequent invocations are simple lookups to the trust score
		 * 
		 * 1. get all the edges from the source agent
		 * 2. for each edge from #1, get the experiences
		 * 3. calculate sij for each sink using experiences in #2
		 * 4. normalize cij matrix
		 * 5. get tij matrix by multiplying transformed cij
		 */
		
		//if(!this.matrixFilled) 
		{
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
			
			//transpose and multiplication
			trustScores = new Matrix(cijMatrix);
			Matrix trans = trustScores.transpose();
			
			Matrix orig = new Matrix(trans.getArray());

			//trust everyone equally in the beginning
			double tempScore = 1.0/(double)numVertices;
			double[][] tk = new double[numVertices][numVertices];
			for(int i=0;i<numVertices;i++)
			{
				for(int j=0;j<numVertices;j++) tk[i][j] = tempScore;
			}
			Matrix tkMatrix = new Matrix(tk);
			
			double[][] p = new double[numVertices][numVertices];
			for(int i=0;i<numVertices;i++)
			{
				for(int j=0;j<numVertices;j++) p[i][j] = tempScore; 
			}
			Matrix pMatrix = new Matrix(p); //preTrusted matrix
			
			double a=0.2; //TODO make this configurable
			Matrix tkplus1Matrix = null;
			
			
			for(int i=0;i<this.iterations;i++)
			{
				tkplus1Matrix = trans.times(tkMatrix);
				tkplus1Matrix = tkplus1Matrix.times(1-a);
				tkplus1Matrix = tkplus1Matrix.plus(pMatrix.times(a));
				tkMatrix = new Matrix(tkplus1Matrix.getArrayCopy());
				//System.out.println(printMatrix(tkplus1Matrix.getArray()));
				//System.out.println("row=" + tkplus1Matrix.getRowDimension() + " column="+tkplus1Matrix.getColumnDimension());
			}
			
			this.trustScores = tkMatrix;
//			for(int i=0;i<this.iterations;i++) trans = orig.times(trans);
//			trustScores = trans.transpose(); //re-transpose
//			
			//this.matrixFilled = true;

			logger.info("cijMatrix after multiplying " + this.iterations + " times = " +  this.printMatrix(trustScores.getArray()));
			
		}	
		
		return trustScores.getArray()[this.agentIdmapping.get(sink.id)][0];
		
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
	public boolean assertGraph2ListenType(Graph g) throws Exception
	{
		if(!(g instanceof FeedbackHistoryGraph)) return false;
		return true;
	}

	@Override
	public boolean assertGraph2OutputType(Graph g) throws Exception
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
		return true;
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
