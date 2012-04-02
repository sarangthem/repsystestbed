package cu.rst.gwt.client;

import java.util.ArrayList;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class TestbedUI implements EntryPoint {
	/**
	 * The message displayed to the user when the server cannot be reached or
	 * returns an error.
	 */
	private static final String SERVER_ERROR = "An error occurred while "
			+ "attempting to contact the server. Please check your network "
			+ "connection and try again.";


	//TODO move this to server
	public static ArrayList algTable = new ArrayList();
	public static ArrayList graphTable = new ArrayList();
	public static	ArrayList<Object> workflowTable = new ArrayList<Object>();
	final FlexTable flexTAlg = new FlexTable();
	final FlexTable flexTGraph = new FlexTable();
	final FlexTable flexTWF = new FlexTable();
	public static final String JSON_URL = GWT.getModuleBaseURL() + "rstservice?";
	Label errMsg = new Label("Error:");
	final VerticalPanel vertP = new VerticalPanel();
	final ParthyButton addB = new ParthyButton("Add");
	final AlgAddPPanel addAlgPanel = new AlgAddPPanel(flexTAlg);
	final GraphAddPanel addGraphPanel = new GraphAddPanel(flexTGraph);
	final WorkflowAddPanel addWorkflowPanel = new WorkflowAddPanel(flexTWF); 
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() 
	{
		Hyperlink h1 = new Hyperlink();
		h1.setText("Tests");
		
		HorizontalPanel hpanel = new HorizontalPanel();
		
		TabPanel tabP = new TabPanel();

	
		
		flexTAlg.setText(0, 0, "Name");
		flexTAlg.setText(0, 1, "");
		
		flexTGraph.setText(0, 0, "Name");
		flexTGraph.setText(0, 1, "");
		
		flexTWF.setText(0, 0, "Name");
		flexTWF.setText(0, 1, "Definition");
		flexTWF.setText(0, 2, "");
		flexTWF.setText(0, 3, "");
		flexTWF.setText(0, 4, "");
		
		tabP.add(flexTAlg, "Algorithms");
		tabP.add(flexTGraph, "Graphs");
		tabP.add(flexTWF, "Workflows");
		tabP.selectTab(0);
				
		vertP.add(tabP);
		vertP.add(addB);
		vertP.add(errMsg);
		errMsg.setVisible(false);
		
		hpanel.add(vertP);
		
		
		RootPanel.get().add(hpanel);
		RootPanel.get().add(addAlgPanel);
		addAlgPanel.setVisible(false);
		
		tabP.addSelectionHandler(addB);
		populateAlgs();
		
		addB.addClickHandler(new ClickHandler()
		{

			public void onClick(ClickEvent event) 
			{
				if(addB.tabSelected == 0)
				{
					addAlgPanel.setVisible(true);
				}
				else if(addB.tabSelected == 1)
				{
					addGraphPanel.center();
				}
				else if(addB.tabSelected == 2)
				{
					addWorkflowPanel.center();
				}
			}
		});
		
	}
	
	private void populateAlgs()
	{
		String url = this.JSON_URL;
		url = url + "op=get_algs";
		// Send request to server and catch any errors.
	    RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);
	    try 
	    {
	    	Request request = builder.sendRequest(null, new RequestCallback()
	    	 {

				@Override
				public void onResponseReceived(Request request, Response response) 
				{
					 if (200 == response.getStatusCode()) 
					 {
						 updateTable(asArrayOfAlgData(response.getText()));
					 }
					 else 
					 {
						 displayError("Couldn't retrieve JSON (" + response.getStatusText() + ")");
				     }
					
				}

				@Override
				public void onError(Request request, Throwable exception) 
				{
					displayError("Couldn't retrieve JSON");
					
				}
	    		 
	    	 });
	    }
	    catch(RequestException e)
	    {
	    	displayError("Couldn't retrieve JSON.");
	    }
	}
	
	private final native JsArray<AlgData> asArrayOfAlgData(String json) 
	/*-{
    	return eval(json);
  	}-*/;
	
	private void displayError(String msg)
	{
		errMsg.setText(msg);
	}
	
	private void updateTable(JsArray<AlgData> data)
	{
		for(int i = 0; i<data.length(); i++)
		{
			addAlgPanel.addAlg(data.get(i).getName());
		}
	}
	
}
