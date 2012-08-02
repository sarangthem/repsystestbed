package cu.rst.core.petrinet;

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
public class Token implements Comparable
{	
	public ArrayList m_changes;
	public Object m_place;
	public int m_id;
	private static int g_id;

	public Token(ArrayList changes, Object place)
	{
		m_changes = changes;
		m_place = place;
		m_id = g_id++;
	}

	@Override
	public int compareTo(Object o) 
	{
		Token t = (Token)o; //yes, cast exception can occur. So caller make sure you catch this
		if(m_id < t.m_id) return -1;
		else if(m_id > t.m_id) return 1;
		return 0;
	}
	
	@Override
	public boolean equals(Object o)
	{
		if(!(o instanceof Token)) return false;
		Token t = (Token)o;
		return t.m_id==m_id;
				
	}
	
	
}