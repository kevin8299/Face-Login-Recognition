/**
 * Read the sample face image in the predefined path
 * 
 * @author kevin
 * 
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class SamplesRead{
	public Map<String,ArrayList<String>> allSamples = new HashMap<String,ArrayList<String>>();
	
	/**
	 * Constructor to the class, which is the main function to read all the images in the input path
	 * 
	 * @param path
	 * @throws FileNotFoundException
	 */
	SamplesRead(String path) throws FileNotFoundException{
		File fl = new File(path);
		File[] list = fl.listFiles();
		for(int i =0;i<list.length;i++){
			String fnAll = list[i].getName();
			if(list[i].isFile()){
				String[] fnAllSplit =fnAll.split("\\.");
				if(fnAllSplit.length!=2)
					continue;
				String fnAll0 = fnAllSplit[0];
				String fnAll1 = fnAllSplit[1];
				if("jpg".equals(fnAll1)){
					String[] fnAll0Split  = fnAll0.split("_");
					if(fnAll0Split.length!=2)
						continue;
					String fnAll00 = fnAll0Split[0];//person name
					String fnAll01 = fnAll0Split[1];//pic id
					if(allSamples.containsKey(fnAll00)){
						ArrayList<String> perPicIds = allSamples.get(fnAll00);
						perPicIds.add(fnAll01);
						allSamples.remove(fnAll00);
						allSamples.put(fnAll00, perPicIds);
					}
					else{
						ArrayList<String> id = new ArrayList<String>();
						id.add(fnAll01);
						allSamples.put(fnAll00,id);
					}
				}
			}
		}
	}
		
	/**
	 * Do unit test here
	 * 
	 * @param args
	 * @throws FileNotFoundException
	 */
	public static void main(String[] args) throws FileNotFoundException{
		String path = ".\\pic\\";
		SamplesRead s = new SamplesRead(path);
		Map<String,ArrayList<String>> allSamples = s.allSamples;
		System.out.println("Over!" + allSamples.size());
		Iterator<String> iter = allSamples.keySet().iterator();
		while(iter.hasNext()){
			
		}
	}
	
}