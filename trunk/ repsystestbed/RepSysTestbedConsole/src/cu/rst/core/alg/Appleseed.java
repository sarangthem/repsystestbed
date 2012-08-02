/**
 * 
 */
package cu.rst.core.alg;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import cu.rst.core.alg.Algorithm;
import cu.rst.core.graphs.Agent;
import cu.rst.core.graphs.Graph;
import cu.rst.core.graphs.Graph.Type;
import cu.rst.core.graphs.ReputationEdge;
import cu.rst.core.graphs.RG;
import cu.rst.core.petrinet.PetriNetEdge;
import cu.rst.core.petrinet.Place;
import cu.rst.core.petrinet.Token;
import cu.rst.util.Util;
/**
 * @author partheinstein
 *
 */
public class Appleseed extends Algorithm
{
/*
	@Override
	public double calculateTrustScore(Agent src, Agent sink) throws Exception
	{
		
		//input
		Agent seed = new Agent(); //TODO pick a seed 
		double energy = 5;
		double decay = 0.8;
		double convergenceThreshold = 0.01;
		double m = 0;
		
		ReputationGraph clonedGraph = (ReputationGraph) super.m_graph2Listen.clone();
		
		Set<Agent> allVertices = clonedGraph.vertexSet();
		
		ArrayList<Agent> verticesMinus1 = new ArrayList<Agent>();
		ArrayList<Agent> vertices = new ArrayList<Agent>();
		
		Hashtable<Agent, Double> energiesMinus1 = new Hashtable<Agent, Double>();
		Hashtable<Agent, Double> energies = new Hashtable<Agent, Double>();
		
		Hashtable<Agent, Double> trustScoresMinus1 = new Hashtable<Agent, Double>();
		Hashtable<Agent, Double> trustScores = new Hashtable<Agent, Double>();
		
		Iterator it = allVertices.iterator();
		while(it.hasNext())
		{
			Agent a = (Agent)it.next();
			energiesMinus1.put(a, new Double(0));
			energies.put(a, new Double(0));
			trustScoresMinus1.put(a, new Double(0));
			trustScores.put(a, new Double(0));
		}
		
		energiesMinus1.put(seed, energy);
		
		do
		{
			//set V_i = V_i-1
			vertices = (ArrayList<Agent>) verticesMinus1.clone();
			//for all x in V_i-1, set in_i(x) = 0
			for(Agent x : verticesMinus1)
			{
				energies.put(x, (new Double(0)));
			}
			
			for(Agent x : verticesMinus1)
			{
				double tempTrustScore = trustScoresMinus1.get(x) + (1 - decay) * energiesMinus1.get(x);
				trustScores.put(x, tempTrustScore);
				Set<ReputationEdge> outgoingEdges = clonedGraph.outgoingEdgesOf(x);
				for(ReputationEdge e : outgoingEdges)
				{
					if(!vertices.contains(e.sink))
					{
						vertices.add((Agent) e.sink);
						trustScores.put((Agent)e.sink, new Double(0));
						energies.put((Agent) e.sink, new Double(0));
						clonedGraph.addEdge((Agent) e.sink, seed);
						((ReputationEdge) clonedGraph.getEdge((Agent)e.sink, seed)).setReputation(1);
					}
					
					double tempWeight = ((ReputationEdge) clonedGraph.getEdge(x, (Agent)e.sink)).getReputation();
					double sumWeights = 0;
					Set<ReputationEdge> outgoingEdgesTemp = clonedGraph.outgoingEdgesOf(x);
					for(ReputationEdge e1 : outgoingEdgesTemp)
					{
						if(!e.sink.equals(e1.sink))
						{
							sumWeights = e1.getReputation();
						}
					}
					double weight = tempWeight + sumWeights;
					double value = energies.get((Agent)e.sink) + decay * energiesMinus1.get(x) * weight;
					energies.put((Agent)e.sink, value);
				}
				
			}
			
			
		}while(m <= convergenceThreshold);
		
		
		
		
		
		return 0;
	}
*/
	@Override
	public boolean assertVariablePrecondition(double variable) throws Exception
	{
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean assertGraph2ListenType(Graph g) throws Exception
	{
		if(!(g instanceof RG)) return false;
		return true;
	}

	@Override
	public boolean assertGraph2OutputType(Graph g) throws Exception
	{
		if(!(g instanceof RG)) return false;
		return true;
	}

	public double calculateTrustScore(Agent src, Agent sink, RG rg) throws Exception
	{
		double inEnergy = 1;
		double decay = 0.85;
		double threshold = 0.9;
		//returns {(agent, trust rank)}
		Hashtable<Agent, Double> trustRanks = trusts(src, inEnergy, decay, threshold, rg.vertexSet().size(), rg);
//		System.out.println(src);
//		System.out.println(sink);
//		System.out.println(trustRanks.get(sink));
//		System.out.println(trustRanks.get(new Agent(1)));
//		System.out.println(trustRanks.get(new Agent(2)));
//		System.out.println(trustRanks.get(new Agent(3)));
//		
		return trustRanks.containsKey(sink) ? (Double) trustRanks.get(sink) : -1;

	}

	
	public Appleseed()
	{
	}
	
	
	
	private Hashtable<Agent, Double> trusts(Agent s, double inEnergy, double decay, double threshold, int totalNumAgents, RG rg) throws Exception
	{
		//each element corresponds to 'in' energies of agents for ith iteration
		ArrayList<Hashtable<Agent, Double>> in = new ArrayList<Hashtable<Agent, Double>>(); 
		//each element corresponds to trust ranks of agents for ith iteration
		ArrayList<Hashtable<Agent, Double>> trust = new ArrayList<Hashtable<Agent, Double>>(); 
		//already visited notes
		ArrayList<ArrayList<Agent>> v = new ArrayList<ArrayList<Agent>>();
		int i = 0;
		
		Hashtable<Agent, Double> temp = new Hashtable<Agent, Double>();
		temp.put(s, inEnergy);
		in.add(temp);
		
		temp = new Hashtable<Agent, Double>();
		temp.put(s, (double)0);
		trust.add(temp);
		
		ArrayList<Agent> temp1 = new ArrayList<Agent>();
		temp1.add(s);
		v.add(temp1);
		
		do
		{
			i++;
			
			temp1 = new ArrayList<Agent>();
			temp1.addAll(v.get(i-1)); 
			v.add(temp1);//V(i) = V(i-1)
			
			temp = new Hashtable<Agent, Double>();
			for(Agent a : v.get(i-1))
			{
				temp.put(a, (double)0);
			}
			in.add(temp); //for all x in V(i-1) : in(i)(x) = 0;
			
			trust.add(new Hashtable<Agent, Double>());
			
			for(Agent x : v.get(i-1))
			{
				double temp2 = trust.get(i-1).get(x) + (1 - decay) * in.get(i-1).get(x);
				trust.get(i).put(x, temp2);
				
				double total = 0;
				Set<ReputationEdge> outgoingEdges = (Set<ReputationEdge>) rg.outgoingEdgesOf(x);
				for(ReputationEdge e : outgoingEdges)
				{
					total = total + e.getReputation();
				}
				
				for(ReputationEdge e : (Set<ReputationEdge>) rg.outgoingEdgesOf(x))
				{
					if(!Util.isPresent(v.get(i), e.sink))
					{
						v.get(i).add((Agent) e.sink);
						trust.get(i).put((Agent)e.sink, (double)0);
						in.get(i).put((Agent) e.sink, (double)0);
						//for some reason this adds '1' as the edge between e.sink and s
						//rg.addEdge(e.sink, s, (double)1.0);
						ReputationEdge re = new ReputationEdge((Agent) e.sink, s);
						re.setReputation(1.0);
						rg.addEdge(e.sink, s, re);
						rg.setEdgeWeight(re, 1.0);
					}
					double w = e.getReputation() / total;
					temp2 = in.get(i).get(e.sink) + decay * in.get(i-1).get(x) * w;
					in.get(i).put((Agent) e.sink, temp2);
					
					
				}
			}
			
		}while(i<50);
		
		return trust.get(i);

	}

	@Override
	public Type getInputGraphType() throws Exception
	{
		return Graph.Type.RG;
	}

	@Override
	public Type getOutputGraphType() throws Exception
	{
		return Graph.Type.RG;
	}

	@Override
	public ArrayList update(ArrayList<Token> tokens, Place p) throws Exception 
	{
		Util.assertNotNull(tokens);
		if(tokens.size()==0) throw new Exception("Need atleast 1 token"); 
		//assume that the place in the first token is a RG and thats all you need.
		//Revisit as requirements arise
		
		ArrayList toReturn = new ArrayList();
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
			for(Agent src : (Set<Agent>)rg0.vertexSet())
			{
				for(Agent sink : (Set<Agent>)rg0.vertexSet())
				{
					//make a copy because this impl of AppleSeed destroys the original input;
					RG rgTemp = rg0.clone(false);
					double rep =  this.calculateTrustScore(src, sink, rgTemp);
					//rep = -1 if there at least no path from src to sink
					//rep = NaN if there is no path from src to any 
					if(rep>=0) //there is a path
					{
						toReturn.add(new ReputationEdge(src, sink, rep));
					}
				}
				
			}
		}
		
		return toReturn;
	}

	@Override
	public ArrayList update(ArrayList<Token> tokens) throws Exception 
	{
		Util.assertNotNull(tokens);
		if(tokens.size()==0) throw new Exception("Need atleast 1 token"); 
		//assume that the place in the first token is a RG and thats all you need.
		//Revisit as requirements arise
		
		ArrayList toReturn = new ArrayList();
		RG rg0 = (RG) ((Place)tokens.get(0).m_place).getGraph();
		
		for(Agent src : (Set<Agent>)rg0.vertexSet())
		{
			for(Agent sink : (Set<Agent>)rg0.vertexSet())
			{
				//make a copy because this impl of AppleSeed destroys the original input;
				RG rgTemp = rg0.clone(false);
				double rep =  this.calculateTrustScore(src, sink, rgTemp);
				//rep = -1 if there at least no path from src to sink
				//rep = NaN if there is no path from src to any 
				if(rep>=0) //there is a path
				{
					toReturn.add(new ReputationEdge(src, sink, rep));
				}
			}
			
		}
		
		return toReturn;
	}


}
