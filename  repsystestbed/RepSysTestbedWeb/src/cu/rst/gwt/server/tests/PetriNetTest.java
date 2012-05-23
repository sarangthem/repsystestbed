package cu.rst.gwt.server.tests;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.BasicConfigurator;

import cu.rst.gwt.server.alg.eg.DiscretizingAlgorithm;
import cu.rst.gwt.server.alg.eg.EigenTrust;
import cu.rst.gwt.server.alg.eg.NormalizingAlgorithm;
import cu.rst.gwt.server.alg.eg.PeerTrust;
import cu.rst.gwt.server.alg.eg.RankComparisonAlgorithm;
import cu.rst.gwt.server.alg.eg.StringSink;
import cu.rst.gwt.server.data.Feedback;
import cu.rst.gwt.server.graphs.FeedbackHistoryEdgeFactory;
import cu.rst.gwt.server.graphs.FeedbackHistoryGraph;
import cu.rst.gwt.server.graphs.ReputationEdgeFactory;
import cu.rst.gwt.server.graphs.ReputationGraph;
import cu.rst.gwt.server.petrinet.PetriNet;
import cu.rst.gwt.server.petrinet.PetriNetEdgeFactory;
import cu.rst.gwt.server.petrinet.Place;
import cu.rst.gwt.server.petrinet.Token;
import cu.rst.gwt.server.petrinet.Transition;
import cu.rst.gwt.server.util.DefaultArffFeedbackGenerator;

public class PetriNetTest {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception 
	{
		BasicConfigurator.configure();
		
		//testET();
		//testPT();
		testETPT();
	}
	
	public static void testET() throws Exception
	{
		PetriNet workflow = new PetriNet(new PetriNetEdgeFactory());
		
		//create a feedback history graph
		FeedbackHistoryGraph fhg1 = new FeedbackHistoryGraph(new FeedbackHistoryEdgeFactory());
		Place fhgPlace1 = new Place(fhg1);
		
		DiscretizingAlgorithm da = new DiscretizingAlgorithm();
		Transition daTransition = new Transition(da);
		daTransition.setPetriNet(workflow);
		
		FeedbackHistoryGraph fhg2 = new FeedbackHistoryGraph(new FeedbackHistoryEdgeFactory());
		Place fhgPlace2 = new Place(fhg2);
		
		EigenTrust et = new EigenTrust();
		Transition etTransition = new Transition(et);
		etTransition.setPetriNet(workflow);
		
		ReputationGraph rg = new ReputationGraph(new ReputationEdgeFactory());
		Place rgPlace = new Place(rg);
		
		workflow.addEdge(fhgPlace1, daTransition, 1);
		workflow.addEdge(daTransition, fhgPlace2, 1);
		workflow.addEdge(fhgPlace2, etTransition, 1);
		workflow.addEdge(etTransition, rgPlace, 1);
		
		//parse the feedbacks from the arff file
		DefaultArffFeedbackGenerator feedbackGen = new DefaultArffFeedbackGenerator();
		ArrayList<Feedback> feedbacks = (ArrayList<Feedback>) feedbackGen.generateHardcoded("feedbacks.arff");
	
		Token t = new Token(feedbacks, fhgPlace1);
		fhgPlace1.putToken(t);
		
		workflow.traverse(fhgPlace1);
		
		System.out.println(fhg2);
		System.out.println(rg);
	}
	
	public static void testPT() throws Exception
	{
		PetriNet workflow = new PetriNet(new PetriNetEdgeFactory());
		
		//create a feedback history graph
		FeedbackHistoryGraph fhg = new FeedbackHistoryGraph(new FeedbackHistoryEdgeFactory());
		Place fhgPlace = new Place(fhg);
		
		PeerTrust pt = new PeerTrust();
		Transition ptTransition = new Transition(pt);
		ptTransition.setPetriNet(workflow);
		
		ReputationGraph rg1 = new ReputationGraph(new ReputationEdgeFactory());
		Place rgPlace1 = new Place(rg1);
		
		NormalizingAlgorithm na = new NormalizingAlgorithm();
		Transition naTransition = new Transition(na);
		naTransition.setPetriNet(workflow);
		
		ReputationGraph rg2 = new ReputationGraph(new ReputationEdgeFactory());
		Place rgPlace2 = new Place(rg2);
		
		workflow.addEdge(fhgPlace, ptTransition, 1);
		workflow.addEdge(ptTransition, rgPlace1, 1);
		workflow.addEdge(rgPlace1, naTransition, 1);
		workflow.addEdge(naTransition, rgPlace2, 1);
		
		
		//parse the feedbacks from the arff file
		DefaultArffFeedbackGenerator feedbackGen = new DefaultArffFeedbackGenerator();
		ArrayList<Feedback> feedbacks = (ArrayList<Feedback>) feedbackGen.generateHardcoded("feedbacks.arff");
	
		Token t = new Token(feedbacks, fhgPlace);
		fhgPlace.putToken(t);
		
		workflow.traverse(fhgPlace);
		
		System.out.println(fhg);
		System.out.println(rg2);
	}
	
	public static void testETPT() throws Exception
	{
		PetriNet workflow = new PetriNet(new PetriNetEdgeFactory());
		
		//create a feedback history graph
		FeedbackHistoryGraph fhg1 = new FeedbackHistoryGraph(new FeedbackHistoryEdgeFactory());
		Place fhgPlace1 = new Place(fhg1);
		
		DiscretizingAlgorithm da = new DiscretizingAlgorithm();
		Transition daTransition = new Transition(da);
		daTransition.setPetriNet(workflow);
		
		FeedbackHistoryGraph fhg2 = new FeedbackHistoryGraph(new FeedbackHistoryEdgeFactory());
		Place fhgPlace2 = new Place(fhg2);
		
		EigenTrust et = new EigenTrust();
		Transition etTransition = new Transition(et);
		etTransition.setPetriNet(workflow);
		
		ReputationGraph rg1 = new ReputationGraph(new ReputationEdgeFactory());
		Place rgPlace1 = new Place(rg1);

		PeerTrust pt = new PeerTrust();
		Transition ptTransition = new Transition(pt);
		ptTransition.setPetriNet(workflow);
		
		ReputationGraph rg2 = new ReputationGraph(new ReputationEdgeFactory());
		Place rgPlace2 = new Place(rg2);
		
		NormalizingAlgorithm na = new NormalizingAlgorithm();
		Transition naTransition = new Transition(na);
		naTransition.setPetriNet(workflow);
		
		ReputationGraph rg3 = new ReputationGraph(new ReputationEdgeFactory());
		Place rgPlace3 = new Place(rg3);

		RankComparisonAlgorithm rkcmp = new RankComparisonAlgorithm();
		Transition rkcmpTransition = new Transition(rkcmp);
		rkcmpTransition.setPetriNet(workflow);
		
		StringSink strSink = new StringSink();
		Place strSinkPlace = new Place(strSink);
		
		workflow.addEdge(fhgPlace1, daTransition, 1);
		workflow.addEdge(daTransition, fhgPlace2, 1);
		workflow.addEdge(fhgPlace2, etTransition, 1);
		workflow.addEdge(etTransition, rgPlace1, 1);
		workflow.addEdge(fhgPlace1, ptTransition, 1);
		workflow.addEdge(ptTransition, rgPlace2, 1);
		workflow.addEdge(rgPlace2, naTransition, 1);
		workflow.addEdge(naTransition, rgPlace3, 1);
		workflow.addEdge(rgPlace1, rkcmpTransition, 1);
		workflow.addEdge(rgPlace3, rkcmpTransition, 1);
		workflow.addEdge(rkcmpTransition, strSinkPlace, 2);
		
		//parse the feedbacks from the arff file
		DefaultArffFeedbackGenerator feedbackGen = new DefaultArffFeedbackGenerator();
		ArrayList<Feedback> feedbacks = (ArrayList<Feedback>) feedbackGen.generateHardcoded("feedbacks.arff");
		
		Token t1 = new Token(feedbacks, fhgPlace1);
		Token t2 = new Token(feedbacks, fhgPlace1);
		fhgPlace1.putToken(t1);
		fhgPlace1.putToken(t2);
		
		workflow.traverse(fhgPlace1);
		
	}

}
