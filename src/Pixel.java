public class Pixel {
    int x;
    int y;
    private double value;

    Pixel(int x, int y){
        this.x = x;
        this.y = y;
        this.value = 0;
    }

    Pixel(int x, int y, double val){
        this.x = x;
        this.y = y;
        this.value = val;
    }

    public double getValue(){
        return this.value;
    }

    public boolean equals(Object o){
        if (o == this) {
            return true;
        }
        if (!(o instanceof Pixel)) {
            return false;
        }
        Pixel p = (Pixel) o;
        return this.x == p.x && this.y == p.y;
    }
}
