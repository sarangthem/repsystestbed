package cu.rst.gwt.client;

import com.google.gwt.core.client.JavaScriptObject;

public class GraphData extends JavaScriptObject 
{
	protected GraphData(){}
	
	// JSNI methods to get the name.
	public final native String getName() /*-{ return this.m_name; }-*/; 
	
	// JSNI methods to get the type.
	public final native String getType() /*-{ return this.m_type; }-*/;
}
