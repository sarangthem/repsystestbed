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

package org.pneditor.editor.commands;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.pneditor.petrinet.PetriNet;
import org.pneditor.util.Command;
import org.pneditor.petrinet.Subnet;
import org.pneditor.petrinet.Transition;

/**
 * 
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class AddTransitionCommand implements Command
{

	private Subnet subnet;
	private int x, y;
	private Transition createdTransition;
	private PetriNet petriNet;
	private cu.rst.core.petrinet.PetriNet workflow;
	private cu.rst.core.petrinet.Transition rstTransition;
	Logger logger = LogManager.getLogger(AddTransitionCommand.class.getName());

	public AddTransitionCommand(Subnet subnet, int x, int y, PetriNet petriNet, cu.rst.core.petrinet.PetriNet workflow)
	{
		this.subnet = subnet;
		this.x = x;
		this.y = y;
		this.petriNet = petriNet;
		this.workflow = workflow;
	}

	public void execute()
	{
		createdTransition = new Transition();
		createdTransition.setCenter(x, y);
		petriNet.getNodeSimpleIdGenerator().setUniqueId(createdTransition);
		petriNet.getNodeLabelGenerator().setLabelToNewlyCreatedNode(
				createdTransition);
		subnet.addElement(createdTransition);
		
		rstTransition = new cu.rst.core.petrinet.Transition();
		boolean added = workflow.addVertex(rstTransition);
		if(!added)
		{
			logger.error("Transition not added.");
		}
		createdTransition.setRSTTransition(rstTransition);
		try
		{
			rstTransition.setWorkflow(workflow);
		} catch (Exception e)
		{
			logger.error(e);
		}
		
		
	}

	public void undo()
	{
		new DeleteElementCommand(createdTransition).execute();
	}

	public void redo()
	{
		subnet.addElement(createdTransition);
	}

	@Override
	public String toString()
	{
		return "Add transition";
	}

}
