package main;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.imageio.ImageIO;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Point3;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;
import org.opencv.objdetect.CascadeClassifier;

//
// Detects faces in an image, draws boxes around them, and writes the results
// to "faceDetection.png".
//
class DetectFaceDemo {
	String path = "C:/Users/Root/Desktop/lena1.png";
	String path2 = "C:/Users/Root/Desktop/lena.png";
	String path3 = "C:/Users/Root/Desktop/new.png"; 
  public void run() {
	int n = 3;
	int color = 0;
	double width;
	double height;
	double ratio;
	double focal;
	 //(169*17.49)/(54/12);
	double distance = 0;
	double distancewidth = 0;
	double realwidth = 54.0;
	double Bracket_Distance = 462;//462
	double Sensor_Distance = 1.5;
	double Target_Height = 110.125;
	double Sensor_Height = 0.6125;
	double Bracket_Height = 31.6875;
	double posX = 0;
	double posY = 0;
	Moments moments = new Moments();
	Point thing2;
	
	double DeltaHeight = Target_Height - (Bracket_Height + Sensor_Height);
	double Kinect_Distance = Bracket_Distance - Sensor_Distance;
	double Yrot;
	double Kinect_Angle = (Math.atan2(DeltaHeight,Kinect_Distance)*180/Math.PI);
	
	Point3 thing;
	
    System.out.println("\nRunning DetectFaceDemo");
    Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT,new Size(n,n));
    List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
    Mat imagergb = Highgui.imread(path,Highgui.CV_LOAD_IMAGE_COLOR);
    Mat imagergb2 = Highgui.imread(path,Highgui.CV_LOAD_IMAGE_COLOR);
    Mat imagegray = Highgui.imread(path,Highgui.CV_LOAD_IMAGE_GRAYSCALE);

    Imgproc.cvtColor(imagergb,imagegray, Imgproc.COLOR_RGB2GRAY);
    Imgproc.threshold(imagegray,imagegray,250,255, Imgproc.THRESH_BINARY);
    Imgproc.erode(imagegray,imagegray,element);
    Imgproc.dilate(imagegray,imagegray,element);
    Imgproc.findContours(imagegray,contours,new Mat(), Imgproc.RETR_LIST,Imgproc.CHAIN_APPROX_SIMPLE);
    Imgproc.cvtColor(imagegray,imagegray, Imgproc.COLOR_GRAY2BGR);
    
    
    
    MatOfPoint2f approxCurve = new MatOfPoint2f();
    System.out.println(contours.size());
    
    for (int i=0; i<contours.size(); i++)
    {
        //Convert contours(i) from MatOfPoint to MatOfPoint2f
        MatOfPoint2f contour2f = new MatOfPoint2f( contours.get(i).toArray() );
        //Processing on mMOP2f1 which is in type MatOfPoint2f
        double approxDistance = Imgproc.arcLength(contour2f, true)*0.02;
        Imgproc.approxPolyDP(contour2f, approxCurve, approxDistance, true);

        //Convert back to MatOfPoint
        MatOfPoint points = new MatOfPoint( approxCurve.toArray() );

        // Get bounding rect of contour
        //Imgproc.HuMoments(moments,new Mat());
        //posY = moments.m01/Imgproc.contourArea(contours.get(i));
        Rect rect = Imgproc.boundingRect(points);
        width = rect.width;
        height = rect.height;
        ratio = width/height;
        
        //if(Math.abs(Imgproc.contourArea(contours.get(i))) > 3500 && Math.abs(Imgproc.contourArea(contours.get(i))) < 7000 && Imgproc.isContourConvex(points)){
        color = (color+1)*4;
        if(Math.abs(ratio) > 2.5 && Math.abs(ratio) < 5.5){
        	
        System.out.println(rect);
        System.out.println("Pixel Width: " + width);
        Imgproc.drawContours(imagegray,contours,i,new Scalar(0,255,0));
        
         // draw enclosing rectangle (all same color, but you could use variable i to make them unique)
        Core.rectangle(imagegray,rect.tl(), rect.br(), new Scalar(255, 100, 0),10, 8,0);
        //System.out.println("Focal Length: " + focal);
        //distance = ((54/12)*focal)/width;
        //distancewidth = (((54/12)*640/(rect.width)))/2;
        //distance = (distancewidth)/(0.455);
        thing2 = new Point((rect.x + rect.width/2)/Imgproc.contourArea(contours.get(i)), (rect.y+rect.height/2)/Imgproc.contourArea(contours.get(i)));
        thing = new Point3((thing2.x - 640/2), -(thing2.y - 488/2),0);
//        System.out.println(thing.y);
//        System.out.println(rect.y);
//        System.out.println(rect.height);
        //focal = (17.49*rect.y*0.6125) /(12*488);
        //distance = ((2.8 * 300 * 488)/(rect.y * Sensor_Height));
        Yrot = (thing.y)/(488/2)*22.5;
        distance = (DeltaHeight / (Math.tan((Kinect_Angle + Yrot)*Math.PI/180)))/12;
        
        System.out.println("Distance: " + distance);
        
        	if(Math.abs(ratio) > 2.5 && Math.abs(ratio) < 5  ){
        		
        		Core.circle(imagegray,new Point(rect.x + rect.width/2, rect.y+rect.height/2), 5, new Scalar(255,0,0),1);
        	}
       }
    }
    
    String textDistance = "Distance: " + String.valueOf(distance);
    Core.addWeighted(imagergb,1,imagegray,2,0, imagergb2);
    Core.line(imagergb2, new Point(640/2, 0), new Point(640/2, 488), new Scalar(0,255,255), 1);
    Core.line(imagergb2, new Point(0, 488/2), new Point(640, 488/2), new Scalar(0,255,255), 1);
    Core.putText(imagergb2,textDistance, new Point(10,10),Core.FONT_HERSHEY_SIMPLEX,0.25, new Scalar(0,255,255));
    //Highgui.imwrite(path3, imagegray);
    new LoadImage(path3,imagergb2);
  }
}

public class Console {
  public static void main(String[] args) {
    System.out.println("Hello, OpenCV");

    // Load the native library.
    System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    new DetectFaceDemo().run();
  }
}
