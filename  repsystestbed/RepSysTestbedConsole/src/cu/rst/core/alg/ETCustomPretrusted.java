package cu.rst.core.alg;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import Jama.Matrix;
import cu.rst.core.graphs.Agent;
import cu.rst.core.graphs.FHG;
import cu.rst.core.graphs.Feedback;
import cu.rst.core.graphs.FeedbackHistoryGraphEdge;
import cu.rst.core.graphs.Graph;
import cu.rst.core.graphs.Graph.Type;
import cu.rst.core.graphs.RG;
import cu.rst.core.graphs.ReputationEdge;
import cu.rst.core.petrinet.Place;
import cu.rst.core.petrinet.Token;

public class ETCustomPretrusted extends Algorithm
{

	//config parameters that are used by the algorithm
	private static String THRESHOLD2SATISFY = "Threshold2Satisfy";
	private static String NUMITERATIONS = "NumberOfIterations";
	private static String LOG4JPPROPERTIES = "Log4jPropLocation";
	
	private final int DEFAULT_ITERATIONS = 20;

	private int iterations;
	private Matrix trustScores;
	private double[][] cijMatrix;
	private Hashtable<Integer, Integer> agentIdmapping;
	private static double MINIMUM_TRUST_SCORE = 0.0;
	private static double MAXIMUM_TRUST_SCORE = 1.0;
	
	
	static Logger logger = Logger.getLogger(ETCustomPretrusted.class.getName());
	
	
	
	public ETCustomPretrusted()
	{
		this.iterations = DEFAULT_ITERATIONS;
		this.agentIdmapping = new Hashtable<Integer, Integer>();
	}
	
	@Override
	public void setConfig(Properties config)
	{
		if(config != null)
		{
			try
			{
				
				Integer iterations = new Integer(config.getProperty(ETCustomPretrusted.NUMITERATIONS));
				if(iterations>0) this.iterations = iterations;
				else this.iterations = DEFAULT_ITERATIONS;
				
			}
			catch(Exception e)
			{
				this.iterations = DEFAULT_ITERATIONS;
			}
		}
		else
		{
			this.iterations = DEFAULT_ITERATIONS;
		}
		if(config.getProperty(this.LOG4JPPROPERTIES)==null)
			BasicConfigurator.configure();
		else
			PropertyConfigurator.configure(config.getProperty(this.LOG4JPPROPERTIES));
		
	}
	
	
	@Override
	public ArrayList<ReputationEdge> update(ArrayList<Token> tokens) throws Exception
	{
		
		
		if(this.iterations<1) throw new Exception("Number of iterations is less than 1");
		ArrayList<ReputationEdge> changes = new ArrayList<ReputationEdge>();

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
		
		for(Token t : tokens)
		{
			Place place = (Place) t.m_place;
			if(!(place.getGraph() instanceof FHG))
			{
				throw new Exception("A FHG expected.");
			}
			
			//ideally you should only be using the feedbacks in token t. Here we are cheating and using all the feedbacks
			//but thats ok because ET uses the entire FHG anyways.
		
			FHG fhg = (FHG) place.getGraph();
			
			int numVertices = fhg.vertexSet().size();
			cijMatrix = new double[numVertices][numVertices];
			
			Set<Agent> agents = fhg.vertexSet();
			
			int internalId=0;
			this.agentIdmapping.clear();
			for(Agent a : agents)
			{
				this.agentIdmapping.put(a.id, internalId);
				internalId++;
			}
			
			for(Agent source : agents)
			{
				Set<FeedbackHistoryGraphEdge> allOutgoingEdges = fhg.outgoingEdgesOf(source); 
				
				
				//fill up cij matrix
				for(FeedbackHistoryGraphEdge edge : allOutgoingEdges)
				{
					ArrayList<Feedback> feedbacks = edge.feedbacks;
					double sij=0;
					for(Feedback feedback : feedbacks)
					{
//						if(feedback.value >= threshold2Satisfy) sij++;
//						else sij--;
						if(feedback.value == 1) sij++;
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
			
			int pretrustedAgent = 1;
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
					
					//agent i doesnt trust anyone, trust the pre-trusted guy.
					else
					{
						cijMatrix[i][pretrustedAgent]=1.0;
					}
				}
			}
		
			logger.info("cijMatrix after normalization = " +  this.printMatrix(cijMatrix));
			
			/*
			 * t(0) = p;
			 * repeat
			 *  t(k+1) = C(T).t(k);
			 *  t(k+1) = (1 − a).t(k+1) + a.p;
			 *  delta = ||t(k+1) − t(k)||;
			 * until delta < tau;
			 */
			
			//get the transpose matrix.
			trustScores = new Matrix(cijMatrix);
			Matrix trans = trustScores.transpose();
			
			//t(0) = p
			double[][] p = new double[numVertices][1];
			for(int i=0;i<numVertices;i++)
			{
				for(int j=0;j<1;j++)
				{
					if(i==pretrustedAgent) p[i][j] = 1; 
				}
			}
			System.out.println("Pretrusted:");
			System.out.println(printMatrix(p));
			
			Matrix tkMatrix = new Matrix(p); //t(0)
			Matrix pMatrix = new Matrix(p); //preTrusted matrix
			
			double a=0.2; //TODO make this configurable
			Matrix tkplus1Matrix = null;
			
			
			for(int i=0;i<this.iterations;i++)
			{
				//note that t(0) = p
				tkplus1Matrix = trans.times(tkMatrix);
				tkplus1Matrix = tkplus1Matrix.times(1-a);
				tkplus1Matrix = tkplus1Matrix.plus(pMatrix.times(a));
				tkMatrix = new Matrix(tkplus1Matrix.getArrayCopy());
				System.out.println("Iteration: " + i);
				System.out.println(printMatrix(tkplus1Matrix.getArray()));
				//System.out.println("row=" + tkplus1Matrix.getRowDimension() + " column="+tkplus1Matrix.getColumnDimension());
			}
			
			this.trustScores = tkMatrix;

			logger.info("cijMatrix after multiplying " + this.iterations + " times = " +  this.printMatrix(trustScores.getArray()));
			
			//create all the reputation edges that needs to be added to RG. Its a complete graph
			for(Agent src : (Set<Agent>)fhg.vertexSet())
			{
				for(Agent sink : (Set<Agent>)fhg.vertexSet())
				{
					ReputationEdge edge = new ReputationEdge(src, sink, trustScores.getArray()[this.agentIdmapping.get(sink.id)][0]);
					changes.add(edge);
				}
			}
			
		}
		
		return changes;

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
	public boolean assertGraph2ListenType(Graph g) throws Exception
	{
		if(!(g instanceof FHG)) return false;
		return true;
	}

	@Override
	public boolean assertGraph2OutputType(Graph g) throws Exception
	{
		if(!(g instanceof RG)) return false;
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
