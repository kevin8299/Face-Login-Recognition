/**
 * Real implementation to do face detection and face recognition
 * 
 * @author kevin
 * 
 */

import java.io.FileNotFoundException;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

public class DetectRecog{
	int sampleNum;
	int personNum;
	HttpFaceRecog recogHttp;
	String path;
	String lastString = "";
	int delay = 0;
	CascadeClassifier face_cascade;
	String face_cascade_name;
	Imshow imOri;
	Imshow imFace;
	
	/**
	 * Constructor to the class to init class parameters
	 */
	public DetectRecog(int pNum, int sNum, String p){
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		path = p;
		personNum = pNum;
		sampleNum = sNum;
		recogHttp =new HttpFaceRecog(path);
		imOri = new Imshow("Original");
		imFace = new Imshow("Face");
		face_cascade = new CascadeClassifier();
		face_cascade_name = "lbpcascade_frontalface.xml";
		if(!face_cascade.load(face_cascade_name))
			System.out.println("(!)Error loading" +face_cascade_name+"\n");
	}
	
	/**
	 * Main process to do face detection and recognition 
	 */
	public void run(){
		recogHttp.clear();
		VideoCapture camera = new VideoCapture(0);
		if(!camera.isOpened()){
			System.out.println("Cannot capture image!");
		}
		else{
			Mat frame = new Mat();
			int n = -1;
			System.out.println("Sampling process start,press 's' to start it!");
			while(true){
				if(camera.read(frame)){
					imOri.showImage(frame);
					if(imOri.Window.uploadStart){
						SamplesRead allSam;
						try{
							allSam = new SamplesRead(path);
							recogHttp.uploadSamples(allSam.allSamples);
							imOri.Window.uploadStart = false;
							imOri.Window.uploaded = true;
						}catch(FileNotFoundException e){
							e.printStackTrace();
						}
					}
					
					if(imOri.Window.uploaded){
						printOnce("Samples have been uploaded,Now press 'r' to start Recognition!");
						if(imOri.Window.recogStart){
							//printOnce("Recognizing process...");
							Mat detectedFace = detect(frame);
							if(detectedFace != null){
								imFace.showImage(detectedFace);
								String fl = path + "recog.jpg";
								Highgui.imwrite(fl,detectedFace);
								String recogResult = recogHttp.recognize(fl);
								System.out.println("Recognization R=result:"+recogResult);
								String[] recogSplit =recogResult.split("_");
								int len = recogSplit.length;
								if(len>=2){
									double conf = Double.valueOf(recogSplit[len-1]);
									if(conf>0.5){
										String name = "";
										for(int i = 0;i<len-1;i++){
											name+=recogSplit[i];
										}
										
										System.out.println("***********");
										System.out.println("Confident recognization result:"+name);
										System.out.println("***********");
									}
								}
								else
									System.out.println("Recognization result ERROR!!!");
							}
						}
					}
					else{//Sampling process
						if(imOri.Window.start){//&&imOri.Window.perNum<personNum
							if(imOri.Window.again){
								n=1;
								imOri.Window.again = false;		
							}
								
							if(imOri.Window.perNum >= personNum-1 && n >= sampleNum-1){
							//if(!(imOri.Window.perNum>=personNum-1&&n>=sampleNum-1)){
								printOnce("Max person number is reached ! Please press 'u' to upload the samples!");
							}
							else{
								if(n<sampleNum-1){
									Mat detectedFace = detect(frame);
									if(detectedFace!=null){
										imFace.showImage(detectedFace);
										printOnce("Person " + imOri.Window.perNum + "Sample "+ (n+1) + " is detected and stored !");
										n++;
										String fileName = imOri.Window.perNum + "_" +n;
										Highgui.imwrite(path +fileName +".jpg",detectedFace);
									}
								}
								else{
									printOnce("Max sample number is reached! click 'a ' to switch to another person ");
								}
							}
						}
					}
				}
			}
		}
		
		camera.release();
	}
			
	/**
	 * Detect face in input image with definition check
	 * 
	 * @param frame: input image 
	 * @return
	 */
	public Mat detect(Mat frame){
		delay++;
		if(delay>100){//delay
			delay = 0;
			Mat frame_gray = new Mat();
			Imgproc.cvtColor(frame, frame_gray, Imgproc.COLOR_RGB2GRAY);
			System.out.println("Times Up!!!");
			MatOfRect faces = new MatOfRect();
			face_cascade.detectMultiScale(frame_gray,faces,1.1,7,0,new Size(24,24),new Size(640,280));
			for(Rect rect:faces.toArray()){
				Core.rectangle(frame_gray,new Point(rect.x,rect.y),new Point(rect.x+rect.width,rect.y+rect.height),new Scalar(0,0,255));
				Mat roi = new Mat(frame,rect);
				if(defRatio(roi)>11){
					//System.out.println("=====");
					//System.out.println("Required definition face is detected!");
					//imOri.showImage(roi);
					return roi;
				}
			}
		}
		return null;
	}
					
	/**
	 * Print info for interaction
	 * 
	 * @param s: String to print out
	 */
	public void printOnce(String s){
		printOnceCore(s);
		lastString = s;
	}
	
	/**
	 * Implementation for print info only once
	 * 
	 * @param s: String to print out
	 */
	public void printOnceCore(String s){
		if(!lastString.equals(s)){
			System.out.println(s);
		}
	}

	/**
	 * Do definition check of the input face image 
	 *  
	 * @param frame: input face image 
	 * @return: definition ratio
	 */
	public double defRatio(Mat frame){
		Mat gray = new Mat();
		Imgproc.cvtColor(frame,gray,Imgproc.COLOR_RGB2GRAY);
		Mat img = gray;
		double temp = 0;
		double DR = 0;
		int i,j;
		double height = (double)img.height();
		double width  =(double)img.width();
		
		for(i = 0;i<height-1;i++){
			for(j=0;j<width-1;j++){
				temp+=Math.sqrt(Math.pow(img.get(i+1,j)[0]-img.get(i,j)[0],2)+Math.pow(img.get(i,j+1)[0]-img.get(i,j)[0],2));
				temp+=Math.abs(img.get(i+1,j)[0]-img.get(i,j)[0]+Math.abs(img.get(i,j+1)[0]-img.get(i,j)[0]));
			}
		}
		DR = temp/(width*height);
		//System.out.println("defRatio:"+DR);
		return DR;
	}
}

