/**
 * 
 */
package cu.rst.gwt.server.petrinet;

import java.util.Set;

import org.apache.log4j.Logger;
import org.jgrapht.graph.SimpleDirectedGraph;

import cu.rst.gwt.server.util.Util;

/**
 * @author partheinstein
 *
 */
public class PetriNet extends SimpleDirectedGraph<PetriNetElementIntf, PetriNetEdge>
{
	protected static int globalCounter = 0;
	static Logger logger = Logger.getLogger(PetriNet.class.getName());
	
	public PetriNet(PetriNetEdgeFactory pnef) 
	{
		super(pnef);	
	}
	
	public void addEdge(PetriNetElementIntf src, PetriNetElementIntf sink, int tokens) throws Exception
	{
		logger.debug("addEdge invoked.");
		Util.assertNotNull(src);
		Util.assertNotNull(sink);
		
		if(this.containsVertex(src) && this.containsVertex(sink))
		{
			//cannot have sink to src edge - i.e. no bidirectional edge
			boolean sinkConnectedToSrc = false;
			Set objs = this.outgoingEdgesOf(sink);
			for(Object o : objs)
			{
				if(o.equals(src))
				{
					sinkConnectedToSrc = true;
					break;
				}
			}
			
			if(sinkConnectedToSrc)
			{
				throw new Exception("PetriNet edges cannot be bidirectional.");
			}
		}
		if(!this.containsVertex(src))
		{
			this.addVertex(src);
		}
		if(!this.containsVertex(sink))
		{
			this.addVertex(sink);
		}
	
		addEdge(src, sink);
		PetriNetEdge e = this.getEdge(src, sink);
		e.setTokens(tokens);
		
		
		logger.debug("Edge added: " + e);
		
	}
	
	/**
	 * Recursive depth first search.
	 * @param p place to start
	 * @throws Exception
	 */
	public void traverse(Place p) throws Exception
	{
		logger.debug("traverse invoked()");
		Util.assertNotNull(p);
		logger.debug("Starting from " + p);
		
		if(!this.assertVertexExist(p)) throw new Exception("Place not in the net.");
		
		//do the place.update to make sure the changes in all the tokens are reflected.
		//here we don't really care about what the place returned after updating itself.
		p.update(p.getTokens()); 
		logger.debug("Updated " + p);
		
		Set<PetriNetEdge> outgoingEdgesOfP = this.outgoingEdgesOf(p);
		if(outgoingEdgesOfP != null && outgoingEdgesOfP.size() > 0)
		{
			logger.debug("Number of transitions from " + p + ": " + outgoingEdgesOfP.size());
			//for each outgoing edges of the place
			for(PetriNetEdge e1 : outgoingEdgesOfP)
			{
				if(e1.sink instanceof Transition)
				{
					//fire the transition.
					logger.debug("Firing " + ((Transition)e1.sink));
					if(((Transition)e1.sink).fire())
					{
						logger.debug("Fired " + ((Transition)e1.sink));
						Set<PetriNetEdge> outgoingEdgesOfT = this.outgoingEdgesOf((PetriNetElementIntf) e1.sink);
						if(outgoingEdgesOfT != null && outgoingEdgesOfT.size() > 0)
						{
							logger.debug("Number of places from " + ((Transition)e1.sink) + ": " + outgoingEdgesOfT.size());
							for(PetriNetEdge e2 : outgoingEdgesOfT)
							{
								if(e2.sink instanceof Place)
								{
									traverse((Place) e2.sink);
								}
								else
								{
									throw new Exception("Expected a place.");
								}
							}
						}
					}
				}
				else
				{
					throw new Exception("Expected a transition.");
				}
			}
		}
		
		logger.debug("traverse completed.");
		
		
	}
	
	

}
