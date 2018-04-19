package ru.maklas.mengine;

/**
 * Created by maklas on 07.10.2017.
 */

class FloatAverager {

    private final float[] values;
    private float avg = 0;
    private int counter = 0;


    public FloatAverager(int avgSize) {
        if (avgSize <= 0 ){
            throw new ArrayIndexOutOfBoundsException();
        }
        this.values = new float[avgSize];
    }

    public synchronized void addFloat(float val){
        values[counter++] = val;

        if (counter == values.length){
            counter = 0;
        }
    }

    private void calculateAvg(){
        float sum = 0;
        for (float val : values) {
            sum += val;
        }

        avg = sum / values.length;
    }


    public synchronized float getAvg(){
        calculateAvg();
        return avg;
    }

    public float getMin(){
        float min = values[0];
        for (float val : values) {
            if (val < min){
                min = val;
            }
        }
        return min;
    }

    public float getMax(){
        float max = values[0];
        for (float val : values) {
            if (val > max){
                max = val;
            }
        }
        return max;
    }

    public float getLast(){
        int frame = counter - 1;
        if (frame < 0){
            frame = values.length - 1;
        }
        return values[frame];
    }

    public boolean madeCircle(){
        return counter == values.length - 1;
    }

    public void fill(float f){
        for (int i = 0; i < values.length; i++) {
            values[i] = f;
        }
    }
}
