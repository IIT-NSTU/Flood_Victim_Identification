import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.Scanner;

public class FloodDetection {

    public static void main(String[] args) {
        int u, v, distFlood;

        System.out.println("Enter height from user to drone (u): ");
        Scanner sc = new Scanner(System.in);
        u = sc.nextInt();

        System.out.println("Enter distance user to flood-affected area (v): ");
        v = sc.nextInt();

        distFlood = (int) Math.sqrt(u * u + v * v);

        System.out.println("Distance between user and flood-affected area (distFlood): " + distFlood);

        sc.close();

        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        CascadeClassifier faceDetector = new CascadeClassifier();
        faceDetector.load("P:\\Program\\SPL\\MyOpenCv1\\src\\haarcascade_frontalface_alt.xml"); // Provide the correct path

        Mat originalImage = Imgcodecs.imread("P:\\Program\\SPL\\MyOpenCv1\\src\\Image\\gettyimages-103331705-612x612.jpg"); // Provide the correct path

        Mat thresholdedImage = preprocessImage(originalImage);

        MatOfRect faceDetections = new MatOfRect();
        faceDetector.detectMultiScale(thresholdedImage, faceDetections); // Detect faces

        boolean waterDetected = checkForWater(originalImage);

        boolean nonFloodAffectedDetected = false;

        for (Rect rect : faceDetections.toArray()) {
            boolean isFloodVictim = isFloodVictim(rect, distFlood, waterDetected);

            if (isFloodVictim) {
                // Draw a green rectangle around the face
                Imgproc.rectangle(originalImage, new org.opencv.core.Point(rect.x, rect.y),
                        new org.opencv.core.Point(rect.x + rect.width, rect.y + rect.height),
                        new Scalar(0, 255, 0), 3);

                // Calculate the center of the face
                int dotRadius = 3;
                int dotX = rect.x + rect.width / 2;
                int dotY = rect.y + rect.height / 2;

                // Draw a red dot at the center of the face
                Imgproc.circle(originalImage, new org.opencv.core.Point(dotX, dotY), dotRadius, new Scalar(255, 0, 0), -1);

                System.out.println("Flood Victim Detected at X: " + dotX + ", Y: " + dotY);
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
                System.out.println("No water detected; victims not identified.");
            }
        }

        System.out.println("Detection Complete");
    }

    public static BufferedImage matToBufferedImage(Mat mat) {
        int type = BufferedImage.TYPE_BYTE_GRAY;
        if (mat.channels() == 3) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        } else if (mat.channels() == 4) {
            type = BufferedImage.TYPE_4BYTE_ABGR;
        }
        BufferedImage bufferedImage = new BufferedImage(mat.cols(), mat.rows(), type);
        byte[] data = ((DataBufferByte) bufferedImage.getRaster().getDataBuffer()).getData();
        mat.get(0, 0, data);
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
        // Define criteria to classify a person as a flood victim:
        // 1. Water is detected (you can adjust this condition)
        // 2. The face is within a certain distance from the drone

        // Define a threshold distance from the drone (you can adjust this)
        int maxDistance = 150; // Adjust as needed

        // Define a minimum face size to consider (adjust this based on your image)
        int minFaceSize = 10; // Adjust as needed

        // Calculate the size of the detected face
        int faceWidth = rect.width;
        int faceHeight = rect.height;

        // Check if water is detected and the face is within the threshold distance
        // Also, ensure that the face size is larger than the minimum required size
        return waterDetected && distFlood <= maxDistance && faceWidth >= minFaceSize && faceHeight >= minFaceSize;
    }

    private static boolean checkForWater(Mat image) {
        Mat grayImage = new Mat();
        Imgproc.cvtColor(image, grayImage, Imgproc.COLOR_BGR2GRAY);

        Scalar meanIntensity = Core.mean(grayImage);

        double threshold = 150;

        return meanIntensity.val[0] < threshold;
    }
}
