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
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.pneditor.editor.Root;
import org.pneditor.editor.commands.SetLabelCommand;
import org.pneditor.editor.filechooser.ClassFileType;
import org.pneditor.editor.filechooser.FileChooserDialog;
import org.pneditor.editor.filechooser.FileType;
import org.pneditor.editor.filechooser.FileTypeException;
import org.pneditor.petrinet.Document;
import org.pneditor.petrinet.Node;
import org.pneditor.petrinet.Transition;
import org.pneditor.util.GraphicsTools;

import cu.rst.core.alg.Algorithm;
import cu.rst.util.Util;

/**
 * 
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class SetAlgorithmAction extends AbstractAction
{

	private Root root;
	private Logger logger = LogManager.getLogger(SetAlgorithmAction.class.getName());

	public SetAlgorithmAction(Root root)
	{
		this.root = root;
		String name = "Set Algorithm";
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
			if(clickedNode instanceof Transition)
			{
				FileChooserDialog chooser = new FileChooserDialog();
				ArrayList<FileType> fileTypes = new ArrayList<FileType>();
				fileTypes.add(new ClassFileType());
				for (FileType fileType : fileTypes)
				{
					chooser.addChoosableFileFilter(fileType);
				}
				
				chooser.setAcceptAllFileFilterUsed(false);
				chooser.setCurrentDirectory(root.getCurrentDirectory());

				if (chooser.showOpenDialog(root.getParentFrame()) == JFileChooser.APPROVE_OPTION)
				{

					File file = chooser.getSelectedFile();
					try
					{
						Algorithm alg = Util.createAlgorithmInstance(file.getAbsolutePath());
						alg.setWorkflow(root.getDocument().workflow);
						Transition t = ((Transition) clickedNode);
						t.getRSTTransition().setAlgorithm(alg);
						
						logger.debug("Algorithm " + alg.getName() + " set.");
					}
					catch(Exception ex)
					{
						JOptionPane.showMessageDialog(root.getParentFrame(), ex.getMessage());
					}
				}
				root.setCurrentDirectory(chooser.getCurrentDirectory());
			}
		}
	}
}
