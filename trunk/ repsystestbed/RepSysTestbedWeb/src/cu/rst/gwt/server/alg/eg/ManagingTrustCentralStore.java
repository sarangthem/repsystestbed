package cu.rst.gwt.server.alg.eg;



import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Random;

import org.apache.log4j.Logger;

import cu.rst.gwt.server.data.Feedback;
import cu.rst.gwt.server.entities.Agent;


public class ManagingTrustCentralStore
{

	static Logger logger = Logger.getLogger(ManagingTrustCentralStore.class.getName());
	public class MTCentralStoreRecord
	{
		public Agent aboutAgent;
		public int complaints;
		public MTCentralStoreRecord(Agent aboutAgent)
		{
			this.aboutAgent = aboutAgent;
			this.complaints = 1; //smart
		}
		
		public String toString()
		{
			return "About agent = " + aboutAgent.id + " num complaints = " + complaints;
		}
	}
	
	/*
	 * {Agent storing, Agent about whom info is store, number of complaints against}
	 * {Agent storing, Agent about whom info is store, number of by against}
	 * 
	 * Each agent might be storing for more than one agent. So the hashtable index should by the agent storing the info.
	 * Each value should be {Agent aboutAgent, int complaints) - MTCentralStoreRecord
	 * Note that multiple agents can stored the complaint data about a particular agent
	 */
	
	Hashtable complaintsAgainst;
	Hashtable complaintsBy;
	int maxAgentId;
	
	public ManagingTrustCentralStore()
	{

		complaintsAgainst = new Hashtable();
		complaintsBy = new Hashtable();
		maxAgentId = 0;
		
	}
	
	/**
	 * clears all data in complaintsAgainst and complaintsBy hashtables
	 */
	public void clearData()
	{
		complaintsAgainst.clear();
		complaintsBy.clear();
		maxAgentId = 0;
	}
	
	/*
	 * read from an arraylist of satisfactions and populate the hashtables.
	 * if the satisfaction is below certain threshold, that it is considered a complaint.
	 * maxAgentId is used to randomly pick an agent to store the data
	 * TODO note that here all agents have the same threshold. you might want to change this later. 
	 * TODO make use of exceptions
	 */
	public void readData(ArrayList feedbacks, double threshold, int maxAgentId) throws Exception
	{
		this.maxAgentId = maxAgentId;
		Iterator it = feedbacks.iterator();
		while(it.hasNext())
		{
			Feedback tr = (Feedback)it.next();
			//we care only if it is  complaint
			addTransaction(tr, threshold, maxAgentId);
		}
	}
	
	public void addTransaction(Feedback tr, double threshold, int maxAgentId)
	{
		//System.out.println("tr.value.doubleValue():-" + tr.value.doubleValue());
		//System.out.println("threshold:- " + threshold);
		if(tr.value.doubleValue()<threshold)
		{
			/*
			 * need to pick arbitrary agents to store the data. this should be random but in this version, 
			 * all data is stored at the source node of the satisfaction for complaints against 
			 * and the sink node stores for complaints by . Note that multiple arbitrary agents
			 * can store data. Not just one. But in this version only the source agent stores.
			 * 
			 * Note A: above todo done.
			 * Note that the above logic is flawed because when querying cfi and cri, one of them would be 0 which causes problems
			 * with the calculation in ManagingTrust. It is 0 because source stores complaints against only about a particular agent 
			 * and no complaints by the same agent. So you have to use an arbitrary agent!
			 * 
			 */
			
			int randomAgentId = (new Random()).nextInt(maxAgentId);
			
			//no duplicates within a MTCentralStoreRecord
			//ArrayList allComplaints = (ArrayList)complaintsAgainst.remove(sat.source.id); //see Note A on why this is commented.
			ArrayList allComplaints = (ArrayList)complaintsAgainst.remove(randomAgentId); //you are removing, make sure to store it back
			
			if(allComplaints!=null)
			{
				boolean found = false;
				Iterator it1 = allComplaints.iterator();
				while(it1.hasNext())
				{
					MTCentralStoreRecord record = (MTCentralStoreRecord)it1.next();
					if(record.aboutAgent.id==tr.getAssesee().id)
					{
						record.complaints++; 
						found=true;
						break;
					}
				}
				if(!found)
				{
					//none of the MTCentralStoreRecord is for the sink node
					MTCentralStoreRecord record = new MTCentralStoreRecord(tr.getAssesee());
					allComplaints.add(record); 
					
				}
				//complaintsAgainst.put(sat.source.id, allComplaints); //see Note A on why this is commented.
				complaintsAgainst.put(randomAgentId, allComplaints); //add back
			}else //this agent never stored anything before
			{
				MTCentralStoreRecord record = new MTCentralStoreRecord(tr.getAssesee());
				ArrayList complaints = new ArrayList();
				complaints.add(record);
				//complaintsAgainst.put(sat.source.id, complaints); //TODO need to change so that the source doesn't store
				complaintsAgainst.put(randomAgentId, complaints);
			}
			
			
			
			//no duplicates within a MTCentralStoreRecord
			//ArrayList complaints = (ArrayList)complaintsBy.remove(sat.sink.id); //see Note A on why this is commented.
			ArrayList complaints = (ArrayList)complaintsBy.remove(randomAgentId);
			if(complaints!=null)
			{
				boolean found = false;
				Iterator it1 = complaints.iterator();
				while(it1.hasNext())
				{
					MTCentralStoreRecord record = (MTCentralStoreRecord)it1.next();
					/*
					 * note that in this version, both the arbitrary agent to store and the agents which files complaints
					 * are the same. but the if(record.aboutAgent.id==sat.sink.id)  is there for future when different agents
					 * would be storing about different agents.
					 */
					if(record.aboutAgent.id==tr.getAssesee().id) 
					{
						record.complaints++; 
						found=true;
						/*
						 * commenting this because record actually stores about the same agent which files complaints.
						 * actually there should be one element in complaints arraylist because the hash key and aboutAgent = same agent (sink).
						 * nevertheless, this is for caution
						 */
						//break; 
					}
				}
				if(!found)
				{
					//none of the MTCentralStoreRecord is for the sink node
					MTCentralStoreRecord record = new MTCentralStoreRecord(tr.getAssesee());
					complaints.add(record); 
					
				}
				//complaintsBy.put(sat.sink.id, complaints);
				complaintsBy.put(randomAgentId, complaints);
				
				
			}else//this agent never stored anything before
			{
				MTCentralStoreRecord record = new MTCentralStoreRecord(tr.getAssesee());
				complaints = new ArrayList();
				complaints.add(record);
				//complaintsBy.put(sat.sink.id, complaints);
				complaintsBy.put(randomAgentId, complaints);
				
			}
			
			
		}else
		{
			System.out.println("Not a complaint.");
		}

		
	}
	
	/*
	 * query input: an arbitrary agent and the agent about whom we need to know
	 * query output: Number of complaints filed against an agent
	 */
 	public int queryComplaintsAgainst(Agent arbitraryAgent, Agent anAgent)
	{
 		logger.info("queryComplaintsAgainst. arbitrary: " + arbitraryAgent + " aboubt " + anAgent);
		//get all the complaints stored by the arbitraryAgent
		ArrayList allComplaints = (ArrayList)this.complaintsAgainst.get(arbitraryAgent.id);
//		logger.info("queryComplaintsAgainst. total complaints stored by arb agent: " +  allComplaints.size());
		if(allComplaints!=null)
		{
			Iterator it = allComplaints.iterator();
			while(it.hasNext())
			{
				//go through each MTCentralStoreRecord and see if the complaint stored is for the given agent
				MTCentralStoreRecord record = (MTCentralStoreRecord) it.next();
				if(record.aboutAgent.id == anAgent.id) return record.complaints;
			}
		}
		
		return 0;
	}
	/*
	 * query input: an arbitrary agent and the agent about whom we need to know
	 * query output: number of complaints filed by an agent
	 */
	public int queryComplaintsby(Agent arbitraryAgent, Agent anAgent)
	{
		logger.info("queryComplaintsby. arbitrary: " + arbitraryAgent + " aboubt " + anAgent);
		//get all the complaints stored by the arbitraryAgent
		ArrayList allComplaints = (ArrayList)this.complaintsBy.get(arbitraryAgent.id);
//		logger.info("queryComplaintsAgainst. total complaints stored by arb agent: " +  allComplaints.size());
		if(allComplaints==null) return 0;
		if(allComplaints!=null)
		{
			Iterator it = allComplaints.iterator();
			while(it.hasNext())
			{
				//go through each MTCentralStoreRecord and see if the complaint stored is for the given agent
				MTCentralStoreRecord record = (MTCentralStoreRecord) it.next();
				if(record.aboutAgent.id == anAgent.id) return record.complaints;
			}
		}
		return 0;
	}
	
	public void printMTCentralStore()
	{
		System.out.println("Complaints Against hashtable");
		System.out.println("============================");
		for(int i=0;i<maxAgentId;i++)
		{
			System.out.println("Agent " + i + " stores");
			System.out.println("---------------------");
			ArrayList complaints = (ArrayList)complaintsAgainst.get(i);
			if(complaints!=null)
			{
				Iterator it = complaints.iterator();
				while(it.hasNext())
				{
					MTCentralStoreRecord record = (MTCentralStoreRecord)it.next();
					System.out.println(record.toString());
				}
				System.out.println("-----------------------");
			}//else nothing stored
		}
		
		System.out.println("Complaints By hashtable");
		System.out.println("============================");
		for(int i=0;i<maxAgentId;i++)
		{
			System.out.println("Agent " + i + " stores");
			System.out.println("---------------------");
			ArrayList complaints = (ArrayList)complaintsBy.get(i);
			if(complaints!=null)
			{
				Iterator it = complaints.iterator();
				while(it.hasNext())
				{
					MTCentralStoreRecord record = (MTCentralStoreRecord)it.next();
					System.out.println(record.toString());
				}
				System.out.println("-----------------------");
			}//else nothing stored
		}
		
	}
	
	public static void main(String[] args) throws Exception
	{
		Agent a = new Agent();
		Agent b = new Agent();
		Agent c = new Agent();
		Agent d = new Agent();
		Feedback tr = new Feedback(a,b,0.2);
		Feedback tr1 = new Feedback(c,b,0.2);
		Feedback tr2 = new Feedback(d,b,0.2);
		Feedback tr3 = new Feedback(b,a,0.2);
		Feedback tr4 = new Feedback(c,a,0.2);
		Feedback tr5 = new Feedback(d,a,0.2);
		Feedback tr6 = new Feedback(b,c,0.2);
		Feedback tr7 = new Feedback(a,c,0.2);
		Feedback tr8 = new Feedback(d,c,0.2);
		Feedback tr9 = new Feedback(a,d,0.2);
		Feedback tr10 = new Feedback(b,d,0.2);
		Feedback tr11 = new Feedback(c,d,0.2);

		ArrayList transactions = new ArrayList();
		transactions.add(tr);
		transactions.add(tr1);
		transactions.add(tr2);
		transactions.add(tr3);
		transactions.add(tr4);
		transactions.add(tr5);
		transactions.add(tr6);
		transactions.add(tr7);
		transactions.add(tr8);
		transactions.add(tr9);
		transactions.add(tr10);
		transactions.add(tr11);

		ManagingTrustCentralStore store = new ManagingTrustCentralStore();
		store.readData(transactions, 0.5, 4);
		
		store.printMTCentralStore();
		
		System.out.println("Number of complaints stored by agent a by quering a is " + store.queryComplaintsby(a, a));
		System.out.println("Number of complaints stored about agent a by querying b is " + store.queryComplaintsAgainst(b, a));
		
		
	

	}

	
	
}
