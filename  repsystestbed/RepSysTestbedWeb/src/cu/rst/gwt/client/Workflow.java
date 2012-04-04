package cu.rst.gwt.client;

import java.io.Serializable;

public class Workflow implements Serializable
{
	private String m_name;
	private String m_defn;
	
	public Workflow(){}
	
	public Workflow(String name, String defn)
	{
		setName(name);
		setDefn(defn);
	}
	
	public String getDefn() 
	{
		return m_defn;
	}
	public void setDefn(String m_defn) 
	{
		this.m_defn = m_defn;
	}
	public String getName() 
	{
		return m_name;
	}
	public void setName(String m_name) 
	{
		this.m_name = m_name;
	}
	
	@Override
	public boolean equals(Object o)
	{
		if(!(o instanceof Workflow))
		{
			return false;
		}
		
		if(((Workflow)o).getName().equals(getName())) return true;
		return false;
		
	}
}
