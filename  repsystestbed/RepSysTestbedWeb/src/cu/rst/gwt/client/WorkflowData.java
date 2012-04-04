package cu.rst.gwt.client;

import com.google.gwt.core.client.JavaScriptObject;

public class WorkflowData extends JavaScriptObject 
{
	protected WorkflowData(){}
	
	public final native String getName() /*-{ return this.m_name; }-*/; 
	public final native String getDefn() /*-{ return this.m_defn; }-*/;
}
