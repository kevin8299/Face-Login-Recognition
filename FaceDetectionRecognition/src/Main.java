/**
 * Main entry point for this program
 * 
 * @author kevin
 *
 */
public class Main
{
	public static void main(String[] args)
	{
		int sampleNum = 4;
		int personNum = 3;
		String picPath = ".\\pic\\";
		DetectRecog dr = new DetectRecog(personNum, sampleNum, picPath);
		dr.run();
	}
}