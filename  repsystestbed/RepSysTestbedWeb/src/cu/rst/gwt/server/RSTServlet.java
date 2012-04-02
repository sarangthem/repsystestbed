package cu.rst.gwt.server;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
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

public class RSTServlet extends HttpServlet 
{
	
	//Algorithm class is used for sendings json structures but the class and properties bytes are
	//are never sent back to the client
	Hashtable<String, Algorithm> algs = new Hashtable<String, Algorithm>();
	Hashtable<String, byte[]> algClasses = new Hashtable<String, byte[]>();
	Hashtable<String, byte[]> propClasses = new Hashtable<String, byte[]>();
	
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException 
	{
		String op = req.getParameter("op");
		if(op.toLowerCase().trim().equals("get_algs"))
		{
			processGetAlgs(req, resp);
		}
		else if(op.toLowerCase().trim().equals("rem_alg"))
		{
			processRemoveAlg(req, resp);
		}
		
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
		propClasses.remove(algName);
		
		PrintWriter out = resp.getWriter();
		out.print(pos);
		out.flush();
		
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
//		String op = req.getParameter("op");
//		if(op.toLowerCase().trim().equals("putalgs"))
//		{
			processPutAlgs(req, resp);
//		}
			
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
					this.algClasses.put(algName, classBytes);
					if(propBytes != null && propBytes.length != 0)
					{
						this.propClasses.put(algName, propBytes);
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

