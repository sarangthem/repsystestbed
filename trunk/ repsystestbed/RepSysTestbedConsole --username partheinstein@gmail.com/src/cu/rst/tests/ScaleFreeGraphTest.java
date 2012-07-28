package cu.rst.tests;

public class ScaleFreeGraphTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) 
	{
		org.jgrapht.generate.ScaleFreeGraphGenerator gla;
		
		int[][] a = new int[3][4];
		a[0][1] = 3;
		a[2][1] = 1;
		for(int i=0;i<a.length;i++)
		{
			for(int j=0;j<a[i].length;j++)
			{
				System.out.print(a[i][j] + " ");
			}
			System.out.println();
		}

	}

}
