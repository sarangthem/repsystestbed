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
import java.io.File;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
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
import cu.rst.util.Util;

/**
 * 
 * @author partheinstein
 */
public class SetDataValueAction extends AbstractAction
{

	private Root root;
	private Logger logger = Logger
			.getLogger(SetDataValueAction.class.getName());

	public SetDataValueAction(Root root)
	{
		this.root = root;
		String name = "Set Data Value";
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
					File f = displayFileChooser();
					if (p.getRSTPlace().getContainedElement() instanceof FHG)
					{
						FHG fhg = (FHG) p.getRSTPlace().getContainedElement();

						if (f != null)
						{
							ArrayList<Feedback> feedbacks;
							try
							{
								feedbacks = (ArrayList<Feedback>) Util
										.generateHardcoded(f.getAbsolutePath());
								fhg.addFeedbacks(feedbacks);
								logger.debug("FHG populated.");
							} catch (Exception e1)
							{
								JOptionPane.showMessageDialog(
										root.getParentFrame(), e1.getMessage());
							}

						}
					} else if (p.getRSTPlace().getContainedElement() instanceof RG)
					{
						RG rg = (RG) p.getRSTPlace().getContainedElement();

						if (f != null)
						{
							ArrayList<ReputationEdge> repEdges;
							try
							{
								repEdges = Util.generateReputationEdges(f
										.getAbsolutePath());
								rg.addEdges(repEdges);
								logger.debug("RG populated.");
							} catch (Exception e1)
							{
								JOptionPane.showMessageDialog(
										root.getParentFrame(), e1.getMessage());
							}

						}

					} else if (p.getRSTPlace().getContainedElement() instanceof TG)
					{
						TG tg = (TG) p.getRSTPlace().getContainedElement();

						if (f != null)
						{
							ArrayList<TrustEdge> repEdges;
							try
							{
								repEdges = Util.generateTrustEdges(f
										.getAbsolutePath());
								tg.addTrustEdges(repEdges);
								logger.debug("TG populated.");
							} catch (Exception e1)
							{
								JOptionPane.showMessageDialog(
										root.getParentFrame(), e1.getMessage());
								
							}

						}
					}
				} else if (p.getRSTPlace() != null
						&& p.getRSTPlace().getContainedElement() != null
						&& p.getRSTPlace().getContainedElement() instanceof BooleanSink)
				{
					BooleanSink bs = (BooleanSink) p.getRSTPlace()
							.getContainedElement();
					String value = JOptionPane.showInputDialog(
							root.getParentFrame(), "Value:",
							bs.m_val);

					if (value != null)
					{
						Boolean val = Boolean.parseBoolean(value);
						bs.setVal(val);
						logger.debug("Boolean value set.");
					}
				} else if (p.getRSTPlace() != null
						&& p.getRSTPlace().getContainedElement() != null
						&& p.getRSTPlace().getContainedElement() instanceof DoubleSink)
				{
					DoubleSink ds = (DoubleSink) p.getRSTPlace()
							.getContainedElement();
					String value = JOptionPane.showInputDialog(
							root.getParentFrame(), "Value:",
							ds.m_val);

					if (value != null)
					{
						try
						{
							Double val = Double.valueOf(value);
							ds.setVal(val);
							logger.debug("Double value populated.");
						} catch (NumberFormatException nfe)
						{
							JOptionPane.showMessageDialog(
									root.getParentFrame(), nfe.getMessage());
						}
					}

				}
			}
		}
	}

	private File displayFileChooser()
	{
		FileChooserDialog chooser = new FileChooserDialog();
		ArrayList<FileType> fileTypes = new ArrayList<FileType>();
		fileTypes.add(new ArffFileType());
		for (FileType fileType : fileTypes)
		{
			chooser.addChoosableFileFilter(fileType);
		}

		chooser.setAcceptAllFileFilterUsed(false);
		chooser.setCurrentDirectory(root.getCurrentDirectory());

		if (chooser.showOpenDialog(root.getParentFrame()) == JFileChooser.APPROVE_OPTION)
		{
			File file = chooser.getSelectedFile();
			root.setCurrentDirectory(chooser.getCurrentDirectory());
			return file;
		} else
		{
			return null;
		}
	}
}
