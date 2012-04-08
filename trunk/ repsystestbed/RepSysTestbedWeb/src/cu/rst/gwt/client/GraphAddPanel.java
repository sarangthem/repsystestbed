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
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;

public class GraphAddPanel extends FormPanel 
{
	final Label nameL;
	final TextBox nameTB;
	final Button okB;
	final FlexTable flexT;
	final FileUpload graphUpload;
	final ListBox listbox;

	public class GraphAddClickHandler implements ClickHandler
	{
		private GraphAddPanel panel;
		public GraphAddClickHandler(GraphAddPanel panel)
		{
			setPanel(panel);
		}
		public GraphAddPanel getPanel() {
			return panel;
		}
		public void setPanel(GraphAddPanel panel) {
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

	
	public GraphAddPanel(final FlexTable flexT)
	{
		super();
		this.flexT = flexT;
		
		//TODO - create static constants for the op codes 
	    super.setAction(GWT.getModuleBaseURL() + "newgraph");
	    super.setEncoding(FormPanel.ENCODING_MULTIPART);
	    super.setMethod(FormPanel.METHOD_POST);
	    
		nameL = new Label("Graph name:");
		nameTB = new TextBox();
		nameTB.setName("nameFormElement");
		okB = new Button("OK");
		listbox = new ListBox();
		listbox.setName("graphTypeElement");
		listbox.addItem("FHG");
		listbox.addItem("RG");
		listbox.addItem("TG");
		listbox.setVisibleItemCount(1);
		
		graphUpload = new FileUpload();
		graphUpload.setName("graphUploadFormElement");
		
		VerticalPanel holder = new VerticalPanel();

		holder.add(nameL);
		holder.add(nameTB);
		holder.add(graphUpload);
		holder.add(listbox);

		holder.add(okB);
		
		add(holder);
		
		okB.addClickHandler(new GraphAddClickHandler(this));
		
		super.addSubmitHandler(new FormPanel.SubmitHandler() 
		{
			
			@Override
			public void onSubmit(SubmitEvent event) 
			{
				final String graphName = nameTB.getText().trim();
				addGraph(graphName);
			}
		});
		
		super.addSubmitCompleteHandler(new FormPanel.SubmitCompleteHandler()
		{
			
			@Override
			public void onSubmitComplete(SubmitCompleteEvent event) 
			{
				 //Window.alert(event.getResults());
				
			}
		});

		

	}
	
	public void addGraph(final String name)
	{
		if(!TestbedUI.graphTable.contains(name) && !name.equals(""))
		{
			TestbedUI.graphTable.add(name);
			int row = flexT.insertRow(flexT.getRowCount());
			flexT.setText(row, 0, name);
			Button removeB = new Button("x");
			flexT.setWidget(row, 1, removeB);
			removeB.addClickHandler(new ClickHandler()
			{
				public void onClick(ClickEvent event) 
				{
					int removeIndex = TestbedUI.graphTable.indexOf(name);
					
					String url = TestbedUI.JSON_URL;
					url = url + "op=rem_graph" + "&graph_name=" + name;
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
					
				    if(removeIndex >= 0) flexT.removeRow(removeIndex + 1);
				    TestbedUI.graphTable.remove(name);
				}
			});
		}
		
	}
}
