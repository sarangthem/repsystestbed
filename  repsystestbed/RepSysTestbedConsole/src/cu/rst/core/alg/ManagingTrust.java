/**
 * 
 */
package cu.rst.core.alg;


import java.util.ArrayList;
import java.util.Properties;
import java.util.Random;
import java.util.Set;

import org.apache.log4j.Logger;

import cu.rst.core.graphs.Agent;
import cu.rst.core.graphs.FHG;
import cu.rst.core.graphs.Feedback;
import cu.rst.core.graphs.FeedbackHistoryGraphEdge;
import cu.rst.core.graphs.Graph;
import cu.rst.core.graphs.Graph.Type;
import cu.rst.core.graphs.RG;
import cu.rst.core.graphs.ReputationEdge;
import cu.rst.core.petrinet.Place;
import cu.rst.core.petrinet.Token;


/**
 * @author partheinstein
 * Note that ManagingTrust produces a distrust graph. 
 *
 */
public class ManagingTrust extends Algorithm
{
	
	ManagingTrustCentralStore store;
	int maxAgents; 
	int numArbitraryAgentsToPick; 
	static Logger logger = Logger.getLogger(ManagingTrust.class.getName());
	boolean printOnConsole = false;
	
	public ManagingTrust()
	{
		super();
		store = new ManagingTrustCentralStore(); //filled when addExperience method is invoked
	}
	
	@Override
	public void setConfig(Properties config)
	{

	}
	
	/*
	 * trustor is the agent calculating the trust score (seems not really needed)
	 * trustee is the agent whose trust is being calculated
	 * 
	 * Note 1:
	 * ai = arbitrary agent
	 * w = number of different ai
	 * fi = number of times ai is found when querying for both cfi and cri.
	 * s = sum(i=1,i=w) {fi}
	 * 
	 * Note 2: Although you are calculating the local trust score which is one agent's trust perspective of another,
	 * the trustor info is needed in managingtrust algorithm. in a way, the local trust algorithm tries to approach the 
	 * global trust algorithm. perhaps this is the aim of all local trust algorithms.
	 */
	
	
	public Double calculateTrustScoreInternal(Agent sink, FHG fhg) throws Exception
	{
		//get all the feedbacks in the system. this may be resource intensive
		Set<FeedbackHistoryGraphEdge> allEdges = fhg.edgeSet();
		ArrayList<Feedback> allFeedbacks = new ArrayList<Feedback>();
		for(FeedbackHistoryGraphEdge e : allEdges)
		{
			for(Feedback f : e.feedbacks)
			{
				allFeedbacks.add(f);
			}
		}
		
		if(allFeedbacks.isEmpty()) throw new Exception("Add a feedback before calling this method");
		
		maxAgents = fhg.vertexSet().size();
		numArbitraryAgentsToPick = maxAgents; //TODO hardcoded, change it.
		int total = 0;
		int s = 0;
		
		if(numArbitraryAgentsToPick>maxAgents) throw new Exception("numArbitraryAgentsToPick > maxAgents");
		
		int[] arbitraryAgents = new int[numArbitraryAgentsToPick];
		for(int i=0;i<numArbitraryAgentsToPick;i++) arbitraryAgents[i]=-1;
		
		store.clearData(); //clear everything before starting, just to be safe
		store.readData(allFeedbacks, maxAgents);  
		
		if(printOnConsole) store.printMTCentralStore();
		
		/*
		 * need to calculate the following:
		 * fi, cri, cfi, s 
		 * 
		 * 1. find out how many arbitrary agents need to be picked
		 * 2. find out which arbitrary agents need to be picked
		 * 3. iterate over each arbitrary agent and get cri and cfi 
		 * 
		 */
		
		//find out which arbitrary agents need to be picked
		Random random = new Random();
		arbitraryAgents[0] = random.nextInt(numArbitraryAgentsToPick); //first one is never a duplicate, so ok to use index 0
		for(int i=0;i<numArbitraryAgentsToPick;i++)
		{
			boolean exists = false;
			int temp;
			do
			{
				temp = random.nextInt(maxAgents);
				exists = false;
				for(int j=0;j<numArbitraryAgentsToPick;j++)
				{
					if(arbitraryAgents[j]==temp)
					{
						exists=true; //found it, try again
					}
				}
			}while(exists);
			arbitraryAgents[i]=temp;
		}
		
		logger.debug("Agents picked: ");
		for(int i=0;i<numArbitraryAgentsToPick;i++)
		{
			logger.debug(arbitraryAgents[i]+" ");
		}
		
		//iterate over each arbitrary agent and get cri and cfi 
		int[] cri = new int[maxAgents];
		int[] cfi = new int[maxAgents];
		int[] fi = new int[maxAgents];
		int[] crnormi = new int[maxAgents];
		int[] cfnormi = new int[maxAgents];
		
		//each index i corresponds to the id of the agent
		for(int i=0;i<maxAgents;i++)
		{
			cri[i]=-1;
			cfi[i]=-1;
			fi[i]=-1;
			cfnormi[i]=0;
			crnormi[i]=0;
		}
		
		for(int i=0;i<numArbitraryAgentsToPick;i++)
		{
			Agent arbAgent = new Agent(arbitraryAgents[i]);
			int complaintsAgainst = store.queryComplaintsAgainst(arbAgent, sink);
			logger.info("Complaints against:: Arbitrary agent " + arbAgent + " returned " + complaintsAgainst + " about " + sink);
			int complaintsFiled = store.queryComplaintsby(arbAgent, sink);
			logger.info("Complaints by:: Arbitrary agent " + arbAgent + " returned " + complaintsFiled + " about " + sink);
			cri[arbitraryAgents[i]] = complaintsAgainst;
			cfi[arbitraryAgents[i]] = complaintsFiled;
			fi[arbitraryAgents[i]] = complaintsAgainst + complaintsFiled;
			
			
		}
		
		for(int i=0;i<maxAgents;i++)
		{
			if(fi[i]!=-1) s = s + fi[i]; //you dont want to count negative
		}
		
		if(s==0)
		{
			logger.info("The arbitrary agents found nothing. Can't calculate anything. " +
					"The called must decide how to intepret no information as either trust or don't trust.");
			return 0.0;
		}
		
		//calculate crnormi and cfnormi
		
		for(int i=0;i<maxAgents;i++)
		{
			if(cri[i] ==-1 && cfi[i]==-1)
			{
				//do nothing, crnormi=0
			}else
			{
				crnormi[i] = (int)((double)cri[i] * (1 - (Math.pow( (double)((s-fi[i])/s) , (double)s)) ));
			}
		}
		
		for(int i=0;i<maxAgents;i++)
		{
			if(cri[i] ==-1 && cfi[i]==-1)
			{
				//do nothing, cfnormi=0
			}else
			{
				cfnormi[i] = (int)((double)cfi[i] * (1 - (Math.pow( (double)((s-fi[i])/s) , (double)s)) ));
			}
		}
		
		for(int i=0;i<maxAgents;i++)
		{
			if(printOnConsole) logger.debug("ai=" + i + " cfnormi[i]=" + cfnormi[i] + " crnormi[i]=" + crnormi[i]);
			
		}
		
//		double avg=0;
//		int count=0;
//		for(int i=0;i<maxAgents;i++)
//		{
//			if(!(cfnormi[i]==0 && crnormi[i]==0))
//			{
//				avg = avg +  cfnormi[i]* crnormi[i];
//				count++;
//			}
//		}
//		 avg = avg/(count);
//		 return avg;
		
		//see Note 3
		double crnormiavg = 0;
		double cfnormiavg = 0;
		int countCr=0;
		int countCf=0;
		for(int i=0;i<maxAgents;i++)
		{
			if (cfnormi[i]!=0) 
			{
				cfnormiavg+=cfnormi[i];
				countCf++;
			}
			if (crnormi[i]!=0) 
			{
				crnormiavg+=crnormi[i];
				countCr++;
			}
		}
		
		crnormiavg = (double)crnormiavg/(double)countCr;
		cfnormiavg = (double)cfnormiavg/(double)countCf;
		
		if(printOnConsole) logger.debug(" crnormiavg = " + crnormiavg + " cfnormiavg = " + cfnormiavg);
		
		for(int i=0;i<maxAgents;i++)
		{
			
			if(crnormi[i]!=0 && cfnormi[i]!=0) //if 0 that means these agents stored nothing, dont use them
			{
				/*
				 * Note 3: calculate threshold using cravg and cfavg for each i. you should be maintaining cravg and cfavg
				 * and passing them to the method below. But in this version, just pass cfNormi and crNormi.
				 * Likely you will always find that threshold is less than the crnormi*cfnormi. To simplify things, we will 
				 * just take the avg of all the crnormi and cfnormi and use that as the threshold
				 */
				 
////				double thresholdToTrust = calculateThreshold(crnormi[i], cfnormi[i]); //see note 3 for why it is commented
//				double thresholdToTrust = crnormiavg * cfnormiavg; //see note 3
				
				double thresholdToTrust = this.calculateThreshold(crnormiavg, cfnormiavg);
				
				if(printOnConsole) logger.debug("Threshold for agent " + i + " is " + thresholdToTrust);
				
				//simple trust graph exploration as per the paper
				if( (crnormi[i] * cfnormi[i]) <= thresholdToTrust)
				{
					total = total + 1; //+ve total means trustworthy
				}else
				{
					total = total - 1;
				}
			}
			 
		}
		
		//DEBUGGING: TODO REMOVE THIS
		//total = 100;
		logger.debug("Trustscore for agent " + sink.id + " is " + total); 
		return (double)total;
	}
	
	/**
	 * 
	 * @param crnormiavg
	 * @param cfnormiavg
	 * @return
	 */
			
	private double calculateThreshold(double crnormiavg, double cfnormiavg)
	{
		return Math.pow(0.5 + 4/(Math.sqrt(crnormiavg*cfnormiavg)), 2) * crnormiavg*cfnormiavg; 
	}


	public double calculateTrustScore(Agent src, Agent sink, FHG fhg) throws Exception
	{
		logger.debug("Calculating trustscore:-" + src + " " +sink);
		return calculateTrustScoreInternal(sink, fhg);
	}


	

	@Override
	public boolean assertGraph2ListenType(Graph g) throws Exception
	{
		if(!(g instanceof FHG)) return false;
		return true;
	}

	@Override
	public boolean assertGraph2OutputType(Graph g) throws Exception
	{
		if(!(g instanceof RG)) return false;
		return true;
	}

	@Override
	public boolean assertVariablePrecondition(double variable) throws Exception
	{
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public Type getInputGraphType() throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Type getOutputGraphType() throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList update(ArrayList<Token> tokens) throws Exception 
	{
		ArrayList<ReputationEdge> changes = new ArrayList<ReputationEdge>();
		if(tokens!=null && tokens.size()>0)
		{
			for(Token t : tokens)
			{
				FHG fhg = (FHG) ((Place)t.m_place).getGraph();
				for(Agent src : (Set<Agent>)fhg.vertexSet())
				{
					for(Agent sink : (Set<Agent>)fhg.vertexSet())
					{
						if(!src.equals(sink))
						{
							double rep = calculateTrustScore(src, sink, fhg);
							changes.add(new ReputationEdge(src, sink, rep));
						}
					}
				}
			}
		}
		return changes;
	}

	
}
