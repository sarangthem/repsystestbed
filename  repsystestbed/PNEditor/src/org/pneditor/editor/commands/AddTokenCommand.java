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

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;
import org.pneditor.petrinet.Marking;
import org.pneditor.petrinet.Place;
import org.pneditor.petrinet.PlaceNode;
import org.pneditor.util.Command;

import cu.rst.core.petrinet.Token;

/**
 * 
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class AddTokenCommand implements Command
{

	private PlaceNode placeNode;
	private Marking marking;
	private Logger logger = Logger.getLogger(AddTokenCommand.class.getName());

	public AddTokenCommand(PlaceNode placeNode, Marking marking)
	{
		this.placeNode = placeNode;
		this.marking = marking;
	}

	public void execute()
	{

		int tokens = marking.getTokens(placeNode) + 1;

		Place p = (Place) placeNode;
		if (p.getRSTPlace() != null)
		{
			for (int i = 0; i < tokens; i++)
			{
				p.getRSTPlace().putToken(new Token(null, p.getRSTPlace()));
				logger.debug("Added tokens");
			}
		}
		marking.setTokens(placeNode, tokens);

	}

	public void undo()
	{
		new RemoveTokenCommand(placeNode, marking).execute();
	}

	public void redo()
	{
		execute();
	}

	@Override
	public String toString()
	{
		return "Add token";
	}

}
