/**
 * This file contains implementation of cmd tool that reads an image with hole and fill it use ImProLibLite.
 */
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.File;

public class CmdUtils {
    // Compulsory line for OpenCv
    static{ System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }

    private static String filePath = null;
    private static String maskPath = null;
    private static int z = 0;
    private static float epsilon = 0;
    private static int connectivityType = 0;
    private static String fileName = null;

    private static boolean validateInput(){
        if (filePath == null){
            System.err.println("The image path is missing");
            return false;
        } else {
            File file = new File(filePath);
            fileName = new String(file.getName());
            if (!file.exists()) {
                System.err.println("The image path:" + filePath + " not exist");
                return false;
            }
        }
        if (maskPath == null){
            System.err.println("The mask path is missing");
            return false;
        } else {
            File file = new File(maskPath);
            if (!file.exists()) {
                System.err.println("The image path:" + maskPath + " not exist");
                return false;
            }
        }
        if (z <= 0){
            System.err.println("z value is missing or invalid");
            return false;
        }
        if (epsilon <= 0){
            System.err.println("epsilon value is missing or invalid");
            return false;
        }
        if (connectivityType != 4 && connectivityType !=8 ){
            System.err.println("connectivity value is missing or invalid, must by 4 or 8");
            return false;
        }
        return true;
    }


    private static void parseArgs(String[] args){
        for (int i = 0; i < args.length; i++) {
            if (i == args.length-1){
                System.err.println("Error: one or more arguments are missing");
                return;
            }
            if("-im".equals(args[i])){
                filePath = new String(args[++i]);
            }
            else if ("-mask".equals(args[i])){
                maskPath = new String(args[++i]);
            }
            else if ("-z".equals(args[i])){
                try {
                    z = Integer.parseInt(args[++i]);
                } catch (NumberFormatException e){
                    System.err.println("Error: z value must by integer");
                    return;
                }
            }
            else if ("-c".equals(args[i])){
                try {
                    connectivityType = Integer.parseInt(args[++i]);
                } catch (NumberFormatException e){
                    System.err.println("Error: connectivity value must by 4 or 8");
                    return;
                }
            }
            else if ("-e".equals(args[i])){
                try {
                    epsilon = Float.parseFloat(args[++i]);
                } catch (NumberFormatException e){
                    System.err.println("Error: epsilon value must by float number");
                    return;
                }
            }
        }
    }


    public static void main(String[] args) {
        parseArgs(args);
        if (!validateInput()){
            return;
        }
        System.out.println(filePath);
        System.out.println(z);
        System.out.println(epsilon);
        System.out.println(connectivityType);
        System.out.println(maskPath);

        Mat src = Imgcodecs.imread(filePath, Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
        Mat mask = Imgcodecs.imread(maskPath, Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
        if (mask.empty() || src.empty()){
            System.err.println("Error: invalid format for image/mask");
        }
        Mat im1 = new Mat();
        System.out.println("carve a hole");
        ImProLibLite.carveHoleUsingMask(src, mask, im1);
        if (connectivityType == 8){
            System.out.println("fill with 8");
            ImProLibLite.fillHole(im1, ImProLibLite.ConnectivityType.C8, z, epsilon);
        } else { // connectivityType == 4 :
            System.out.println("fill with 4");
            ImProLibLite.fillHole(im1, ImProLibLite.ConnectivityType.C4, z, epsilon);
        }
        ImProLibLite.reconvertNormalizedImage(im1);
        Imgcodecs.imwrite("externals/result_" + fileName, im1);
        System.out.println(im1.dump());
    }
}
