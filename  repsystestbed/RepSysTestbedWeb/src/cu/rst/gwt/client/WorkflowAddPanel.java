package cu.rst.gwt.client;

import java.util.ArrayList;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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

public class WorkflowAddPanel extends PopupPanel 
{
	FormPanel form;
	Label nameL;
	TextBox nameTB;
	Label workflowDefnL;
	TextBox workflowDefnTB;
	Button okB;
	FlexTable flexT;
	//TODO move this to server

	class GraphElement
	{
		String name;
		String workFlowDefn;
		
		public GraphElement(String name, String workFlowDefn)
		{
			this.name = name;
			this.workFlowDefn = workFlowDefn;
		}
		
		@Override
		public boolean equals(Object o)
		{
			if(!(o instanceof GraphElement))
			{
				return false;
			}
			return ((GraphElement)o).name.equals(name);
		}
	}
	public WorkflowAddPanel(FlexTable flexTable)
	{
		super(true);
		this.flexT = flexTable;
		form = new FormPanel();
	    form.setAction("/myFormHandler");
		nameL = new Label("Workflow name:");
		nameTB = new TextBox();
		workflowDefnL = new Label("Workflow definition:");
		workflowDefnTB = new TextBox();
		
		okB = new Button("OK");
		
	
		VerticalPanel holder = new VerticalPanel();
		
		holder.add(nameL);
		holder.add(nameTB);
		holder.add(workflowDefnL);
		holder.add(workflowDefnL);
		holder.add(workflowDefnTB);
		holder.add(okB);
		add(holder);
		
		okB.addClickHandler(new ClickHandler()
		{

			public void onClick(ClickEvent event) 
			{
				final String workflowDefnName = nameTB.getText().toUpperCase().trim();
				if(!contains(workflowDefnName) && !workflowDefnName.equals(""))
				{
					TestbedUI.workflowTable.add(new GraphElement(workflowDefnName, workflowDefnTB.getText()));
					int row = flexT.insertRow(flexT.getRowCount());
					flexT.setText(row, 0, workflowDefnName);
					flexT.setText(row, 1, workflowDefnTB.getText());
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
							int removeIndex = indexOf(workflowDefnName);
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
				nameTB.setText("");	
				workflowDefnTB.setText("");
				hide();			
			}
		});
	}
		
	
	public boolean contains(String name)
	{
		for(Object ge : TestbedUI.workflowTable)
		{
			if(((GraphElement)ge).name.equals(name.toUpperCase().trim()))
			{
				return true;
			}
		}
		return false;
	}
	
	public int indexOf(String name)
	{
		return TestbedUI.workflowTable.indexOf(new GraphElement(name, null));
	}
	
	
}
