package ru.maklas.mengine;

/**
 * Created by maklas on 07.10.2017.
 */

class LongAverager {

    private final long[] values;
    private long avg = 0;
    private int counter = 0;


    public LongAverager(int avgSize) {
        if (avgSize <= 0 ){
            throw new ArrayIndexOutOfBoundsException();
        }
        this.values = new long[avgSize];
    }


    public void addLong(long val){
        values[counter++] = val;

        if (counter == values.length){
            counter = 0;
        }
    }


    public long getMin(){
        long[] values = this.values;
        long min = values[0];
        for (long val : values) {
            if (val < min){
                min = val;
            }
        }
        return min;
    }

    public int size(){
        return values.length;
    }

    public long getMax(){
        long[] values = this.values;
        long max = values[0];
        for (long val : values) {
            if (val > max){
                max = val;
            }
        }
        return max;
    }

    private void calculateAvg(){
        long[] values = this.values;
        long sum = 0;

        for (long val : values) {
            sum += val;
        }

        avg = sum / values.length;
    }


    public long getAvg(){
        calculateAvg();
        return avg;
    }

    public boolean madeCircle(){
        return counter == values.length - 1;
    }

    public void fill(long i){
        for (int j = 0; j < values.length; j++) {
            values[j] = i;
        }
    }

}
