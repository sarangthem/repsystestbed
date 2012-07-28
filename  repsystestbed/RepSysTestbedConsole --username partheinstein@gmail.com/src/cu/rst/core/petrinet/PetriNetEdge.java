package cu.rst.core.petrinet;

import org.apache.log4j.Logger;

import cu.rst.core.graphs.TestbedEdge;
import cu.rst.util.Util;

public class PetriNetEdge extends TestbedEdge
{
	private static final long serialVersionUID = 1L;
	
	static Logger logger = Logger.getLogger(PetriNetEdge.class.getName());
	private int m_numTokens;
	
	/**
	 * Creates an edge between a place/transition and a transition/place respectively.
	 * @param src A place or a transition
	 * @param sink A place or a transition
	 */
	public PetriNetEdge(PetriNetElementIntf src, PetriNetElementIntf sink, int tokens)
	{
		Util.assertNotNull(src);
		Util.assertNotNull(sink);
		super.src = src;
		super.sink = sink;
		m_numTokens = tokens;
	}
	
	public void setTokens(int tokens)
	{
		m_numTokens = tokens;
	}
	
	public int getTokens()
	{
		return m_numTokens;
	}
	
	@Override
	public String toString()
	{
		return "Src: " + src + " Sink: " + sink + " Edge weight: " + getTokens();
	}
	
	@Override
	public boolean equals(Object o)
	{
		if(!(o instanceof PetriNetEdge)) return false;
		PetriNetEdge e = (PetriNetEdge)o;
		return e.src.equals(this.src) && e.sink.equals(this.sink);
		
	}
}
