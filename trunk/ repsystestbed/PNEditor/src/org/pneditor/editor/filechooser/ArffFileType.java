package org.pneditor.editor.filechooser;

import java.io.File;

import javax.swing.Icon;

import org.pneditor.petrinet.Document;

public class ArffFileType extends FileType
{

	@Override
	public String getExtension()
	{
		return "arff";
	}

	@Override
	public String getName()
	{
		return "ARFF";
	}

	@Override
	public void save(Document document, File file) throws FileTypeException
	{
		throw new UnsupportedOperationException("Loading not supported.");
		
	}

	@Override
	public Document load(File file) throws FileTypeException
	{
		throw new UnsupportedOperationException("Loading not supported.");
	}

	@Override
	public Icon getIcon()
	{
		throw new UnsupportedOperationException("Loading not supported.");
	}

}
