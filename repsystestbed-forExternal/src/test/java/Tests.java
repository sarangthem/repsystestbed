import static cu.rst.util.Util.*;
import static org.testng.Assert.*;

import org.testng.annotations.Test;

import cu.rst.alg.EigenTrustv1;
import cu.rst.graph.FHG;
import cu.rst.graph.RG;

public class Tests {

	/*
	 * Sanity Tests
	 */

	@Test
	public void eigenvectorSanityTest_AWest_PennState() {

		// This test uses the example provided in
		// http://rtg.cis.upenn.edu/qtm/doc/p2p_reputation.pdf. As per this
		// example, the expected eigenvector is [[0.35], [0.49], [0.19]]. We
		// were unable to verify this result using our implementation. However
		// the results from our implementation and results from manual
		// calculation are the same. So it is likely the results provided in the
		// link above are incorrect for the given input.
		// Manual calculation steps:
		// iter1
		// t1 = {{0, 0.66, 0.125}, {1, 0, 0.875}, {0, 0.33, 0}} . {{0.33}
		// ,{0.33}, {0.33}}
		// t1 = {{0.25905}, {0.61875}, {0.1089}}
		// t1 = 0.5 * {{0.25905}, {0.61875}, {0.1089}} + 0.5 * {{0.33} ,{0.33},
		// {0.33}}
		// t1 = {{0.294525}, {0.474375}, {0.21945}}
		//
		// iter2
		// t2 = {{0, 0.66, 0.125}, {1, 0, 0.875}, {0, 0.33, 0}} . {{0.294525},
		// {0.474375}, {0.21945}}
		// t2 = {{0.340519}, {0.486544}, {0.156544}}
		// t2 = 0.5 * {{0.340519}, {0.486544}, {0.156544}} + 0.5 * {{0.33}
		// ,{0.33}, {0.33}}
		// t2 = {{0.33526}, {0.408272}, {0.243272}}
		//
		// iter3
		// t3 = {{0, 0.66, 0.125}, {1, 0, 0.875}, {0, 0.33, 0}} . {{0.33526},
		// {0.408272}, {0.243272}}
		// t3 = {{0.299869}, {0.548123}, {0.13473}}
		// t3 = 0.5 * {{0.299869}, {0.548123}, {0.13473}} + 0.5 * {{0.33}
		// ,{0.33}, {0.33}}
		// t3 = {{0.314935}, {0.439062}, {0.232365}}
		//
		// iter4
		// t4 = {{0, 0.66, 0.125}, {1, 0, 0.875}, {0, 0.33, 0}} . {{0.314935},
		// {0.439062}, {0.232365}}
		// t4 = {{0.318827}, {0.518254}, {0.14489}}
		// t4 = 0.5 * {{0.318827}, {0.518254}, {0.14489}} + 0.5 * {{0.33}
		// ,{0.33}, {0.33}}
		// t4 = {{0.324414}, {0.424127}, {0.237445}}
		//
		// iter5
		// t5 = {{0, 0.66, 0.125}, {1, 0, 0.875}, {0, 0.33, 0}} . {{0.324414},
		// {0.424127}, {0.237445}}
		// t5 = {{0.309604}, {0.532178}, {0.139962}}
		// t5 = 0.5 * {{0.309604}, {0.532178}, {0.139962}} + 0.5 * {{0.33}
		// ,{0.33}, {0.33}}
		// t5 = {{0.319802}, {0.431089}, {0.234981}}
		//
		// iter6
		// t6 = {{0, 0.66, 0.125}, {1, 0, 0.875}, {0, 0.33, 0}} . {{0.319802},
		// {0.431089}, {0.234981}}
		// t6 = {{0.313891}, {0.52541}, {0.142259}}
		// t6 = 0.5 * {{0.313891}, {0.52541}, {0.142259}} + 0.5 * {{0.33}
		// ,{0.33}, {0.33}}
		// t6 = {{0.321946}, {0.427705}, {0.23613}}
		//
		// iter7
		// t7 = {{0, 0.66, 0.125}, {1, 0, 0.875}, {0, 0.33, 0}} . {{0.321946},
		// {0.427705}, {0.23613}}
		// t7 = {{0.311802}, {0.52856}, {0.141143}}
		// t7 = 0.5 * {{0.311802}, {0.52856}, {0.141143}} + 0.5 * {{0.33}
		// ,{0.33}, {0.33}}
		// t7 = {{0.320901}, {0.42928}, {0.235572}}
		//
		// iter8
		// t8 = {{0, 0.66, 0.125}, {1, 0, 0.875}, {0, 0.33, 0}} . {{0.320901},
		// {0.42928}, {0.235572}}
		// t8 = {{0.312771}, {0.527027}, {0.141662}}
		// t8 = 0.5 * {{0.312771}, {0.527027}, {0.141662}} + 0.5 * {{0.33}
		// ,{0.33}, {0.33}}
		// t8 = {{0.321386}, {0.428514}, {0.235831}}
		//
		// iter 9
		// t9 = {{0, 0.66, 0.125}, {1, 0, 0.875}, {0, 0.33, 0}} . {{0.321386},
		// {0.428514}, {0.235831}}
		// t9 = {{0.312298}, {0.527738}, {0.14141}}
		// t9 = 0.5 * {{0.312298}, {0.527738}, {0.14141}} + 0.5 * {{0.33}
		// ,{0.33}, {0.33}}
		// t9 = {{0.321149}, {0.428869}, {0.235705}}
		//
		// iter 10
		// t10 = {{0, 0.66, 0.125}, {1, 0, 0.875}, {0, 0.33, 0}} . {{0.321149},
		// {0.428869}, {0.235705}}
		// t10 = {{0.312517}, {0.527391}, {0.141527}}
		// t10 = 0.5 * {{0.312517}, {0.527391}, {0.141527}} + 0.5 * {{0.33}
		// ,{0.33}, {0.33}}
		// t10 = {{0.321259}, {0.428696}, {0.235764}}

		double[][] normalizedFeedbacks = { { 0, 1, 0 },
				{ 0.66666666, 0, 0.33333333 }, { 0.125, 0.875, 0 } };

		double a = 0.5;

		double[][] eigenVector = (new EigenTrustv1()).computeEigenVector(
				normalizedFeedbacks, a, 20);

		assertTrue(Math.abs(eigenVector[0][0] - 0.32) < 0.1);
		assertTrue(Math.abs(eigenVector[1][0] - 0.42) < 0.1);
		assertTrue(Math.abs(eigenVector[2][0] - 0.23) < 0.1);

	}

	@Test
	public void eigenvectorSanityTest_Lian_UCSB() {

		// This test uses the samples provided in
		// iptps06.cs.ucsb.edu/talks/lian-maze-iptps06.ppt. Even the value of
		// the weight given to the pretrusted peers is not given in this
		// example, it appears the authors gave it a value of 0.
		double[][] normalizedFeedbacks = { { 0, 0.9, 0.1 }, { 0.9, 0, 0.1 },
				{ 0.2, 0.2, 0.6 } };

		double[][] eigenVector = (new EigenTrustv1()).computeEigenVector(
				normalizedFeedbacks, 0, 20);

		assertTrue(Math.abs(eigenVector[0][0] - 0.4) < 0.1);
		assertTrue(Math.abs(eigenVector[1][0] - 0.4) < 0.1);
		assertTrue(Math.abs(eigenVector[2][0] - 0.2) < 0.1);

	}

	@Test
	public void markovChainSanityTestFromWikipedia() {
		// http://en.wikipedia.org/wiki/Markov_chain
		double[][] normalizedFeedbacks = { { 0.9, 0.075, 0.025 },
				{ 0.15, 0.8, 0.05 }, { 0.25, 0.25, 0.5 } };

		double[][] expectedEigenVector = { { 0.625, 0.3125, 0.0625 } };

		double[][] computedEigenVector = EigenTrustv1.computeEigenVectorTmp(20,
				normalizedFeedbacks);

		assertTrue(Math.abs(computedEigenVector[0][0]
				- expectedEigenVector[0][0]) < 0.1);
		assertTrue(Math.abs(computedEigenVector[0][1]
				- expectedEigenVector[0][1]) < 0.1);
		assertTrue(Math.abs(computedEigenVector[0][2]
				- expectedEigenVector[0][2]) < 0.1);

		// System.out.println(Arrays.deepToString(computedEigenVector));
	}

	@Test
	public void eigenTrustRefactorSanity() throws Exception {
		String input = "src/test/resources/input/eigentrust_sanity.arff";

		FHG fhg = generateFHG(input);
		RG rg1 = (RG) eigenTrust(fhg);
		RG rg2 = (RG) (new EigenTrustv1()).execute(fhg);

		assertTrue(spearman(rg1, rg2) == 1);
	}

	/*
	 * Experiments Tests
	 */

	@Test
	public void testEigenTrust_NormAttack1() throws Exception {
		String input1 = "src/test/resources/input/normalization1.arff";
		String input2 = "src/test/resources/input/normalization2.arff";

		RG rg1 = (RG) eigenTrust(discretize(generateFHG(input1)));
		RG rg2 = (RG) eigenTrust(discretize(generateFHG(input2)));

		double spearmanCoeff = spearman(rg1, rg2);

		assertTrue(spearmanCoeff == 1);
	}

	@Test
	public void testPeerTrust_NormAttack1() throws Exception {
		String input1 = "src/test/resources/input/normalization1.arff";
		String input2 = "src/test/resources/input/normalization2.arff";

		RG rg1 = (RG) peerTrust(generateFHG(input1));
		RG rg2 = (RG) peerTrust(generateFHG(input2));

		double spearmanCoeff = spearman(rg1, rg2);

		// PeerTrust behaves odd in this situation
		assertFalse(spearmanCoeff == 1);
	}

	@Test
	public void testEigenTrust_SelfPromAttack_1() throws Exception {
		String input1 = "src/test/resources/input/normalization1.arff";
		String input2 = "src/test/resources/input/exp1b_bootstrap.arff";

		RG rg1 = (RG) eigenTrust(discretize(generateFHG(input1)));
		RG rg2 = (RG) eigenTrust(discretize(generateFHG(input2)));

		double spearmanCoeff = spearman(rg1, rg2);

		// Expect the two graphs to be different
		assertFalse(spearmanCoeff == 1);
	}

	@Test
	public void testPeerTrust_SelfPromAttack_2() {
		String input1 = "src/test/resources/input/normalization1.arff";
		String input2 = "src/test/resources/input/exp1b_bootstrap.arff";

		try {
			RG rg1 = (RG) peerTrust(generateFHG(input1));
			RG rg2 = (RG) peerTrust(generateFHG(input2));
		} catch (Exception e) {
			// TODO assert type of exception and not exception message
			assertEquals(e.getMessage(),
					"total trust score is 0. It should never be zero.");
		}
	}

	@Test
	public void testEigenTrust_SlanderingAttack_1() throws Exception {
		String input1 = "src/test/resources/input/normalization1.arff";
		String input2 = "src/test/resources/input/exp1c_slandering.arff";

		RG rg1 = (RG) eigenTrust(discretize(generateFHG(input1)));
		RG rg2 = (RG) eigenTrust(discretize(generateFHG(input2)));

		double spearmanCoeff = spearman(rg1, rg2);

		assertTrue(spearmanCoeff == 1);
	}

	@Test
	public void testPeerTrust_SlanderingAttack_1() {
		String input1 = "src/test/resources/input/normalization1.arff";
		String input2 = "src/test/resources/input/exp1c_slandering.arff";

		try {
			RG rg1 = (RG) peerTrust(generateFHG(input1));
			RG rg2 = (RG) peerTrust(generateFHG(input2));
		} catch (Exception e) {
			// TODO assert type of exception and not exception message
			assertEquals(e.getMessage(),
					"total trust score is 0. It should never be zero.");
		}
	}

	@Test
	public void testEigenTrust_SlanderingAttack_2() throws Exception {
		String input1 = "src/test/resources/input/normalization1.arff";
		String input2 = "src/test/resources/input/exp1d_slandering.arff";

		RG rg1 = (RG) eigenTrust(discretize(generateFHG(input1)));
		RG rg2 = (RG) eigenTrust(discretize(generateFHG(input2)));

		double spearmanCoeff = spearman(rg1, rg2);

		assertTrue(spearmanCoeff == 1);
	}

	@Test
	public void testPeerTrust_SlanderingAttack_e() throws Exception {
		String input1 = "src/test/resources/input/normalization1.arff";
		String input2 = "src/test/resources/input/exp1d_slandering.arff";

		RG rg1 = (RG) peerTrust(generateFHG(input1));
		RG rg2 = (RG) peerTrust(generateFHG(input2));

		double spearmanCoeff = spearman(rg1, rg2);

		assertTrue(spearmanCoeff == 1);
	}
}