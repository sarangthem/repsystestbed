package cu.rst.gwt.client;

import java.io.Serializable;

public class Graph  implements Serializable
{
	private String m_name;
	private String m_type;
	public Graph(){}
	
	public Graph(String name, String type)
	{
		setName(name);
		setType(type);
	}
	
	public String getName() 
	{
		return m_name;
	}
	
	public void setName(String m_name) 
	{
		this.m_name = m_name;
	}

	public String getType() 
	{
		return m_type;
	}

	public void setType(String m_type)
	{
		this.m_type = m_type;
	}
	
}
