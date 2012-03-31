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

public class AlgAddPPanel extends PopupPanel 
{
	FormPanel form;
	Label nameL;
	TextBox nameTB;
	Label classL;
	Label propL;
	Button okB;
	FlexTable flexT;
	//TODO move this to server
	
	ListBox graphsLB;
	CheckBox evalAlgCB;
	
	public AlgAddPPanel(final FlexTable flexT)
	{
		//click outside to close window
		super(true);
		this.flexT = flexT;
		form = new FormPanel();
	    form.setAction("/myFormHandler");
		nameL = new Label("Algorithm name:");
		nameTB = new TextBox();
		classL = new Label("Java class location:");
		propL = new Label("Properties file location:");
		okB = new Button("OK");
		evalAlgCB = new CheckBox("Evaluation Algorithm");
		graphsLB = new ListBox();
		graphsLB.setVisible(false);
		
		FileUpload classUpload = new FileUpload();
		FileUpload propUpload = new FileUpload();
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
		add(holder);
		
		okB.addClickHandler(new ClickHandler()
		{

			public void onClick(ClickEvent event) 
			{
				final String algName = nameTB.getText().toUpperCase().trim();
				addAlg(algName);
				nameTB.setText("");				
				hide();			
			}
			
		});
		
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
	
	public void addAlg(final String name)
	{
		if(!TestbedUI.algTable.contains(name) && !name.equals(""))
		{
			TestbedUI.algTable.add(name);
			int row = flexT.insertRow(flexT.getRowCount());
			flexT.setText(row, 0, name);
			Button removeB = new Button("x");
			flexT.setWidget(row, 1, removeB);
			
			removeB.addClickHandler((new ClickHandler()
			{
				public void onClick(ClickEvent event) 
				{
					int removeIndex = TestbedUI.algTable.indexOf(name);
					if(removeIndex >= 0)
					{
						TestbedUI.algTable.remove(removeIndex);
						flexT.removeRow(removeIndex + 1); //don't remove the table header
					}
				}
				
			}));

		}
	}
	
}

