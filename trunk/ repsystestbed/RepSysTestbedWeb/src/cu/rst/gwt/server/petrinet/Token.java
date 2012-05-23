package cu.rst.gwt.server.petrinet;

import java.util.ArrayList;

/**
 * Each token contains a list of changes. These changes can be Feedbacks, reputation edges, trust edges.
 * It also has a reference to a place which can be a FHG, RG, or a TG. It's use is as follows:
 * In addition to the changes in a graph, an algorithm also needs to the graph itself. This reference is set by:
 * 1. a user
 * 2. a transition (See Transition.fire(). for e.g. ET creates tokens, adds the new reputation edges and sets RG as m_place). 
 * 
 * @author partheinstein
 *
 */
public class Token
{
	public static enum Token_Type{};
	
	public ArrayList m_changes;
	public Object m_place;
	
	public Token(ArrayList changes, Object place)
	{
		m_changes = changes;
		m_place = place;
	}
}