/**
 * 
 */
package cu.rst.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.jgrapht.graph.SimpleDirectedGraph;

import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

import cu.rst.core.exceptions.GenericTestbedException;
import cu.rst.core.graphs.Agent;
import cu.rst.core.graphs.Feedback;
import cu.rst.core.graphs.ReputationEdge;
import cu.rst.core.graphs.ReputationEdgeFactory;
import cu.rst.core.graphs.RG;
import cu.rst.core.graphs.TG;
import cu.rst.core.graphs.TestbedEdge;
import cu.rst.core.graphs.TrustEdge;
import cu.rst.core.graphs.TrustEdgeFactory;
import cu.rst.core.alg.Algorithm;

/**
 * @author partheinstein
 *
 */
public class Util
{
	public static final String WORKFLOW_DEFINITION = "workflow";
	private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(Util.class.getName());

	public static void assertNotNull(Object o) throws NullPointerException
	{
		if(o == null) throw new NullPointerException();
	}
	
	public static void assertFileExists(Object o) throws FileNotFoundException
	{
		if(o instanceof String)
		{
			File f = new File ((String)o);
			if(!f.exists())  throw new FileNotFoundException();
		}else
		{
			throw new FileNotFoundException("Input file name is not a String");
		}
	}
	
	 public static String readFromFile(String fileName) 
	 {
		 String dataLine = "";
		 String fileContents = "";
		 try {
			  File inFile = new File(fileName);
			  BufferedReader br = new BufferedReader(new InputStreamReader(
			      new FileInputStream(inFile)));
			
			  do
			  {
			      dataLine = br.readLine();
			      if(dataLine != null) 
			      {
			    	  fileContents = fileContents + dataLine + "\r\n" ;
			      }
			     
				  
			  }while(dataLine != null);
			
			  
			  br.close();
		} catch (FileNotFoundException ex) {
		  return (null);
		} catch (IOException ex) {
		  return (null);
		}
		return (fileContents);

	 }
		  
//	  public static void writeToFile(String fileName, String dataLine) throws Exception
//	  {
//		
//		  FileWriter fw = null;		
//		  try 
//		  {
//			  File outFile = new File(fileName);
//    		  fw = new FileWriter(outFile);
//    		  fw.write(dataLine);
//    		  fw.close();
//			 
//		  } catch (Exception ex) 
//		  {
//			  if(fw != null)
//			  {
//				  try
//				  {
//					  fw.close();
//				  }catch(Exception e)
//				  {
//					  throw e;
//				  }
//			  }
//			  throw ex;
//		  }
//	  }
	
	@Deprecated 
	public static ArrayList<TestbedEdge> getPaths(SimpleDirectedGraph graph, Agent src, Agent sink, boolean errorAtLoop) throws Exception
	{
		// check input
		assertNotNull(graph);
		assertNotNull(src);
		assertNotNull(sink);
		if(!graph.containsVertex(src)) throw new Exception("Source " + src + " does not exist in the graph.");
		if(!graph.containsVertex(sink)) throw new Exception("Sink " + sink + " does not exist in the graph.");
		
		ArrayList<TestbedEdge> edges = new ArrayList();
		
		Set<TestbedEdge> outgoingEdges = graph.outgoingEdgesOf(src);
		for(TestbedEdge e : outgoingEdges)
		{
			if(e.sink.equals(sink))
			{
				edges.add(e);
			}
			else
			{
				ArrayList<Agent> agentsVisited = new ArrayList<Agent>();
				agentsVisited.add((Agent) e.src);
				if(depthFirstSearch(graph, (Agent) e.sink, sink, edges, agentsVisited, errorAtLoop))
				{
					edges.add(e);
				}
			}
		}
		
		return edges;
		
	}
	

	/**
	 *  
	 * Depth First Search
	 * @param graph
	 * @param src
	 * @param sink
	 * @param edges
	 * @return
	 */
	@Deprecated
	public static boolean depthFirstSearch(SimpleDirectedGraph graph, Agent src, Agent sink, ArrayList<TestbedEdge> edges, ArrayList<Agent> agentsVisited, boolean errorAtLoop) throws Exception
	{
		// check input
		assertNotNull(graph);
		assertNotNull(src);
		assertNotNull(sink);
		if(agentsVisited == null) agentsVisited = new ArrayList();
		
		int initialSize = edges.size();
		
		Set<TestbedEdge> tempEdges = graph.outgoingEdgesOf(src);
		for(TestbedEdge e : tempEdges)
		{
			Agent tempSink = (Agent) e.sink;
			if(tempSink != null)
			{
				if(isPresent(agentsVisited, tempSink))
				{
					if(errorAtLoop) throw new Exception("Loop found. Stopping DFS.");
				}
				else
				{
					agentsVisited.add(src);
					if(tempSink.equals(sink))
					{
						edges.add(e);
					}
					else
					{
						 if(depthFirstSearch(graph, tempSink, sink, edges, agentsVisited, errorAtLoop))
						 {
							 edges.add(e);
						 }
					}
				}
			}
		}
		if(initialSize == edges.size()) return false;
		else return true;
	}
	

	public static ArrayList<ArrayList<TestbedEdge>> getPaths(SimpleDirectedGraph graph, Agent src, Agent sink) throws Exception
	{
		//check inputs
		assertNotNull(src);
		assertNotNull(sink);
		assertNotNull(graph);
		
		LinkedList<Agent> visited = new LinkedList<Agent>();
		visited.add(src);
		ArrayList<LinkedList<Agent>> pathNodes = new ArrayList<LinkedList<Agent>>();
		depthFirstSearch(graph, sink, visited, pathNodes);
		
		ArrayList<ArrayList<TestbedEdge>> pathEdges = new ArrayList<ArrayList<TestbedEdge>>();
		for(LinkedList path : pathNodes)
		{
			ArrayList<TestbedEdge> edges = new ArrayList<TestbedEdge>();
			for(int i=0; i<path.size()-1; i++)
			{
				edges.add((TestbedEdge) graph.getEdge(path.get(i), path.get(i+1)));
			}
			pathEdges.add(edges);
		}
		
		return pathEdges;
	}

	/**
	 * Performs a depth first search. To starting the search, add the begin node to the visited linkedlist.
	 * Returns a lists of agents
	 * @param graph
	 * @param sink
	 * @param visited
	 * @param paths
	 * @throws Exception
	 */
	public static void depthFirstSearch(SimpleDirectedGraph graph, Agent sink, LinkedList<Agent> visited, ArrayList<LinkedList<Agent>> paths) throws Exception
	{
		assertNotNull(sink);
		assertNotNull(paths);
		assertNotNull(visited);
		
		Set<TestbedEdge> edges = graph.outgoingEdgesOf(visited.getLast());
				
		for(TestbedEdge e : edges)
		{
			assertNotNull(e.sink);
			if(visited.contains(e.sink))
			{
				continue;
			}
			if(e.sink.equals(sink))
			{
				visited.add((Agent) e.sink);
				paths.add(new LinkedList(visited));
				visited.removeLast();
				break;
			}
		}
		
		for(TestbedEdge e : edges)
		{
			assertNotNull(e.sink);
			if(visited.contains(e.sink) || e.sink.equals(sink))
			{
				continue;
			}
			visited.addLast((Agent) e.sink);
			//printPath(visited);
			depthFirstSearch(graph, sink, visited, paths);
			visited.removeLast();
		}
		
	}
	
	 public static void printPath(LinkedList<Agent> visited) 
	 {
        for (Agent node : visited) 
        {
            System.out.print(node);
            System.out.print(" ");
        }
        System.out.println();
	 }
	 
	 public static void printPath(ArrayList<TestbedEdge> path)
	 {
		for(TestbedEdge e : path)
		{
			System.out.print(e);
			System.out.print(" ");
		}
        System.out.println();
	 }

	
	public static boolean isPresent(ArrayList objects, Object o)
	{
		assertNotNull(objects);
		assertNotNull(o);
		
		for(Object o2 : objects)
		{
			if(o2.equals(o)) return true;
		}
		return false;
	}
		
	  
	public static double round(double Rval, int Rpl) 
	{  
		double p = (double)Math.pow(10,Rpl);
		Rval = Rval * p;
		double tmp = Math.round(Rval);
		return (double)tmp/p;
	}
	
	 /**
     * Loads the class from the given path and returns an instance of it. 
     * @param classPath The path to the class
     * @return A new instance of the class
     */
    public static Object newClass(String classPath) throws Exception
    {
       if (classPath.endsWith(".class")) 
       {
            return classInstance(loadClass(new File(classPath)));
       } 
       else 
       {
    	   throw new Exception("Only .class loading is supported.");
       }
    }

    /**
     * Loads the class from the given file and returns the class.  Returns null if it could not be loaded.
     * The class file must still be in its proper package.
     * @param classFile the path to the class
     * @return The class file that was loader
     */
    public static Class loadClass(File classFile)
    {
        if (classFile == null) 
        {
            return null;
        }
        String className = classFile.getName().split(".class")[0];
        for (int i = 0; i < 1000; i++) 
        {
            File dir = classFile.getParentFile(); // Create a File object on the root of the directory containing the class file
            try 
            {
                ClassLoader cl = new URLClassLoader(new URL[]{dir.toURI().toURL()});
                return cl.loadClass(className);

            } 
            catch (NoClassDefFoundError e)
            {
                className = dir.getPath().substring(dir.getPath().lastIndexOf(File.separator) + 1) + "." + className;
                classFile = classFile.getParentFile();
                continue;
            } 
            catch (Exception ex) 
            {
                ex.printStackTrace();
                return null;
            }
        }
        return null;
    }
    
   
    /**
     * Returns a new instance of the given class
     * @param c the class to get an instance from
     * @return The new isntance of the given class
     */
    public static Object classInstance(Class c) throws Exception
    {
        try 
        {
            Object o = c.newInstance();
            return o;
        } 
        catch (InstantiationException ex) 
        {
            throw new Exception("This class must have a default constructor to be used.", ex);
        }
    }
    

    public static Object newClass(String name, byte[] classBytes) throws Exception
    {
		if(name == null || classBytes == null)
		{
			throw new NullPointerException("Either name or the class bytes is null");
		}
		
		ByteArrayClassLoader bacl = new ByteArrayClassLoader();
		
		return classInstance(bacl.loadClass(name, classBytes));
		
		
    }
    
    //source: http://www.exampledepot.com/egs/java.io/file2bytearray.html
    // Returns the contents of the file in a byte array.
    public static byte[] getBytesFromFile(File file) throws IOException 
    {
    	FileInputStream is = new FileInputStream(file);

        // Get the size of the file
        long length = file.length();

        // You cannot create an array using a long type.
        // It needs to be an int type.
        // Before converting to an int type, check
        // to ensure that file is not larger than Integer.MAX_VALUE.
        if (length > Integer.MAX_VALUE) {
            // File is too large
        }

        // Create the byte array to hold the data
        byte[] bytes = new byte[(int)length];

        // Read in the bytes
        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
               && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
            offset += numRead;
        }

        // Ensure all the bytes have been read in
        if (offset < bytes.length) {
            throw new IOException("Could not completely read file "+file.getName());
        }

        // Close the input stream and return bytes
        is.close();
        return bytes;
    }
    
    public static Algorithm createAlgorithmInstance(String classPath) throws Exception
	{
		try
		{
			Algorithm alg = (Algorithm) Util.newClass(classPath);
			return alg;
			
		}
		catch(Exception e)
		{
			throw new Exception("Could not load algorithm " + classPath, e);
		}
	}
    
    public static List<Feedback> generateHardcoded(String arffFileName) throws Exception
	{
		ArrayList<Feedback> feedbacks = null;
		
		/**
		 * 
		 * @attribute assessorID string
		 * @attribute assesseeID string
		 * @attribute feedbackValue string
		 */
		
		DataSource source;
		try
		{
			source = new DataSource(arffFileName);
			Instances instances = source.getDataSet();
			feedbacks = new ArrayList<Feedback>();
			logger.debug("Number of instances in arff file is " + instances.numInstances());
			
			Enumeration enu = instances.enumerateInstances();
			//get all the feedback lines
			
			while(enu.hasMoreElements())
			{
				Instance temp = (Instance)enu.nextElement();
				logger.info("Parsing " + temp);
				String[] feedbackInstance = new String[3];
				//go through each feedback line
				
				if(temp.numValues()!=3) throw new Exception("Feedback line does not have 3 elements. This is illegal.");
				
				for(int i=0;i<temp.numValues();i++)
				{
					//number of values == 3
					feedbackInstance[i] = temp.stringValue(i);					
				}
				Agent assessor = new Agent(new Integer(feedbackInstance[0]));
				Agent assessee = new Agent(new Integer(feedbackInstance[1]));
				Double value = new Double(feedbackInstance[2]);
				
				Feedback f = new Feedback(assessor, assessee, value);
				feedbacks.add(f);
				logger.info("Added " + f );
				
			}

		} catch (Exception e)
		{
			logger.info("Error parsing arff file '" + arffFileName +"'.");
			logger.info(e.getStackTrace());
			throw e;
		}
		
		return feedbacks;
				
	}
    
    public static RG createGraph(String arffFileName) throws Exception
	{
		RG repGraph = new RG(new ReputationEdgeFactory());
		
		Util.assertNotNull(arffFileName);
		Util.assertFileExists(arffFileName);
		
		DataSource source;
		try
		{
			source = new DataSource(arffFileName);
			Instances instances = source.getDataSet();
			logger.debug("Number of instances in arff file is " + instances.numInstances());
			
			Enumeration enu = instances.enumerateInstances();
			//get all the feedback lines
			
			while(enu.hasMoreElements())
			{
				Instance temp = (Instance)enu.nextElement();
				logger.info("Parsing " + temp);
				String[] repInstance = new String[3];
				//go through each feedback line
				
				if(temp.numValues()!=3) throw new GenericTestbedException("Reputation line does not have 3 elements. This is illegal.");
				
				for(int i=0;i<temp.numValues();i++)
				{
					//number of values == 3
					repInstance[i] = String.valueOf((int)temp.value(i));				
				}
				Agent src = new Agent(new Integer(repInstance[0]));
				Agent sink = new Agent(new Integer(repInstance[1]));
				Double reputation = new Double(repInstance[2]);
				
				if(!repGraph.containsVertex(src))
				{
					repGraph.addVertex(src);
				}
				
				if(!repGraph.containsVertex(sink))
				{
					repGraph.addVertex(sink);
				}
				
				repGraph.addEdge(src, sink, (double)reputation);
				ReputationEdge repEdge = (ReputationEdge) repGraph.getEdge(src, sink);
				repEdge.setReputation(reputation);
				System.out.println(repGraph);
			}
			
			return repGraph;

		} catch (Exception e)
		{
			logger.error("Error parsing arff file '" + arffFileName +"'.");
			logger.error(e);
			throw new GenericTestbedException("Error parsing arff file.", e);
		}
			
	}
    
    public static ArrayList<ReputationEdge> generateReputationEdges(String arffFileName) throws Exception
	{
		RG repGraph = new RG(new ReputationEdgeFactory());
		
		Util.assertNotNull(arffFileName);
		Util.assertFileExists(arffFileName);
		
		DataSource source;
		ArrayList<ReputationEdge> repEdges = new ArrayList<ReputationEdge>();
		try
		{
			source = new DataSource(arffFileName);
			Instances instances = source.getDataSet();
			logger.debug("Number of instances in arff file is " + instances.numInstances());
			
			Enumeration enu = instances.enumerateInstances();
			//get all the feedback lines
			
			while(enu.hasMoreElements())
			{
				Instance temp = (Instance)enu.nextElement();
				logger.info("Parsing " + temp);
				String[] repInstance = new String[3];
				//go through each feedback line
				
				if(temp.numValues()!=3) throw new GenericTestbedException("Reputation line does not have 3 elements. This is illegal.");
				
				for(int i=0;i<temp.numValues();i++)
				{
					//number of values == 3
					repInstance[i] = String.valueOf((int)temp.value(i));				
				}
				Agent src = new Agent(new Integer(repInstance[0]));
				Agent sink = new Agent(new Integer(repInstance[1]));
				Double reputation = new Double(repInstance[2]);
				repEdges.add(new ReputationEdge(src, sink, reputation));
				
			}
			
			return repEdges;

		} catch (Exception e)
		{
			logger.error("Error parsing arff file '" + arffFileName +"'.");
			logger.error(e);
			throw new GenericTestbedException("Error parsing arff file.", e);
		}
			
	}
    
    public static ArrayList<TrustEdge> generateTrustEdges(String arffFileName) throws Exception
	{
		TG trustGraph = new TG(new TrustEdgeFactory());
		
		Util.assertNotNull(arffFileName);
		Util.assertFileExists(arffFileName);
		ArrayList<TrustEdge> tEdges = new ArrayList<TrustEdge>();
		DataSource source;
		try
		{
			source = new DataSource(arffFileName);
			Instances instances = source.getDataSet();
			logger.debug("Number of instances in arff file is " + instances.numInstances());
			
			Enumeration enu = instances.enumerateInstances();
			
			
			while(enu.hasMoreElements())
			{
				Instance temp = (Instance)enu.nextElement();
				logger.info("Parsing " + temp);
				String[] feedbackInstance = new String[3];
				//go through each feedback line
				
				if(temp.numValues()!=2) throw new GenericTestbedException("Trust line does not have 2 elements. This is illegal.");
				
				for(int i=0;i<temp.numValues();i++)
				{
					//number of values == 2
					feedbackInstance[i] = temp.stringValue(i);					
				}
				Agent src = new Agent(new Integer(feedbackInstance[0]));
				Agent sink = new Agent(new Integer(feedbackInstance[1]));
				
				tEdges.add(new TrustEdge(src, sink));
			}
			
		} 
		catch (Exception e)
		{
			logger.info("Error parsing arff file '" + arffFileName +"'.");
			logger.info(e.getStackTrace());
			throw new GenericTestbedException("Error parsing arff file.", e);
		}
		return tEdges;
		
	}
	
	public static void main(String[] args) throws Exception
	{
//		ReputationGraph repGraph = new ReputationGraph(new ReputationEdgeFactory());
//		Agent a0 = new Agent();
//		Agent a1 = new Agent();
//		Agent a2 = new Agent();
//		Agent a3 = new Agent();
//		Agent a4 = new Agent();
//		
//		repGraph.addVertex(a0);
//		repGraph.addVertex(a1);
//		repGraph.addVertex(a2);
//		repGraph.addVertex(a3);
//		repGraph.addVertex(a4);
//		
//		repGraph.addEdge(a0, a1, 0);
//		repGraph.addEdge(a1, a2, 0);
//		repGraph.addEdge(a2, a3, 0);
//		repGraph.addEdge(a1, a3, 0);
//		repGraph.addEdge(a0, a4, 0);
//		repGraph.addEdge(a4, a3, 0);
//		repGraph.addEdge(a1, a0, 0);
//		repGraph.addEdge(a1, a4, 0);
//		
//		ArrayList<TestbedEdge> paths = Util.getPaths(repGraph, a0, a3, false);
//		if(paths != null)
//		{
//			System.out.println(paths.size());
//			for(TestbedEdge e : paths)
//			{
//				System.out.println(e);
//			}
//		}
//		System.out.println("-------");
//
//		
//		ArrayList<ArrayList<TestbedEdge>> pathsLinkedList =  getPaths(repGraph, a0, a3);
//		
//		for(ArrayList<TestbedEdge> list : pathsLinkedList)
//		{
//			printPath(list);
//		}
		

	}

}
