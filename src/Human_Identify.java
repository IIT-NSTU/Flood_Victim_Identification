import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

public class Human_Identify {

    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        CascadeClassifier faceDetector = new CascadeClassifier();
        faceDetector.load("P:\\Program\\SPL\\MyOpenCv1\\src\\haarcascade_frontalface_alt.xml");

        Mat image = Imgcodecs.imread("p:\\program\\SPL\\MyOpenCv1\\src\\Image\\multiple faces1.webp");

        MatOfRect faceDetections = new MatOfRect();
        faceDetector.detectMultiScale(image, faceDetections);

        for (Rect rect : faceDetections.toArray()) {

            Imgproc.rectangle(image, new Point(rect.x, rect.y),
                    new Point(rect.x + rect.width, rect.y + rect.height),
                    new Scalar(0, 255, 0), 3);

            int dotRadius = 3;
            int dotThickness = -1;
            int dotShift = 2;

            int dotX = rect.x + rect.width / 2;
            int dotY = rect.y + rect.height / 2;

            Imgproc.circle(image, new Point(dotX, dotY), dotRadius, new Scalar(255, 0, 0), dotThickness);
        }

        String outputFilename = "Output.jpg";

        Imgcodecs.imwrite("P:\\Program\\SPL\\MyOpenCv1\\src\\Image\\Test_1.jpg",image);

        System.out.print("Faces Detected");
}
}
