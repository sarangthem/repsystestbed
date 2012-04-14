package cu.rst.gwt.client;

import java.util.ArrayList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;

public class WorkflowAddPanel extends FormPanel 
{
	Label nameL;
	TextBox nameTB;
	Label workflowDefnL;
	TextBox workflowDefnTB;
	Button okB;
	FlexTable flexT;

	
	public class WorkflowAddClickHandler implements ClickHandler
	{
		private WorkflowAddPanel panel;
		public WorkflowAddClickHandler(WorkflowAddPanel panel)
		{
			setPanel(panel);
		}
		public WorkflowAddPanel getPanel() {
			return panel;
		}
		public void setPanel(WorkflowAddPanel panel) {
			this.panel = panel;
		}
		
		@Override
		public void onClick(ClickEvent event) 
		{
			panel.submit();
			panel.setVisible(false);
			panel.reset();
		}
	}
	
	public WorkflowAddPanel(FlexTable flexTable)
	{
		super();
		this.flexT = flexTable;
	    super.setAction(GWT.getModuleBaseURL() + "newworkflow");
	    super.setEncoding(FormPanel.ENCODING_MULTIPART);
	    super.setMethod(FormPanel.METHOD_POST);
		nameL = new Label("Workflow name:");
		nameTB = new TextBox();
		nameTB.setName("nameFormElement");
		workflowDefnL = new Label("Workflow definition:");
		workflowDefnTB = new TextBox();
		workflowDefnTB.setName("defnFormElement");
		okB = new Button("OK");
		
	
		VerticalPanel holder = new VerticalPanel();
		
		holder.add(nameL);
		holder.add(nameTB);
		holder.add(workflowDefnL);
		holder.add(workflowDefnL);
		holder.add(workflowDefnTB);
		holder.add(okB);
		add(holder);
		
		okB.addClickHandler(new WorkflowAddClickHandler(this));
		
		super.addSubmitHandler(new FormPanel.SubmitHandler() 
		{
			
			@Override
			public void onSubmit(SubmitEvent event) 
			{
				final String workflowName = nameTB.getText().trim();
				final String workflowDefn = workflowDefnTB.getText().trim();
				addWorkflow(workflowName, workflowDefn);
			}
		});
		
		super.addSubmitCompleteHandler(new FormPanel.SubmitCompleteHandler()
		{
			
			@Override
			public void onSubmitComplete(SubmitCompleteEvent event) 
			{
				 if(event.getResults() != null && !event.getResults().isEmpty())
				 {
					 Window.alert("An error occured while adding this workflow. See server logs.");
					 final String workflowName = nameTB.getText().trim();
					 int removeIndex = indexOf(workflowName);
					 if(removeIndex >= 0)
					 {
						 TestbedUI.workflowTable.remove(removeIndex);
						 flexT.removeRow(removeIndex + 1); //don't remove the table header
					 }
				 }
				
			}
		});
		
		
	}
		
	
	public void addWorkflow(final String workflowName, final String workflowDefn) 
	{
		if(!TestbedUI.workflowTable.contains(new Workflow(workflowName, null)) && !workflowName.equals("")
				&& !workflowDefn.equals(""))
		{
			TestbedUI.workflowTable.add(new Workflow(workflowName, workflowDefn));
			int row = flexT.insertRow(flexT.getRowCount());
			flexT.setText(row, 0, workflowName);
			flexT.setText(row, 1, workflowDefn);
			Button removeB = new Button("x");
			flexT.setWidget(row, 2, removeB);
			Button runB = new Button(">");
			flexT.setWidget(row, 3, runB);
			Button evalB = new Button("O");
			flexT.setWidget(row, 4, evalB);
			
			removeB.addClickHandler((new ClickHandler()
			{
				public void onClick(ClickEvent event) 
				{
					int removeIndex = indexOf(workflowName);
					String url = TestbedUI.JSON_URL;
					url = url + "op=rem_workflow" + "&workflow_name=" + workflowName;
					// Send request to server and catch any errors.
				    RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);
				    try 
				    {
				    	Request request = builder.sendRequest(null, new RequestCallback()
				    	 {

							@Override
							public void onResponseReceived(Request request, Response response) 
							{
								if(response.getStatusCode() == 200)
								{
									//Window.alert("Graph " + name + " removed.");
								}
								else
								{
									Window.alert("Got error from server: " + response.getStatusCode());
								}
								
							}

							@Override
							public void onError(Request request, Throwable exception) 
							{
								//TODO - do something
								
							}
				    		 
				    	 });
				    }
				    catch(RequestException e)
				    {
				    	//TODO - do something
				    }
					
					if(removeIndex >= 0)
					{
						TestbedUI.workflowTable.remove(removeIndex);
						flexT.removeRow(removeIndex + 1); //don't remove the table header
					}
				}
				
			}));
			
			runB.addClickHandler(new ClickHandler()
			{

				public void onClick(ClickEvent event) 
				{
					PopupPanel ppanel = new PopupPanel(true);
					FlowPanel fPanel = new FlowPanel();
					fPanel.setTitle("Workflow");
					
					fPanel.add(new Label("This is where graph transformations will be displayed."));
					ppanel.add(fPanel);
					ppanel.center();
					
					int removeIndex = indexOf(workflowName);
					String url = TestbedUI.JSON_URL;
					url = url + "op=run_workflow" + "&workflow_name=" + workflowName;
					// Send request to server and catch any errors.
				    RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);
					try 
				    {
				    	Request request = builder.sendRequest(null, new RequestCallback()
				    	 {

							@Override
							public void onResponseReceived(Request request, Response response) 
							{
								if(response.getStatusCode() == 200)
								{
									//TODO: Report some status here and display the graphs
								}
								else
								{
									Window.alert("Got error from server: " + response.getStatusCode());
								}
								
							}

							@Override
							public void onError(Request request, Throwable exception) 
							{
								//TODO - do something
								
							}
				    		 
				    	 });
				    }
				    catch(RequestException e)
				    {
				    	//TODO - do something
				    }
					
				}
				
			});
			
			evalB.addClickHandler(new ClickHandler()
			{

				public void onClick(ClickEvent event) 
				{
					PopupPanel ppanel = new PopupPanel(true);
					Button evalB = new Button("Evaluate");
					Label algL = new Label("Choose Algorithm:");
					ListBox lb = new ListBox();
					for(Object n : TestbedUI.algTable)
					{
						lb.addItem((String) n);
					}
					VerticalPanel vpanel = new VerticalPanel();
					vpanel.add(algL);
					vpanel.add(lb);
					vpanel.add(evalB);
					ppanel.add(vpanel);
					ppanel.center();
				}
				
			});
		}
		
	}


	public boolean contains(String name)
	{
		for(Object ge : TestbedUI.workflowTable)
		{
			if(((Workflow)ge).getName().equals(name.toUpperCase().trim()))
			{
				return true;
			}
		}
		return false;
	}
	
	public int indexOf(String name)
	{
		return TestbedUI.workflowTable.indexOf(new Workflow(name, null));
	}
	
	
}
