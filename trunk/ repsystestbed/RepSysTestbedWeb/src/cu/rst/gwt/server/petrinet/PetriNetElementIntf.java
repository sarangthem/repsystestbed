package cu.rst.gwt.server.petrinet;

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
	
}
