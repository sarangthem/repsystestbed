/**
 * 
 */
package cu.rst.tests;

import java.io.File;
import java.util.ArrayList;

import org.apache.log4j.BasicConfigurator;

import cu.rst.core.alg.ManagingTrust;
import cu.rst.core.graphs.FHG;
import cu.rst.core.graphs.Feedback;
import cu.rst.core.graphs.TG;
import cu.rst.core.petrinet.PetriNet;
import cu.rst.core.petrinet.Place;
import cu.rst.core.petrinet.Token;
import cu.rst.core.petrinet.Transition;
import cu.rst.util.FeedbackGenerator;
import cu.rst.util.DotWriter;

/**
 * @author partheinstein
 *
 */
public class ManagingTrustTest
{

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception
	{
		if(args!=null && args.length!=1)
		{
			throw new Exception("Invalid input parameters. ManagingTrustTest <input\\file.arff>");			
		}
		BasicConfigurator.configure();
		PetriNet workflow = new PetriNet();
		
		ManagingTrust mt = new ManagingTrust();
		FHG fhg0 = new FHG();
		TG tg0 = new TG();
		
		Transition t0 = new Transition(mt);
		t0.setWorkflow(workflow);
		Place p0 = new Place(fhg0);
		Place p1 = new Place(tg0);
		
		workflow.addEdge(p0, t0, 1);
		workflow.addEdge(t0, p1, 1);
		
		FeedbackGenerator feedbackGen = new FeedbackGenerator();
		ArrayList<Feedback> feedbacks = (ArrayList<Feedback>) feedbackGen.generateHardcoded(args[0]);		
		
		Token t = new Token(feedbacks, p0);
		p0.putToken(t, true);
		
		t0.fire2();
		
		String temp = args[0].split("\\\\")[1];
		File f = new File("output\\" + temp + "\\" + mt.getName());
		f.mkdirs();
		String graphVizLocation = "C:\\Program Files (x86)\\Graphviz 2.28\\bin\\dot.exe";
		DotWriter.write2(workflow, "output\\" + temp + "\\" + mt.getName() + "\\wf", graphVizLocation);
		DotWriter.write(fhg0, "output\\" + temp + "\\" + mt.getName() + "\\fhg0", graphVizLocation);
		DotWriter.write(tg0, "output\\" + temp + "\\" + mt.getName() + "\\tg0", graphVizLocation);

	}

}
