package cu.rst.gwt.server.util;

import java.util.List;

import cu.rst.gwt.server.data.Feedback;

public abstract class FeedbackGenerator 
{
	/**
	 * Implement this method to generate feedbacks
	 * @param strategy
	 * @return a list of feedbacks for an agent
	 */
	
	public abstract List<Feedback> generate(String propertiesFileName) throws Exception;
	
	/**
	 * Implement this method to parse an arff file with hand-crafted feedbacks
	 * @param arffFileName
	 * @return a list of feedbacks
	 */
		
	public abstract List<Feedback> generateHardcoded(String fileName) throws Exception;

}
