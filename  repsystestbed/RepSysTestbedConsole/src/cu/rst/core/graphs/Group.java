package cu.rst.core.graphs;

import java.util.ArrayList;

/**
 * @author partheinstein
 *
 */
@Deprecated
public class Group
{
	public class TargetGpStrAssignment
	{
		public Group m_targetGroup;
		public Strategy m_strategy;
		TargetGpStrAssignment(Group g, Strategy s)
		{
			m_targetGroup = g;
			m_strategy = s;
		}
	}

	
	private int groupId;
	private int maxNumOfAgents;
	private ArrayList<Agent> members;
	private ArrayList<TargetGpStrAssignment> targetGpStrAssignments;
	
	public Group(int groupId, int maxNumOfAgents)
	{
		this.groupId = groupId;
		this.setMaxNumOfAgents(maxNumOfAgents);
		members = new ArrayList<Agent>();
		targetGpStrAssignments = new ArrayList<TargetGpStrAssignment>();
	}
	
	public int getGroupID()
	{
		return this.groupId;
	}
	
	public void joinGroup(Agent agent)
	{
		if(!alreadyExists(agent)) members.add(agent);
	}
	
	public boolean leaveGroup(Agent agent)
	{
		return members.remove(agent);
	}
	
	public boolean alreadyExists(Agent agent)
	{
		return members.contains(agent);
	}
	
	public ArrayList<Agent> getMembers()
	{
		return members;
	}
	
	public void assignTargetGpStrategy(Group targetGroup, Strategy s)
	{
		targetGpStrAssignments.add(new TargetGpStrAssignment(targetGroup, s));
	}
	
	public ArrayList<TargetGpStrAssignment> getTargetGpStrategyAssignments()
	{
		return targetGpStrAssignments;
	}

	/**
	 * @param maxNumOfAgents the maxNumOfAgents to set
	 */
	public void setMaxNumOfAgents(int maxNumOfAgents)
	{
		this.maxNumOfAgents = maxNumOfAgents;
	}

	/**
	 * @return the maxNumOfAgents
	 */
	public int getMaxNumOfAgents()
	{
		return maxNumOfAgents;
	}
	
	@Override
	public boolean equals(Object o)
	{
		Group otherGroup = (Group)o;
		if(this.groupId == otherGroup.groupId) return true;
		return false;
	}

}
