/**
 * 
 */
package cu.rst.core.alg;

import java.io.FileWriter;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import cu.rst.core.graphs.Graph;
import cu.rst.core.graphs.Graph.Type;
import cu.rst.core.graphs.RG;
import cu.rst.core.petrinet.Place;
import cu.rst.core.petrinet.Token;

/**
 * @author partheinstein
 *
 */
public class FileManager extends Algorithm
{
	static Logger logger = Logger.getLogger(FileWriter.class.getName());
	String m_fileName;
	
	public FileManager(String fileName) throws Exception
	{
		m_fileName = fileName;

	}
	
	@Override
	public boolean assertGraph2ListenType(Graph g) throws Exception 
	{
		return true;
	}

	@Override
	public boolean assertGraph2OutputType(Graph g) throws Exception 
	{
		return true;
	}

	@Override
	public boolean assertVariablePrecondition(double variable) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Type getInputGraphType() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Type getOutputGraphType() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList update(ArrayList<Token> tokens) throws Exception 
	{
		Token t1 = null;
		Token t2 = null;
		ArrayList changes = new ArrayList();
		if(tokens!=null && tokens.size() > 1)
		{
			t1 = tokens.get(0);
			t2 = tokens.get(1);
			
			RG rg1 = (RG) ((Place)t1.m_place).getGraph();
			RG rg2 = (RG) ((Place)t2.m_place).getGraph();
			logger.debug("RG1:");
			logger.debug(rg1);
			logger.debug("RG2");
			logger.debug(rg2);

//			DotWriter.write(rg1, m_fileName + "_rg1");
//			DotWriter.write(rg2, m_fileName + "_rg2");
			
//			m_writer.write("RG1:" + '\n');
//			for(ReputationEdge e : (Set<ReputationEdge>)rg1.edgeSet())
//			{
//				m_writer.write(e.src + "'s trusts " + e.sink + " by "+ e.getReputation() + '\n');
//			}
//			
//			m_writer.write("RG2:" + '\n');
//			for(ReputationEdge e : (Set<ReputationEdge>)rg2.edgeSet())
//			{
//				m_writer.write(e.src + "'s trusts " + e.sink + " by "+ e.getReputation() + '\n');
//			}
			
			changes.add(new String("Yo mama!"));
//			m_writer.close();
		}
		
		return changes;
		
	}

}
