import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.HashSet;

import static java.lang.Math.*;


public class ImProLibLite {
    public static enum ConnectivityType
    {
        C8, // 8-neighbors connectivity
        C4; // 4-neighbors connectivity
    }


    // Compulsory line for OpenCv
    static{ System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }

    /**
     * Carve an hole in the given image using the mask.
     * Normalize the image values to 0 - 1.
     * Set image values to -1 in the mask positions.
     * Convert to type:CV_32FC1
     * @param im grayScale image (0-255).
     * @param mask grayScale image (0-255), (the black pixels represent the hole).
     * @param dst Save the result to dst.
     */
    public static void carveHoleUsingMask(Mat im, Mat mask, Mat dst){
        if (im.size().width != mask.size().width && im.size().height != mask.size().height){
            return;
        }
        Core.normalize(im, dst,0.0,1.0, Core.NORM_MINMAX, CvType.CV_32FC1);
        Mat binMask = new Mat();
        Imgproc.threshold(mask, binMask, 127, 1, Imgproc.THRESH_BINARY_INV);
        dst.setTo(new Scalar(-1), binMask);
    }

    /**
     * Returns set with the neighbors of the given "hole" pixel, using the given connectivity
     * type convention. The neighbors include only pixels that not belong to the hole.
     * @param im the full image.
     * @param p the hole pixel
     * @param ctype connectivity convention.
     * @return set of neighbors pixels.
     */
    private static HashSet<Pixel> getBoundaryForHolePixel(Mat im, Pixel p, ConnectivityType ctype){
        HashSet<Pixel> neighbors = new HashSet<Pixel>();
        switch (ctype)
        {
            case C4:
                int[] x = new int[]{max(p.x-1, 0), p.x+1, p.x, p.x};
                int[] y = new int[]{p.y, p.y, max(p.y-1, 0), p.y+1};
                for (int i = 0; i < x.length; i++) {
                    double [] val = im.get(x[i], y[i]);
                    if (val[0] != (-1)){
                        neighbors.add(new Pixel(x[i], y[i], val[0]));
                    }
                }
                break;
            case C8:
                for (int i =max(p.x-1, 0); i < (p.x +2); i++) {
                    for (int j = max(p.y-1, 0); j < (p.y +2); j++) {
                        double [] val = im.get(i, j);
                        if (val[0] != (-1)){
                            neighbors.add(new Pixel(i, j, val[0]));
                        }
                    }
                }
                break;
        }
        return neighbors;
    }

    private static HashSet<Pixel> findBoundary(Mat im, ConnectivityType ctype){
        Size s = im.size();
        HashSet<Pixel> bounds = new HashSet<Pixel>();
        for (int i = 0; i < s.height; i++) {
            for (int j = 0; j < s.width; j++) {
                double[] val = im.get(i, j);
                if (val[0] == (-1)){
                    bounds.addAll(getBoundaryForHolePixel(im, new Pixel(i, j), ctype));
                }
            }
        }
        return bounds;
    }

    private static void fillHole(Mat im, HashSet<Pixel> bound, WeightingFunc wf)
    {
        Size s = im.size();
        for (int i = 0; i < s.height; i++) {
            for (int j = 0; j < s.width; j++) {
                double[] val = im.get(i, j);
                if (val[0] == (-1)){
                    double numeratorSum = 0;
                    double denominatorSum = 0;
                    for (Pixel p : bound){
                        double weight = wf.calculateWeight(p, new Pixel(i, j));
                        denominatorSum += weight;
                        numeratorSum += (weight * im.get(p.x, p.y)[0]);
                    }
                    im.put(i, j, (numeratorSum/denominatorSum));
                }
            }
        }
    }

    private  static void reconvertAndSave(Mat im){
        Core.multiply(im, new Scalar(255), im);
        im.convertTo(im, CvType.CV_8UC1);
        Imgcodecs.imwrite("externals/test1.jpg", im);
    }

    public static void main(String[] args) {

        Mat src = Imgcodecs.imread("externals/monkey.jpg", Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
//        Mat mask = Imgcodecs.imread("externals/mask.png", Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
//        carveHoleUsingMask(src, mask);
//        HashSet<Pixel> bounds = findBoundary(src);
//        WeightingFunc wf = new WeightingFunc(2, 1);
//        fillHole(src, bounds, wf);
//        reconvertAndSave(src);

//        System.out.println(src.dump());
        System.out.println("lib test");

    }
}
