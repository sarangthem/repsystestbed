package cu.rst.gwt.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;

import com.google.gson.Gson;

import cu.rst.gwt.client.Algorithm;
import cu.rst.gwt.client.Graph;
import cu.rst.gwt.client.Workflow;
import cu.rst.gwt.server.alg.eg.EigenTrust;
import cu.rst.gwt.server.alg.eg.PeerTrust;
import cu.rst.gwt.server.data.Feedback;
import cu.rst.gwt.server.graphs.FeedbackHistoryEdgeFactory;
import cu.rst.gwt.server.graphs.FeedbackHistoryGraph;
import cu.rst.gwt.server.graphs.ReputationEdgeFactory;
import cu.rst.gwt.server.graphs.ReputationGraph;
import cu.rst.gwt.server.graphs.TrustEdgeFactory;
import cu.rst.gwt.server.graphs.TrustGraph;
import cu.rst.gwt.server.util.DefaultArffFeedbackGenerator;
import cu.rst.gwt.server.util.Util;

public class RSTServlet extends HttpServlet 
{
	
	//Algorithm class is used for sendings json structures but the class and properties bytes are
	//are never sent back to the client
	Hashtable<String, Algorithm> algsTable = new Hashtable<String, Algorithm>();
	Hashtable<String, cu.rst.gwt.server.alg.Algorithm> algClassesTable = new Hashtable<String, cu.rst.gwt.server.alg.Algorithm>();
	Hashtable<String, byte[]> propFileTable = new Hashtable<String, byte[]>();
	Hashtable<String, Workflow> workflowsTable = new Hashtable<String, Workflow>();
	
	Hashtable<String, Graph> graphsTable = new Hashtable<String, Graph>();
	Hashtable<String, cu.rst.gwt.server.graphs.Graph> graphFileTable = new Hashtable<String, cu.rst.gwt.server.graphs.Graph>();
	Hashtable<String, byte[]> graphBytesTable = new Hashtable<String, byte[]>();
	
	private byte[] fhgBytes = new byte[]{0x40,0x72,0x65,0x6c,0x61,0x74,0x69,0x6f,0x6e,0x20,0x6d,0x79,0x64,0x61,0x74,0x61,0x73,
										0x65,0x74,0x0d,0x0a,0x0d,0x0a,0x40,0x61,0x74,0x74,0x72,0x69,0x62,0x75,0x74,0x65,0x20,
										0x61,0x73,0x73,0x65,0x73,0x73,0x6f,0x72,0x49,0x44,0x20,0x73,0x74,0x72,0x69,0x6e,0x67,
										0x0d,0x0a,0x40,0x61,0x74,0x74,0x72,0x69,0x62,0x75,0x74,0x65,0x20,0x61,0x73,0x73,0x65,
										0x73,0x73,0x65,0x65,0x49,0x44,0x20,0x73,0x74,0x72,0x69,0x6e,0x67,0x0d,0x0a,0x40,0x61,
										0x74,0x74,0x72,0x69,0x62,0x75,0x74,0x65,0x20,0x66,0x65,0x65,0x64,0x62,0x61,0x63,0x6b,
										0x56,0x61,0x6c,0x75,0x65,0x20,0x73,0x74,0x72,0x69,0x6e,0x67,0x0a,0x0d,0x0a,0x40,0x64,
										0x61,0x74,0x61,0x0d,0x0a,0x30,0x2c,0x31,0x2c,0x30,0x2e,0x38,0x0d,0x0a,0x0a,0x30,0x2c,
										0x31,0x2c,0x30,0x2e,0x38,0x0d,0x0a,0x0a,0x30,0x2c,0x31,0x2c,0x30,0x2e,0x32,0x0d,0x0a,
										0x0a,0x31,0x2c,0x30,0x2c,0x30,0x2e,0x39,0x0d,0x0a,0x0a,0x31,0x2c,0x32,0x2c,0x30,0x2e,
										0x37,0x0d,0x0a,0x0a,0x31,0x2c,0x32,0x2c,0x30,0x2e,0x39,0x0d,0x0a,0x0a,0x31,0x2c,0x32,
										0x2c,0x30,0x2e,0x31,0x0d,0x0a,0x32,0x2c,0x33,0x2c,0x30,0x2e,0x37,0x0d,0x0a,0x34,0x2c,
										0x31,0x2c,0x30,0x2e,0x38,0x0d,0x0a,0x35,0x2c,0x31,0x2c,0x30,0x2e,0x34,0x0d,0x0a,0x35,
										0x2c,0x31,0x2c,0x30,0x2e,0x32,0x0d,0x0a,0x35,0x2c,0x31,0x2c,0x30,0x2e,0x31,0x0d,0x0a,
										0x35,0x2c,0x31,0x2c,0x30,0x2e,0x37,0x0d,0x0a,0x35,0x2c,0x31,0x2c,0x30,0x2e,0x38,0x0d,
										0x0a,0x35,0x2c,0x31,0x2c,0x30,0x2e,0x39,0x0d,0x0a,0x35,0x2c,0x31,0x2c,0x30,0x2e,0x37,
										0x0d,0x0a,0x35,0x2c,0x31,0x2c,0x30,0x2e,0x38,0x0d,0x0a,0x35,0x2c,0x31,0x2c,0x30,0x2e,
										0x39,0x0d,0x0a,0x35,0x2c,0x31,0x2c,0x30,0x2e,0x37,0x0d,0x0a,0x35,0x2c,0x31,0x2c,0x30,
										0x2e,0x38,0x0d,0x0a,0x35,0x2c,0x31,0x2c,0x30,0x2e,0x39,0x0d,0x0a,0x35,0x2c,0x31,0x2c,
										0x30,0x2e,0x37,0x0d,0x0a,0x35,0x2c,0x31,0x2c,0x30,0x2e,0x38,0x0d,0x0a,0x35,0x2c,0x31,
										0x2c,0x30,0x2e,0x39,0x00};
	
	public RSTServlet()
	{
		populateTables();
	}
	
	private void populateTables()
	{
		
		algsTable.put("cu.rst.gwt.server.alg.eg.EigenTrust", new Algorithm("cu.rst.gwt.server.alg.eg.EigenTrust"));
		algClassesTable.put("cu.rst.gwt.server.alg.eg.EigenTrust", new EigenTrust());		
		
		algsTable.put("cu.rst.gwt.server.alg.eg.PeerTrust", new Algorithm("cu.rst.gwt.server.alg.eg.PeerTrust"));
		algClassesTable.put("cu.rst.gwt.server.alg.eg.PeerTrust", new PeerTrust());
		
		graphsTable.put("fhg1", new Graph("fhg1", "FHG"));
		graphFileTable.put("fhg1", new FeedbackHistoryGraph(new FeedbackHistoryEdgeFactory()));
		graphBytesTable.put("fhg1", fhgBytes);
		
		graphsTable.put("rg1", new Graph("rg1", "RG"));
		graphFileTable.put("rg1", new ReputationGraph(new ReputationEdgeFactory()));
		
		graphsTable.put("rg2", new Graph("rg2", "RG"));
		graphFileTable.put("rg2", new ReputationGraph(new ReputationEdgeFactory()));
		
		workflowsTable.put("workflow1", new Workflow("workflow1", "fhg1>cu.rst.gwt.server.alg.eg.EigenTrust>rg1"));
		workflowsTable.put("workflow2", new Workflow("workflow2", "fhg1>cu.rst.gwt.server.alg.eg.PeerTrust>rg2"));
		
	}
	
	
	
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
			else if(op.toLowerCase().trim().equals("get_workflows"))
			{
				processGetWorkflows(req, resp);
			}
			else if(op.toLowerCase().trim().equals("rem_workflow"))
			{
				processRemoveWorkflow(req, resp);
			}
			else if(op.toLowerCase().trim().equals("run_workflow"))
			{
				processRunWorkflow(req, resp);
			}
			else if(op.toLowerCase().trim().equals("reset_workflows"))
			{
				//See Bug report #6
				processResetWorkflows(req, resp);
			}
		}

	}
	
	private void processGetWorkflows(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		PrintWriter out = resp.getWriter();
		Gson gson = new Gson();
		out.println('[');
		for(Workflow a : workflowsTable.values())
		{
			out.print(gson.toJson(a));
			out.println(',');
		}
		out.println(']');
		out.flush();
	}
	
	private void processResetWorkflows(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		for(String o : this.graphFileTable.keySet())
		{
			this.graphFileTable.get(o).removeAllObservers();
		}
	}

	private void processRunWorkflow(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		//TODO implement this
		
	}

	private void processRemoveWorkflow(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		String workflowName = req.getParameter("workflow_name");
		
		int pos = -1;
		int i = 0;
		for(String n : workflowsTable.keySet())
		{
			if(n.equals(workflowName))
			{
				pos = i;
				break;
			}
			i++;
		}
		
		graphsTable.remove(workflowName);
		graphFileTable.remove(workflowName);

		PrintWriter out = resp.getWriter();
		out.print(graphsTable.size() - pos);
		out.flush();
		
	}

	private void processGetGraphs(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException 
	{
		PrintWriter out = resp.getWriter();
		Gson gson = new Gson();
		out.println('[');
		for(Graph a : graphsTable.values())
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
		for(String n : algsTable.keySet())
		{
			if(n.equals(algName))
			{
				pos = i;
				break;
			}
			i++;
		}
		
		algsTable.remove(algName);
		algClassesTable.remove(algName);
		propFileTable.remove(algName);
		
		PrintWriter out = resp.getWriter();
		out.print(algsTable.size() - pos);
		out.flush();
		
	}
	
	private void processRemoveGraph(HttpServletRequest req, HttpServletResponse resp)  throws ServletException, IOException 
	{
		String graphName = req.getParameter("graph_name");
		
		int pos = -1;
		int i = 0;
		for(String n : graphsTable.keySet())
		{
			if(n.equals(graphName))
			{
				pos = i;
				break;
			}
			i++;
		}
		
		graphsTable.remove(graphName);
		graphFileTable.remove(graphName);

		PrintWriter out = resp.getWriter();
		out.print(graphsTable.size() - pos);
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
				if(!this.workflowsTable.containsKey(workflowName))
				{
					this.workflowsTable.put(workflowName, new Workflow(workflowName, workflowDefn));

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
			if(!this.graphsTable.containsKey(graphName))
			{
				this.graphsTable.put(graphName, new Graph(graphName, graphType));
				this.graphBytesTable.put(graphName, graphBytes);
				if(graphBytes != null && graphBytes.length != 0)
				{
					if(graphType.equals("FHG"))
					{
						//DefaultArffFeedbackGenerator gen = new DefaultArffFeedbackGenerator();
						FeedbackHistoryGraph fhg = new FeedbackHistoryGraph(new FeedbackHistoryEdgeFactory());
						//fhg.addFeedbacks((ArrayList<Feedback>) gen.generateHardcoded(graphBytes), false);
						this.graphFileTable.put(graphName, fhg);
					}
				}
				else
				{
					// no graph data provided
					if(graphType.equals("FHG"))
					{
						FeedbackHistoryGraph fhg = new FeedbackHistoryGraph(new FeedbackHistoryEdgeFactory());
						this.graphFileTable.put(graphName, fhg);
					}
					else if(graphType.equals("RG"))
					{
						ReputationGraph rg = new ReputationGraph(new ReputationEdgeFactory());
						this.graphFileTable.put(graphName, rg);
					}
					else if(graphType.equals("TG"))
					{
						TrustGraph tg = new TrustGraph(new TrustEdgeFactory());
						this.graphFileTable.put(graphName, tg);
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
			if(!this.algsTable.containsKey(algName))
			{
				this.algsTable.put(algName, new Algorithm(algName));
				if(classBytes != null && classBytes.length != 0)
				{
					this.algClassesTable.put(algName, (cu.rst.gwt.server.alg.Algorithm) Util.newClass(algName, classBytes));
									
					if(propBytes != null && propBytes.length != 0)
					{
						this.propFileTable.put(algName, propBytes);
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
		for(Algorithm a : algsTable.values())
		{
			out.print(gson.toJson(a));
			out.println(',');
		}
		out.println(']');
		out.flush();
	}
}

