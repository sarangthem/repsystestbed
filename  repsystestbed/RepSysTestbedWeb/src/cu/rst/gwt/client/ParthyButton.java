package cu.rst.gwt.client;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.Button;

public class ParthyButton extends Button implements SelectionHandler
{
	public int tabSelected;
	public ParthyButton(String name)
	{
		super(name);
		tabSelected = 0;
	}
	public void onSelection(SelectionEvent event) 
	{
		tabSelected = (Integer) event.getSelectedItem();
		
	}
	
	
}
