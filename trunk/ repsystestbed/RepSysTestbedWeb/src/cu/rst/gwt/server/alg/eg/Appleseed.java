/**
 * 
 */
package cu.rst.gwt.server.alg.eg;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

import cu.rst.gwt.server.alg.ReputationAlgorithm;
import cu.rst.gwt.server.entities.Agent;
import cu.rst.gwt.server.graphs.Graph;
import cu.rst.gwt.server.graphs.ReputationEdge;
import cu.rst.gwt.server.graphs.ReputationGraph;
import cu.rst.gwt.server.graphs.TestbedEdge;
import cu.rst.gwt.server.graphs.Graph.Type;

/**
 * @author partheinstein
 *
 */
public class Appleseed extends ReputationAlgorithm
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
	public boolean assertVariablePrecondition(double variable) throws Exception
	{
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean assertGraph2ListenType(Graph g) throws Exception
	{
		if(!(g instanceof ReputationGraph)) return false;
		return true;
	}

	@Override
	public boolean assertGraph2OutputType(Graph g) throws Exception
	{
		if(!(g instanceof ReputationGraph)) return false;
		return true;
	}

	@Override
	public double calculateTrustScore(Agent src, Agent sink) throws Exception
	{
		Hashtable t = trusts(src, 2, 0.85, 0.9, m_graph2Listen.vertexSet().size());
		System.out.println(src);
		System.out.println(sink);
		System.out.println(t.get(sink));
		return (Double) t.get(sink);
	}
	
	private double[] m_energies;
	
	public Appleseed()
	{
		outputGraphType = OutputGraphType.COPY_INPUT_GRAPH;
	}
	
	private void energize(double energy, Agent s, double threshold, int numAgents, 
			double[] energies, double decay)
	{
		if(energies == null) energies = new double[numAgents];
		
		energies[s.id] =+ decay * energy;
		double totalOutgoingEdgesWeights;
		
		/*
		 * Backward trust propagation as per authors:
		 * A virtual edge from every sink to seed is added and its weight is set to 1. 
		 */
		
		//it's ok to do type casting to reputation edge because the preconditions guarantee that its a rep edge.
		for(ReputationEdge edge : (Set<ReputationEdge>) m_graph2Listen.outgoingEdgesOf(s))
		{
			if(edge.getReputation() >= 0) totalOutgoingEdgesWeights =+ edge.getReputation();
		}
		
		if(energy > threshold)
		{
			for(ReputationEdge edge : (Set<ReputationEdge>) m_graph2Listen.outgoingEdgesOf(s))
			{
				energize( (1 - decay) * energies[s.id] * edge.getReputation(), (Agent) edge.sink, 
						threshold, numAgents, energies, decay);
			}
		}
		return;
		
	}
	
	
	
	private Hashtable<Agent, Double> trusts(Agent s, double in, double decay, double threshold, int totalNumAgents) throws Exception
	{
		Hashtable<Agent, Double> in_0 = new  Hashtable<Agent, Double>();
		Hashtable<Agent, Double> in_1 = new  Hashtable<Agent, Double>();
		Hashtable<Agent, Double> trust_0 = new  Hashtable<Agent, Double>();
		Hashtable<Agent, Double> trust_1 = new  Hashtable<Agent, Double>();
		
		HashSet<Agent> v_0 = new HashSet<Agent>();
		HashSet<Agent> v_1;
		
		in_0.put(s, in);
		trust_0.put(s, (double) 0);
		v_0.add(s);
		double m = 0;
		
		do
		{
			v_1 = new HashSet(v_0);
			for(Agent x : v_0)
			{
				in_1.put(x, (double) 0);
			}
			
			for(Agent x: v_0)
			{
				double tempTrust_0 = 0;
				double tempin_0 = 0;
				
				if(trust_0.containsKey(x))
				{
					tempTrust_0 = trust_0.remove(x);
				}
				
				if(in_0.containsKey(x))
				{
					tempin_0 = in_0.remove(x);
				}
				
				double tempTrust_1 = tempTrust_0 + (1 - decay) * tempin_0;
				trust_1.put(x, tempTrust_1);
				
				//copy the outgoing edges and add extra edges (x to s)
				Set<ReputationEdge> outgoingEdges1 = (Set<ReputationEdge>) m_graph2Listen.outgoingEdgesOf(x);
				HashSet<ReputationEdge> outgoingEdges2 = new HashSet<ReputationEdge>(outgoingEdges1);
				for(ReputationEdge edge : outgoingEdges1)
				{
					if(!v_1.contains(edge.sink))
					{
						ReputationEdge e = new ReputationEdge((Agent) edge.sink, s);
						e.setReputation(1);
						outgoingEdges2.add(e);
					}
				}
				
				//calculate the sum of edge weights
				double total = 0;
				for(ReputationEdge edge1 : outgoingEdges2)
				{
					total = total + edge1.getReputation();
				}
				
				//a check
				if(total <= 0) throw new Exception("total<=0");
				
				for(ReputationEdge edge : outgoingEdges2)
				{
					if(!v_1.contains(edge.sink))
					{
						v_1.add((Agent) edge.sink);
						trust_1.put((Agent)edge.sink, (double) 0);
						in_1.put((Agent) edge.sink, (double) 0);
					}
					
					double w = edge.getReputation() / total;
					
					double tempin_1 = 0;
					
					if(in_1.containsKey(edge.sink))
					{
						tempin_1 = in_1.remove(edge.sink);
					}
					
					tempin_0 = 0;
					
					if(in_0.containsKey(edge.sink))
					{
						tempin_0 = in_0.remove(edge.sink);
					}
					
					
					
					
					
					
					
					
					
					
					
					
					
					
					
					
					tempin_1 = tempin_1 + decay * tempin_0 * w;
					System.out.println("src = " + s + ",sink = " + edge.sink + ":= " + tempin_1);
					in_1.put((Agent) edge.sink, tempin_1);
					
				}
				
			}
			
			
		}while(false);
		
		return trust_1;		

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


}
