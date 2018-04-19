package ru.maklas.mengine;

/**
 * Created by maklas on 07.10.2017.
 */

class LongAverager {

    private final long[] values;
    private long avg = 0;
    private int counter = 0;
    private boolean loopWasMade = false;


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
            loopWasMade = true;
        }
    }


    public long getMin(){
        int countUpTo = loopWasMade ? values.length : counter;
        long[] values = this.values;
        long min = values[0];
        for (int i = 0; i < countUpTo; i++) {
            if (values[i] < min){
                min = values[i];
            }
        }
        return min;
    }

    public int size(){
        return values.length;
    }

    public long getMax(){
        int countUpTo = loopWasMade ? values.length : counter;
        long[] values = this.values;
        long max = values[0];
        for (int i = 0; i < countUpTo; i++) {
            if (values[i] > max){
                max = values[i];
            }
        }
        return max;
    }

    private void calculateAvg(){
        if (loopWasMade){
            long[] values = this.values;
            long sum = 0;

            for (long val : values) {
                sum += val;
            }

            avg = sum / values.length;
        } else {
            if (counter == 0){
                avg = 0;
            } else {
                long[] values = this.values;
                long sum = 0;
                for (int i = 0; i < counter; i++) {
                    sum += values[i];
                }
                avg = sum / counter;
            }
        }
    }


    public long getAvg(){
        calculateAvg();
        return avg;
    }

    public void fill(long i){
        for (int j = 0; j < values.length; j++) {
            values[j] = i;
        }
    }

}
