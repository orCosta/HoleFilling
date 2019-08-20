/**
 * The file contains implementation of small image processing lib.
 * The lib contains function that can fill a hole in an image.
 */
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import java.util.HashSet;
import static java.lang.Math.*;


public class ImProLibLite {
    // Compulsory line for OpenCv
    static{ System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }

    public static enum ConnectivityType
    {
        C8, // 8-neighbors connectivity
        C4; // 4-neighbors connectivity
    }

    /**
     * Defines a template for Neighbor Getter obj, these objects gets pixel and
     * image and returns the neighbor pixels according to unique connectivity method.
     */
    private interface NeighborsGetter{
        HashSet<Pixel> getNeighbors(Pixel p, Mat im);
    }

    /**
     * Returns 8-connected pixels.
     * all pixels that touches with one of their edges or corners.
     */
    private static class _8ConnectivityNeighbors implements NeighborsGetter
    {
        @Override
        public HashSet<Pixel> getNeighbors(Pixel p, Mat im) {
            HashSet<Pixel> neighbors = new HashSet<Pixel>();
            for (int i = max(p.x-1, 0); i < (p.x +2); i++) {
                for (int j = max(p.y-1, 0); j < (p.y +2); j++) {
                    double [] val = im.get(i, j);
                    neighbors.add(new Pixel(i, j, val[0]));
                    }
                }
            neighbors.remove(p);
            return neighbors;
        }
    }

    /**
     * Returns 4-connected pixels.
     * all pixels that touches with one of their edges.
     */
    private static class _4ConnectivityNeighbors implements NeighborsGetter
    {
        @Override
        public HashSet<Pixel> getNeighbors(Pixel p, Mat im) {
            HashSet<Pixel> neighbors = new HashSet<Pixel>();
            int[] x = new int[]{max(p.x-1, 0), p.x+1, p.x, p.x};
            int[] y = new int[]{p.y, p.y, max(p.y-1, 0), p.y+1};
            for (int i = 0; i < x.length; i++) {
                double [] val = im.get(x[i], y[i]);
                neighbors.add(new Pixel(x[i], y[i], val[0]));
            }
            return neighbors;
        }
    }

    /**
     * Creates an NeighborGetter object and return it.
     * @param t the connectivity type of the getter obj.
     * @return instance of NeighborGetter.
     */
    private static NeighborsGetter createNeighborsGetter(ConnectivityType t){
        NeighborsGetter ng = null;
        switch (t){
            case C8:
                ng = new _8ConnectivityNeighbors();
                break;
            case C4:
                ng = new _4ConnectivityNeighbors();
                break;
        }
        return ng;
    }

    /**
     * Calculates the weight of two Pixels base on the Euclidean Dist between them.
     * use the equation: W(a, b) = (||a-b||^z + epsilon)^-1
     */
    static class WeightingDefaultFunc implements PixelsWeight{
        int z;
        double epsilon;
        WeightingDefaultFunc(int z, float e){
            this.z = z;
            this.epsilon = e;
        }
        public float getWeight(Pixel p1, Pixel p2){
            float euclideanDist = (float) sqrt(pow(p1.x - p2.x, 2) + (pow(p1.y - p2.y, 2)));
            return (float)(1/((pow(euclideanDist, this.z)) + this.epsilon));
        }
    }

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
     * Gets image that contains one hole with (-1) pixels value.
     * Returns all the pixels around the hole s.t each boundary pixel is
     * connected to the hole with the given type definition.
     * @param im image contains one hole.
     * @return A set with all the boundary pixels.
     */
    private static HashSet<Pixel> findHoleBoundary(Mat im, ConnectivityType t){
        NeighborsGetter ng = createNeighborsGetter(t);
        Size s = im.size();
        HashSet<Pixel> bounds = new HashSet<>();
        HashSet<Pixel> pixelBounds = new HashSet<>();
        for (int i = 0; i < s.height; i++) {
            for (int j = 0; j < s.width; j++) {
                double[] val = im.get(i, j);
                if (val[0] == (-1)){
                    pixelBounds = ng.getNeighbors(new Pixel(i, j), im);
                    for (Pixel n : pixelBounds){
                        if (n.getValue() != (-1)){
                            bounds.add(n);
                        }
                    }
                }
            }
        }
        return bounds;
    }

    /**
     * Fills a hole inside the given image, that marks by -1 pixels.
     * Defines the hole's pixels value as an weighted average of the pixels around the hole.
     * Using connectivity definition and weight function for the calculation.
     * @param im the image to fill.
     * @param t connectivity type.
     * @param wf pixels weight object.
     */
    public static void fillHole(Mat im, ConnectivityType t, PixelsWeight wf){
        HashSet<Pixel> bound = findHoleBoundary(im, t);
        Size s = im.size();
        for (int i = 0; i < s.height; i++) {
            for (int j = 0; j < s.width; j++) {
                double[] val = im.get(i, j);
                if (val[0] == (-1)){
                    double numeratorSum = 0;
                    double denominatorSum = 0;
                    for (Pixel p : bound){
                        float weight = abs(wf.getWeight(p, new Pixel(i, j)));
                        denominatorSum += weight;
                        numeratorSum += (weight * im.get(p.x, p.y)[0]);
                    }
                    im.put(i, j, (numeratorSum/denominatorSum));
                }
            }
        }
    }

    /**
     * Fills a hole inside the given image, that marks by -1 pixels.
     * Defines the hole's pixels value as an weighted average of the pixels around the hole.
     * Use the Lib's default pixel weight object for the hole filling calculation.
     * Need z and epsilon values to define it.
     * The Lib's default equation: W(a, b) = (||a-b||^z + epsilon)^-1
     */
    public static void fillHole(Mat im, ConnectivityType t, int z, float eps)
    {
        WeightingDefaultFunc wf = new WeightingDefaultFunc(z, eps);
        fillHole(im, t, wf);
    }

    /**
     * Q2
     * Approximate method that fo over all the nearest neighbors.
     */
    public static void fillHoleQ2(Mat im, ConnectivityType t, int z, float eps){
        WeightingDefaultFunc wf = new WeightingDefaultFunc(z, eps);
        float fixWeight = wf.getWeight(new Pixel(1, 1), new Pixel(2, 2));
        HashSet<Pixel> bound = findHoleBoundary(im, t);
        NeighborsGetter ng = createNeighborsGetter(t);
        Size s = im.size();
        for (int i = 0; i < s.height; i++) {
            for (int j = 0; j < s.width; j++) {
                double[] val = im.get(i, j);
                if (val[0] == (-1)){
                    double numeratorSum = 0;
                    double denominatorSum = 0;
                    for (Pixel p : ng.getNeighbors(new Pixel(i, j), im)){
                        if (p.getValue() != -1){
                            denominatorSum += fixWeight;
                            numeratorSum += (fixWeight * p.getValue());
                        }
                    }
                    im.put(i, j, (numeratorSum/denominatorSum));
                }
            }
        }
    }

    /**
     * Get GrayScale image with 0-1 values, CV_32FC1 type
     * and convert it to 0-255 scale and CV_8UC1 type.
     */
    public static void reconvertNormalizedImage(Mat im){
        Core.multiply(im, new Scalar(255), im);
        im.convertTo(im, CvType.CV_8UC1);
    }
}


