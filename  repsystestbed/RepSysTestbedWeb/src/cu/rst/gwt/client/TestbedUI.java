package cu.rst.gwt.client;

import java.util.ArrayList;

import org.apache.log4j.BasicConfigurator;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.ui.Button;
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
public class TestbedUI implements EntryPoint, SelectionHandler<Integer> 
{
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
	public static	ArrayList<Workflow> workflowTable = new ArrayList<Workflow>();
	final FlexTable flexTAlg = new FlexTable();
	final FlexTable flexTGraph = new FlexTable();
	final FlexTable flexTWF = new FlexTable();
	public static final String JSON_URL = GWT.getModuleBaseURL() + "rstservice?";
	Label errMsg = new Label("Error:");
	final VerticalPanel vertP = new VerticalPanel();
	final ParthyButton addB = new ParthyButton("Add");
	public final Button resetB = new Button("Reset");
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
		HorizontalPanel hPanel2 = new HorizontalPanel();
		hPanel2.add(addB);
		hPanel2.add(resetB);
		vertP.add(hPanel2);
		resetB.setVisible(false);
		errMsg.setVisible(false);
		
		hpanel.add(vertP);
		
		
		RootPanel.get().add(hpanel);
		RootPanel.get().add(addAlgPanel);
		RootPanel.get().add(addGraphPanel);
		RootPanel.get().add(addWorkflowPanel);
		addAlgPanel.setVisible(false);
		addGraphPanel.setVisible(false);
		addWorkflowPanel.setVisible(false);
		
		tabP.addSelectionHandler(addB);
		tabP.addSelectionHandler(this);
		populateAlgs();
		populateGraphs();
		populateWorkflows();
		
		addB.addClickHandler(new ClickHandler()
		{

			public void onClick(ClickEvent event) 
			{
				if(addB.tabSelected == 0)
				{
					addAlgPanel.setVisible(true);
					addGraphPanel.setVisible(false);
					addWorkflowPanel.setVisible(false);
				}
				else if(addB.tabSelected == 1)
				{
					addGraphPanel.setVisible(true);
					addAlgPanel.setVisible(false);
					addWorkflowPanel.setVisible(false);
				}
				else if(addB.tabSelected == 2)
				{
					addWorkflowPanel.setVisible(true);
					addAlgPanel.setVisible(false);
					addGraphPanel.setVisible(false);
				}
			}
		});
		
		resetB.addClickHandler(new ClickHandler()
		{

			public void onClick(ClickEvent event) 
			{
				String url = TestbedUI.JSON_URL;
				url = url + "op=reset_workflows";
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
								 updateAlgTable(asArrayOfAlgData(response.getText()));
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
		});
		
		
		
	}
	
	private void populateWorkflows() 
	{
		String url = this.JSON_URL;
		url = url + "op=get_workflows";
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
						 updateWorkflowTable(asArrayOfWorkflowData(response.getText()));
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



	private void populateGraphs() 
	{
		String url = this.JSON_URL;
		url = url + "op=get_graphs";
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
						 updateGraphTable(asArrayOfGraphData(response.getText()));
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
						 updateAlgTable(asArrayOfAlgData(response.getText()));
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
	
	private final native JsArray<GraphData> asArrayOfGraphData(String json) 
	/*-{
    	return eval(json);
  	}-*/;
	
	private final native JsArray<WorkflowData> asArrayOfWorkflowData(String json) 
	/*-{
    	return eval(json);
  	}-*/;
	
	private void displayError(String msg)
	{
		errMsg.setText(msg);
	}
	
	private void updateAlgTable(JsArray<AlgData> data)
	{
		for(int i = 0; i<data.length(); i++)
		{
			addAlgPanel.addAlg(data.get(i).getName());
		}
	}
	
	private void updateGraphTable(JsArray<GraphData> data)
	{
		for(int i = 0; i<data.length(); i++)
		{
			addGraphPanel.addGraph(data.get(i).getName());
		}
	}
	private void updateWorkflowTable(
			JsArray<WorkflowData> data) {
		for(int i = 0; i<data.length(); i++)
		{
			addWorkflowPanel.addWorkflow(data.get(i).getName(), data.get(i).getDefn());
		}
		
	}

	@Override
	public void onSelection(SelectionEvent<Integer> event) 
	{
		int tabSelected = (Integer) event.getSelectedItem();
		if(tabSelected == 2)
		{
			resetB.setVisible(true);
		}
		else
		{
			resetB.setVisible(false);
		}
		addAlgPanel.setVisible(false);
		addGraphPanel.setVisible(false);
		addWorkflowPanel.setVisible(false);
	}
}
