/**
 * 
 */
package cu.rst.tests;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;

import javax.swing.JFrame;

import org.jgrapht.EdgeFactory;
import org.jgrapht.graph.SimpleDirectedGraph;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;

/**
 * @author partheinstein
 *
 */
@SuppressWarnings("serial")
public class JgraphTest extends JFrame
{
	public class Node
	{
		public String m_name;
		public Node(String name)
		{
			m_name = name;
		}
	}
	
	public class TEdge
	{
		public Node m_src;
		public Node m_sink;
		public TEdge(Node src, Node sink)
		{
			m_src = src;
			m_sink = sink;
		}
	}

	
	public class TestEdgeFactory implements EdgeFactory<Node, TEdge>
	{
		
		public TestEdgeFactory(){}

		public TEdge createEdge(Node src, Node sink)
		{
			try 
			{
				return new TEdge(src, sink);
			} catch (Exception e) {
				System.exit(1);
			}
			return null; //should not come here
		}
		
	}
	
	private static final Dimension DEFAULT_SIZE = new Dimension( 530, 320 );
	private static final Color DEFAULT_BG_COLOR = Color.decode( "#FAFBFF" );
	
	
	@SuppressWarnings("unchecked")
	public JgraphTest()
	{
		super("Hello World!");
		//create jgrapht graph and add some nodes
		SimpleDirectedGraph g = new SimpleDirectedGraph(new TestEdgeFactory());
		Node v1 = new Node("v1");
		Node v2 = new Node("v2");
		Node v3 = new Node("v3");
		Node v4 = new Node("v4");
		
		g.addVertex(v1);
        g.addVertex(v2);
        g.addVertex(v3);
        g.addVertex(v4);
        g.addEdge(v1, v2);
        g.addEdge(v2, v3);
        g.addEdge(v3, v4);
        g.addEdge(v4, v3);
        
        //graphx
        final mxGraph graph = new mxGraph();
        Object parent = graph.getDefaultParent();
        graph.getModel().beginUpdate();
        
        HashMap<String, Object> jgraphVertices = new HashMap<String, Object>();
        
        try
        {
        	for(Object o : g.vertexSet())
        	{
        		Object o1 = graph.insertVertex(parent,((Node)o).m_name, ((Node)o).m_name, 20, 20, 80, 30);
        		jgraphVertices.put(((Node)o).m_name, o1);
        		
        	}
        	for(Object o : g.edgeSet())
        	{
        		Node src = ((TEdge)o).m_src;
        		Node sink = ((TEdge)o).m_sink;
        		graph.insertEdge(parent, null, "edge", jgraphVertices.get(src.m_name), jgraphVertices.get(sink.m_name));
        	}
        }finally
        {
        	graph.getModel().endUpdate();
        }
        
		final mxGraphComponent graphComponent = new mxGraphComponent(graph);
		getContentPane().add(graphComponent);
		
		graphComponent.getGraphControl().addMouseListener(new MouseAdapter()
		{
		
			public void mouseReleased(MouseEvent e)
			{
				Object cell = graphComponent.getCellAt(e.getX(), e.getY());
				
				if (cell != null)
				{
					System.out.println("cell="+graph.getLabel(cell));
				}
			}
		});

	}
	

	
	public static void main(String[] args)
	{
		JgraphTest frame = new JgraphTest();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(400, 320);
		frame.setVisible(true);

	}

}
