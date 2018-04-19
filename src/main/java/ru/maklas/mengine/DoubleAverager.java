package ru.maklas.mengine;

/**
 * Created by maklas on 07.10.2017.
 */

class DoubleAverager {

    private final double[] values;
    private double avg = 0;
    private int counter = 0;


    public DoubleAverager(int avgSize) {
        if (avgSize <= 0 ){
            throw new ArrayIndexOutOfBoundsException();
        }
        this.values = new double[avgSize];
    }

    public void addDouble(double val){
        values[counter++] = val;

        if (counter == values.length){
            counter = 0;
        }
    }

    private void calculateAvg(){
        double sum = 0;
        for (double val : values) {
            sum += val;
        }

        avg = sum / values.length;
    }


    public double getAvg(){
        calculateAvg();
        return avg;
    }

    public double getMin(){
        double min = values[0];
        for (double val : values) {
            if (val < min){
                min = val;
            }
        }
        return min;
    }

    public double getMax(){
        double max = values[0];
        for (double val : values) {
            if (val > max){
                max = val;
            }
        }
        return max;
    }

    public double getLast(){
        int frame = counter - 1;
        if (frame < 0){
            frame = values.length - 1;
        }
        return values[frame];
    }

    public void fill(double f){
        for (int i = 0; i < values.length; i++) {
            values[i] = f;
        }
    }
}
