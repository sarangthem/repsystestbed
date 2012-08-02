package cu.rst.core.alg;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Set;

import cu.rst.core.graphs.Agent;
import cu.rst.core.graphs.Graph;
import cu.rst.core.graphs.Graph.Type;
import cu.rst.core.graphs.RG;
import cu.rst.core.graphs.ReputationEdge;
import cu.rst.core.petrinet.Place;
import cu.rst.core.petrinet.Token;
import cu.rst.util.Util;

public class Spearman extends Algorithm
{
	public class RepElement 
	{
		public Agent agent;
		public double reputation;
		public RepElement(Agent agent, double reputation)
		{
			this.agent = agent;
			this.reputation = reputation;
		}
		
	}
	
	public class RepElementComparator implements Comparator<RepElement>
	{
		@Override
		public int compare(RepElement o1, RepElement o2) 
		{
			if(o1.reputation == o2.reputation) return 0;
			if(o1.reputation > o2.reputation) return 1;
			else return -1;
		}
	}

	@Override
	public boolean assertGraph2ListenType(Graph g) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean assertGraph2OutputType(Graph g) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean assertVariablePrecondition(double variable) throws Exception {
		// TODO Auto-generated method stub
		return false;
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
	
	public void print(ArrayList<RepElement> res)
	{
		for(RepElement re : res)
		{
			System.out.println(re.agent + ", " + re.reputation);
		}
	}
	
	public int getPosition(ArrayList<RepElement> res, Agent a)
	{
		for(int i=0;i<res.size();i++)
		{
			if(res.get(i).agent.equals(a)) return i; 
		}
		return -1; //not found
	}

	@Override
	public ArrayList update(ArrayList<Token> tokens) throws Exception 
	{
		Util.assertNotNull(tokens);
		if(tokens.size() != 2) throw new Exception("Expects two tokens.");
		
		Token t1 = tokens.get(0);
		Token t2 = tokens.get(1);
		RG rg1 = (RG)((Place)t1.m_place).getGraph();
		RG rg2 = (RG) ((Place)t2.m_place).getGraph();
		
		//assuming global reputation (all incoming edges have the same weight)
		ArrayList<RepElement> rg1Agents = new ArrayList<RepElement>();
		for(Agent a : (Set<Agent>) rg1.vertexSet())
		{
			Set<ReputationEdge> incomingEdges = (Set<ReputationEdge>) rg1.incomingEdgesOf(a);
			if(incomingEdges!=null && incomingEdges.size()>1)
			{
				rg1Agents.add(new RepElement(a, ((ReputationEdge) incomingEdges.toArray()[0]).getReputation()));
			}
		}
		
		//assuming global reputation (all incoming edges have the same weight)
		ArrayList<RepElement> rg2Agents = new ArrayList<RepElement>();
		for(Agent a : (Set<Agent>) rg2.vertexSet())
		{
			Set<ReputationEdge> incomingEdges = (Set<ReputationEdge>) rg2.incomingEdgesOf(a);
			if(incomingEdges!=null && incomingEdges.size()>1)
			{
				rg2Agents.add(new RepElement(a, ((ReputationEdge) incomingEdges.toArray()[0]).getReputation()));
			}
		}
		
		print(rg1Agents);
		Collections.sort(rg1Agents, new RepElementComparator());
		System.out.println("-");
		print(rg1Agents);
		System.out.println("-");
		
		print(rg2Agents);
		Collections.sort(rg2Agents, new RepElementComparator());
		System.out.println("-");
		print(rg2Agents);
		
		int total = 0;
		for(int i=rg1Agents.size(); i>0; i--)
		{
			total = total + i;
		}
		double avgRank = total / rg1Agents.size(); // assuming rg1.size == rg2.size
		
		double numerator = 0, denominator1 = 0, demoninator2 = 0;
		for(Agent a : (Set<Agent>) rg1.vertexSet())
		{
			int xi = getPosition(rg1Agents, a);
			int yi = getPosition(rg2Agents, a);
			numerator = numerator + (xi - avgRank) * (yi - avgRank);
		}
		
		for(Agent a : (Set<Agent>) rg1.vertexSet())
		{
			int xi = getPosition(rg1Agents, a);
			denominator1 = denominator1 + (xi - avgRank) * (xi - avgRank);
		}
		
		for(Agent a : (Set<Agent>) rg1.vertexSet())
		{
			int yi = getPosition(rg2Agents, a);
			demoninator2 = demoninator2 + (yi - avgRank) * (yi - avgRank);
		}
		
		double coeff = numerator/Math.sqrt(denominator1 * demoninator2);
		System.out.println("Coeff: " + coeff);
		
		return null;
	}

}
