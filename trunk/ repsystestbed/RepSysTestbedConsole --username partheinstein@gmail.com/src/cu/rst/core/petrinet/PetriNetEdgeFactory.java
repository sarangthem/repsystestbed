package cu.rst.core.petrinet;

import org.jgrapht.EdgeFactory;

public class PetriNetEdgeFactory  implements EdgeFactory<PetriNetElementIntf, PetriNetEdge>
{

	@Override
	public PetriNetEdge createEdge(PetriNetElementIntf arg0, PetriNetElementIntf arg1) {
		return new PetriNetEdge(arg0, arg1, 0);
	}

}
