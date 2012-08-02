package cu.rst.tests;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import org.apache.log4j.BasicConfigurator;

import cu.rst.core.alg.Algorithm;
import cu.rst.core.alg.Discretizer;
import cu.rst.core.alg.ETCustomPretrusted;
import cu.rst.core.alg.ETNoPretrusted;
import cu.rst.core.alg.EigenTrust;
import cu.rst.core.alg.FileManager;
import cu.rst.core.alg.ManagingTrust;
import cu.rst.core.alg.Normalizer;
import cu.rst.core.alg.PeerTrust;
import cu.rst.core.alg.NullSink;
import cu.rst.core.alg.Spearman;
import cu.rst.core.alg.EvalDP;
import cu.rst.core.graphs.Feedback;
import cu.rst.core.graphs.Agent;
import cu.rst.core.graphs.FeedbackHistoryEdgeFactory;
import cu.rst.core.graphs.FHG;
import cu.rst.core.graphs.Graph;
import cu.rst.core.graphs.ReputationEdgeFactory;
import cu.rst.core.graphs.RG;
import cu.rst.core.petrinet.PetriNet;
import cu.rst.core.petrinet.PetriNetEdgeFactory;
import cu.rst.core.petrinet.Place;
import cu.rst.core.petrinet.Token;
import cu.rst.core.petrinet.Transition;
import cu.rst.util.DefaultArffFeedbackGenerator;
import cu.rst.util.DotWriter;

public class PetriNetTest {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception 
	{
		BasicConfigurator.configure();
//		String inputFile = "input\\exp2a_apriori_et";
//		
//		testETNoPretrusted(inputFile);
//		Graph.resetCounter();
//		Algorithm.resetCounter();
//		
//		testET(inputFile);
//		Graph.resetCounter();
//		Algorithm.resetCounter();
//		
//		testETCustomPretrusted(inputFile);
//		Graph.resetCounter();
//		Algorithm.resetCounter();
//		
//		inputFile = "input\\exp1a_negfeedback";
//		testET(inputFile);
//		Graph.resetCounter();
//		Algorithm.resetCounter();
//		
//		testPT(inputFile);
//		Graph.resetCounter();
//		Algorithm.resetCounter();
//		
//		testETPT(inputFile);
//		Graph.resetCounter();
//		Algorithm.resetCounter();
//		
//		inputFile = "input\\exp1b_bootstrap";
//		testET(inputFile);
//		Graph.resetCounter();
//		Algorithm.resetCounter();
//		
//		inputFile = "input\\exp1c_slandering";
//		testET(inputFile);
//		Graph.resetCounter();
//		Algorithm.resetCounter();
//		
//		inputFile = "input\\exp1d_slandering";
//		testET(inputFile);
//		Graph.resetCounter();
//		Algorithm.resetCounter();
//		
//		testPT(inputFile);
//		Graph.resetCounter();
//		Algorithm.resetCounter();
//		
//		inputFile = "input\\exp1e_ripple";
//		testET(inputFile);
//		Graph.resetCounter();
//		Algorithm.resetCounter();
//		
//		testPT(inputFile);
//		Graph.resetCounter();
//		Algorithm.resetCounter();
		
//		testPT3("\\samplePT");
		
//		testETNoPretrusted(inputFile);
//		testPT(inputFile);
//		testETCustomPretrusted(inputFile);

		try
		{
			testETPT2("input\\exp1a_negfeedback");
//			testETPT("input\\exp1b");
//			testETPT("input\\exp1c");
//			testETPT("input\\exp1d");
//			testETPT("input\\exp1e");
//			testETPT("input\\exp2a");
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
//		testETPT("input\\exp2c");
		
		//testMT();
		
//		testETPT2("input\\spearman");
//		testPT2("input\\evaldp");
	}
	
	public static void testETNoPretrusted(String arffFileName) throws Exception
	{
		PetriNet workflow = new PetriNet(new PetriNetEdgeFactory());
		
		//create a feedback history graph
		FHG fhg1 = new FHG(new FeedbackHistoryEdgeFactory());
		Place fhgPlace1 = new Place(fhg1);
		
		Discretizer da = new Discretizer();
		Transition daTransition = new Transition(da);
		daTransition.setWorkflow(workflow);
		
		FHG fhg2 = new FHG(new FeedbackHistoryEdgeFactory());
		Place fhgPlace2 = new Place(fhg2);
		
		ETNoPretrusted et = new ETNoPretrusted();
		Transition etTransition = new Transition(et);
		etTransition.setWorkflow(workflow);
		
		RG rg = new RG(new ReputationEdgeFactory());
		Place rgPlace = new Place(rg);
		
		workflow.addEdge(fhgPlace1, daTransition, 1);
		workflow.addEdge(daTransition, fhgPlace2, 1);
		workflow.addEdge(fhgPlace2, etTransition, 1);
		workflow.addEdge(etTransition, rgPlace, 1);
		
		//parse the feedbacks from the arff file
		DefaultArffFeedbackGenerator feedbackGen = new DefaultArffFeedbackGenerator();
		ArrayList<Feedback> feedbacks = (ArrayList<Feedback>) feedbackGen.generateHardcoded(arffFileName + ".arff");
	
		Token t = new Token(feedbacks, fhgPlace1);
		fhgPlace1.putToken(t);
		
		workflow.traverse(fhgPlace1);
		
//		feedbacks = new ArrayList<Feedback>();
//		feedbacks.add(new Feedback(new Agent(0), new Agent(2), 0.1));
//		Token t2 = new Token(feedbacks, fhgPlace1);
//		fhgPlace1.putToken(t2);
//		
//		workflow.traverse(fhgPlace1);
		
		String temp = arffFileName.split("\\\\")[1];
		File f = new File("output\\" + temp + "\\etnopretrusted");
		f.mkdirs();
		String graphVizLocation = "C:\\Program Files (x86)\\Graphviz 2.28\\bin\\dot.exe";
		DotWriter.write2(workflow, "output\\" + temp + "\\etnopretrusted\\wf", graphVizLocation);
		DotWriter.write(fhg1, "output\\" + temp + "\\etnopretrusted\\" + fhg1.getName(), graphVizLocation);
		DotWriter.write(fhg2, "output\\" + temp + "\\etnopretrusted\\" + fhg2.getName(), graphVizLocation);
		DotWriter.write(rg, "output\\" + temp + "\\etnopretrusted\\" + rg.getName(), graphVizLocation);

	}
	
	public static void testETCustomPretrusted(String arffFileName) throws Exception
	{
		PetriNet workflow = new PetriNet(new PetriNetEdgeFactory());
		
		//create a feedback history graph
		FHG fhg1 = new FHG(new FeedbackHistoryEdgeFactory());
		Place fhgPlace1 = new Place(fhg1);
		
		Discretizer da = new Discretizer();
		Transition daTransition = new Transition(da);
		daTransition.setWorkflow(workflow);
		
		FHG fhg2 = new FHG(new FeedbackHistoryEdgeFactory());
		Place fhgPlace2 = new Place(fhg2);
		
		ETCustomPretrusted et = new ETCustomPretrusted();
		Transition etTransition = new Transition(et);
		etTransition.setWorkflow(workflow);
		
		RG rg = new RG(new ReputationEdgeFactory());
		Place rgPlace = new Place(rg);
		
		workflow.addEdge(fhgPlace1, daTransition, 1);
		workflow.addEdge(daTransition, fhgPlace2, 1);
		workflow.addEdge(fhgPlace2, etTransition, 1);
		workflow.addEdge(etTransition, rgPlace, 1);
		
		//parse the feedbacks from the arff file
		DefaultArffFeedbackGenerator feedbackGen = new DefaultArffFeedbackGenerator();
		ArrayList<Feedback> feedbacks = (ArrayList<Feedback>) feedbackGen.generateHardcoded(arffFileName + ".arff");
	
		Token t = new Token(feedbacks, fhgPlace1);
		fhgPlace1.putToken(t);
		
		workflow.traverse(fhgPlace1);
		
//		feedbacks = new ArrayList<Feedback>();
//		feedbacks.add(new Feedback(new Agent(0), new Agent(2), 0.1));
//		Token t2 = new Token(feedbacks, fhgPlace1);
//		fhgPlace1.putToken(t2);
//		
//		workflow.traverse(fhgPlace1);
		
		String temp = arffFileName.split("\\\\")[1];
		File f = new File("output\\" + temp + "\\etcustompretrusted");
		f.mkdirs();
		String graphVizLocation = "C:\\Program Files (x86)\\Graphviz 2.28\\bin\\dot.exe";
		DotWriter.write2(workflow, "output\\" + temp + "\\etcustompretrusted\\wf", graphVizLocation);
		DotWriter.write(fhg1, "output\\" + temp + "\\etcustompretrusted\\" + fhg1.getName(), graphVizLocation);
		DotWriter.write(fhg2, "output\\" + temp + "\\etcustompretrusted\\" + fhg2.getName(), graphVizLocation);
		DotWriter.write(rg, "output\\" + temp + "\\etcustompretrusted\\" + rg.getName(), graphVizLocation);

	}
	
	public static void testET(String arffFileName) throws Exception
	{
		PetriNet workflow = new PetriNet(new PetriNetEdgeFactory());
		
		//create a feedback history graph
		FHG fhg1 = new FHG(new FeedbackHistoryEdgeFactory());
		Place fhgPlace1 = new Place(fhg1);
		
		Discretizer da = new Discretizer();
		Transition daTransition = new Transition(da);
		daTransition.setWorkflow(workflow);
		
		FHG fhg2 = new FHG(new FeedbackHistoryEdgeFactory());
		Place fhgPlace2 = new Place(fhg2);
		
		EigenTrust et = new EigenTrust();
		Transition etTransition = new Transition(et);
		etTransition.setWorkflow(workflow);
		
		RG rg = new RG(new ReputationEdgeFactory());
		Place rgPlace = new Place(rg);
		
		workflow.addEdge(fhgPlace1, daTransition, 1);
		workflow.addEdge(daTransition, fhgPlace2, 1);
		workflow.addEdge(fhgPlace2, etTransition, 1);
		workflow.addEdge(etTransition, rgPlace, 1);
		
		//parse the feedbacks from the arff file
		DefaultArffFeedbackGenerator feedbackGen = new DefaultArffFeedbackGenerator();
		ArrayList<Feedback> feedbacks = (ArrayList<Feedback>) feedbackGen.generateHardcoded(arffFileName + ".arff");
	
		Token t = new Token(feedbacks, fhgPlace1);
		fhgPlace1.putToken(t);
		
		workflow.traverse(fhgPlace1);
		
//		feedbacks = new ArrayList<Feedback>();
//		feedbacks.add(new Feedback(new Agent(0), new Agent(2), 0.1));
//		Token t2 = new Token(feedbacks, fhgPlace1);
//		fhgPlace1.putToken(t2);
//		
//		workflow.traverse(fhgPlace1);
		
		String temp = arffFileName.split("\\\\")[1];
		File f = new File("output\\" + temp + "\\et");
		f.mkdirs();
		String graphVizLocation = "C:\\Program Files (x86)\\Graphviz 2.28\\bin\\dot.exe";
		DotWriter.write2(workflow, "output\\" + temp + "\\et\\wf", graphVizLocation);
		DotWriter.write(fhg1, "output\\" + temp + "\\et\\" + fhg1.getName(), graphVizLocation);
		DotWriter.write(fhg2, "output\\" + temp + "\\et\\" + fhg2.getName(), graphVizLocation);
		DotWriter.write(rg, "output\\" + temp + "\\et\\" + rg.getName(), graphVizLocation);

	}
	
	
	public static void testPT(String arffFileName) throws Exception
	{
		PetriNet workflow = new PetriNet(new PetriNetEdgeFactory());
		
		//create a feedback history graph
		FHG fhg = new FHG(new FeedbackHistoryEdgeFactory());
		Place fhgPlace = new Place(fhg);
		
		PeerTrust pt = new PeerTrust();
		Transition ptTransition = new Transition(pt);
		ptTransition.setWorkflow(workflow);
		
		RG rg1 = new RG(new ReputationEdgeFactory());
		Place rgPlace1 = new Place(rg1);
		
		Normalizer na = new Normalizer();
		Transition naTransition = new Transition(na);
		naTransition.setWorkflow(workflow);
		
		RG rg2 = new RG(new ReputationEdgeFactory());
		Place rgPlace2 = new Place(rg2);
		
		workflow.addEdge(fhgPlace, ptTransition, 1);
		workflow.addEdge(ptTransition, rgPlace1, 1);
		workflow.addEdge(rgPlace1, naTransition, 1);
		workflow.addEdge(naTransition, rgPlace2, 1);
		
		
		//parse the feedbacks from the arff file
		DefaultArffFeedbackGenerator feedbackGen = new DefaultArffFeedbackGenerator();
		ArrayList<Feedback> feedbacks = (ArrayList<Feedback>) feedbackGen.generateHardcoded(arffFileName + ".arff");
	
		Token t = new Token(feedbacks, fhgPlace);
		fhgPlace.putToken(t);
		
		workflow.traverse(fhgPlace);
		
//		feedbacks = new ArrayList<Feedback>();
//		feedbacks.add(new Feedback(new Agent(0), new Agent(2), 0.1));
//		Token t2 = new Token(feedbacks, fhgPlace);
//		fhgPlace.putToken(t2);
//		
//		workflow.traverse(fhgPlace);
		
		String temp = arffFileName.split("\\\\")[1];
		File f = new File("output\\" + temp + "\\pt");
		f.mkdirs();
		String graphVizLocation = "C:\\Program Files (x86)\\Graphviz 2.28\\bin\\dot.exe";
		DotWriter.write2(workflow, "output\\" + temp + "\\pt\\wf", graphVizLocation);
		DotWriter.write(fhg, "output\\" + temp + "\\pt\\" + fhg.getName() , graphVizLocation);
		DotWriter.write(rg1, "output\\" + temp + "\\pt\\" + rg1.getName(), graphVizLocation);
		DotWriter.write(rg2, "output\\" + temp + "\\pt\\" + rg2.getName(), graphVizLocation);
	}
	
	public static void testETPT(String arffFileName) throws Exception
	{
		PetriNet workflow = new PetriNet(new PetriNetEdgeFactory());
		
		//create a feedback history graph
		FHG fhg1_0 = new FHG(new FeedbackHistoryEdgeFactory());
		Place fhgPlace1_0 = new Place(fhg1_0);
		FHG fhg1_1 = new FHG(new FeedbackHistoryEdgeFactory());
		Place fhgPlace1_1 = new Place(fhg1_1);
		
		Discretizer da = new Discretizer();
		Transition daTransition = new Transition(da);
		daTransition.setWorkflow(workflow);
		
		FHG fhg2 = new FHG(new FeedbackHistoryEdgeFactory());
		Place fhgPlace2 = new Place(fhg2);
		
//		ETNoPretrusted et = new ETNoPretrusted();
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
//		FileManager fm = new FileManager("output\\" + temp + "_");
//		Transition fmTransition = new Transition(fm);
//		fmTransition.setWorkflow(workflow);
		
//		NullSink strSink = new NullSink();
//		Place strSinkPlace = new Place(strSink);
		
		workflow.addEdge(fhgPlace1_0, daTransition, 1);
		workflow.addEdge(daTransition, fhgPlace2, 1);
		workflow.addEdge(fhgPlace2, etTransition, 1);
		workflow.addEdge(etTransition, rgPlace1, 1);
		workflow.addEdge(fhgPlace1_1, ptTransition, 1);
		workflow.addEdge(ptTransition, rgPlace2, 1);
		workflow.addEdge(rgPlace2, naTransition, 1);
		workflow.addEdge(naTransition, rgPlace3, 1);
//		workflow.addEdge(rgPlace1, fmTransition, 1);
//		workflow.addEdge(rgPlace3, fmTransition, 1);
//		workflow.addEdge(fmTransition, strSinkPlace, 2);
		
		//parse the feedbacks from the arff file
		DefaultArffFeedbackGenerator feedbackGen = new DefaultArffFeedbackGenerator();
		ArrayList<Feedback> feedbacks = (ArrayList<Feedback>) feedbackGen.generateHardcoded(arffFileName + ".arff");
		
		Token t1 = new Token(feedbacks, fhgPlace1_0);
		Token t2 = new Token(feedbacks, fhgPlace1_1); 
		fhgPlace1_0.putToken(t1);
		fhgPlace1_1.putToken(t2);
		
		try
		{
			workflow.traverse(fhgPlace1_0);
			workflow.traverse(fhgPlace1_1);
			
//			feedbacks = new ArrayList<Feedback>();
//			feedbacks.add(new Feedback(new Agent(0), new Agent(2), 0.1));
//			
//			Token t3 = new Token(feedbacks, fhgPlace1_0);
//			Token t4 = new Token(feedbacks, fhgPlace1_1); 
//			fhgPlace1_0.putToken(t3);
//			fhgPlace1_1.putToken(t4);
//			
//			workflow.traverse(fhgPlace1_0);
//			workflow.traverse(fhgPlace1_1);
			
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
		
		File f = new File("output\\" + temp + "\\etpt");
		f.mkdirs();
		
		String graphVizLocation = "C:\\Program Files (x86)\\Graphviz 2.28\\bin\\dot.exe";
		DotWriter.write2(workflow, "output\\" + temp + "\\etpt\\" + "wf", graphVizLocation);
		DotWriter.write(fhg1_0, "output\\" + temp + "\\etpt\\" + fhg1_0.getName(), graphVizLocation);
		DotWriter.write(fhg1_1, "output\\" + temp + "\\etpt\\" + fhg1_1.getName(), graphVizLocation);
		DotWriter.write(fhg2, "output\\" + temp + "\\etpt\\" + fhg2.getName(), graphVizLocation);
		DotWriter.write(rg1, "output\\" + temp + "\\etpt\\" + rg1.getName(), graphVizLocation);
		DotWriter.write(rg2, "output\\" + temp + "\\etpt\\" + rg2.getName(), graphVizLocation);
		DotWriter.write(rg3, "output\\" + temp + "\\etpt\\" + rg3.getName(), graphVizLocation);
		
		

	}
	
	public static void testMT() throws Exception
	{
		PetriNet workflow = new PetriNet(new PetriNetEdgeFactory());
		
		//create a feedback history graph
		FHG fhg1 = new FHG(new FeedbackHistoryEdgeFactory());
		Place fhgPlace1 = new Place(fhg1);
		
		Discretizer da = new Discretizer();
		Transition daTransition = new Transition(da);
		daTransition.setWorkflow(workflow);
		
		FHG fhg2 = new FHG(new FeedbackHistoryEdgeFactory());
		Place fhgPlace2 = new Place(fhg2);
		
		ManagingTrust mt = new ManagingTrust();
		Transition mtTransition = new Transition(mt);
		mtTransition.setWorkflow(workflow);
		
		RG rg = new RG(new ReputationEdgeFactory());
		Place rgPlace = new Place(rg);
		
		workflow.addEdge(fhgPlace1, daTransition, 1);
		workflow.addEdge(daTransition, fhgPlace2, 1);
		workflow.addEdge(fhgPlace2, mtTransition, 1);
		workflow.addEdge(mtTransition, rgPlace, 1);
		
		//parse the feedbacks from the arff file
		DefaultArffFeedbackGenerator feedbackGen = new DefaultArffFeedbackGenerator();
		ArrayList<Feedback> feedbacks = (ArrayList<Feedback>) feedbackGen.generateHardcoded("feedbacks.arff");
	
		Token t = new Token(feedbacks, fhgPlace1);
		fhgPlace1.putToken(t);
		
		workflow.traverse(fhgPlace1);
		
		System.out.println(fhg2);
		System.out.println(rg);
	}
	
	public static void testETPT2(String arffFileName) throws Exception
	{
		PetriNet workflow = new PetriNet(new PetriNetEdgeFactory());
		
		//create a feedback history graph
		FHG fhg1_0 = new FHG(new FeedbackHistoryEdgeFactory());
		Place fhgPlace1_0 = new Place(fhg1_0);
		FHG fhg1_1 = new FHG(new FeedbackHistoryEdgeFactory());
		Place fhgPlace1_1 = new Place(fhg1_1);
		
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
		
		cu.rst.gwt.server.entities.File file = new cu.rst.gwt.server.entities.File("coeff.txt", "coeff");
		Place flPlace = new Place(file);
		
		workflow.addEdge(fhgPlace1_0, daTransition, 1);
		workflow.addEdge(daTransition, fhgPlace2, 1);
		workflow.addEdge(fhgPlace2, etTransition, 1);
		workflow.addEdge(etTransition, rgPlace1, 1);
		workflow.addEdge(fhgPlace1_1, ptTransition, 1);
		workflow.addEdge(ptTransition, rgPlace2, 1);
		workflow.addEdge(rgPlace2, naTransition, 1);
		workflow.addEdge(naTransition, rgPlace3, 1);
		workflow.addEdge(rgPlace1, spTransition, 1);
		workflow.addEdge(rgPlace3, spTransition, 1);
		workflow.addEdge(spTransition, flPlace, 1);		
		
		File f = new File("output\\" + temp + "\\etpt");
		f.mkdirs();
		String graphVizLocation = "C:\\Program Files (x86)\\Graphviz 2.28\\bin\\dot.exe";
		DotWriter.write2(workflow, "output\\" + temp + "\\etpt\\wf", graphVizLocation);
		
		//parse the feedbacks from the arff file
		DefaultArffFeedbackGenerator feedbackGen = new DefaultArffFeedbackGenerator();
		ArrayList<Feedback> feedbacks = (ArrayList<Feedback>) feedbackGen.generateHardcoded(arffFileName + ".arff");
		
		Token t1 = new Token(feedbacks, fhgPlace1_0);
		fhgPlace1_0.putToken(t1, true);
		daTransition.fire();
		etTransition.fire();
		
//		workflow.traverse(fhgPlace1_0);
		
		Token t2 = new Token(feedbacks, fhgPlace1_1);
		fhgPlace1_1.putToken(t2);
		
//		workflow.traverse(fhgPlace1_1);
		
	}
	
	public static void testPT2(String arffFileName) throws Exception
	{
		PetriNet workflow = new PetriNet(new PetriNetEdgeFactory());
		
		//create a feedback history graph
		FHG fhg1_0 = new FHG(new FeedbackHistoryEdgeFactory());
		Place fhgPlace1_0 = new Place(fhg1_0);
		
		FHG fhg1_1 = new FHG(new FeedbackHistoryEdgeFactory());
		Place fhgPlace1_1 = new Place(fhg1_1);
		
		RG rg1 = new RG(new ReputationEdgeFactory());
		Place rgPlace1 = new Place(rg1);

		PeerTrust pt = new PeerTrust();
		Transition ptTransition = new Transition(pt);
		ptTransition.setWorkflow(workflow);
		
		RG rg2 = new RG(new ReputationEdgeFactory());
		Place rgPlace2 = new Place(rg2);
		
		PeerTrust pt2 = new PeerTrust();
		Transition ptTransition2 = new Transition(pt2);
		ptTransition.setWorkflow(workflow);
		
		String temp = arffFileName.split("\\\\")[1];
		EvalDP sp = new EvalDP();
		Transition evalTransition = new Transition(sp);
		evalTransition.setWorkflow(workflow);
		
		cu.rst.gwt.server.entities.File file = new cu.rst.gwt.server.entities.File("boolean.txt", "boolean");
		Place flPlace = new Place(file);
		
		workflow.addEdge(fhgPlace1_0, ptTransition, 1);
		workflow.addEdge(ptTransition, rgPlace1, 1);
		workflow.addEdge(ptTransition, fhgPlace1_0, 1);
		workflow.addEdge(fhgPlace1_0, evalTransition, 1);
		workflow.addEdge(rgPlace1, evalTransition, 1);
		
		workflow.addEdge(fhgPlace1_1, ptTransition2, 1);
		workflow.addEdge(ptTransition2, rgPlace2, 1);
		workflow.addEdge(ptTransition2, fhgPlace1_1, 1);
		workflow.addEdge(fhgPlace1_1, evalTransition, 1);
		workflow.addEdge(rgPlace2, evalTransition, 1);
		
		workflow.addEdge(evalTransition, flPlace, 1);
				
		
		File f = new File("output\\" + temp + "\\pt");
		f.mkdirs();
		String graphVizLocation = "C:\\Program Files (x86)\\Graphviz 2.28\\bin\\dot.exe";
		DotWriter.write2(workflow, "output\\" + temp + "\\pt\\wf", graphVizLocation);
		
	}
	
	public static void testPT3(String arffFileName) throws Exception
	{
		PetriNet workflow = new PetriNet(new PetriNetEdgeFactory());
		
		//create a feedback history graph
		FHG fhg1_0 = new FHG(new FeedbackHistoryEdgeFactory());
		Place fhgPlace1_0 = new Place(fhg1_0);
		
		RG rg1 = new RG(new ReputationEdgeFactory());
		Place rgPlace1 = new Place(rg1);

		PeerTrust pt = new PeerTrust();
		Transition ptTransition = new Transition(pt);
		ptTransition.setWorkflow(workflow);
		
		String temp = arffFileName.split("\\\\")[1];
		
		workflow.addEdge(fhgPlace1_0, ptTransition, 1);
		workflow.addEdge(ptTransition, rgPlace1, 1);
		
		File f = new File("output\\" + temp + "\\pt");
		f.mkdirs();
		String graphVizLocation = "C:\\Program Files (x86)\\Graphviz 2.28\\bin\\dot.exe";
		DotWriter.write2(workflow, "output\\" + temp + "\\pt\\wf", graphVizLocation);
		
	}

}
