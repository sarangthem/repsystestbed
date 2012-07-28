package cu.rst.core.petrinet;

import java.util.ArrayList;

public interface PetriNetElementIntf
{
	/**
	 * 
	 * @param Tokens contain changes based on which an update would occur. These changes can be feedbacks,
	 * reputation edges or trust edges
	 * @return After an update has happened, the transition may provide a list of changes in terms of reputation
	 * edges/trust edges which are added to each token and put in the outgoing places. The places then do the
	 * necessary update to themselves (for e.g. add the edges in the list of changes in each token.) After updating,
	 * the places may return a list of changes.
	 * 
	 * @throws Exception
	 */
	public ArrayList update(ArrayList<Token> tokens) throws Exception;
	
	/**
	 * This method must be overwritten if an algorithm needs to tell the testbed what changes to make in what outgoing place.
	 * For example you have rg0<->alg->rg1 as the workflow. In this case, the alg may want to only add new edges to rg1 but not to rg0.
	 * Or maybe the alg may want to add different set of edges to rg0 vs rg1    
	 * @param tokens
	 * @param p
	 * @return
	 * @throws Exception
	 */
	public ArrayList update(ArrayList<Token> tokens, Place p) throws Exception;
	
	public String getName() throws Exception;
	
	public void setWorkflow(PetriNet net) throws Exception;
	
}
