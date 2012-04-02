package cu.rst.gwt.client;

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
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public final class AlgAddPPanel extends FormPanel 
{
	final Label nameL;
	final TextBox nameTB;
	final Label classL;
	final Label propL;
	final Button okB;
	final FlexTable flexT;
	final FileUpload classUpload;
	final FileUpload propUpload;
	//TODO move this to server
	
	ListBox graphsLB;
	CheckBox evalAlgCB;
	
	public class AlgAddClickHandler implements ClickHandler
	{
		private AlgAddPPanel panel;
		public AlgAddClickHandler(AlgAddPPanel panel)
		{
			setPanel(panel);
		}
		public AlgAddPPanel getPanel() {
			return panel;
		}
		public void setPanel(AlgAddPPanel panel) {
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
	

	public AlgAddPPanel(final FlexTable flexT)
	{
		//click outside to close window
		super();
		this.flexT = flexT;
		
		//TODO - create static constants for the op codes 
	    super.setAction(GWT.getModuleBaseURL() + "newalg");
	    super.setEncoding(FormPanel.ENCODING_MULTIPART);
	    super.setMethod(FormPanel.METHOD_POST);
	    
		nameL = new Label("Algorithm name:");
		nameTB = new TextBox();
		nameTB.setName("nameFormElement");
		
		
		classL = new Label("Java class location:");
		propL = new Label("Properties file location:");
		
		okB = new Button("OK");
		
		evalAlgCB = new CheckBox("Evaluation Algorithm");
		
		graphsLB = new ListBox();
		graphsLB.setVisible(false);
		
		classUpload = new FileUpload();
		propUpload = new FileUpload();
		classUpload.setName("classUploadFormElement");
		propUpload.setName("propUploadFormElement");
		
		
		VerticalPanel holder = new VerticalPanel();
		HorizontalPanel hPanel = new HorizontalPanel();
		
		hPanel.add(evalAlgCB);
		hPanel.add(graphsLB);
		holder.add(nameL);
		holder.add(nameTB);
		holder.add(classL);
		holder.add(classUpload);
		holder.add(propL);
		holder.add(propUpload);
		holder.add(hPanel);
		holder.add(okB);
		
		super.setWidget(holder);
		//add(form);
		
		okB.addClickHandler(new AlgAddClickHandler(this));
		
		evalAlgCB.addClickHandler(new ClickHandler()
		{

			public void onClick(ClickEvent event) 
			{
				for(Object s : TestbedUI.graphTable)
				{
					if(!contains((String) s)) graphsLB.addItem((String)s);
				}
				if(evalAlgCB.getValue())
				{
					graphsLB.setVisible(true);
				}
				else
				{
					graphsLB.setVisible(false);
				}
			}
			
			
		});
		
		super.addSubmitHandler(new FormPanel.SubmitHandler() 
		{
			
			@Override
			public void onSubmit(SubmitEvent event) 
			{
				final String algName = nameTB.getText().trim();
				addAlg(algName);
				nameTB.setText("");
			}
		});
		
		super.addSubmitCompleteHandler(new FormPanel.SubmitCompleteHandler()
		{
			
			@Override
			public void onSubmitComplete(SubmitCompleteEvent event) 
			{
				 Window.alert(event.getResults());
				
			}
		});


	}
	
	public boolean contains(String s)
	{
		for(int i=0; i<graphsLB.getItemCount(); i++)
		{
			if(s.equals(graphsLB.getItemText(i)))
			{
				return true;
			}
		}
		return false;
	}
	
//	public void addAlg(final String name)
//	{
//		if(!TestbedUI.algTable.contains(name) && !name.equals(""))
//		{
//			TestbedUI.algTable.add(name);
//			int row = flexT.insertRow(flexT.getRowCount());
//			flexT.setText(row, 0, name);
//			Button removeB = new Button("x");
//			flexT.setWidget(row, 1, removeB);
//			
//			removeB.addClickHandler((new ClickHandler()
//			{
//				public void onClick(ClickEvent event) 
//				{
//					int removeIndex = TestbedUI.algTable.indexOf(name);
//					if(removeIndex >= 0)
//					{
//						TestbedUI.algTable.remove(removeIndex);
//						flexT.removeRow(removeIndex + 1); //don't remove the table header
//					}
//				}
//				
//			}));
//
//		}
//	}
	
	public void addAlg(final String name)
	{
		int row = flexT.insertRow(flexT.getRowCount());
		flexT.setText(row, 0, name);
		Button removeB = new Button("x");
		flexT.setWidget(row, 1, removeB);
		removeB.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event) 
			{
				//int removeIndex = TestbedUI.algTable.indexOf(name);
				
				String url = TestbedUI.JSON_URL;
				url = url + "op=rem_alg" + "&alg_name=" + name;
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
								int removeIndex = -1;
								try
								{
									String t = response.getText();
									removeIndex = Integer.parseInt(t);
								}
								catch(Exception e)
								{
									Window.alert(e.toString());
								}
								if(removeIndex >= 0) flexT.removeRow(removeIndex + 1);
							}
							
						}

						@Override
						public void onError(Request request, Throwable exception) 
						{

							
						}
			    		 
			    	 });
			    }
			    catch(RequestException e)
			    {

			    }
			}
		});
	}
	
}

