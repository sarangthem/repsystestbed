/**
 * 
 */
package cu.rst.tests;

import java.io.File;
import java.util.ArrayList;

import org.apache.log4j.BasicConfigurator;

import cu.rst.core.alg.Algorithm;
import cu.rst.core.alg.EigenTrustv2;
import cu.rst.core.alg.SybilAttackEval;
import cu.rst.core.graphs.RG;
import cu.rst.core.graphs.ReputationEdgeFactory;
import cu.rst.core.petrinet.BooleanSink;
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
 * Rep graph:
 * a,b,1
 * b,c,1
 * a,d,1
 * d,e,1
 * Models a Sybil attack and measures the performance of an alg based on how many
 * (c,x,1) and (x,b) edges need to be added in order to cause r(a,b) <= r(a,c)  
 *
 */
public class SybilSlander 
{
	public static void runTest(Algorithm alg, String repGraphFile) throws Exception
	{
		Util.assertNotNull(repGraphFile);
		
		RG rg2 = ReputationGraphCreator.createGraph(repGraphFile); //initially its the same graph as rg0
		RG rg3 = new RG(new ReputationEdgeFactory());
		
		PetriNet workflow = new PetriNet(new PetriNetEdgeFactory());
		
		Place rg2Place = new Place(rg2);
		
		Transition algTransition = new Transition(alg);
		algTransition.setWorkflow(workflow);
		
		Place rg3Place = new Place(rg3);
		
		SybilAttackEval eval = new SybilAttackEval();
		Transition evalTransition = new Transition(eval);
		evalTransition.setWorkflow(workflow);
		
		BooleanSink boolSink = new BooleanSink();
		Place bsPlace = new Place(boolSink);
		
		workflow.addEdge(rg2Place, algTransition, 1);
		workflow.addEdge(algTransition, rg3Place, 1);
		workflow.addEdge(algTransition, rg2Place, 1);
		
		workflow.addEdge(rg3Place, evalTransition, 1);
		workflow.addEdge(evalTransition, rg2Place, 1);
		workflow.addEdge(evalTransition, bsPlace, 1);
		
		
		Token t1 = new Token(new ArrayList(), rg2Place);
		rg2Place.putToken(t1, false);
	
		
		int i=0;
		do
		{
			algTransition.fire2();
//			evalTransition.fire();
			int numRepEdges = rg2Place.numTokens();
			System.out.println("Number of reputation Edges required: " + numRepEdges);
			if(boolSink.getVal()) break;
			i++;
		}while(i<1);
		
		String temp = repGraphFile.split("\\\\")[1];
		File f = new File("output\\" + temp + "\\" + alg.getName());
		f.mkdirs();
		String graphVizLocation = "C:\\Program Files (x86)\\Graphviz 2.28\\bin\\dot.exe";
		DotWriter.write2(workflow, "output\\" + temp + "\\" + alg.getName() + "\\wf", graphVizLocation);
		DotWriter.write(rg2, "output\\" + temp + "\\" + alg.getName() + "\\rg2", graphVizLocation);
		DotWriter.write(rg3, "output\\" + temp + "\\" + alg.getName() + "\\rg3", graphVizLocation);
		
	}

	public static void main(String[] args) throws Exception
	{
		BasicConfigurator.configure();
		runTest(new EigenTrustv2(), "input\\exp_sybilSlander2.arff");
//		runTest(new Appleseed(), "input\\exp_sybilSlander.arff");

	}

}
