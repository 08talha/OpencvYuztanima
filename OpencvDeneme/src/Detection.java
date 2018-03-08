import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;



public class Detection {
	static JFrame frame;
	static JLabel lbl;
	static ImageIcon icon;
	static boolean kontrol = false;
	static int stt=0;
	static MatOfRect faces;
	static Mat image;
	public static void main(String[] args) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		/*Cascade Classifier için ö?retilmi? veri kümesi, opencv build/etc/haarcascades/ içerisinde yer almaktadyr
		  Daha fazla bilgi için Haar Cascade synyflandyrycylaryna bakabilirsiniz.
		*/

		CascadeClassifier cascadeFaceClassifier = new CascadeClassifier(
				"/home/talha/opencv-3.4.1/data/haarcascades/haarcascade_frontalface_default.xml");
		CascadeClassifier cascadeEyeClassifier = new CascadeClassifier(
				"/home/talha/opencv-3.4.1/data/haarcascades/haarcascade_eye.xml");
		
		CascadeClassifier cascadeNoseClassifier = new CascadeClassifier(
				"/home/talha/opencv-3.4.1/data/haarcascades/haarcascade_mcs_nose.xml");
	    //CascadeClassifier cascadeMouthClassifier = new CascadeClassifier("OpenCV/haarcascades/haarcascade_mcs_mouth.xml"); haarcascade_mcs_mouth on 2.4.11
		//Varsayylan kamera aygytyny ba?lat
		VideoCapture videoDevice = new VideoCapture();
		videoDevice.open(0);
		if (videoDevice.isOpened()) {
		//Sonsuz bir döngü ile sürekli olarak görüntü aky?y sa?lanyr 	
			while (true) {		
				Mat frameCapture = new Mat();
				videoDevice.read(frameCapture);
				image=new Mat();
				videoDevice.read(image);
				//Yakalanan görüntüyü önce dönü?tür ve frame içerisine yükle
				faces = new MatOfRect();
				cascadeFaceClassifier.detectMultiScale(frameCapture, faces);								
				//Yakalanan çerçeve varsa içerisinde dön ve yüzün boyutlary ölçüsünde bir kare çiz
				for (Rect rect : faces.toArray()) {
					//Sol üst kö?esine metin yaz
					Imgproc.putText(frameCapture, "Face", new Point(rect.x,rect.y-5), 1, 2, new Scalar(0,0,255));								
					Imgproc.rectangle(frameCapture, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height),
							new Scalar(0, 100, 0),3);
					
					if(kontrol){
						stt++;
						//resmiYakala("yuz"+stt,frameCapture,rect.size(),rect.width,rect.height);
						kontrol=false;
					}
					//System.out.println(rect.size()+" "+frameCapture.type()+"  "+faces.type());
				}
				
				
				/* //Gözleri bul ve bulunan array içerisinde dönerek kare çiz
				MatOfRect eyes = new MatOfRect();
				//cascadeEyeClassifier.detectMultiScale(frameCapture, eyes);
				for (Rect rect : eyes.toArray()) {
					//Sol üst kö?esine metin yaz
					Imgproc.putText(frameCapture, "Eye", new Point(rect.x,rect.y-5), 1, 2, new Scalar(0,0,255));				
					//Kare çiz
					Imgproc.rectangle(frameCapture, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height),
							new Scalar(200, 200, 100),2);
				}
				
				//Burunlary bul ve bulunan array içerisinde dönerek kare çiz
				/*MatOfRect nose = new MatOfRect();
				//cascadeNoseClassifier.detectMultiScale(frameCapture, nose);
				for (Rect rect : nose.toArray()) {
					//Sol üst kö?esine metin yaz
					Imgproc.putText(frameCapture, "Nose", new Point(rect.x,rect.y-5), 1, 2, new Scalar(0,0,255));				
					//Kare çiz
					Imgproc.rectangle(frameCapture, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height),
							new Scalar(50, 255, 50),2);
				}
				
				//A?yz bul ve bulunan array içerisinde dönerek kare çiz
			   /*MatOfRect mouth = new MatOfRect();
				cascadeMouthClassifier.detectMultiScale(frameCapture, mouth);
				for (Rect rect : mouth.toArray()) {
					
					Imgproc.rectangle(frameCapture, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height),
							new Scalar(129, 90, 50),2);
				}
				
				*/
				
				//Resmi swing nesnesinde gösterebilmek için önce image haline çevir ve ekrana bas
				PushImage(ConvertMat2Image(frameCapture));
				//System.out.println(String.format("%s yüz(FACES) %s göz(EYE) %s burun(NOSE) detected.", faces.toArray().length,eyes.toArray().length,nose.toArray().length));
			}
		} else {
			System.out.println("Video aygytyna ba?lanylamady.");
			return;
		}
	}
	private static void yuzTani(){
		CascadeClassifier cascadeFaceClassifier = new CascadeClassifier(
				"/home/talha/opencv-3.4.1/data/haarcascades/haarcascade_frontalface_default.xml");
		Mat yuzMat;
		 Mat yuzMatGray;
		Mat yuzMatResize1 = new Mat();
		MatOfRect faceDetections = new MatOfRect();
		cascadeFaceClassifier.detectMultiScale(image, faceDetections, 1.1, 7,0, new Size(150,40), new Size());
	    Rect rectCrop=null;
	    		
	    for (Rect rect : faceDetections.toArray()) {
	        Imgproc.rectangle(image, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 255, 0));
	        rectCrop = new Rect(rect.x, rect.y, rect.width, rect.height);
	    }
	    
	    if(rectCrop != null){
	    	yuzMat = new Mat(image,rectCrop);	
	    	Mat yuzMat2 = new Mat();
	    	yuzMatResize1 = new Mat();
			Size boyutlar = new Size(100,100);
			Imgproc.resize( yuzMat, yuzMatResize1, boyutlar);  	    
			yuzMatGray = new Mat();
	    	Imgproc.cvtColor(yuzMatResize1, yuzMatGray, Imgproc.COLOR_RGB2GRAY);
	    	
	    	MatOfByte matOfByte = new MatOfByte(); 
			Imgcodecs.imencode(".jpg", yuzMatGray, matOfByte);
			Imgcodecs.imencode(".jpg", yuzMatGray, matOfByte);
	    	byte[] matOfByteArr = matOfByte.toArray();
	    	//System.out.println(matOfByte.toString());
	    	yuzMat2=Imgcodecs.imread("talha.jpg");
	    	MatOfByte matOfByte2 = new MatOfByte(); 
			Imgcodecs.imencode(".jpg", yuzMatGray, matOfByte);
			Imgcodecs.imencode(".jpg", yuzMatGray, matOfByte);
	    	
	    	int width=0,height=0,karsilastir=0;
	    	
	    	if(yuzMat2.width()>yuzMatGray.width()){
	    		width=yuzMat2.width();
	    	}else{
	    		width=yuzMatGray.width();
	    	}
	    	if(yuzMat2.height()>yuzMatGray.height()){
	    		height=yuzMat2.height();
	    	}else{
	    		height=yuzMatGray.height();
	    	}
	    	for(int i=0;i<width;i++){
	    		for(int j=0;j<height;j++){
	    			double[] rgb = yuzMatGray.get(i, j);
	    			double[] rgb2 = yuzMat2.get(i, j);
	    			
	    			//Renk kodları artık bu dizi içerisinde tutulmaktadır
	    			//System.out.println(rgb[0]+"  "+rgb2[0]);
	    			if(rgb[0]==rgb2[0] || (rgb[0]-3<=rgb2[0] && rgb[0]+3 >= rgb2[0])){
	    				karsilastir++;
	    			}/*else if(rgb[1]==rgb2[1] || ((rgb[1]-5 <= rgb2[1]) && (rgb[1]+5 >= rgb2[1]))){
	    				karsilastir++;
	    			}else if(rgb[2]==rgb2[2] || (rgb[2]-5 <= rgb2[2] && rgb[2]+5 >= rgb2[2])){
	    				karsilastir++;
	    			}*/
	    		}
	    	}
	    	System.out.println(karsilastir);
	    	//System.out.println(max/karsilastir+"   max: "+max +"  karsilastir: "+karsilastir);
	    }
	}
	private static void resmiYakala() {
		// yüzü yakalayıp kesen program
		CascadeClassifier cascadeFaceClassifier = new CascadeClassifier(
				"/home/talha/opencv-3.4.1/data/haarcascades/haarcascade_frontalface_default.xml");
		Mat yuzMat;
		 Mat yuzMatGray;
		Mat yuzMatResize1 = new Mat();
		MatOfRect faceDetections = new MatOfRect();
		cascadeFaceClassifier.detectMultiScale(image, faceDetections, 1.1, 7,0, new Size(150,40), new Size());
	    Rect rectCrop=null;
	    		
	    for (Rect rect : faceDetections.toArray()) {
	        Imgproc.rectangle(image, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 255, 0));
	        rectCrop = new Rect(rect.x, rect.y, rect.width, rect.height);
	    }
	    
	    if(rectCrop != null){
	    	yuzMat = new Mat(image,rectCrop);	
	    	yuzMatResize1 = new Mat();
			Size boyutlar = new Size(100,100);
			Imgproc.resize( yuzMat, yuzMatResize1, boyutlar);  	    
			yuzMatGray = new Mat();
	    	Imgproc.cvtColor(yuzMatResize1, yuzMatGray, Imgproc.COLOR_RGB2GRAY);
	    	
	    	MatOfByte matOfByte = new MatOfByte(); 
			Imgcodecs.imencode(".jpg", yuzMatGray, matOfByte);
			Imgcodecs.imencode(".jpg", yuzMatGray, matOfByte);
	    	byte[] matOfByteArr = matOfByte.toArray();
	    	System.out.println(matOfByte.toString());
	    	Imgcodecs.imwrite("talha.jpg", yuzMatGray);
	    }
		
	}
	//Mat nesnesini image tipine dönü?tür
	private static BufferedImage ConvertMat2Image(Mat kameraVerisi) {
	
		
		MatOfByte byteMatVerisi = new MatOfByte();
		//Ara belle?e verilen formatta görüntü kodlar
		Imgcodecs.imencode(".jpg", kameraVerisi, byteMatVerisi);
		//Mat nesnesinin toArray() metodu elemanlary byte dizisine çevirir
		byte[] byteArray = byteMatVerisi.toArray();
		BufferedImage goruntu = null;
		try {
			InputStream in = new ByteArrayInputStream(byteArray);
			goruntu = ImageIO.read(in);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return goruntu;
	}
  	
//Bir frame (çerçeve) olu?turur
	public static void PencereHazirla() {
		frame = new JFrame();
		frame.setLayout(new FlowLayout());
		frame.setSize(700, 600);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JButton btnStop = new JButton("Stop");
		btnStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				kontrol=true;
				//resmiYakala();
				yuzTani();
			    
			}
		});
		btnStop.setBounds(319, 385, 117, 25);
		frame.getContentPane().add(btnStop);
		
	}
	//Resmi gösterecek label olu?turur
	public static void PushImage(Image img2) {
		//Pencere olu?turulmamy? ise hazyrlanyr
		if (frame == null)
			PencereHazirla();
		//Daha önceden bir görüntü yüklenmi? ise yenisi için kaldyryr
		if (lbl != null)
			frame.remove(lbl);
		icon = new ImageIcon(img2);
		lbl = new JLabel();
		lbl.setIcon(icon);
		frame.add(lbl);
		//Frame nesnesini yeniler
		frame.revalidate();
	}

}
class ImageProcessor  {
	
	public BufferedImage toBufferedImage(Mat matrix){
		int type = BufferedImage.TYPE_BYTE_GRAY;
		if ( matrix.channels() > 1 ) {
			type = BufferedImage.TYPE_3BYTE_BGR;
		}
		int bufferSize = matrix.channels()*matrix.cols()*matrix.rows();
		byte [] buffer = new byte[bufferSize];
		matrix.get(0,0,buffer); // get all the pixels
		BufferedImage image = new BufferedImage(matrix.cols(),matrix.rows(), type);
		final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		System.arraycopy(buffer, 0, targetPixels, 0, buffer.length);  
		//System.out.println("gör data:"+Arrays.toString(targetPixels));
		return image;
	}
	
	public static byte[] toByte(Mat matrix) {		
		int type = BufferedImage.TYPE_BYTE_GRAY;
		
		if ( matrix.channels() > 1 ) {
			type = BufferedImage.TYPE_3BYTE_BGR;
		}
		
		int bufferSize = matrix.channels()*matrix.cols()*matrix.rows();
		byte [] buffer = new byte[bufferSize];
		matrix.get(0,0,buffer);		
		BufferedImage image = new BufferedImage(matrix.cols(),matrix.rows(), type);
		
		final byte[] targetPixels1 = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		System.arraycopy(buffer, 0, targetPixels1, 0, buffer.length);  
		
		return targetPixels1;
	}	
	
	public static BufferedImage toBufferedImg(byte[] yuz) {		
		InputStream in = new ByteArrayInputStream(yuz);
		BufferedImage bufImg=new BufferedImage(100, 100, BufferedImage.TYPE_BYTE_GRAY);
		try {
			bufImg = ImageIO.read(in);
			//ImageIO.write(bufImg, "jpg", new File("yuz_goruntu_1.jpg"));					

		} catch (IOException e) {
			e.printStackTrace();
		}
		return bufImg;		
	}	

}
