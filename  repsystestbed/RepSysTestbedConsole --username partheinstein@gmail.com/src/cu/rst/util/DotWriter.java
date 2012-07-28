/**
 * 
 */
package cu.rst.util;

import java.io.FileWriter;
import java.text.DecimalFormat;

import org.jgrapht.graph.DirectedPseudograph;
import org.jgrapht.graph.SimpleDirectedGraph;

import cu.rst.core.graphs.Feedback;
import cu.rst.core.graphs.Agent;
import cu.rst.core.graphs.FHG;
import cu.rst.core.graphs.FeedbackHistoryGraphEdge;
import cu.rst.core.graphs.Graph;
import cu.rst.core.graphs.ReputationEdge;
import cu.rst.core.graphs.RG;
import cu.rst.core.graphs.TrustEdge;
import cu.rst.core.graphs.TG;
import cu.rst.core.petrinet.PetriNet;
import cu.rst.core.petrinet.PetriNetEdge;
import cu.rst.core.petrinet.Place;
import cu.rst.core.petrinet.Transition;

/**
 * @author partheinstein
 *
 */
public class DotWriter 
{
	public static void write(DirectedPseudograph g, String fn, String graphVizLocation) throws Exception
	{
		Util.assertNotNull(g);
		FileWriter fw = new FileWriter(fn + ".dot");
		if(g instanceof FHG)
		{
			FHG fhg = ((FHG) g);
			fw.write("digraph G {");
			
			for(Object o : fhg.edgeSet())
			{
				FeedbackHistoryGraphEdge e = (FeedbackHistoryGraphEdge)o;
				fw.write(((Agent)e.src).id + " -> ");
				fw.write(((Agent)e.sink).id + " [label=\"{");
				
				for(int i=0; i<e.feedbacks.size();i++)
				{
					Feedback f = e.feedbacks.get(i);
					fw.write(new Double(f.value).toString());
					if(i != e.feedbacks.size()-1) fw.write(", ");	
				}
				
				fw.write("}\"];");
				
			}
			fw.write("}");
			fw.close();
			
			//invoke Graphviz to output a jpeg file of the graph.
			Runtime.getRuntime().exec(graphVizLocation + " -Tjpg " + fn + ".dot -o " + fn + ".jpg");
			
		}
		else if(g instanceof RG)
		{
			RG rg = (RG)g;
			fw.write("digraph G {");
			
			for(Object o : rg.edgeSet())
			{
				ReputationEdge e = (ReputationEdge)o;
				fw.write(((Agent)e.src).id + " -> ");
				fw.write(((Agent)e.sink).id + " [label=\"" + roundTwoDecimals(e.getReputation()) + "\"];");
			}
			fw.write("}");
			fw.close();
			
			//invoke Graphviz to output a jpeg file of the graph.
			Runtime.getRuntime().exec(graphVizLocation + " -Tjpg " + fn + ".dot -o " + fn + ".jpg");

			
		}
		else if(g instanceof TG)
		{
			TG tg = (TG)g;
			fw.write("digraph G {");
			
			for(Object o : tg.edgeSet())
			{
				TrustEdge e = (TrustEdge)o;
				fw.write(((Agent)e.src).id + " -> " + ((Agent)e.sink).id);
			}
			fw.write("}");
			fw.close();
			
			//invoke Graphviz to output a jpeg file of the graph.
			Runtime.getRuntime().exec(graphVizLocation + " -Tjpg " + fn + ".dot -o " + fn + ".jpg");
		}
		
		
	}
	
	public static void write(SimpleDirectedGraph g, String fn, String graphVizLocation) throws Exception
	{
		Util.assertNotNull(g);
		FileWriter fw = new FileWriter(fn + ".dot");
		if(g instanceof PetriNet)
		{
			PetriNet pn = (PetriNet)g;
			fw.write("digraph G {");
			
			for(PetriNetEdge e : pn.edgeSet())
			{
				
				if(e.src instanceof Place && e.sink instanceof Transition)
				{
					fw.write((((Transition)e.sink).getAlg()).getName() + "[shape=box];");
					fw.write((((Place)e.src).getGraph()).getName() + " -> " 
						+ (((Transition)e.sink).getAlg()).getName() + "[label=\"" + e.getTokens() + "\"];");
				}
				else if(e.src instanceof Transition && e.sink instanceof Place)
				{
					fw.write((((Transition)e.src).getAlg()).getName() + "[shape=box];");
					fw.write((((Transition)e.src).getAlg()).getName() + " -> " 
							+ (((Place)e.sink).getGraph()).getName() + "[label=\"" + e.getTokens() + "\"];");
				}
			}
			
			fw.write("}");
			fw.close();
			
			//invoke Graphviz to output a jpeg file of the graph.
			Runtime.getRuntime().exec(graphVizLocation + " -Tjpg " + fn + ".dot -o " + fn + ".jpg");
		}
	}
	
	public static void write2(SimpleDirectedGraph g, String fn, String graphVizLocation) throws Exception
	{
		Util.assertNotNull(g);
		FileWriter fw = new FileWriter(fn + ".dot");
		if(g instanceof PetriNet)
		{
			PetriNet pn = (PetriNet)g;
			fw.write("digraph G {");
//			fw.write("orientation=landscape,");
			
			for(PetriNetEdge e : pn.edgeSet())
			{
				
				if(e.src instanceof Place && e.sink instanceof Transition)
				{
					fw.write(((Place)e.src).getName() + "[shape=circle," +  "label=" + ((Place)e.src).getName() + "];");
					fw.write((((Transition)e.sink).getAlg()).getName() + "[shape=rect,height=0.01,width=1, " +  "label=" + (((Transition)e.sink).getAlg()).getName() + "];");
					fw.write((((Place)e.src).getGraph()).getName() + " -> " 
//						+ (((Transition)e.sink).getAlg()).getName() + "[label=\"" + e.getTokens() + "\"];");
							+ (((Transition)e.sink).getAlg()).getName() + "[label=\"\"];");
				}
				else if(e.src instanceof Transition && e.sink instanceof Place)
				{
					fw.write(((Place)e.sink).getName() + "[shape=circle," +  "label=" + ((Place)e.sink).getName() + "];");
					fw.write((((Transition)e.src).getAlg()).getName() + "[shape=box];");
					fw.write((((Transition)e.src).getAlg()).getName() + " -> " 
//							+ ((Place)e.sink).getName() + "[label=\"" + e.getTokens() + "\"];");
							+ ((Place)e.sink).getName()+ "[label=\"\"];");
				}
			}
			
			fw.write("}");
			fw.close();
			
			//invoke Graphviz to output a jpeg file of the graph.
			Runtime.getRuntime().exec(graphVizLocation + " -Tjpg " + fn + ".dot -o " + fn + ".jpg");
		}
	}
	
	public static double roundTwoDecimals(double d) 
	{
        DecimalFormat twoDForm = new DecimalFormat("#.##");
        try
        {
        	return Double.valueOf(twoDForm.format(d));
        }
        catch(Exception e)
        {
        	//Likely a NaN, return -1;
        	return -1;
        }
       
	}
}


