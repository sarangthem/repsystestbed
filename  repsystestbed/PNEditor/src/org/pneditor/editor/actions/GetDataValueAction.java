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
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import org.apache.log4j.Logger;
import org.pneditor.editor.Root;
import org.pneditor.editor.commands.SetLabelCommand;
import org.pneditor.editor.filechooser.ArffFileType;
import org.pneditor.editor.filechooser.ClassFileType;
import org.pneditor.editor.filechooser.FileChooserDialog;
import org.pneditor.editor.filechooser.FileType;
import org.pneditor.petrinet.Node;
import org.pneditor.petrinet.Place;
import org.pneditor.petrinet.Transition;
import org.pneditor.util.GraphicsTools;

import cu.rst.core.alg.Algorithm;
import cu.rst.core.graphs.FHG;
import cu.rst.core.graphs.Feedback;
import cu.rst.core.graphs.Graph;
import cu.rst.core.graphs.RG;
import cu.rst.core.graphs.ReputationEdge;
import cu.rst.core.graphs.TG;
import cu.rst.core.graphs.TrustEdge;
import cu.rst.core.petrinet.BooleanSink;
import cu.rst.core.petrinet.DoubleSink;
import cu.rst.util.DotWriter;
import cu.rst.util.Util;

/**
 * 
 * @author partheinstein
 */
public class GetDataValueAction extends AbstractAction
{

	private Root root;
	private Logger logger = Logger
			.getLogger(SetDataValueAction.class.getName());

	public GetDataValueAction(Root root)
	{
		this.root = root;
		String name = "Get Data Value";
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

			if (clickedNode != null && clickedNode instanceof Place)
			{
				Place p = (Place) clickedNode;
				if (p.getRSTPlace() != null
						&& p.getRSTPlace().getContainedElement() != null
						&& p.getRSTPlace().getContainedElement() instanceof Graph)
				{
					Graph graph = (Graph) p.getRSTPlace().getContainedElement();
					try
					{
						File f = new File("output\\");
						f.mkdirs();
						String graphVizLocation = "C:\\Program Files (x86)\\Graphviz 2.28\\bin\\dot.exe";
						DotWriter.write(graph, "output\\" + graph.getName(),
								graphVizLocation);
						Thread.sleep(500); //sleep some time before opening otherwise ImageIO might throw an error
						File f1 = new File("output\\" + graph.getName()
								+ ".jpg");
						BufferedImage pic = ImageIO.read(f1);
						JLabel picLabel = new JLabel(new ImageIcon(pic));
						picLabel.setSize(50, 50);
						JDialog dialog = new JDialog(root.getParentFrame(),
								f1.getAbsolutePath());
						dialog.add(picLabel);
						dialog.setSize(500, 500);
						dialog.setVisible(true);
					} catch (Exception ex)
					{
						JOptionPane.showMessageDialog(root.getParentFrame(),
								ex.getMessage());
					}

				} else if (p.getRSTPlace() != null
						&& p.getRSTPlace().getContainedElement() != null
						&& p.getRSTPlace().getContainedElement() instanceof BooleanSink)
				{
					BooleanSink bs = (BooleanSink) p.getRSTPlace()
							.getContainedElement();

					JOptionPane.showMessageDialog(root.getParentFrame(),
							bs.m_val);

				} else if (p.getRSTPlace() != null
						&& p.getRSTPlace().getContainedElement() != null
						&& p.getRSTPlace().getContainedElement() instanceof DoubleSink)
				{
					DoubleSink ds = (DoubleSink) p.getRSTPlace()
							.getContainedElement();
					JOptionPane.showMessageDialog(root.getParentFrame(),
							ds.m_val);

				}
			}
		}
	}

}
