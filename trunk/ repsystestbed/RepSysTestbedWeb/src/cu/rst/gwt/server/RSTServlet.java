package cu.rst.gwt.server;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import com.google.gson.Gson;
import com.google.gwt.core.client.GWT;

import cu.rst.gwt.client.Algorithm;
import cu.rst.gwt.client.Graph;
import cu.rst.gwt.client.Workflow;
import cu.rst.gwt.server.data.Feedback;
import cu.rst.gwt.server.graphs.FeedbackHistoryEdgeFactory;
import cu.rst.gwt.server.graphs.FeedbackHistoryGraph;
import cu.rst.gwt.server.graphs.ReputationEdgeFactory;
import cu.rst.gwt.server.graphs.ReputationGraph;
import cu.rst.gwt.server.graphs.TrustEdgeFactory;
import cu.rst.gwt.server.graphs.TrustGraph;
import cu.rst.gwt.server.parse.WorkflowParser2;
import cu.rst.gwt.server.util.DefaultArffFeedbackGenerator;
import cu.rst.gwt.server.util.Util;

public class RSTServlet extends HttpServlet 
{
	
	//Algorithm class is used for sendings json structures but the class and properties bytes are
	//are never sent back to the client
	Hashtable<String, Algorithm> algs = new Hashtable<String, Algorithm>();
	Hashtable<String, cu.rst.gwt.server.alg.Algorithm> algClasses = new Hashtable<String, cu.rst.gwt.server.alg.Algorithm>();
	Hashtable<String, byte[]> propFile = new Hashtable<String, byte[]>();
	Hashtable<String, Workflow> workflows = new Hashtable<String, Workflow>();
	
	Hashtable<String, Graph> graphs = new Hashtable<String, Graph>();
	Hashtable<String, cu.rst.gwt.server.graphs.Graph> graphFile = new Hashtable<String, cu.rst.gwt.server.graphs.Graph>();
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException 
	{
		String op = req.getParameter("op");
		if(op != null)
		{
			if(op.toLowerCase().trim().equals("get_algs"))
			{
				processGetAlgs(req, resp);
			}
			else if(op.toLowerCase().trim().equals("rem_alg"))
			{
				processRemoveAlg(req, resp);
			}
			else if(op.toLowerCase().trim().equals("get_graphs"))
			{
				processGetGraphs(req, resp);
			}
			else if(op.toLowerCase().trim().equals("rem_graph"))
			{
				processRemoveGraph(req, resp);
			}
			else if(op.toLowerCase().trim().equals("rem_workflow"))
			{
				processRemoveWorkflow(req, resp);
			}
			else if(op.toLowerCase().trim().equals("run_workflow"))
			{
				processRunWorkflow(req, resp);
			}
		}

		
	}
	
	private void processRunWorkflow(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		String workflowName = req.getParameter("workflow_name");
		String workflowDefn = this.workflows.get(workflowName).getDefn();
		
		Hashtable<String, Object> tempBag = new Hashtable<String, Object>();
		
		//put the algorithms
		for(String o : this.algClasses.keySet())
		{
			tempBag.put(o, this.algClasses.get(o));
		}
		
		//put the graphs
		for(String o : this.graphFile.keySet())
		{
			tempBag.put(o, this.graphFile.get(o));
		}
		
		try 
		{
			WorkflowParser2 parser2 = new WorkflowParser2(workflowDefn, tempBag);
			parser2.run();
		
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			throw new ServletException(e);
		}
		
	}

	private void processRemoveWorkflow(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		String workflowName = req.getParameter("workflow_name");
		
		int pos = -1;
		int i = 0;
		for(String n : workflows.keySet())
		{
			if(n.equals(workflowName))
			{
				pos = i;
				break;
			}
			i++;
		}
		
		graphs.remove(workflowName);
		graphFile.remove(workflowName);

		PrintWriter out = resp.getWriter();
		out.print(graphs.size() - pos);
		out.flush();
		
	}

	private void processGetGraphs(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException 
	{
		PrintWriter out = resp.getWriter();
		Gson gson = new Gson();
		out.println('[');
		for(Graph a : graphs.values())
		{
			out.print(gson.toJson(a));
			out.println(',');
		}
		out.println(']');
		out.flush();
		
	}

	private void processRemoveAlg(HttpServletRequest req, HttpServletResponse resp)  throws ServletException, IOException 
	{
		String algName = req.getParameter("alg_name");
		
		int pos = -1;
		int i = 0;
		for(String n : algs.keySet())
		{
			if(n.equals(algName))
			{
				pos = i;
				break;
			}
			i++;
		}
		
		algs.remove(algName);
		algClasses.remove(algName);
		propFile.remove(algName);
		
		PrintWriter out = resp.getWriter();
		out.print(algs.size() - pos);
		out.flush();
		
	}
	
	private void processRemoveGraph(HttpServletRequest req, HttpServletResponse resp)  throws ServletException, IOException 
	{
		String graphName = req.getParameter("graph_name");
		
		int pos = -1;
		int i = 0;
		for(String n : graphs.keySet())
		{
			if(n.equals(graphName))
			{
				pos = i;
				break;
			}
			i++;
		}
		
		graphs.remove(graphName);
		graphFile.remove(graphName);

		PrintWriter out = resp.getWriter();
		out.print(graphs.size() - pos);
		out.flush();
		
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{

		String op = req.getRequestURI();
		if(op.indexOf("newalg") > 0)
		{
			processPutAlgs(req, resp);
		}
		else if(op.indexOf("newgraph") > 0)
		{
			processPutGraphs(req, resp);
		}
		else if(op.indexOf("newworkflow") > 0)
		{
			processPutWorkflow(req, resp);
		}

			
	}
	
	private void processPutWorkflow(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		try 
		{
			ServletFileUpload upload = new ServletFileUpload();
			String workflowName = null;
			String workflowDefn = null;

			FileItemIterator iterator = upload.getItemIterator(req);
			while (iterator.hasNext()) 
			{
				FileItemStream item = iterator.next();
				InputStream stream = item.openStream();
				//not a file (e.g text field)
				if (item.isFormField())
				{
					if(item.getFieldName().equals("nameFormElement")) 
					{  
						StringWriter writer = new StringWriter();
						IOUtils.copy(stream, writer);
						workflowName = writer.toString();
					}
					else if(item.getFieldName().equals("defnFormElement"))
					{
						StringWriter writer = new StringWriter();
						IOUtils.copy(stream, writer);
						workflowDefn = writer.toString();
					}
					
				}
			}
			
			if(workflowName != null && workflowDefn !=null)
			{
				if(!this.workflows.containsKey(workflowName))
				{
					this.workflows.put(workflowName, new Workflow(workflowName, workflowDefn));

				}
			}
			else
			{
				throw new Exception("No workflow name or defn");
			}
			
			//don't add if its already there.
			
			
		      
		}
		catch (Exception ex) 
		{
			throw new ServletException(ex);
		}
		
	}

	private void processPutGraphs(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		try 
		{
			ServletFileUpload upload = new ServletFileUpload();
			String graphName = null;
			String graphType = null;
			byte[] graphBytes = null;

			FileItemIterator iterator = upload.getItemIterator(req);
			while (iterator.hasNext()) 
			{
				FileItemStream item = iterator.next();
				InputStream stream = item.openStream();
				//not a file (e.g text field)
				if (item.isFormField()) 
				{  
					if(item.getFieldName().equals("nameFormElement"))
					{
						StringWriter writer = new StringWriter();
						IOUtils.copy(stream, writer);
						graphName = writer.toString();	
					}
					
					if(item.getFieldName().equals("graphTypeElement"))
					{
						StringWriter writer = new StringWriter();
						IOUtils.copy(stream, writer);
						graphType = writer.toString();	
					}
					
					
				}
				else 
				{
					if(item.getFieldName().equals("graphUploadFormElement"))
					{
						ByteArrayOutputStream out = new ByteArrayOutputStream();
						int len;
						byte[] buffer = new byte[8192];
	    	 			while ((len = stream.read(buffer, 0, buffer.length)) != -1)
	    	 			{
	    	 				out.write(buffer, 0, len);
	    	 			}
	    	 			graphBytes = out.toByteArray();
					}
					

				}
			}
			
			if(graphName == null || graphType == null)
			{
				throw new Exception("No graph name or type.");
			}
			
			//don't add if its already there.
			if(!this.graphs.containsKey(graphName))
			{
				this.graphs.put(graphName, new Graph(graphName, graphType));
				if(graphBytes != null && graphBytes.length != 0)
				{
					if(graphType.equals("FHG"))
					{
						DefaultArffFeedbackGenerator gen = new DefaultArffFeedbackGenerator();
						FeedbackHistoryGraph fhg = new FeedbackHistoryGraph(new FeedbackHistoryEdgeFactory());
						fhg.addFeedbacks((ArrayList<Feedback>) gen.generateHardcoded(graphBytes), false);
						this.graphFile.put(graphName, fhg);
					}
				}
				else
				{
					// no graph data provided
					if(graphType.equals("FHG"))
					{
						FeedbackHistoryGraph fhg = new FeedbackHistoryGraph(new FeedbackHistoryEdgeFactory());
						this.graphFile.put(graphName, fhg);
					}
					else if(graphType.equals("RG"))
					{
						ReputationGraph rg = new ReputationGraph(new ReputationEdgeFactory());
						this.graphFile.put(graphName, rg);
					}
					else if(graphType.equals("TG"))
					{
						TrustGraph tg = new TrustGraph(new TrustEdgeFactory());
						this.graphFile.put(graphName, tg);
					}
					else
					{
						throw new Exception("Unknown graph type.");
					}
				}
			}
			
		      
		}
		catch (Exception ex) 
		{
			throw new ServletException(ex);
		}
		
	}

	private void processPutAlgs(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		try 
		{
			ServletFileUpload upload = new ServletFileUpload();
			String algName = null;
			byte[] classBytes = null;
			byte[] propBytes = null;

			FileItemIterator iterator = upload.getItemIterator(req);
			while (iterator.hasNext()) 
			{
				FileItemStream item = iterator.next();
				InputStream stream = item.openStream();
				//not a file (e.g text field)
				if (item.isFormField() && item.getFieldName().equals("nameFormElement")) 
				{  
					StringWriter writer = new StringWriter();
					IOUtils.copy(stream, writer);
					algName = writer.toString();

				}
				else 
				{
					if(item.getFieldName().equals("classUploadFormElement"))
					{
						ByteArrayOutputStream out = new ByteArrayOutputStream();
						int len;
						byte[] buffer = new byte[8192];
	    	 			while ((len = stream.read(buffer, 0, buffer.length)) != -1)
	    	 			{
	    	 				out.write(buffer, 0, len);
	    	 			}
	    	 			classBytes = out.toByteArray();
					}
					else if(item.getFieldName().equals("propUploadFormElement"))
					{
						ByteArrayOutputStream out = new ByteArrayOutputStream();
						int len;
						byte[] buffer = new byte[8192];
	    	 			while ((len = stream.read(buffer, 0, buffer.length)) != -1)
	    	 			{
	    	 				out.write(buffer, 0, len);
	    	 			}
	    	 			propBytes = out.toByteArray();
					}


				}
			}
			
			if(algName == null)
			{
				throw new Exception("No alg name");
			}
			
			//don't add if its already there.
			if(!this.algs.containsKey(algName))
			{
				this.algs.put(algName, new Algorithm(algName));
				if(classBytes != null && classBytes.length != 0)
				{
					this.algClasses.put(algName, (cu.rst.gwt.server.alg.Algorithm) Util.newClass(algName, classBytes));
									
					if(propBytes != null && propBytes.length != 0)
					{
						this.propFile.put(algName, propBytes);
					}
				}
			}
			
		      
		}
		catch (Exception ex) 
		{
			throw new ServletException(ex);
		}

    }
		
	

	private void processGetAlgs(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		PrintWriter out = resp.getWriter();
		Gson gson = new Gson();
		out.println('[');
		for(Algorithm a : algs.values())
		{
			out.print(gson.toJson(a));
			out.println(',');
		}
		out.println(']');
		out.flush();
	}
}

