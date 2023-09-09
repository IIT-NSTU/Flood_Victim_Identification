import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.Scanner;

public class Human_Identify_FloodVictims_2 {

    public static void main(String[] args) {
        int u, v, distFlood;

        System.out.println("Enter height from user to drone (u): ");
        Scanner sc = new Scanner(System.in);
        u = sc.nextInt();

        System.out.println("Enter distance user to flood affected area (v): ");
        v = sc.nextInt();

        distFlood = (int) Math.sqrt(u * u + v * v);

        System.out.println("Distance between user and flood affected area (distFlood): " + distFlood);

        sc.close();

        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        CascadeClassifier faceDetector = new CascadeClassifier();
        faceDetector.load("P:\\Program\\SPL\\MyOpenCv1\\src\\haarcascade_frontalface_alt.xml");

        Mat originalImage = Imgcodecs.imread("P:\\Program\\SPL\\MyOpenCv1\\src\\Image\\");

        Mat thresholdedImage = preprocessImage(originalImage);

        MatOfRect faceDetections = new MatOfRect();
        faceDetector.detectMultiScale(thresholdedImage, faceDetections);

        boolean waterDetected = checkForWater(originalImage);

        boolean nonFloodAffectedDetected = false;

        for (Rect rect : faceDetections.toArray()) {
            boolean isFloodVictim = isFloodVictim(rect, distFlood, waterDetected);

            if (isFloodVictim) {
                Imgproc.rectangle(originalImage, new org.opencv.core.Point(rect.x, rect.y),
                        new org.opencv.core.Point(rect.x + rect.width, rect.y + rect.height),
                        new Scalar(0, 255, 0), 3);

                int dotRadius = 3;
                int dotThickness = -1;

                int dotX = rect.x + rect.width / 2;
                int dotY = rect.y + rect.height / 2;

                Imgproc.circle(originalImage, new org.opencv.core.Point(dotX, dotY), dotRadius, new Scalar(255, 0, 0), dotThickness);

                System.out.println("Flood Victim Detected!");
            } else {
                nonFloodAffectedDetected = true;
            }
        }

        BufferedImage guiImage = matToBufferedImage(originalImage);

        JFrame frame = new JFrame("Flood Victim Face Detection");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(new JLabel(new ImageIcon(guiImage)), BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);

        if (nonFloodAffectedDetected) {
            System.out.println("Non-Flood Affected People Detected!");
        } else {
            if (!waterDetected) {
                System.out.println("Victims not identified");
            }
        }

        System.out.println("Detection Complete");
    }

    public static BufferedImage matToBufferedImage(Mat mat) {
        int type = BufferedImage.TYPE_BYTE_GRAY;
        if (mat.channels() > 1) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }
        BufferedImage bufferedImage = new BufferedImage(mat.cols(), mat.rows(), type);
        mat.get(0, 0, ((DataBufferByte) bufferedImage.getRaster().getDataBuffer()).getData());
        return bufferedImage;
    }


    public static Mat preprocessImage(Mat inputImage) {
        BufferedImage bufferedImage = matToBufferedImage(inputImage);


        GrayScale_1 grayscale = new GrayScale_1();


        BufferedImage grayImage = grayscale.convertToGrayscale(bufferedImage);


        SobelEdgeDetection sobelEdgeDetection = new SobelEdgeDetection();
        BufferedImage sobelImage = sobelEdgeDetection.applySobel(grayImage);

        return bufferedImageToMat(sobelImage);
    }

    public static Mat bufferedImageToMat(BufferedImage bufferedImage) {
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();
        Mat mat = new Mat(height, width, CvType.CV_8UC3);


        BufferedImage convertedImage = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        convertedImage.getGraphics().drawImage(bufferedImage, 0, 0, null);

        byte[] data = ((DataBufferByte) convertedImage.getRaster().getDataBuffer()).getData();
        mat.put(0, 0, data);

        return mat;
    }




    private static boolean isFloodVictim(Rect rect, int distFlood, boolean waterDetected) {

        return waterDetected;
    }

    private static boolean checkForWater(Mat image) {
        Mat grayImage = new Mat();
        Imgproc.cvtColor(image, grayImage, Imgproc.COLOR_BGR2GRAY);


        Scalar meanIntensity = Core.mean(grayImage);


        double threshold = 120;

        return meanIntensity.val[0] < threshold;
    }
}
