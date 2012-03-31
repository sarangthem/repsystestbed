/**
 * 
 */
package cu.rst.gwt.server.alg;

import java.util.Set;

import org.jgrapht.graph.SimpleDirectedGraph;

import cu.rst.gwt.server.entities.Agent;
import cu.rst.gwt.server.exceptions.GenericTestbedException;
import cu.rst.gwt.server.graphs.FeedbackHistoryGraph;
import cu.rst.gwt.server.graphs.FeedbackHistoryGraphEdge;
import cu.rst.gwt.server.graphs.ReputationEdge;
import cu.rst.gwt.server.graphs.ReputationEdgeFactory;
import cu.rst.gwt.server.graphs.ReputationGraph;

/**
 * @author partheinstein
 *
 */
public abstract class ReputationAlgorithmType2 extends ReputationAlgorithm
{

	
	/**
	 * set the local feedback history graph variable and also initializes the
	 * reputation graph based.
	 * @param feedbackHistoryGraph 
	 */

	@Override
	public void setGraph2Listen(SimpleDirectedGraph feedbackHistoryGraph) throws Exception
	{

		assertGraph2ListenType(feedbackHistoryGraph);
		this.m_graph2Listen = (FeedbackHistoryGraph)feedbackHistoryGraph;
		if(m_graph2Output == null) m_graph2Output = new ReputationGraph(new ReputationEdgeFactory());
		//initialize the reputation graph
		if(produceCompleteGraph)
		{
			Set<FeedbackHistoryGraphEdge> edges = ((FeedbackHistoryGraph)this.m_graph2Listen).edgeSet();
			for(FeedbackHistoryGraphEdge edge : edges)
			{
				Agent src = (Agent)edge.src;
				Agent sink = (Agent)edge.sink;
				m_graph2Output.addVertex(src);
				m_graph2Output.addVertex(sink);
				m_graph2Output.addEdge(src, sink);
			}
		}
		else
		{
			FeedbackHistoryGraph feedbackHistoryGraphTransitive = ((FeedbackHistoryGraph)this.m_graph2Listen).getTransitiveClosureGraph();
			Set<FeedbackHistoryGraphEdge> edges = feedbackHistoryGraphTransitive.edgeSet();
			for(FeedbackHistoryGraphEdge edge : edges)
			{
				Agent src = (Agent)edge.src;
				Agent sink = (Agent)edge.sink;
				m_graph2Output.addVertex(src);
				m_graph2Output.addVertex(sink);
				m_graph2Output.addEdge(src, sink);
			}
		}
		
	}
	
}
