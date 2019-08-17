import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

public class WeightingFunc{
    int z;
    double epsilon;

    WeightingFunc(int z, double e){
        this.z = z;
        this.epsilon = e;
    }

    public double calculateWeight(Pixel p1, Pixel p2){
        double euclideanDist = sqrt(pow(p1.x - p2.x, 2) + (pow(p1.y - p2.y, 2)));
        return 1/((pow(euclideanDist, this.z)) + this.epsilon);
    }

//    public double getWeight(){}
}
