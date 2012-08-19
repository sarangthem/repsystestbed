/*
 * Copyright (C) 2008-2010 Martin Riesz <riesz.martin at gmail.com>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.pneditor.editor.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import org.pneditor.editor.Root;
import org.pneditor.editor.commands.SetLabelCommand;
import org.pneditor.petrinet.Node;
import org.pneditor.petrinet.Place;
import org.pneditor.util.GraphicsTools;

import cu.rst.core.graphs.FHG;
import cu.rst.core.graphs.RG;
import cu.rst.core.graphs.TG;
import cu.rst.core.petrinet.BooleanSink;
import cu.rst.core.petrinet.DoubleSink;

/**
 * 
 * @author partheinstein
 */
public class SetDataTypeAction extends AbstractAction
{

	private Root root;

	public SetDataTypeAction(Root root)
	{
		this.root = root;
		String name = "Set Data Type";
		putValue(NAME, name);
		putValue(SMALL_ICON, GraphicsTools.getIcon("pneditor/label.gif"));
		putValue(SHORT_DESCRIPTION, name);
		// putValue(MNEMONIC_KEY, KeyEvent.VK_R);
		// putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("R"));
		setEnabled(false);
	}

	public void actionPerformed(ActionEvent e)
	{
		if (root.getClickedElement() != null
				&& root.getClickedElement() instanceof Node)
		{
			Node clickedNode = (Node) root.getClickedElement();
			Object[] possibilities = { "Feedback History Graph",
					"Reputation Graph", "Trust Graph", "Number", "Boolean" };
			String s = (String) JOptionPane.showInputDialog(
					root.getParentFrame(), "Select Type:\n", "Set Data",
					JOptionPane.PLAIN_MESSAGE, null, possibilities, "Graph");
			
			if(clickedNode instanceof Place)
			{
				Place p = (Place) clickedNode;
				if(s != null)
				{
					if(s.equals("Feedback History Graph"))
					{
						FHG fhg = new FHG();
						p.setRSTPlace(new cu.rst.core.petrinet.Place(fhg));
					}
					else if(s.equals("Reputation Graph"))
					{
						RG rg = new RG();
						p.setRSTPlace(new cu.rst.core.petrinet.Place(rg));
					}
					else if(s.equals("Trust Graph"))
					{
						TG tg = new TG();
						p.setRSTPlace(new cu.rst.core.petrinet.Place(tg));
					}
					else if(s.equals("Number"))
					{
						DoubleSink ds = new DoubleSink();
						p.setRSTPlace(new cu.rst.core.petrinet.Place(ds));
					}
					else if(s.equals("Boolean"))
					{
						BooleanSink bs = new BooleanSink();
						p.setRSTPlace(new cu.rst.core.petrinet.Place(bs));
					}	
					
					try
					{
						p.getRSTPlace().setWorkflow(root.getDocument().workflow);
					} 
					catch (Exception e1)
					{
						JOptionPane.showMessageDialog(root.getParentFrame(), e1);
					}
				}
				
			}
			
			
			
		}
	}
}
