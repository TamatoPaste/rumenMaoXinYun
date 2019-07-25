package com.edu.bbbt.highgui;

import org.opencv.core.Mat;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * This class was designed for use in Java applications
 * to recreate the OpenCV HighGui functionalities.
 *
 * HighGui 类 是 final 的。
 * 本类中的成员变量，成员方法全是static的
 */
public final class HighGui {

    // Constants for namedWindow 标识图片显示的常量
    // 当图片以window_autoseize 模式显示时，将显示图像原始大小
    // 否则，将会对图片缩放以适应窗口
    /*
    *  图像为  8位无符号类型，就显示图像本来的样子
    *           16 位无符号类型，或32位整形，使用像数值除以256 [0,65535] 映射到[0,255]
    *           32位浮点数，像数值乘以255，[0,1] 映射到[0,255]
    * */
    public final static int WINDOW_NORMAL = ImageWindow.WINDOW_NORMAL;
    public final static int WINDOW_AUTOSIZE = ImageWindow.WINDOW_AUTOSIZE;

    // Control Variables
    public static int n_closed_windows = 0;
    public static int pressedKey = -1;
    public static CountDownLatch latch = new CountDownLatch(1);

    // Windows Map
    public static Map<String, ImageWindow> windows = new HashMap<String, ImageWindow>();

    // 用于创建一个窗口，作为容纳图片和进度条的容器，若同名容器已存在，则不做任何操作
    // 若是简单显示图片，不用显示调用
    public static void namedWindow(String winname) {
        namedWindow(winname, HighGui.WINDOW_AUTOSIZE);
    }
    // winname被用作窗口的标识符
    // flag是窗口的标识，可以取window_normal = 0,window_autosize=1,window_opengl,这个没找到
    // 上面方法不填flag，就是默认按window_autosize生成窗口
    public static void namedWindow(String winname, int flag) {
        ImageWindow newWin = new ImageWindow(winname, flag);
        if (windows.get(winname) == null) windows.put(winname, newWin);
        //windows.putIfAbsent(winname, newWin);
    }

    //C++: void imshow(const string& winname, InputArray mat)
    // 用于在指定窗口中显示图片
    // winname 是要显示的窗口标识名称，mat是要显示的图像。
    public static void imshow(String winname, Mat img) {
        if (img.empty()) {
            System.err.println("Error: Empty image in imshow");
            System.exit(-1);
        } else {
            ImageWindow tmpWindow = windows.get(winname);
            if (tmpWindow == null) {
                ImageWindow newWin = new ImageWindow(winname, img);
                windows.put(winname, newWin);
            } else {
                tmpWindow.setMat(img);
            }
        }
    }

    public static Image toBufferedImage(Mat m) {
        int type = BufferedImage.TYPE_BYTE_GRAY;

        if (m.channels() > 1) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }

        int bufferSize = m.channels() * m.cols() * m.rows();
        byte[] b = new byte[bufferSize];
        m.get(0, 0, b); // get all the pixels
        BufferedImage image = new BufferedImage(m.cols(), m.rows(), type);

        final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(b, 0, targetPixels, 0, b.length);

        return image;
    }

    public static JFrame createJFrame(String title, int flag) {
        JFrame frame = new JFrame(title);

        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                n_closed_windows++;
                if (n_closed_windows == windows.size()) latch.countDown();
            }
        });

        frame.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                pressedKey = e.getKeyCode();
                latch.countDown();
            }
        });

        if (flag == WINDOW_AUTOSIZE) frame.setResizable(false);

        return frame;
    }

    public static void waitKey(){
        waitKey(0);
    }

    public static int waitKey(int delay) {
        // Reset control values
        latch = new CountDownLatch(1);
        n_closed_windows = 0;
        pressedKey = -1;

        // If there are no windows to be shown return
        if (windows.isEmpty()) {
            System.err.println("Error: waitKey must be used after an imshow");
            System.exit(-1);
        }

        // Remove the unused windows
        Iterator<Map.Entry<String,
                ImageWindow>> iter = windows.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<String,
                    ImageWindow> entry = iter.next();
            ImageWindow win = entry.getValue();
            if (win.alreadyUsed) {
                iter.remove();
                win.frame.dispose();
            }
        }

        // (if) Create (else) Update frame
        for (ImageWindow win : windows.values()) {

            if (win.img != null) {

                ImageIcon icon = new ImageIcon(toBufferedImage(win.img));

                if (win.lbl == null) {
                    JFrame frame = createJFrame(win.name, win.flag);
                    JLabel lbl = new JLabel(icon);
                    win.setFrameLabelVisible(frame, lbl);
                } else {
                    win.lbl.setIcon(icon);
                }
            } else {
                System.err.println("Error: no imshow associated with" + " namedWindow: \"" + win.name + "\"");
                System.exit(-1);
            }
        }

        try {
            if (delay == 0) {
                latch.await();
            } else {
                latch.await(delay, TimeUnit.MILLISECONDS);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Set all windows as already used
        for (ImageWindow win : windows.values())
            win.alreadyUsed = true;

        return pressedKey;
    }
    // 关闭窗口，收回窗口内存
    // 对于小程序来说，没有必要手动调用destroyWindow或destroyAllWindow方法，
    // 因为程序在退出时，系统会回收所有内存
    public static void destroyWindow(String winname) {
        ImageWindow tmpWin = windows.get(winname);
        if (tmpWin != null) windows.remove(winname);
    }

    public static void destroyAllWindows() {
        windows.clear();
    }

    public static void resizeWindow(String winname, int width, int height) {
        ImageWindow tmpWin = windows.get(winname);
        if (tmpWin != null) tmpWin.setNewDimension(width, height);
    }

    public static void moveWindow(String winname, int x, int y) {
        ImageWindow tmpWin = windows.get(winname);
        if (tmpWin != null) tmpWin.setNewPosition(x, y);
    }
}
