package cu.rst.tests;

import cu.rst.core.alg.Appleseed;
import cu.rst.core.graphs.Agent;
import cu.rst.core.graphs.RG;
import cu.rst.core.graphs.ReputationEdgeFactory;

public class AppleseedTest 
{
	public static void test1() throws Exception
	{
		RG rg = new RG(new ReputationEdgeFactory());
		Agent a0 = new Agent(0);
		Agent a1 = new Agent(1);
		Agent a2 = new Agent(2);
		Agent a3 = new Agent(3);
		rg.addVertex(a0);
		rg.addVertex(a1);
		rg.addVertex(a2);
		rg.addVertex(a3);
		rg.addEdge(a0, a1, 1.0);
		rg.addEdge(a1, a2, 1);
		rg.addEdge(a2, a0, 0.25);
		rg.addEdge(a2, a1, 0.25);
		rg.addEdge(a2, a2, 0.25);
		rg.addEdge(a2, a3, 0.25);
		rg.addEdge(a3, a0, 0.25);
		rg.addEdge(a3, a1, 0.25);
		rg.addEdge(a3, a2, 0.25);
		rg.addEdge(a3, a3, 0.25);
		
		Appleseed as = new Appleseed();
		as.calculateTrustScore(a0, a0, rg);
	}
	public static void main(String[] args) throws Exception 
	{
		RG rg = new RG(new ReputationEdgeFactory());
		Agent a0 = new Agent(0);
		Agent a1 = new Agent(1);
		Agent a2 = new Agent(2);
		Agent a3 = new Agent(3);
		rg.addVertex(a0);
		rg.addVertex(a1);
		rg.addVertex(a2);
		rg.addVertex(a3);
		rg.addEdge(a0, a1, 1);
		rg.addEdge(a1, a2, 1);
		rg.addEdge(a1, a3, 1);
		
		Appleseed as = new Appleseed();
		as.calculateTrustScore(a0, a1, rg);
		System.out.println("--");
		as.calculateTrustScore(a0, a2, rg);
		System.out.println("--");
		as.calculateTrustScore(a0, a3, rg);
		System.out.println("--");

//		System.out.println(as.calculateTrustScore(a0, a0, rg));
//		System.out.println(as.calculateTrustScore(a0, a1, rg));
//		System.out.println(as.calculateTrustScore(a0, a2, rg));
//		System.out.println(as.calculateTrustScore(a0, a3, rg));
		
	}
}
