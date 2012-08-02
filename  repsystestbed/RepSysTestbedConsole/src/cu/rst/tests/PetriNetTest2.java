package cu.rst.tests;

import java.io.File;
import java.util.ArrayList;

import org.apache.log4j.BasicConfigurator;

import cu.rst.core.alg.Discretizer;
import cu.rst.core.alg.EigenTrust;
import cu.rst.core.alg.EvalDPv2;
import cu.rst.core.alg.EvalDPv3;
import cu.rst.core.alg.EvalDPv4;
import cu.rst.core.alg.Normalizer;
import cu.rst.core.alg.NullSink;
import cu.rst.core.alg.PeerTrust;
import cu.rst.core.alg.Spearman;
import cu.rst.core.graphs.FHG;
import cu.rst.core.graphs.Feedback;
import cu.rst.core.graphs.FeedbackHistoryEdgeFactory;
import cu.rst.core.graphs.RG;
import cu.rst.core.graphs.ReputationEdgeFactory;
import cu.rst.core.petrinet.PetriNet;
import cu.rst.core.petrinet.PetriNetEdgeFactory;
import cu.rst.core.petrinet.Place;
import cu.rst.core.petrinet.Token;
import cu.rst.core.petrinet.Transition;
import cu.rst.util.DefaultArffFeedbackGenerator;
import cu.rst.util.DotWriter;

public class PetriNetTest2 
{

	public static void testSpearman(String arffFileName) throws Exception
	{
		PetriNet workflow = new PetriNet(new PetriNetEdgeFactory());

		FHG fhg = new FHG(new FeedbackHistoryEdgeFactory());
		Place fhgPlace = new Place(fhg);
		
		Discretizer da = new Discretizer();
		Transition daTransition = new Transition(da);
		daTransition.setWorkflow(workflow);
		
		FHG fhg2 = new FHG(new FeedbackHistoryEdgeFactory());
		Place fhgPlace2 = new Place(fhg2);
		
		EigenTrust et = new EigenTrust();
		Transition etTransition = new Transition(et);
		etTransition.setWorkflow(workflow);
		
		RG rg1 = new RG(new ReputationEdgeFactory());
		Place rgPlace1 = new Place(rg1);

		PeerTrust pt = new PeerTrust();
		Transition ptTransition = new Transition(pt);
		ptTransition.setWorkflow(workflow);
		
		RG rg2 = new RG(new ReputationEdgeFactory());
		Place rgPlace2 = new Place(rg2);
		
		Normalizer na = new Normalizer();
		Transition naTransition = new Transition(na);
		naTransition.setWorkflow(workflow);
		
		RG rg3 = new RG(new ReputationEdgeFactory());
		Place rgPlace3 = new Place(rg3);

		String temp = arffFileName.split("\\\\")[1];
		Spearman sp = new Spearman();
		Transition spTransition = new Transition(sp);
		spTransition.setWorkflow(workflow);
		
		NullSink ns = new NullSink();
		Place flPlace = new Place(ns);
		
		workflow.addEdge(fhgPlace, daTransition, 1);
		workflow.addEdge(daTransition, fhgPlace2, 1);
		workflow.addEdge(fhgPlace2, etTransition, 1);
		workflow.addEdge(etTransition, rgPlace1, 1);
		workflow.addEdge(fhgPlace, ptTransition, 1);
		workflow.addEdge(ptTransition, rgPlace2, 1);
		workflow.addEdge(rgPlace2, naTransition, 1);
		workflow.addEdge(naTransition, rgPlace3, 1);
		workflow.addEdge(rgPlace1, spTransition, 1);
		workflow.addEdge(rgPlace3, spTransition, 1);
		workflow.addEdge(spTransition, flPlace, 1);		
		
		//output the petri net
		File f = new File("output\\" + temp + "\\etpt");
		f.mkdirs();
		String graphVizLocation = "C:\\Program Files (x86)\\Graphviz 2.28\\bin\\dot.exe";
		DotWriter.write2(workflow, "output\\" + temp + "\\etpt\\wf", graphVizLocation);
		
		//parse the feedbacks from the arff file
		DefaultArffFeedbackGenerator feedbackGen = new DefaultArffFeedbackGenerator();
		ArrayList<Feedback> feedbacks = (ArrayList<Feedback>) feedbackGen.generateHardcoded(arffFileName + ".arff");
		
		//put the first token and fire some transitions 
		Token t1 = new Token(feedbacks, fhgPlace);
		fhgPlace.putToken(t1, true);
		daTransition.fire();
		etTransition.fire();
		
		//put the second token and fire some transitions
		Token t2 = new Token(feedbacks, fhgPlace);
		fhgPlace.putToken(t2, false); //fhg already updated with the feedbacks
		ptTransition.fire();
		naTransition.fire();
		
		//should be ready to fire spearman transition
		spTransition.fire();
		
	}
	
	public static void testSpearmanET(String arffFileName1, String arffFileName2) throws Exception
	{
		PetriNet workflow = new PetriNet(new PetriNetEdgeFactory());

		FHG fhg0 = new FHG(new FeedbackHistoryEdgeFactory());
		Place fhgPlace0 = new Place(fhg0);
				
		Discretizer da0 = new Discretizer();
		Transition daTransition0 = new Transition(da0);
		daTransition0.setWorkflow(workflow);
		
		FHG fhg2 = new FHG(new FeedbackHistoryEdgeFactory());
		Place fhgPlace2 = new Place(fhg2);
		
		EigenTrust et0 = new EigenTrust();
		Transition etTransition0 = new Transition(et0);
		etTransition0.setWorkflow(workflow);
		
		RG rg1 = new RG(new ReputationEdgeFactory());
		Place rgPlace1 = new Place(rg1);
		
		FHG fhg1 = new FHG(new FeedbackHistoryEdgeFactory());
		Place fhgPlace1 = new Place(fhg1);
		
		Discretizer da1 = new Discretizer();
		Transition daTransition1 = new Transition(da1);
		daTransition1.setWorkflow(workflow);
		
		FHG fhg3 = new FHG(new FeedbackHistoryEdgeFactory());
		Place fhgPlace3 = new Place(fhg3);
		
		EigenTrust et1 = new EigenTrust();
		Transition etTransition1 = new Transition(et1);
		etTransition1.setWorkflow(workflow);
		
		RG rg2 = new RG(new ReputationEdgeFactory());
		Place rgPlace2 = new Place(rg2);
		

		Spearman sp = new Spearman();
		Transition spTransition = new Transition(sp);
		spTransition.setWorkflow(workflow);
		
		NullSink ns = new NullSink();
		Place flPlace = new Place(ns);
		
		workflow.addEdge(fhgPlace0, daTransition0, 1);
		workflow.addEdge(daTransition0, fhgPlace2, 1);
		workflow.addEdge(fhgPlace2, etTransition0, 1);
		workflow.addEdge(etTransition0, rgPlace1, 1);
		
		workflow.addEdge(fhgPlace1, daTransition1, 1);
		workflow.addEdge(daTransition1, fhgPlace3, 1);
		workflow.addEdge(fhgPlace3, etTransition1, 1);
		workflow.addEdge(etTransition1, rgPlace2, 1);
		
		workflow.addEdge(rgPlace1, spTransition, 1);
		workflow.addEdge(rgPlace2, spTransition, 1);
		workflow.addEdge(spTransition, flPlace, 1);		
		

		
		//parse the feedbacks from the arff file
		DefaultArffFeedbackGenerator feedbackGen = new DefaultArffFeedbackGenerator();
		ArrayList<Feedback> feedbacks1 = (ArrayList<Feedback>) feedbackGen.generateHardcoded(arffFileName1 + ".arff");
		ArrayList<Feedback> feedbacks2 = (ArrayList<Feedback>) feedbackGen.generateHardcoded(arffFileName2 + ".arff");
		
		//put the first token and fire some transitions 
		Token t1 = new Token(feedbacks1, fhgPlace0);
		fhgPlace0.putToken(t1, true);
		daTransition0.fire();
		etTransition0.fire();
		
		//put the second token and fire some transitions
		Token t2 = new Token(feedbacks2, fhgPlace1);
		fhgPlace1.putToken(t2, true);
		daTransition1.fire();
		etTransition1.fire();
		
		//should be ready to fire spearman transition
		spTransition.fire();
		
		//output the petri net
		String temp = arffFileName2.split("\\\\")[1];
		File f = new File("output\\" + temp + "\\etpt");
		f.mkdirs();
		String graphVizLocation = "C:\\Program Files (x86)\\Graphviz 2.28\\bin\\dot.exe";
		DotWriter.write2(workflow, "output\\" + temp + "\\et\\wf", graphVizLocation);
		DotWriter.write(fhg0, "output\\" + temp + "\\et\\fhg2", graphVizLocation);
		DotWriter.write(fhg1, "output\\" + temp + "\\et\\fhg3", graphVizLocation);
		DotWriter.write(rg1, "output\\" + temp + "\\et\\rg1", graphVizLocation);
		DotWriter.write(rg2, "output\\" + temp + "\\et\\rg2", graphVizLocation);
		
		
	}
	
	public static void testSpearmanPT(String arffFileName1, String arffFileName2) throws Exception
	{
		PetriNet workflow = new PetriNet(new PetriNetEdgeFactory());

		FHG fhg0 = new FHG(new FeedbackHistoryEdgeFactory());
		Place fhgPlace0 = new Place(fhg0);
						
		FHG fhg2 = new FHG(new FeedbackHistoryEdgeFactory());
		Place fhgPlace2 = new Place(fhg2);
		
		PeerTrust pt0 = new PeerTrust();
		Transition ptTransition0 = new Transition(pt0);
		ptTransition0.setWorkflow(workflow);
		
		RG rg1 = new RG(new ReputationEdgeFactory());
		Place rgPlace1 = new Place(rg1);
		
		Normalizer na0 = new Normalizer();
		Transition naTransition0 = new Transition(na0);
		naTransition0.setWorkflow(workflow);
		
		FHG fhg1 = new FHG(new FeedbackHistoryEdgeFactory());
		Place fhgPlace1 = new Place(fhg1);
		
		
		
		PeerTrust et1 = new PeerTrust();
		Transition ptTransition1 = new Transition(et1);
		ptTransition1.setWorkflow(workflow);
		
		RG rg2 = new RG(new ReputationEdgeFactory());
		Place rgPlace2 = new Place(rg2);
		
		RG rg3 = new RG(new ReputationEdgeFactory());
		Place rgPlace3 = new Place(rg3);
		
		RG rg4 = new RG(new ReputationEdgeFactory());
		Place rgPlace4 = new Place(rg4);
		
		Normalizer na1 = new Normalizer();
		Transition naTransition1 = new Transition(na1);
		naTransition1.setWorkflow(workflow);
		
		
		

		Spearman sp = new Spearman();
		Transition spTransition = new Transition(sp);
		spTransition.setWorkflow(workflow);
		
		NullSink ns = new NullSink();
		Place flPlace = new Place(ns);
		
		workflow.addEdge(fhgPlace0, ptTransition0, 1);
		workflow.addEdge(ptTransition0, rgPlace1, 1);
		workflow.addEdge(rgPlace1, naTransition0, 1);
		workflow.addEdge(naTransition0, rgPlace2, 1);
		
		
		workflow.addEdge(fhgPlace2, ptTransition1, 1);
		workflow.addEdge(ptTransition1, rgPlace3, 1);
		workflow.addEdge(rgPlace3, naTransition1, 1);
		workflow.addEdge(naTransition1, rgPlace4, 1);
		
		workflow.addEdge(rgPlace2, spTransition, 1);
		workflow.addEdge(rgPlace4, spTransition, 1);
		workflow.addEdge(spTransition, flPlace, 1);		
		

		
		//parse the feedbacks from the arff file
		DefaultArffFeedbackGenerator feedbackGen = new DefaultArffFeedbackGenerator();
		ArrayList<Feedback> feedbacks1 = (ArrayList<Feedback>) feedbackGen.generateHardcoded(arffFileName1 + ".arff");
		ArrayList<Feedback> feedbacks2 = (ArrayList<Feedback>) feedbackGen.generateHardcoded(arffFileName2 + ".arff");
		
		//put the first token and fire some transitions 
		Token t1 = new Token(feedbacks1, fhgPlace0);
		fhgPlace0.putToken(t1, true);
		ptTransition0.fire();
		naTransition0.fire();
		
		//put the second token and fire some transitions
		Token t2 = new Token(feedbacks2, fhgPlace2);
		fhgPlace2.putToken(t2, true);
		ptTransition1.fire();
		naTransition1.fire();
		
		//should be ready to fire spearman transition
		spTransition.fire();
		
		//output the petri net
		String temp = arffFileName2.split("\\\\")[1];
		File f = new File("output\\" + temp + "\\pt");
		f.mkdirs();
		String graphVizLocation = "C:\\Program Files (x86)\\Graphviz 2.28\\bin\\dot.exe";
		DotWriter.write2(workflow, "output\\" + temp + "\\pt\\wf", graphVizLocation);
		DotWriter.write(fhg0, "output\\" + temp + "\\pt\\fhg0", graphVizLocation);
		DotWriter.write(fhg2, "output\\" + temp + "\\pt\\fhg2", graphVizLocation);
		DotWriter.write(rg2, "output\\" + temp + "\\pt\\rg2", graphVizLocation);
		DotWriter.write(rg4, "output\\" + temp + "\\pt\\rg4", graphVizLocation);
		
		
	}
	
	/**
	 * For testing version of evaldp which takes only a feedback window of 1
	 * @param arffFileName1
	 * @throws Exception
	 */
	public static void testEvalDPPT(String arffFileName1) throws Exception
	{
		PetriNet workflow = new PetriNet(new PetriNetEdgeFactory());
		
		FHG fhg0 = new FHG(new FeedbackHistoryEdgeFactory());
		FHG fhg1 = new FHG(new FeedbackHistoryEdgeFactory());
		
		Place fhgPlace0 = new Place(fhg0);
		Place fhgPlace1 = new Place(fhg1);
		
		PeerTrust pt0 = new PeerTrust();
		Transition ptTransition0 = new Transition(pt0);
		ptTransition0.setWorkflow(workflow);
		
		PeerTrust pt1 = new PeerTrust();
		Transition ptTransition1 = new Transition(pt1);
		ptTransition1.setWorkflow(workflow);

		RG rg0 = new RG(new ReputationEdgeFactory());
		RG rg1 = new RG(new ReputationEdgeFactory());
		
		Place rgPlace0 = new Place(rg0);
		Place rgPlace1 = new Place(rg1);
		
		EvalDPv2 edp = new EvalDPv2();
		Transition edpTransition = new Transition(edp);
		edpTransition.setWorkflow(workflow);
		
		NullSink  ns = new NullSink();
		Place flPlace = new Place(ns);
		
		workflow.addEdge(fhgPlace0, ptTransition0, 1);
		workflow.addEdge(ptTransition0, rgPlace0, 1);
		workflow.addEdge(ptTransition0, fhgPlace0, 1);
		
		workflow.addEdge(fhgPlace1, ptTransition1, 1);
		workflow.addEdge(ptTransition1, rgPlace1, 1);
		workflow.addEdge(ptTransition1, fhgPlace1, 1);
		
		workflow.addEdge(fhgPlace0, edpTransition, 1);
		workflow.addEdge(fhgPlace1, edpTransition, 1);
		workflow.addEdge(rgPlace0, edpTransition, 1);
		workflow.addEdge(rgPlace1, edpTransition, 1);
		workflow.addEdge(edpTransition, flPlace, 1);
		

		
		//parse the feedbacks from the arff file
		DefaultArffFeedbackGenerator feedbackGen = new DefaultArffFeedbackGenerator();
		ArrayList<Feedback> feedbacks1 = (ArrayList<Feedback>) feedbackGen.generateHardcoded(arffFileName1 + ".arff");		
		
		for(int i=0; i<feedbacks1.size()-1;i++)
		{
			System.out.println(i);
			ArrayList changes = new ArrayList();
			
			//add the first feedback to a token and fire first transition
			changes.add(feedbacks1.get(i)); 
			Token t1 = new Token(changes, fhgPlace0);
			fhgPlace0.putToken(t1, true);
			System.out.println("Invoking PeerTrust0");
			ptTransition0.fire();
			
			//add the second feedback to another token and fire second transition 
			changes = new ArrayList();
			if(i==0) changes.add(feedbacks1.get(i));
			changes.add(feedbacks1.get(i+1));
			t1 = new Token(changes, fhgPlace1);
			fhgPlace1.putToken(t1, true);
			System.out.println("Invoking PeerTrust1");
			ptTransition1.fire();
			
			System.out.println("Invoking evaldp");
			edpTransition.fire();
//			
//			//add the second feedback to a token and fire the first transition
//			//so that fhg0 is updated
//			changes = new ArrayList();
//			changes.add(feedbacks1.get(i+1));
//			t1 = new Token(changes, fhgPlace0, Token.TokenColour.FHG);
//			fhgPlace0.putToken(t1, true);
//			System.out.println("Invoking PeerTrust0");
//			ptTransition0.fire();
//			
//			fhgPlace0.deleteAllTokens();
//			rgPlace0.deleteAllTokens();
			
			//output the petri net
			String temp = arffFileName1.split("\\\\")[1];
			File f = new File("output\\" + temp + "\\pt");
			f.mkdirs();
			String graphVizLocation = "C:\\Program Files (x86)\\Graphviz 2.28\\bin\\dot.exe";
			DotWriter.write(rg0, "output\\" + temp + "\\pt\\rg0_" + i, graphVizLocation);
			DotWriter.write(rg1, "output\\" + temp + "\\pt\\rg1_" + (i+1), graphVizLocation);
			
		}
		
	}
	
	/**
	 * For testing version of evaldp which takes feedback window >= 1
	 * @param arffFileName1
	 * @throws Exception
	 */
	public static void testEvalDPPT2(String arffFileName1) throws Exception
	{
		PetriNet workflow = new PetriNet(new PetriNetEdgeFactory());
		
		FHG fhg0 = new FHG(new FeedbackHistoryEdgeFactory());
		FHG fhg1 = new FHG(new FeedbackHistoryEdgeFactory());
		FHG fhg2 = new FHG(new FeedbackHistoryEdgeFactory());
		
		Place fhgPlace0 = new Place(fhg0);
		Place fhgPlace1 = new Place(fhg1);
		Place fhgPlace2 = new Place(fhg2);
		
		PeerTrust pt0 = new PeerTrust();
		Transition ptTransition0 = new Transition(pt0);
		ptTransition0.setWorkflow(workflow);
		
		PeerTrust pt1 = new PeerTrust();
		Transition ptTransition1 = new Transition(pt1);
		ptTransition1.setWorkflow(workflow);

		PeerTrust pt2 = new PeerTrust();
		Transition ptTransition2 = new Transition(pt2);
		ptTransition2.setWorkflow(workflow);
		
		RG rg0 = new RG(new ReputationEdgeFactory());
		RG rg1 = new RG(new ReputationEdgeFactory());
		RG rg2 = new RG(new ReputationEdgeFactory());
		
		Place rgPlace0 = new Place(rg0);
		Place rgPlace1 = new Place(rg1);
		Place rgPlace2 = new Place(rg2);
		
		EvalDPv2 edp = new EvalDPv2();
		Transition edpTransition = new Transition(edp);
		edpTransition.setWorkflow(workflow);
		
		NullSink ns = new NullSink();
		Place flPlace = new Place(ns);
		
		workflow.addEdge(fhgPlace0, ptTransition0, 1);
		workflow.addEdge(ptTransition0, rgPlace0, 1);
		workflow.addEdge(ptTransition0, fhgPlace0, 1);
		
		workflow.addEdge(fhgPlace1, ptTransition1, 1);
		workflow.addEdge(ptTransition1, rgPlace1, 1);
		workflow.addEdge(ptTransition1, fhgPlace1, 1);
		
		workflow.addEdge(fhgPlace2, ptTransition2, 1);
		workflow.addEdge(ptTransition2, rgPlace2, 1);
		workflow.addEdge(ptTransition2, fhgPlace2, 1);
		
		workflow.addEdge(fhgPlace0, edpTransition, 1);
		workflow.addEdge(fhgPlace1, edpTransition, 1);
		workflow.addEdge(fhgPlace2, edpTransition, 1);
		workflow.addEdge(rgPlace0, edpTransition, 1);
		workflow.addEdge(rgPlace1, edpTransition, 1);
		workflow.addEdge(rgPlace2, edpTransition, 1);
		workflow.addEdge(edpTransition, flPlace, 1);
		
		
		//parse the feedbacks from the arff file
		DefaultArffFeedbackGenerator feedbackGen = new DefaultArffFeedbackGenerator();
		ArrayList<Feedback> feedbacks1 = (ArrayList<Feedback>) feedbackGen.generateHardcoded(arffFileName1 + ".arff");		
		
		for(int i=0; i<feedbacks1.size()-2;i++)
		{
			System.out.println(i);
			ArrayList changes = new ArrayList();
			
			
			changes.add(feedbacks1.get(i)); 
			Token t1 = new Token(changes, fhgPlace0);
			fhgPlace0.putToken(t1, true);
			System.out.println("Invoking PeerTrust0");
			ptTransition0.fire();
			
			changes = new ArrayList();
			if(i==0) changes.add(feedbacks1.get(i));
			changes.add(feedbacks1.get(i+1));
			t1 = new Token(changes, fhgPlace1);
			fhgPlace1.putToken(t1, true);
			System.out.println("Invoking PeerTrust1");
			ptTransition1.fire();
			
			changes = new ArrayList();
			if(i==0)
			{
				changes.add(feedbacks1.get(i));
				changes.add(feedbacks1.get(i+1));
			}
			changes.add(feedbacks1.get(i+2));
			t1 = new Token(changes, fhgPlace2);
			fhgPlace2.putToken(t1, true);
			System.out.println("Invoking PeerTrust1");
			ptTransition2.fire();
			
			
			System.out.println("Invoking evaldp");
			edpTransition.fire();
		}
		
	}
	
	/**
	 * For testing version of evaldp which takes feedback window >= 1
	 * @param arffFileName1
	 * @throws Exception
	 */
	public static void testEvalDPPT3(String arffFileName1) throws Exception
	{
		PetriNet workflow = new PetriNet(new PetriNetEdgeFactory());
		
		FHG fhg0 = new FHG(new FeedbackHistoryEdgeFactory());
		FHG fhg1 = new FHG(new FeedbackHistoryEdgeFactory());
		
		Place fhgPlace0 = new Place(fhg0);
		Place fhgPlace1 = new Place(fhg1);
		
		PeerTrust pt0 = new PeerTrust();
		Transition ptTransition0 = new Transition(pt0);
		ptTransition0.setWorkflow(workflow);
		
		PeerTrust pt1 = new PeerTrust();
		Transition ptTransition1 = new Transition(pt1);
		ptTransition1.setWorkflow(workflow);
		
		RG rg0 = new RG(new ReputationEdgeFactory());
		RG rg1 = new RG(new ReputationEdgeFactory());
		
		Place rgPlace0 = new Place(rg0);
		Place rgPlace1 = new Place(rg1);
		
		EvalDPv3 edp = new EvalDPv3();
		Transition edpTransition = new Transition(edp);
		edpTransition.setWorkflow(workflow);
		
		NullSink ns = new NullSink();
		Place flPlace = new Place(ns);
		
		workflow.addEdge(fhgPlace0, ptTransition0, 1);
		workflow.addEdge(ptTransition0, rgPlace0, 1);
		workflow.addEdge(ptTransition0, fhgPlace0, 1);
		
		workflow.addEdge(fhgPlace1, ptTransition1, 1);
		workflow.addEdge(ptTransition1, rgPlace1, 1);
		workflow.addEdge(ptTransition1, fhgPlace1, 1);
		
		workflow.addEdge(fhgPlace1, edpTransition, 1);
		workflow.addEdge(rgPlace0, edpTransition, 1);
		workflow.addEdge(rgPlace1, edpTransition, 1);
		workflow.addEdge(edpTransition, flPlace, 1);
		
		
		//parse the feedbacks from the arff file
		DefaultArffFeedbackGenerator feedbackGen = new DefaultArffFeedbackGenerator();
		ArrayList<Feedback> feedbacks1 = (ArrayList<Feedback>) feedbackGen.generateHardcoded(arffFileName1 + ".arff");		
		
		for(int i=0; i<feedbacks1.size()-1;i++)
		{
			System.out.println(i);
			ArrayList changes = new ArrayList();
			
			
			changes.add(feedbacks1.get(i)); 
			Token t1 = new Token(changes, fhgPlace0);
			fhgPlace0.putToken(t1, true);
			System.out.println("Invoking PeerTrust0");
			ptTransition0.fire();
			
			changes = new ArrayList();
			if(i==0) changes.add(feedbacks1.get(i));
			changes.add(feedbacks1.get(i+1));
			t1 = new Token(changes, fhgPlace1);
			fhgPlace1.putToken(t1, true);
			System.out.println("Invoking PeerTrust1");
			ptTransition1.fire();
						
			System.out.println("Invoking evaldp");
			edpTransition.fire();
		}
		
	}
	
	/**
	 * For testing version of evaldp which takes feedback window >= 1
	 * @param arffFileName1
	 * @throws Exception
	 */
	public static void testEvalDPPT4(String arffFileName1) throws Exception
	{
		PetriNet workflow = new PetriNet(new PetriNetEdgeFactory());
		
		FHG fhg0 = new FHG(new FeedbackHistoryEdgeFactory());
		FHG fhg1 = new FHG(new FeedbackHistoryEdgeFactory());
		FHG fhg2 = new FHG(new FeedbackHistoryEdgeFactory());
		
		Place fhgPlace0 = new Place(fhg0);
		Place fhgPlace1 = new Place(fhg1);
		Place fhgPlace2 = new Place(fhg2);
		
		PeerTrust pt0 = new PeerTrust();
		Transition ptTransition0 = new Transition(pt0);
		ptTransition0.setWorkflow(workflow);
		
		PeerTrust pt1 = new PeerTrust();
		Transition ptTransition1 = new Transition(pt1);
		ptTransition1.setWorkflow(workflow);

		PeerTrust pt2 = new PeerTrust();
		Transition ptTransition2 = new Transition(pt2);
		ptTransition2.setWorkflow(workflow);
		
		RG rg0 = new RG(new ReputationEdgeFactory());
		RG rg1 = new RG(new ReputationEdgeFactory());
		RG rg2 = new RG(new ReputationEdgeFactory());
		
		Place rgPlace0 = new Place(rg0);
		Place rgPlace1 = new Place(rg1);
		Place rgPlace2 = new Place(rg2);
		
		EvalDPv4 edp = new EvalDPv4();
		Transition edpTransition = new Transition(edp);
		edpTransition.setWorkflow(workflow);
		
		NullSink ns = new NullSink();
		Place flPlace = new Place(ns);
		
		workflow.addEdge(fhgPlace0, ptTransition0, 1);
		workflow.addEdge(ptTransition0, rgPlace0, 1);
		workflow.addEdge(ptTransition0, fhgPlace0, 1);
		
		workflow.addEdge(fhgPlace1, ptTransition1, 1);
		workflow.addEdge(ptTransition1, rgPlace1, 1);
		workflow.addEdge(ptTransition1, fhgPlace1, 1);
		
		workflow.addEdge(fhgPlace2, ptTransition2, 1);
		workflow.addEdge(ptTransition2, rgPlace2, 1);
		workflow.addEdge(ptTransition2, fhgPlace2, 1);
		
		workflow.addEdge(fhgPlace0, edpTransition, 1);
		workflow.addEdge(fhgPlace1, edpTransition, 1);
		workflow.addEdge(fhgPlace2, edpTransition, 1);
		workflow.addEdge(rgPlace0, edpTransition, 1);
		workflow.addEdge(rgPlace1, edpTransition, 1);
		workflow.addEdge(rgPlace2, edpTransition, 1);
		workflow.addEdge(edpTransition, flPlace, 1);
		
		
		//parse the feedbacks from the arff file
		DefaultArffFeedbackGenerator feedbackGen = new DefaultArffFeedbackGenerator();
		ArrayList<Feedback> feedbacks1 = (ArrayList<Feedback>) feedbackGen.generateHardcoded(arffFileName1 + ".arff");		
		
		for(int i=0; i<feedbacks1.size()-2;i++)
		{
			System.out.println(i);
			ArrayList changes = new ArrayList();
			
			
			changes.add(feedbacks1.get(i)); 
			Token t1 = new Token(changes, fhgPlace0);
			fhgPlace0.putToken(t1, true);
			System.out.println("Invoking PeerTrust0");
			ptTransition0.fire();
			
			changes = new ArrayList();
			if(i==0) changes.add(feedbacks1.get(i));
			changes.add(feedbacks1.get(i+1));
			t1 = new Token(changes, fhgPlace1);
			fhgPlace1.putToken(t1, true);
			System.out.println("Invoking PeerTrust1");
			ptTransition1.fire();
			
			changes = new ArrayList();
			if(i==0)
			{
				changes.add(feedbacks1.get(i));
				changes.add(feedbacks1.get(i+1));
			}
			changes.add(feedbacks1.get(i+2));
			t1 = new Token(changes, fhgPlace2);
			fhgPlace2.putToken(t1, true);
			System.out.println("Invoking PeerTrust1");
			ptTransition2.fire();
			
			
			System.out.println("Invoking evaldp");
			edpTransition.fire();
		}
		
	}
	
	public static void main(String[] args) throws Exception
	{
		BasicConfigurator.configure();
//		testSpearman("input\\exp1a_negfeedback");
//		testSpearmanET("input\\exp1a_negfeedback",  "input\\exp1e_ripple");
//		testSpearmanPT("input\\exp1a_negfeedback",  "input\\exp1e_ripple");
//		testEvalDPPT("input\\exp2d_dp1");
//		testEvalDPPT2("input\\exp2d_dp1");
//		testEvalDPPT3("input\\exp2d_dp3");
		testEvalDPPT("input\\exp2d_dp5");
	}

}
