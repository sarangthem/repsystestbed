/**
 * 
 */
package cu.rst.tests;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author partheinstein
 *
 */
public class FileReaderTest
{

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception
	{
		
		
		BufferedWriter writer = new BufferedWriter(new FileWriter("C:\\Users\\partheinstein\\RepSysTestbed\\bla.txt"));
		writer.write("hahaha");
		writer.close();
		
		BufferedReader  reader = new BufferedReader (new FileReader("C:\\Users\\partheinstein\\RepSysTestbed\\bla.txt"));
		
		String fileContents = readFromFile("C:\\Users\\partheinstein\\RepSysTestbed\\workflow.ini");
		
		writeToFile("C:\\Users\\partheinstein\\RepSysTestbed\\workflowCopy.ini", fileContents, false, false);
	}
	

	  public static String readFromFile(String fileName) {
	    String dataLine = "";
	    String fileContents = "";
	    try {
	      File inFile = new File(fileName);
	      BufferedReader br = new BufferedReader(new InputStreamReader(
	          new FileInputStream(inFile)));

	      do
	      {
		      dataLine = br.readLine();
		      if(dataLine == null)
		      {
		    	  System.out.println("null");
		      }else
		      {
		    	  System.out.println(dataLine);
		      }
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
	  
	  public static boolean writeToFile(String fileName, String dataLine,
		      boolean isAppendMode, boolean isNewLine) {
		    if (isNewLine) {
		      dataLine = "\n" + dataLine;
		    }
		    
		    DataOutputStream dos = null;

		    try {
		      File outFile = new File(fileName);
		      if (isAppendMode) {
		        dos = new DataOutputStream(new FileOutputStream(fileName, true));
		      } else {
		        dos = new DataOutputStream(new FileOutputStream(outFile));
		      }

		      dos.writeBytes(dataLine);
		      dos.close();
		    } catch (FileNotFoundException ex) {
		      return (false);
		    } catch (IOException ex) {
		      return (false);
		    }
		    return (true);

		  }


}
