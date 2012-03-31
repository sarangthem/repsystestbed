/**
 * 
 */
package cu.rst.gwt.server.alg;

import java.util.List;
import java.util.Set;

import org.jgrapht.graph.SimpleDirectedGraph;

import cu.rst.gwt.server.entities.Agent;
import cu.rst.gwt.server.graphs.FeedbackHistoryGraph;
import cu.rst.gwt.server.graphs.FeedbackHistoryGraphEdge;
import cu.rst.gwt.server.graphs.Graph;
import cu.rst.gwt.server.graphs.ReputationEdge;
import cu.rst.gwt.server.graphs.ReputationEdgeFactory;
import cu.rst.gwt.server.graphs.ReputationGraph;

/**
 * @author partheinstein
 *
 */
public abstract class ReputationAlgorithmType1b extends ReputationAlgorithm
{

	@Override
	public void setGraph2Listen(Graph g) throws Exception
	{
		if(!(g instanceof ReputationGraph)) throw new Exception("Can only listen to Reputation Graph.");
		
		super.m_graph2Listen = (ReputationGraph)g;
		if(m_graph2Output == null) m_graph2Output = new ReputationGraph(new ReputationEdgeFactory());
		if(produceCompleteGraph)
		{
			Set<ReputationEdge> edges = ((ReputationGraph)super.m_graph2Listen).edgeSet(); //should be ok to cast here
			for(ReputationEdge edge : edges)
			{
				Agent src = (Agent)edge.src;
				Agent sink = (Agent)edge.sink;
				m_graph2Output.addVertex(src);
				m_graph2Output.addVertex(sink);
				m_graph2Output.addEdge(src, sink);
				//not copying the reputation weights. should you? Probably not.
			}
			
		}else
		{
			ReputationGraph ReputationGraphTransitive = ((ReputationGraph)this.m_graph2Listen).getTransitiveClosureGraph();
			Set<ReputationEdge> edges = ReputationGraphTransitive.edgeSet();
			for(ReputationEdge edge : edges)
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
