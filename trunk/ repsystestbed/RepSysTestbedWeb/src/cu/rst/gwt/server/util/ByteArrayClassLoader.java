package cu.rst.gwt.server.util;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;

/**
 * @author partheinstein
 *
 */
public class ByteArrayClassLoader extends ClassLoader
{
	
	public ByteArrayClassLoader() 
	{
		super();
	}
	
	public Class<?> loadClass(final String name, byte[] classBytes) throws Exception
	{
		if(name == null || classBytes == null)
		{
			throw new NullPointerException("Name or the class bytes is null.");
		}
		
		Class<?> classTemp = null;
		//super.loadClass("cu.repsystestbed.algorithms.ReputationAlgorithmType2", true);
		
		classTemp = defineClass(name, classBytes, 0, classBytes.length);

		
		//resolveClass(classTemp);
		return classTemp;
	}
	
}
