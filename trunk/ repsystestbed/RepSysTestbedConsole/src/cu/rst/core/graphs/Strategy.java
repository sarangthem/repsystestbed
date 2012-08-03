package cu.rst.core.graphs;


/**
 * This class encapsulates the behaviour of an agent. That is given a strategy, an agent should create feedbacks. 
 * @author pchandra
 *
 */
@Deprecated
public class Strategy 
{	
	private int numberOfFeedbacks;
	private double feedbackMean, stdDev;
	private int strategyID;


	/**
	 * 
	 * @param strategyID
	 * @param numberOfFeedbacks
	 * @param feedbackMean
	 * @param feedbackStdDev
	 * 
	 */
	public Strategy(int strategyID, int numberOfFeedbacks, double feedbackMean, double stdDev) 
	{
		this.numberOfFeedbacks = numberOfFeedbacks;
		this.feedbackMean = feedbackMean;
		this.strategyID = strategyID;
		this.setStdDev(stdDev);
	}
	/**
	 * @param numberOfFeedbacks the numberOfFeedbacks to set
	 */
	public void setNumberOfFeedbacks(int numberOfFeedbacks) 
	{
		this.numberOfFeedbacks = numberOfFeedbacks;
	}
	/**
	 * @return the numberOfFeedbacks
	 */
	public int getNumberOfFeedbacks() 
	{
		return numberOfFeedbacks;
	}

	/**
	 * @param feedbackMean the feedbackMean to set
	 */
	public void setFeedbackMean(double feedbackMean) 
	{
		this.feedbackMean = feedbackMean;
	}
	/**
	 * @return the feedbackMean
	 */
	public double getFeedbackMean() 
	{
		return feedbackMean;
	}
	
	public int getStrategyID()
	{
		return this.strategyID;
	}
	
	
	@Override
	public boolean equals(Object o)
	{
		if(this.strategyID == ((Strategy)o).getStrategyID()) return true;
		else return false;
	}
	
	/**
	 * @param stdDev the stdDev to set
	 */
	public void setStdDev(double stdDev)
	{
		this.stdDev = stdDev;
	}
	/**
	 * @return the stdDev
	 */
	public double getStdDev()
	{
		return stdDev;
	}

	

}
