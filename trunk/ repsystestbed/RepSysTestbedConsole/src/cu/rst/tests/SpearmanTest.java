package cu.rst.tests;

import java.util.ArrayList;

import cu.rst.core.alg.Spearman;
import cu.rst.core.graphs.Agent;
import cu.rst.core.graphs.RG;
import cu.rst.core.graphs.ReputationEdgeFactory;
import cu.rst.core.petrinet.Place;
import cu.rst.core.petrinet.Token;

public class SpearmanTest {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception 
	{
		RG rg1 = new RG(new ReputationEdgeFactory());
		Agent a0 = new Agent(0);
		Agent a1 = new Agent(1);
		Agent a2 = new Agent(2);
	
		rg1.addVertex(a0);
		rg1.addVertex(a1);
		rg1.addVertex(a2);
		
		rg1.addEdge(a0, a0, 0.7);
		rg1.addEdge(a0, a1, 0.6);
		rg1.addEdge(a0, a2, 0.4);
		rg1.addEdge(a1, a0, 0.7);
		rg1.addEdge(a1, a1, 0.6);
		rg1.addEdge(a1, a2, 0.4);
		rg1.addEdge(a2, a0, 0.7);
		rg1.addEdge(a2, a1, 0.6);
		rg1.addEdge(a2, a2, 0.4);
	
		RG rg2 = new RG(new ReputationEdgeFactory());
	
		rg2.addVertex(a0);
		rg2.addVertex(a1);
		rg2.addVertex(a2);
		
		rg2.addEdge(a0, a0, 0.7);
		rg2.addEdge(a0, a1, 0.6);
		rg2.addEdge(a0, a2, 0.4);
		rg2.addEdge(a1, a0, 0.7);
		rg2.addEdge(a1, a1, 0.6);
		rg2.addEdge(a1, a2, 0.4);
		rg2.addEdge(a2, a0, 0.7);
		rg2.addEdge(a2, a1, 0.6);
		rg2.addEdge(a2, a2, 0.4);
		
		Spearman sp = new Spearman();
		Place p1 = new Place(rg1);
		Place p2 = new Place(rg2);
		Token t1 = new Token(null, p1);
		Token t2 = new Token(null, p2);
		p1.putToken(t1);
		p2.putToken(t2);
		
		ArrayList tokens = new ArrayList();
		tokens.add(t1);
		tokens.add(t2);
		
		sp.update(tokens);
		
		

	}

}
