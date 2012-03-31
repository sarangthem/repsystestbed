package cu.rst.gwt.client;

import java.util.ArrayList;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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

public class GraphAddPanel extends PopupPanel 
{
	FormPanel form;
	Label nameL;
	TextBox nameTB;
	Button okB;
	FlexTable flexT;



	
	public GraphAddPanel(final FlexTable flexT)
	{
		super(true);
		this.flexT = flexT;
		form = new FormPanel();
	    form.setAction("/myFormHandler");
		nameL = new Label("Graph name:");
		nameTB = new TextBox();
		okB = new Button("OK");

		
		FileUpload graphUpload = new FileUpload();
		graphUpload.setName("graphUploadFormElement");
		
		VerticalPanel holder = new VerticalPanel();

		holder.add(nameL);
		holder.add(nameTB);
		holder.add(graphUpload);

		holder.add(okB);
		
		add(holder);
		
		okB.addClickHandler(new ClickHandler()
		{

			public void onClick(ClickEvent event) 
			{
				final String graphName = nameTB.getText().toUpperCase().trim();
				if(!TestbedUI.graphTable.contains(graphName) && !graphName.equals(""))
				{
					TestbedUI.graphTable.add(graphName);
					int row = flexT.insertRow(flexT.getRowCount());
					flexT.setText(row, 0, graphName);
					Button removeB = new Button("x");
					flexT.setWidget(row, 1, removeB);
					
					removeB.addClickHandler((new ClickHandler()
					{
						public void onClick(ClickEvent event) 
						{
							int removeIndex = TestbedUI.graphTable.indexOf(graphName);
							if(removeIndex >= 0)
							{
								TestbedUI.graphTable.remove(removeIndex);
								flexT.removeRow(removeIndex + 1); //don't remove the table header
							}
						}
						
					}));

				}
				nameTB.setText("");				
				hide();			
			}
			
		});
		

		

	}
}
