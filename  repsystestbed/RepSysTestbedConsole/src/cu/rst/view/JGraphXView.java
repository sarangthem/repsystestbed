package cu.rst.view;

import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.jgrapht.graph.SimpleDirectedGraph;

import com.mxgraph.layout.mxOrganicLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.view.mxGraph;

import cu.rst.core.graphs.Agent;
import cu.rst.core.graphs.FeedbackHistoryGraphEdge;
import cu.rst.core.graphs.ReputationEdge;
import cu.rst.core.graphs.TestbedEdge;
import cu.rst.core.graphs.TrustEdge;
import cu.rst.util.Util;

/**
 * @author partheinstein
 * 
 * Every graph has a view, JGraphXView.
 *
 */
public class JGraphXView
{
	public SimpleDirectedGraph m_graphModel;
	public mxGraph m_graph;
	public mxGraphComponent m_graphComponent;
	private HashMap<String, mxCell> jGraphCells;
	public mxOrganicLayout  layout;
	static Logger logger = Logger.getLogger(JGraphXView.class.getName());

	
	public JGraphXView()
	{
		init();
	}
	
	private void init()
	{
		m_graph = new mxGraph();
		m_graph.setMinimumGraphSize(new mxRectangle(0,0,400,400));
		layout = new mxOrganicLayout (m_graph);
		m_graph.setCellsDeletable(false);
		m_graph.setCellsDisconnectable(false);
		m_graph.setCellsEditable(false);
		m_graph.setAutoOrigin(true);
		m_graph.setAutoSizeCells(true);
		m_graph.setCellsLocked(false);
		m_graph.setAllowDanglingEdges(false);
		m_graph.setCellsCloneable(false);
		m_graphComponent = new mxGraphComponent(m_graph);
		m_graphComponent.setAutoExtend(true);
		m_graphComponent.setExportEnabled(true);
		m_graphComponent.setCenterZoom(true);

		
		//key = agent id, value = ref to the cell in jgraph. a cell can be vertex or edge
		jGraphCells = new HashMap<String, mxCell>();
	}
	
	@Deprecated 
	public JGraphXView(SimpleDirectedGraph graphModel) throws Exception
	{

		init();
		if(graphModel == null) throw new Exception("Model graph cannot be null.");
		m_graphModel = graphModel;

		Object parent = m_graph.getDefaultParent();
		m_graph.getModel().beginUpdate();
		try
		{
			//add vertices
			for(Object o : m_graphModel.vertexSet())
			{
				Agent a = (Agent) o;
				mxCell cell = (mxCell) m_graph.insertVertex(parent, new Integer(a.id).toString(), new Integer(a.id).toString(), 20, 20, 20, 20, "ROUNDED");
				jGraphCells.put(new Integer(a.id).toString(), cell);
			}
			//add edges
			for(Object o : m_graphModel.edgeSet())
			{
				TestbedEdge e = (TestbedEdge) o;
				String edgeType = null;
				if(e instanceof FeedbackHistoryGraphEdge)
				{
					edgeType = ((FeedbackHistoryGraphEdge)e).toString3();
				}else if(e instanceof ReputationEdge)
				{
					edgeType = (new Double(Util.round(((ReputationEdge)e).getReputation(), 2))).toString();
				}else if (e instanceof TrustEdge)
				{
					edgeType = "";
				}else
				{
					edgeType = "-";
				}
				String srcIdString = new Integer(((Agent)e.src).id).toString();
				String sinkIdString = new Integer(((Agent)e.sink).id).toString();
				m_graph.insertEdge(parent, null, edgeType, jGraphCells.get(srcIdString), jGraphCells.get(sinkIdString));
			}
		}finally
		{
			m_graph.getModel().endUpdate();
		}
		m_graph.setCellsDeletable(false);
		m_graphComponent = new mxGraphComponent(m_graph);
		
		layout = new mxOrganicLayout (m_graph);
		layout.execute(m_graph.getDefaultParent());
		m_graph = layout.getGraph();
		
	}
	
	
	
	/**
	 * This method adds only the edges specified in the parameter
	 * @param changes list of edges to be added to the underlying jgraph
	 */
	public void update(List<TestbedEdge> changes) throws Exception
	{
		if(changes == null) return;
		if(changes.size() == 0) return;
		logger.info("Updating with changes...");
		Object parent = m_graph.getDefaultParent();
		m_graph.getModel().beginUpdate();
		try
		{
			for(TestbedEdge e : changes)
			{
				String srcIdString = new Integer(((Agent)e.src).id).toString();
				String sinkIdString = new Integer(((Agent)e.sink).id).toString();
				
				Object srcj = jGraphCells.get(srcIdString);
				Object sinkj = jGraphCells.get(sinkIdString);
				
				if(srcj == null)
				{
					//if not present in our hashmap, then the cell doesnt exist in the jgraph
					//so add it and store the reference of the cell
					logger.info("Adding agent " + srcIdString + " to the view.");
					mxCell o = (mxCell) m_graph.insertVertex(parent, srcIdString, srcIdString, 20, 20, 20, 20, "ROUNDED");
					jGraphCells.put(srcIdString, o);
				}
				if(sinkj == null)
				{
					//if not present in our hashmap, then the cell doesnt exist in the jgraph
					//so add it and store the reference of the cell
					logger.info("Adding agent " + sinkIdString + " to the view.");
					mxCell o = (mxCell) m_graph.insertVertex(parent, sinkIdString, sinkIdString, 20, 20, 20, 20, "ROUNDED");
					jGraphCells.put(sinkIdString, o);
				}

				String edgeKey = srcIdString + "-"+ sinkIdString;
				if(!jGraphCells.containsKey(edgeKey))
				{
					//guaranteed jgraphVertices.get(srcIdString) and jgraphVertices.get(sinkIdString) will not be null
					//and that the cells are already present in jraph 
					mxCell o = (mxCell) m_graph.insertEdge(parent, null, "", jGraphCells.get(srcIdString), jGraphCells.get(sinkIdString));
					jGraphCells.put(edgeKey, o);
				}
				else
				{
					//edge exists in graph. remove it and add it again with the updated edge info
					mxCell cell1 = jGraphCells.get(edgeKey);
					m_graph.removeCells(new Object[]{cell1});
					mxCell cell2 = (mxCell) m_graph.insertEdge(parent, null, "", jGraphCells.get(srcIdString), jGraphCells.get(sinkIdString));
					jGraphCells.remove(edgeKey);
					jGraphCells.put(edgeKey, cell2);
					
				}
				//TODO display the changed label on the edge
				
			}
		}catch(Exception e)
		{
			logger.error(e.toString());
		}finally
		{
			m_graph.getModel().endUpdate();
			m_graph.setCellsDeletable(false);
			layout.execute(parent);
			m_graph = layout.getGraph();
		}

		
	}
	
	

}
