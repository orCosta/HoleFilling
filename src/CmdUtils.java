import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

public class CmdUtils {
    // Compulsory line for OpenCv
    static{ System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }

    public static void main(String[] args) {
        Mat src = Imgcodecs.imread("externals/star.png", Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
        Mat mask = Imgcodecs.imread("externals/star_mask.png", Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
        Mat im1 = new Mat();
        ImProLibLite.carveHoleUsingMask(src, mask, im1);
        ImProLibLite.fillHole(im1);
        System.out.println(im1.dump());

    }
}
