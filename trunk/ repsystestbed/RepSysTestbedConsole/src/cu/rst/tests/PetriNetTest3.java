/**
 * 
 */
package cu.rst.tests;

import java.io.File;
import java.util.ArrayList;
import java.util.Set;

import org.apache.log4j.BasicConfigurator;

import cu.rst.core.alg.Algorithm;
import cu.rst.core.alg.Appleseed;
import cu.rst.core.alg.EigenTrustv2;
import cu.rst.core.graphs.Agent;
import cu.rst.core.graphs.RG;
import cu.rst.core.graphs.ReputationEdge;
import cu.rst.core.graphs.ReputationEdgeFactory;
import cu.rst.core.petrinet.PetriNet;
import cu.rst.core.petrinet.PetriNetEdgeFactory;
import cu.rst.core.petrinet.Place;
import cu.rst.core.petrinet.Token;
import cu.rst.core.petrinet.Transition;
import cu.rst.util.DotWriter;
import cu.rst.util.ReputationGraphCreator;
import cu.rst.util.Util;

/**
 * @author partheinstein
 *
 * Test for AS and ET comparisons
 */
public class PetriNetTest3 {

	public  static void testTransitivity1(Algorithm alg, String fileName) throws Exception
	{
		Util.assertNotNull(alg);
		Util.assertNotNull(fileName);
		
		PetriNet workflow = new PetriNet(new PetriNetEdgeFactory());
		
		RG rg0 = ReputationGraphCreator.createGraph(fileName);
		RG rg1 = new RG(new ReputationEdgeFactory());
		
		Place rgPlace0 = new Place(rg0);
		Transition algTransition = new Transition(alg);
		algTransition.setWorkflow(workflow);
		Place rgPlace1 = new Place(rg1);
		
		workflow.addEdge(rgPlace0, algTransition, 1);
		workflow.addEdge(algTransition, rgPlace1, 1);
		
		//this is kind of stupid, design flaw? You shouldn't need to re-add the reputation edges.
		ArrayList changes = new ArrayList();
		for(ReputationEdge e : (Set<ReputationEdge>)rg0.edgeSet())
		{
			changes.add(e);
		}
		
		Token t = new Token(changes, rgPlace0);
		rgPlace0.putToken(t, false); //workaround to the stupidity mentioned above, just don't re-add the edges to RG0
		
		algTransition.fire();
		
		String temp = fileName.split("\\\\")[1];
		File f = new File("output\\" + temp + "\\" + alg.getName());
		f.mkdirs();
		String graphVizLocation = "C:\\Program Files (x86)\\Graphviz 2.28\\bin\\dot.exe";
		DotWriter.write2(workflow, "output\\" + temp + "\\" + alg.getName() + "\\wf", graphVizLocation);
		DotWriter.write(rg0, "output\\" + temp + "\\" + alg.getName() + "\\rg0", graphVizLocation);
		DotWriter.write(rg1, "output\\" + temp + "\\" + alg.getName() + "\\rg1", graphVizLocation);
		
		ReputationEdge re0 = (ReputationEdge) rg1.getEdge(new Agent(0), new Agent(1));
		ReputationEdge re1 = (ReputationEdge) rg1.getEdge(new Agent(0), new Agent(2));
		ReputationEdge re2 = (ReputationEdge) rg1.getEdge(new Agent(0), new Agent(3));
		
		System.out.println(re0);
		System.out.println(re1);
		System.out.println(re2);
		
		System.out.println(alg.getName()  + " evaluation result: " + 
					(re0.getReputation()> re1.getReputation() && re0.getReputation()> re2.getReputation() && re1.getReputation()==re2.getReputation()));
		
		/*
		 * EigenTrust:
		 * (Agent 0 , Agent 1 ,0.2884615371928713)
		 * (Agent 0 , Agent 2 ,0.27564102662776696)
		 * (Agent 0 , Agent 3 ,0.27564102662776696)
		 * EigenTrustv20 evaluation result: true
		 * 
		 * AppleSeed:
		 * (Agent 0 , Agent 1 ,0.3644351934895163)
		 * (Agent 0 , Agent 2 ,0.10325109205044)
		 * (Agent 0 , Agent 3 ,0.10325109205044)
		 * Appleseed1 evaluation result: true
		 */
	
	}
	
	public  static void testTransitivity2(Algorithm alg, String fileName) throws Exception
	{
		Util.assertNotNull(alg);
		Util.assertNotNull(fileName);
		
		PetriNet workflow = new PetriNet(new PetriNetEdgeFactory());
		
		RG rg0 = ReputationGraphCreator.createGraph(fileName);
		RG rg1 = new RG(new ReputationEdgeFactory());
		
		Place rgPlace0 = new Place(rg0);
		Transition algTransition = new Transition(alg);
		algTransition.setWorkflow(workflow);
		Place rgPlace1 = new Place(rg1);
		
		workflow.addEdge(rgPlace0, algTransition, 1);
		workflow.addEdge(algTransition, rgPlace1, 1);
		
		//this is kind of stupid, design flaw? You shouldn't need to re-add the reputation edges.
		ArrayList changes = new ArrayList();
		for(ReputationEdge e : (Set<ReputationEdge>)rg0.edgeSet())
		{
			changes.add(e);
		}
		
		Token t = new Token(changes, rgPlace0);
		rgPlace0.putToken(t, false); //workaround to the stupidity mentioned above, just don't re-add the edges to RG0
		
		algTransition.fire();
		
		String temp = fileName.split("\\\\")[1];
		File f = new File("output\\" + temp + "\\" + alg.getName());
		f.mkdirs();
		String graphVizLocation = "C:\\Program Files (x86)\\Graphviz 2.28\\bin\\dot.exe";
		DotWriter.write2(workflow, "output\\" + temp + "\\" + alg.getName() + "\\wf", graphVizLocation);
		DotWriter.write(rg0, "output\\" + temp + "\\" + alg.getName() + "\\rg0", graphVizLocation);
		DotWriter.write(rg1, "output\\" + temp + "\\" + alg.getName() + "\\rg1", graphVizLocation);
		
		ReputationEdge re0 = (ReputationEdge) rg1.getEdge(new Agent(0), new Agent(5));
		ReputationEdge re1 = (ReputationEdge) rg1.getEdge(new Agent(0), new Agent(2));
		ReputationEdge re2 = (ReputationEdge) rg1.getEdge(new Agent(0), new Agent(3));
		
		System.out.println(re0);
		System.out.println(re1);
		System.out.println(re2);
		
		System.out.println(alg.getName()  + " evaluation result: " + 
					(re0.getReputation() > re1.getReputation() && re0.getReputation() > re2.getReputation()));
		
		/*
		 * EigenTrust:
		 * (Agent 0 , Agent 5 ,0.23451327431943314)
		 * (Agent 0 , Agent 2 ,0.17256637167748146)
		 * (Agent 0 , Agent 3 ,0.17256637167748146)
		 * EigenTrustv20 evaluation result: true
		 * 
		 * AppleSeed:
		 * (Agent 0 , Agent 5 ,0.07949077595470613)
		 * (Agent 0 , Agent 2 ,0.05299385063647075)
		 * (Agent 0 , Agent 3 ,0.05299385063647075)
		 * Appleseed1 evaluation result: true
		 */
	
	}
	
	public  static void testTransitivity3(Algorithm alg, String fileName) throws Exception
	{
		Util.assertNotNull(alg);
		Util.assertNotNull(fileName);
		
		PetriNet workflow = new PetriNet(new PetriNetEdgeFactory());
		
		RG rg0 = ReputationGraphCreator.createGraph(fileName);
		RG rg1 = new RG(new ReputationEdgeFactory());
		
		Place rgPlace0 = new Place(rg0);
		Transition algTransition = new Transition(alg);
		algTransition.setWorkflow(workflow);
		Place rgPlace1 = new Place(rg1);
		
		workflow.addEdge(rgPlace0, algTransition, 1);
		workflow.addEdge(algTransition, rgPlace1, 1);
		
		//this is kind of stupid, design flaw? You shouldn't need to re-add the reputation edges.
		ArrayList changes = new ArrayList();
		for(ReputationEdge e : (Set<ReputationEdge>)rg0.edgeSet())
		{
			changes.add(e);
		}
		
		Token t = new Token(changes, rgPlace0);
		rgPlace0.putToken(t, false); //workaround to the stupidity mentioned above, just don't re-add the edges to RG0
		
		algTransition.fire();
		
		String temp = fileName.split("\\\\")[1];
		File f = new File("output\\" + temp + "\\" + alg.getName());
		f.mkdirs();
		String graphVizLocation = "C:\\Program Files (x86)\\Graphviz 2.28\\bin\\dot.exe";
		DotWriter.write2(workflow, "output\\" + temp + "\\" + alg.getName() + "\\wf", graphVizLocation);
		DotWriter.write(rg0, "output\\" + temp + "\\" + alg.getName() + "\\rg0", graphVizLocation);
		DotWriter.write(rg1, "output\\" + temp + "\\" + alg.getName() + "\\rg1", graphVizLocation);
		
		ReputationEdge re0 = (ReputationEdge) rg1.getEdge(new Agent(0), new Agent(3));
		ReputationEdge re1 = (ReputationEdge) rg1.getEdge(new Agent(0), new Agent(2));
		
		System.out.println(re0);
		System.out.println(re1);
		
		System.out.println(alg.getName()  + " evaluation result: " + 
					(re0.getReputation() > re1.getReputation()));
		
		/*
		 * EigenTrust:
		 * (Agent 0 , Agent 3 ,0.4065126051267138)
		 * (Agent 0 , Agent 2 ,0.22584033678238952)
		 * EigenTrustv22 evaluation result: true
		 * 
		 * AppleSeed:
		 * (Agent 0 , Agent 3 ,0.14094392890663812)
		 * (Agent 0 , Agent 2 ,0.09890985164550031)
		 * Appleseed3 evaluation result: true
		 */
	
	}
	
	public static void main(String[] args) throws Exception
	{
		BasicConfigurator.configure();
//		testTransitivity1(new EigenTrustv2(), "input\\exp_transitivity1.arff");
//		testTransitivity1(new Appleseed(), "input\\exp_transitivity1.arff");
//		testTransitivity2(new EigenTrustv2(), "input\\exp_transitivity2.arff");
//		testTransitivity2(new Appleseed(), "input\\exp_transitivity2.arff");
		testTransitivity3(new EigenTrustv2(), "input\\exp_transitivity3.arff");
		testTransitivity3(new Appleseed(), "input\\exp_transitivity3.arff");
	}

}


