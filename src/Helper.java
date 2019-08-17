import javafx.util.Pair;

import java.util.HashSet;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

public class Helper {

    public static void main(String[] args) {
        System.out.println("helper");
        Pixel p1 = new Pixel(2, 3, 7.7);
        Pixel p2 = new Pixel(2, 5, 7.7);
        int[] h = new int[]{3, 4};
        int[] h1 = new int[]{5, 6};
        for (int i = 0; i < h.length; i++) {
            System.out.println(h[i]);
            System.out.println(h1[i]);

        }


    }
}
