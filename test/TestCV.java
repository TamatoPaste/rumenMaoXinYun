
import org.opencv.core.*;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.opencv.highgui.HighGui.*;
import static org.opencv.imgcodecs.Imgcodecs.imwrite;


public class TestCV {
    static { System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }

    public static void main(String[] args) throws InterruptedException {
        TestCV app = new TestCV();

        // app.show();

        //app.newMat();
        // app.moreChannels();
        // app.rowClone();
        // app.printOtherDS();

        app.drawEllipse();

    }

    private void show(){
        Mat imread = Imgcodecs.imread("C:\\Users\\Administrator\\Desktop\\resources\\dididada.jpg", 4);
        //imwrite("dddd.jpg",imread);

       /* if ( imread.empty() ){
            System.out.println("没读取到图片！");
            System.exit(1);
        }*/
        //namedWindow("Ryzen",1);

        HighGui.imshow("Ryzen",imread);
    }

    private void newMat(){
        Mat mat1 = new Mat(2,2,16,new Scalar(0,0,255));
        Mat mat2 = new Mat(2,2, CvType.CV_8UC3,new Scalar(0,0,255));
        System.out.println( mat1.dump() );
        System.out.println( mat2.dump() );
        System.out.println( mat1 == mat2);
    }

    private void moreChannels(){
        // 有问题
        // 书上是 int[] sz = {2,2,2}; Mat L(3,sz,CV_8U,scalar::all(0))
        // 这个方法应该对应Java Mat的public Mat(int[] sizes, int type, Scalar s)
        // 怎么试都不对
        Mat mat = new Mat(new int[]{2,2,2},CvType.CV_8UC4, Scalar.all(0));
        System.out.println( mat.dump() );
    }

    private void rowClone(){
        Mat mat = new Mat(2,2, CvType.CV_8UC3, new Scalar(1, 2, 3));
        System.out.println( mat.dump() );
        Mat rowClone = mat.row(1).clone();
        System.out.println( rowClone.dump() );
    }

    private void printOtherDS(){
        Point p = new Point(6,2);
        System.out.println( p );

        Point3 point3 = new Point3(1, 2, 3);
        System.out.println( point3 );
    }

    private void drawEllipse() throws InterruptedException {
        Mat mat = new Mat(500, 500, CvType.CV_8UC3, new Scalar(255, 255, 255));

        Imgproc.ellipse(mat,new Point(250,250),new Size(50,100),
                0,0,360,new Scalar(255,129,0),2,8);
        imwrite("E:\\didi.jpg",mat);
        imshow("didi",mat);
        waitKey(0);
    }

    private void matchTemplateTest(){
        //Imgproc.matchTemplate();
    }



}
