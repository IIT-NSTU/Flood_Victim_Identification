import javax.imageio.ImageIO;
import java.io.File;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class SobelEdgeDetection {

    public static BufferedImage applySobel(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        int[][] gx = {{-1, 0, 1}, {-2, 0, 2}, {-1, 0, 1}};
        int[][] gy = {{-1, -2, -1}, {0, 0, 0}, {1, 2, 1}};

        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                int pixelX = (
                        (gx[0][0] * new Color(image.getRGB(x - 1, y - 1)).getRed()) +
                                (gx[0][1] * new Color(image.getRGB(x, y - 1)).getRed()) +
                                (gx[0][2] * new Color(image.getRGB(x + 1, y - 1)).getRed()) +
                                (gx[1][0] * new Color(image.getRGB(x - 1, y)).getRed()) +
                                (gx[1][1] * new Color(image.getRGB(x, y)).getRed()) +
                                (gx[1][2] * new Color(image.getRGB(x + 1, y)).getRed()) +
                                (gx[2][0] * new Color(image.getRGB(x - 1, y + 1)).getRed()) +
                                (gx[2][1] * new Color(image.getRGB(x, y + 1)).getRed()) +
                                (gx[2][2] * new Color(image.getRGB(x + 1, y + 1)).getRed())
                );

                int pixelY = (
                        (gy[0][0] * new Color(image.getRGB(x - 1, y - 1)).getRed()) +
                                (gy[0][1] * new Color(image.getRGB(x, y - 1)).getRed()) +
                                (gy[0][2] * new Color(image.getRGB(x + 1, y - 1)).getRed()) +
                                (gy[1][0] * new Color(image.getRGB(x - 1, y)).getRed()) +
                                (gy[1][1] * new Color(image.getRGB(x, y)).getRed()) +
                                (gy[1][2] * new Color(image.getRGB(x + 1, y)).getRed()) +
                                (gy[2][0] * new Color(image.getRGB(x - 1, y + 1)).getRed()) +
                                (gy[2][1] * new Color(image.getRGB(x, y + 1)).getRed()) +
                                (gy[2][2] * new Color(image.getRGB(x + 1, y + 1)).getRed())
                );

                int magnitude = (int) Math.sqrt((pixelX * pixelX) + (pixelY * pixelY));
                magnitude = Math.min(255, magnitude);
                magnitude = Math.max(0, magnitude);
                result.setRGB(x, y, new Color(magnitude, magnitude, magnitude).getRGB());
            }
        }

        return result;
    }

    public static void main(String[] args) {
        try{
            BufferedImage inputImage = ImageIO.read(new File("P:\\Program\\SPL\\Flood Victim Identificaton\\src\\images\\Initial Image\\image_1.jpeg"));
            BufferedImage outputImage = applySobel(inputImage);
            ImageIO.write(outputImage, "jpg", new File("P:\\Program\\SPL\\Flood Victim Identificaton\\src\\images\\Sobel Edge\\image_1.jpeg"));
            System.out.println("Sobel Edge Detection Successfull");
        }catch(IOException e){
            System.out.println(e);
        }
    }
}
