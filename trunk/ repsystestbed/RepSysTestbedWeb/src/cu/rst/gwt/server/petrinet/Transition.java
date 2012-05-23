package cu.rst.gwt.server.petrinet;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import cu.rst.gwt.server.alg.Algorithm;
import cu.rst.gwt.server.util.Util;

/**
 * A transition embeds an algorithm and a transition is triggered only when there are enough tokens in the 
 * input places. Logic to determine if there are enough tokens is as follows:
 * total required tokens is the sum of weights on all incoming edges of t
 * total available tokens is the sum of the tokens in the input places of t
 * if(total available tokens >= total required tokens) then the transition is fired.
 * Once the transition is fired, number of tokens deleted in each input place =
 * weight of the edge leading to it.
 * 
 *  
 * @author partheinstein
 *
 */
public class Transition implements PetriNetElementIntf
{
	static Logger logger = Logger.getLogger(Transition.class.getName());
	
	protected int m_id;
	private PetriNet m_net; 
	private Algorithm m_alg;
	
	public Transition(Algorithm alg)
	{
		m_alg = alg;
		m_id = PetriNet.globalCounter++;
	}
	
	public void setPetriNet(PetriNet net)
	{
		m_net = net;
	}
	
	public PetriNet getPetriNet()
	{
		return m_net;
	}

	/**
	 * This method returns whether there are sufficient tokens in the input places of this transition. This
	 * method is used to determine whether to fire this transition or not.
	 * @param tokens
	 * @return
	 * @throws Exception
	 */
	public boolean canFire() throws Exception
	{
		logger.debug("canFire() invoked.");
		Util.assertNotNull(m_net);
		
		int totalTokensInInputPlaces = 0;
		int totalTokensRequired = 0;
		
		Set<PetriNetEdge> incomingEdges = m_net.incomingEdgesOf(this);
		if(incomingEdges != null && incomingEdges.size() > 0)
		{
			logger.debug("Number of input places: " + incomingEdges.size());
			for(PetriNetEdge e : incomingEdges)
			{
				
				//determine the tokens we have so far from the input places
				if(e.src instanceof Place)
				{
					logger.debug("Getting tokens from " + ((Place)e.src));
					totalTokensInInputPlaces = totalTokensInInputPlaces + ((Place)e.src).numTokens(); 
				}
				else
				{
					throw new Exception("Source to the edge is expected to be a place.");
				}
			}
			
			Set<PetriNetEdge> outgoingEdges = m_net.outgoingEdgesOf(this);
			if(outgoingEdges!=null && outgoingEdges.size()>0)
			{
				logger.debug("Number of output places: " + outgoingEdges.size());
				for(PetriNetEdge e : outgoingEdges)
				{
					if(e.sink instanceof Place)
					{
						totalTokensRequired = totalTokensRequired + e.getTokens();
					}
				}
				logger.debug("Total tokens required: " + totalTokensRequired);
			}
			
			logger.debug("Number of tokens in the input places: " + totalTokensInInputPlaces + ", Number of required tokens: " + totalTokensRequired);
			//do we have enough of the tokens to match the tokens on the edges?
			if(totalTokensInInputPlaces < totalTokensRequired) return false;	
		}
		else
		{
			logger.debug("This transition cannot fire.");
			return false;
		}
		logger.debug("This transition can fire.");
		return true;
	}
	

	@Override
	public boolean equals(Object o)
	{
		if(o instanceof Transition)
		{
			Transition t = (Transition)o;
			return (t.m_id == this.m_id)? true : false;
		}
		else return false;
	}
	
	/**
	 * This method will fire only if it can. If it can't, its a no-op.
	 * @throws Exception
	 */
	public boolean fire() throws Exception
	{
		logger.debug("fire() invoked.");
		Util.assertNotNull(m_net);
		
		if(canFire())
		{
			ArrayList allTokens = new ArrayList();
			Set<PetriNetEdge> incomingEdges = m_net.incomingEdgesOf(this);
			if(incomingEdges != null && incomingEdges.size() > 0)
			{
				//Determine the tokens and the changes in them
				for(PetriNetEdge e : incomingEdges)
				{
					if(e.src instanceof Place)
					{
						//get only the required amount tokens from the input place.
						List<Token> toks = ((Place)e.src).getTokens(e.getTokens());
						if(toks!=null)
						{
							for(Token t : toks)
							{
								allTokens.add(t);
							}
						}
						
					}
				}
				
				logger.debug("Total tokens in all the input places: " + allTokens.size());
				logger.debug("Calling algorithm.update()");
				
				//call the update function and the new changes
				ArrayList newChanges = this.update(allTokens);
				
				logger.debug("The algorithm returns " + newChanges.size() + " changes.");
				
				//add the tokens to the outgoing places according to the label on the edges
				Set<PetriNetEdge> outgoingEdges = m_net.outgoingEdgesOf(this);
				if(outgoingEdges != null && outgoingEdges.size() > 0)
				{
					logger.debug("Number of outgoing places: " + outgoingEdges.size());
					for(PetriNetEdge e : outgoingEdges)
					{
						if(e.sink instanceof Place)
						{
							int numTokensToAdd = e.getTokens();
							for(int i=0; i<numTokensToAdd; i++)
							{
								Token t = new Token(newChanges, (Place) e.sink);
								((Place)e.sink).putToken(t);
								logger.debug("Added " + numTokensToAdd + " to " + ((Place)e.sink));
							}
							
						}
						else
						{
							throw new Exception("Expected a place."); 
						}
					}
				}
				
				logger.debug("Deleting the tokens from the input places according to the label on the edges.");
				//delete the tokens from the input places according to the label on the edges
				for(PetriNetEdge e : incomingEdges)
				{
					if(e.src instanceof Place)
					{
						((Place)e.src).deleteTokens(e.getTokens());
						logger.debug("Deleted " + e.getTokens() + " tokens from " + ((Place)e.src));
					}
				}
			}
			
			return true;
		}
		return false;
	}

	@Override
	public ArrayList update(ArrayList<Token> tokens) throws Exception 
	{
		ArrayList changes = m_alg.update(tokens);
		if(changes == null) changes = new ArrayList();
		return changes;
	}
	
	@Override
	public String toString()
	{
		return "Transition: " + m_alg.getClass().getCanonicalName();
	}
}
