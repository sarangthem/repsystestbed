package cu.rst.gwt.client;

import java.io.Serializable;

public class Graph  implements Serializable
{
	private String m_name;
	public Graph(){}
	
	public Graph(String name)
	{
		setName(name);
	}
	
	public String getName() 
	{
		return m_name;
	}
	
	public void setName(String m_name) 
	{
		this.m_name = m_name;
	}
	
}
