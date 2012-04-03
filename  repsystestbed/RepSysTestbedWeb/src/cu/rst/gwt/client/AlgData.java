package cu.rst.gwt.client;

import com.google.gwt.core.client.JavaScriptObject;

public class AlgData  extends JavaScriptObject 
{
	protected AlgData(){}
	
	// JSNI methods to get the name.
	public final native String getName() /*-{ return this.m_name; }-*/; 
		
}
