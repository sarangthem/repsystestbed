/**
 * 
 */
package cu.rst.gwt.server.tests;

import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTabbedPane;

import org.apache.log4j.BasicConfigurator;

import cu.repsystestbed.algorithms.ReputationAlgorithm;
import cu.repsystestbed.algorithms.TrustAlgorithm;
import cu.repsystestbed.algorithms.examples.EigenTrust;
import cu.repsystestbed.algorithms.examples.RankbasedTrustAlg;
import cu.repsystestbed.data.Feedback;
import cu.repsystestbed.graphs.FeedbackHistoryEdgeFactory;
import cu.repsystestbed.graphs.FeedbackHistoryGraph;
import cu.repsystestbed.graphs.ReputationEdgeFactory;
import cu.repsystestbed.graphs.ReputationGraph;
import cu.repsystestbed.graphs.TrustEdgeFactory;
import cu.repsystestbed.graphs.TrustGraph;
import cu.repsystestbed.util.DefaultArffFeedbackGenerator;
import cu.repsystestbed.view.JGraphXView;

/**
 * @author partheinstein
 *
 */
public class Workflow2 extends JFrame
{

	public Workflow2(FeedbackHistoryGraph feedbackHistoryGraph, ReputationGraph repGraph,
			TrustGraph trustGraph) throws Exception
	{
		
		super("JGraphTest2");
		
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.addTab("Feedback History Graph", feedbackHistoryGraph.view.m_graphComponent);
		tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);
		tabbedPane.addTab("Reputation Graph", repGraph.view.m_graphComponent);
		tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);
		tabbedPane.addTab("Trust Graph", trustGraph.view.m_graphComponent);
		tabbedPane.setMnemonicAt(2, KeyEvent.VK_3);
		
		getContentPane().add(tabbedPane);	
		addMouseListerners(feedbackHistoryGraph.view);
		addMouseListerners(repGraph.view);
		addMouseListerners(trustGraph.view);
		
		JMenuBar jmenuBar = new JMenuBar();
		JMenu jmenu = new JMenu("File");
		JMenuItem jmenuItem = new JMenuItem("Load Algorithms");
		jmenu.add(jmenuItem);
		jmenuBar.add(jmenu);
		
		super.setJMenuBar(jmenuBar);
		
	}
	
	private void addMouseListerners(final JGraphXView viewer)
	{
		viewer.m_graphComponent.getGraphControl().addMouseListener(new MouseAdapter()
		{
		
			public void mouseReleased(MouseEvent e)
			{
				Object cell = viewer.m_graphComponent.getCellAt(e.getX(), e.getY());
				
				if (cell != null)
				{
					Object[] cells = new Object[1];
					cells[0] = cell;
					System.out.println("cell to remove = " + viewer.m_graph.getLabel(cell));
					if(e.getButton() == MouseEvent.BUTTON3) viewer.m_graph.removeCells(cells);
					
				}
			}
		});
	}
	
	public static void main(String[] args) throws Exception
	{
		
		BasicConfigurator.configure();
		
		//create graphs
		FeedbackHistoryGraph feedbackHistoryGraph = new FeedbackHistoryGraph(new FeedbackHistoryEdgeFactory());
		ReputationGraph repGraph = new ReputationGraph(new ReputationEdgeFactory());
		TrustGraph trustGraph = new TrustGraph(new TrustEdgeFactory());
		
		//swing listeners
		Workflow2 workflow2 = new Workflow2(feedbackHistoryGraph, repGraph, trustGraph);
		workflow2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		workflow2.setSize(400, 320);
		workflow2.setVisible(true);
		
		//eigentrust needs to use the feedback history graph
		EigenTrust repAlg = (EigenTrust) ReputationAlgorithm.getInstance("cu.repsystestbed.algorithms.examples.EigenTrust");
		repAlg.setGraph2Listen(feedbackHistoryGraph);
		repAlg.setGraph2Output(repGraph);
		
		//add eigentrust as an observer to the feedback history graph
		feedbackHistoryGraph.addObserver(repAlg);
		
		//rank based trust alg needs to use the reputation graph
		RankbasedTrustAlg trustAlg = (RankbasedTrustAlg) TrustAlgorithm.getInstance("cu.repsystestbed.algorithms.examples.RankbasedTrustAlg");
		trustAlg.setRatio(0.7);
		//must be called in this sequence otherwise setReputationGraph() will create a new trust graph 
		trustAlg.setGraph2Output(trustGraph);
		trustAlg.setGraph2Listen(repGraph);

		
		//add rank based trust alg as an observer to the reputation graph
		repGraph.addObserver(trustAlg);
		
		//parse the feedbacks from the arff file
		DefaultArffFeedbackGenerator feedbackGen = new DefaultArffFeedbackGenerator();
		ArrayList<Feedback> feedbacks = (ArrayList<Feedback>) feedbackGen.generateHardcoded("feedbacks.arff");
		
		//add the feedbacks to the feedback history graph
		feedbackHistoryGraph.addFeedbacks(feedbacks, true);	
		feedbackHistoryGraph.notifyObservers(true);

	}

}
