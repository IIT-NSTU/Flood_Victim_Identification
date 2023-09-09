import java.awt.image.BufferedImage;
public class GrayScale_1 {
    public static BufferedImage convertToGrayscale(BufferedImage originalImage) {
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();
        BufferedImage grayImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int p = grayImage.getRGB(x, y);

                int a = (p >> 24) & 0xff;
                int r = (p >> 16) & 0xff;
                int g = (p >> 8) & 0xff;
                int b = p & 0xff;

                int avg = (r + g + b) / 3;

                p = (a << 24) | (avg << 16) | (avg << 8) | avg;

                grayImage.setRGB(x, y, p);
            }
        }


        return grayImage;
    }
}
