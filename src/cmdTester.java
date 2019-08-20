//import java.io.File;
//
//public class cmdTester {
//    private static String filePath = null;
//    private static String maskPath = null;
//    private static int z = 0;
//    private static float epsilon = 0;
//    private static int connectivityType = 0;
//
//
//    private static boolean validateInput(){
//        if (filePath == null){
//            System.err.println("The image path is missing");
//            return false;
//        } else {
//            File file = new File(filePath);
//            if (!file.exists()) {
//                System.err.println("The image path:" + filePath + " not exist");
//                return false;
//            }
//        }
//        if (maskPath == null){
//            System.err.println("The mask path is missing");
//            return false;
//        } else {
//            File file = new File(maskPath);
//            if (!file.exists()) {
//                System.err.println("The image path:" + maskPath + " not exist");
//                return false;
//            }
//        }
//        if (z <= 0){
//            System.err.println("z value is missing or invalid");
//            return false;
//        }
//        if (epsilon <= 0){
//            System.err.println("epsilon value is missing or invalid");
//            return false;
//        }
//        if (connectivityType != 4 && connectivityType !=8 ){
//            System.err.println("connectivity value is missing or invalid, must by 4 or 8");
//            return false;
//        }
//
//        return true;
//    }
//
//    private static void parseArgs(String[] args){
//        for (int i = 0; i < args.length; i++) {
//
//            if (i == args.length-1){
//                System.err.println("Error: one or more arguments are missing");
//                return;
//            }
//            if("-im".equals(args[i])){
//                filePath = new String(args[++i]);
//            }
//
//            else if ("-mask".equals(args[i])){
//                maskPath = new String(args[++i]);
//            }
//
//            else if ("-z".equals(args[i])){
//                try {
//                    z = Integer.parseInt(args[++i]);
//                } catch (NumberFormatException e){
//                    System.err.println("Error: z value must by integer");
//                    return;
//                }
//            }
//
//            else if ("-c".equals(args[i])){
//                try {
//                    connectivityType = Integer.parseInt(args[++i]);
//                } catch (NumberFormatException e){
//                    System.err.println("Error: connectivity value must by 4 or 8");
//                    return;
//                }
//            }
//
//            else if ("-e".equals(args[i])){
//                try {
//                    epsilon = Float.parseFloat(args[++i]);
//                } catch (NumberFormatException e){
//                    System.err.println("Error: epsilon value must by float number");
//                    return;
//                }
//            }
//
//        }
//    }
//
//    public static void main(String[] args) {
//        parseArgs(args);
//        if (! validateInput()){
//            return;
//        }
//
//
//
//        System.out.println(filePath);
//        System.out.println(z);
//        System.out.println(epsilon);
//        System.out.println(connectivityType);
//        System.out.println(maskPath);
//
//    }
//
//
//
//}
