import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;

import static org.opencv.highgui.HighGui.namedWindow;
import static org.opencv.imgcodecs.Imgcodecs.imwrite;


public class TestCV {
    static { System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }

    public static void main(String[] args){
        Mat imread = Imgcodecs.imread("C:\\Users\\Administrator\\Desktop\\resources\\dididada.jpg", 4);
        imwrite("dddd.jpg",imread);

       /* if ( imread.empty() ){
            System.out.println("没读取到图片！");
            System.exit(1);
        }*/
       //namedWindow("Ryzen",1);

        HighGui.imshow("Ryzen",imread);


    }
}
