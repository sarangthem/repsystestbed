/**
 * 
 */
package cu.rst.core.alg;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;

import Jama.Matrix;
import cu.rst.core.graphs.Agent;
import cu.rst.core.graphs.Graph;
import cu.rst.core.graphs.Graph.Type;
import cu.rst.core.graphs.RG;
import cu.rst.core.graphs.ReputationEdge;
import cu.rst.core.petrinet.PetriNetEdge;
import cu.rst.core.petrinet.Place;
import cu.rst.core.petrinet.Token;

/**
 * @author partheinstein
 * This version of ET takes a rep graph and outputs a rep graph
 *
 */
public class EigenTrustv2 extends Algorithm
{
	//config parameters that are used by the algorithm
	private static String NUMITERATIONS = "NumberOfIterations";
	private static String LOG4JPPROPERTIES = "Log4jPropLocation";
	
	private final int DEFAULT_ITERATIONS = 20;

	private int iterations;
	private Matrix trustScores;
	private double[][] cijMatrix;
	private Hashtable<Integer, Integer> agentIdmapping;
	private static double MINIMUM_TRUST_SCORE = 0.0;
	private static double MAXIMUM_TRUST_SCORE = 1.0;
	
	
	static Logger logger = Logger.getLogger(EigenTrustv2.class.getName());
	
	
	
	public EigenTrustv2()
	{
		this.iterations = DEFAULT_ITERATIONS;
		this.agentIdmapping = new Hashtable<Integer, Integer>();
	}
	

	@Override
	public boolean assertGraph2ListenType(Graph g) throws Exception {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean assertGraph2OutputType(Graph g) throws Exception {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean assertVariablePrecondition(double variable) throws Exception {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public Type getInputGraphType() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Type getOutputGraphType() throws Exception {
		// TODO Auto-generated method stub
		return null;
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
			if(!(place.getGraph() instanceof RG))
			{
				throw new Exception("A RG expected.");
			}
		
			RG rg = (RG) place.getGraph();
			
			int numVertices = rg.vertexSet().size();
			cijMatrix = new double[numVertices][numVertices];
			
			Set<Agent> agents = rg.vertexSet();
			
			int internalId=0;
			this.agentIdmapping.clear();
			for(Agent a : agents)
			{
				this.agentIdmapping.put(a.id, internalId);
				internalId++;
			}
			
			for(Agent source : agents)
			{
				Set<ReputationEdge> allOutgoingEdges = rg.outgoingEdgesOf(source); 
				//fill up cij ReputationEdge
				for(ReputationEdge edge : allOutgoingEdges)
				{
					Agent sinkTemp = (Agent)edge.sink;
					if(this.agentIdmapping.get(source.id) > cijMatrix.length || this.agentIdmapping.get(sinkTemp.id) > cijMatrix.length)
						throw new Exception("Array out of bounds exception will occur. Problem with internal id mapping.");
					
					cijMatrix[this.agentIdmapping.get(source.id)][this.agentIdmapping.get(sinkTemp.id)] = edge.getReputation();		
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
			
			//t(0) = trust everyone equally in the beginning
			double tempScore = 1.0/(double)numVertices;
			double[][] tk = new double[numVertices][1];
			for(int i=0;i<numVertices;i++)
			{
				for(int j=0;j<1;j++) tk[i][j] = tempScore;
			}
			Matrix tkMatrix = new Matrix(tk);
			
			//p = pre trust certain agents
			double[][] p = new double[numVertices][1];
			for(int i=0;i<numVertices;i++)
			{
				for(int j=0;j<1;j++) p[i][j] = tempScore; 
			}
			
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
				System.out.println(printMatrix(tkplus1Matrix.getArray()));
				//System.out.println("row=" + tkplus1Matrix.getRowDimension() + " column="+tkplus1Matrix.getColumnDimension());
			}
			
			this.trustScores = tkMatrix;

			logger.info("cijMatrix after multiplying " + this.iterations + " times = " +  this.printMatrix(trustScores.getArray()));
			
			//create all the reputation edges that needs to be added to RG. Its a complete graph
			for(Agent src : (Set<Agent>)rg.vertexSet())
			{
				for(Agent sink : (Set<Agent>)rg.vertexSet())
				{
					ReputationEdge edge = new ReputationEdge(src, sink, trustScores.getArray()[this.agentIdmapping.get(sink.id)][0]);
					changes.add(edge);
				}
			}
			
		}
		
		return changes;

	}
	
	@Override
	public ArrayList update(ArrayList<Token> tokens, Place p) throws Exception
	{
		if(this.iterations<1) throw new Exception("Number of iterations is less than 1");
		ArrayList<ReputationEdge> changes = new ArrayList<ReputationEdge>();
		
		RG rg0 = (RG) ((Place)tokens.get(0).m_place).getGraph();
		
		boolean addEdges = true;
		
		//if rg0->as->rg0, then no need to invoke the algorithm, just return an empty of list of changes
		for(PetriNetEdge e : (Set<PetriNetEdge>) this.getWorkflow().incomingEdgesOf(this.getTransition()))
		{
			if(p.equals(e.src))
			{
				addEdges = false;
				break;
			}
		}
		
		if(addEdges)
		{

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
				if(!(place.getGraph() instanceof RG))
				{
					throw new Exception("A RG expected.");
				}
			
				RG rg = (RG) place.getGraph();
				
				int numVertices = rg.vertexSet().size();
				cijMatrix = new double[numVertices][numVertices];
				
				Set<Agent> agents = rg.vertexSet();
				
				int internalId=0;
				this.agentIdmapping.clear();
				for(Agent a : agents)
				{
					this.agentIdmapping.put(a.id, internalId);
					internalId++;
				}
				
				for(Agent source : agents)
				{
					Set<ReputationEdge> allOutgoingEdges = rg.outgoingEdgesOf(source); 
					//fill up cij ReputationEdge
					for(ReputationEdge edge : allOutgoingEdges)
					{
						Agent sinkTemp = (Agent)edge.sink;
						if(this.agentIdmapping.get(source.id) > cijMatrix.length || this.agentIdmapping.get(sinkTemp.id) > cijMatrix.length)
							throw new Exception("Array out of bounds exception will occur. Problem with internal id mapping.");
						
						cijMatrix[this.agentIdmapping.get(source.id)][this.agentIdmapping.get(sinkTemp.id)] = edge.getReputation();		
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
				
				//t(0) = trust everyone equally in the beginning
				double tempScore = 1.0/(double)numVertices;
				double[][] tk = new double[numVertices][1];
				for(int i=0;i<numVertices;i++)
				{
					for(int j=0;j<1;j++) tk[i][j] = tempScore;
				}
				Matrix tkMatrix = new Matrix(tk);
				
				//p = pre trust certain agents
				double[][] pretrusted = new double[numVertices][1];
				for(int i=0;i<numVertices;i++)
				{
					for(int j=0;j<1;j++) pretrusted[i][j] = tempScore; 
				}
				
				Matrix pMatrix = new Matrix(pretrusted); //preTrusted matrix
				
				double a=0.2; //TODO make this configurable
				Matrix tkplus1Matrix = null;
				
				
				for(int i=0;i<this.iterations;i++)
				{
					//note that t(0) = p
					tkplus1Matrix = trans.times(tkMatrix);
					tkplus1Matrix = tkplus1Matrix.times(1-a);
					tkplus1Matrix = tkplus1Matrix.plus(pMatrix.times(a));
					tkMatrix = new Matrix(tkplus1Matrix.getArrayCopy());
					System.out.println(printMatrix(tkplus1Matrix.getArray()));
					//System.out.println("row=" + tkplus1Matrix.getRowDimension() + " column="+tkplus1Matrix.getColumnDimension());
				}
				
				this.trustScores = tkMatrix;
	
				logger.info("cijMatrix after multiplying " + this.iterations + " times = " +  this.printMatrix(trustScores.getArray()));
				
				//create all the reputation edges that needs to be added to RG. Its a complete graph
				for(Agent src : (Set<Agent>)rg.vertexSet())
				{
					for(Agent sink : (Set<Agent>)rg.vertexSet())
					{
						ReputationEdge edge = new ReputationEdge(src, sink, trustScores.getArray()[this.agentIdmapping.get(sink.id)][0]);
						changes.add(edge);
					}
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

}
