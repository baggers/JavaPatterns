import java.util.NoSuchElementException;

/**
 * Write a description of class WeatherRecord here.
 * 
 * @author M. Atcheson
 * @version 15/04/11
 */
public class WeatherRecord
{
    private String stationname;
    private String typeofdata; 
    private double[] observations;
    private double[] sortedobs;
    private boolean sorted;
    
    /**
    * Create a weather data record with a name, data type and array of observed values
    * @param stationname String for human-readable identification of weather station location
    * @param typeofdata String to characterise the data e.g. temperature, rainfall, humidity
    * @param observations an array of doubles representing weather observations 
    */
    public WeatherRecord(String stationname, String typeofdata, double [] observations) {
        this.stationname = stationname;
        this.typeofdata = typeofdata;
        this.sorted = false;
        //make a new copy of the input observations
        this.observations = new double[observations.length]; 
        for (int i=0; i < observations.length; i++) {
            this.observations[i] = observations[i];
        }
    }
    
    /**
    * Get the station name associated with this WeatherRecord
    * @return string the station name
    */
    public String getStationname() { return stationname; }
    
    /**
    * Get the type of data recorded in this WeatherRecord
    * @return string the type of data
    */
    public String getTypeofdata() { return typeofdata; }
    
    /**
    * Get the observations recorded in this WeatherRecord
    * @return double[] a copy of this record's observation array
    */
    public double[] getObservations() { 
        double[] obscopy = new double[observations.length];
        for (int i=0; i < observations.length; i++) {
            obscopy[i] = observations[i];
        }
        return obscopy;
    }
    
    /**
    * Find the minimum observation
    * @return double the smallest value in the observations array
    * @throws NoSuchElementException when the observations array is a zero-length array, and therefore has no minimum element
    */
    public double minimum() throws NoSuchElementException {
        if (observations.length > 0) {
            double min = observations[0];
            for (int i = 0; i < observations.length; i++){
                if (observations[i] < min) {min = observations[i];}
            }
            return min;
        } else {
            throw new NoSuchElementException("Attempting to determine the minimum element of a zero-length observations array");
        }
    }

    /**
    * Find the maximum observation
    * @return double the largest value in the observations field
    * @throws NoSuchElementException when the observations array is a zero-length array, and therefore has no maximum element
    */
    public double maximum() throws NoSuchElementException {
        if (observations.length > 0) {
            double max = observations[0];
            for (int i = 0; i < observations.length; i++){
                if (observations[i] > max) {max = observations[i];}
            }
            return max;
        } else {
            throw new NoSuchElementException("Attempting to determine the maximum element of a zero-length observations array");
        }
    }
 
    /**
    * Calculate the average of the elements in the observations field:
    * the sum of all elements divided by the number of elements
    * @return double the average of all values in the observations array
    * @throws NoSuchElementException when the observations array is a zero-length array, and therefore its elements have no average
    */
    public double average() throws NoSuchElementException {
        if (observations.length > 0) {
            double sum = 0;
            for (int i = 0; i < observations.length; i++){
                sum += observations[i];
            }
            return sum/observations.length; 
        } else {
            throw new NoSuchElementException("Attempting to take the average value of a zero-length observations array");
        }
    }
 
    /**
    * Find the standard deviation of weather observations
    * If {x1,...,xn} is a set of values, then the standard deviation is given by
    * the square root of the variance, which is
    * ( (x1-x)^2 + (x2-x)^2 + ... + (xn-x)^2 ) / n
    * where x is the average of {x1,...,xn}.
    * @return double standard deviation of the observations field
    * @throws NoSuchElementException when the observations array is a zero-length array, and therefore its elements have no standard deviation
    */  
    public double stddeviation() throws NoSuchElementException {
        if (observations.length > 0) {
            double avg = this.average();
            double sum = 0;
            for (int i = 0; i < observations.length; i++){
                sum += Math.pow(observations[i] - avg, 2);
            }
            return Math.sqrt(sum/observations.length); 
        } else {
            throw new NoSuchElementException("Attempting to take the standard deviation of a zero-length observations array");
        }
    }

    /**
    * Create an array which contains the cumulative sums of the observation field.
    * For example, if the original array is {7,3,12,5}, then accumulate returns {7,10,22,27}.
    * @return double a new array containing the cummulative sums of the field
    */
    public double[] accumulate() {
        double[] result = new double[observations.length];
        for (int i = 0; i < result.length; i++){
            for (int j = i; j < result.length; j++){
                result[j] += observations[i];
            }
        }
        return result;
    }

    /**
    * Find the position in observation array of the first value 
    * greater than or equal to the argument, if such an element exists
    * For example, the first day of good rain would be one with rainfall >= 5mm
    * @param large a double for the search condition value
    * @return position of element satisfying search criteria
    * or -1 if no value meets this condition
    */
    public int findPosGreaterthan(double large) {
        int index = -1;
        for (int i = 0; i < observations.length; i++){
            if (observations[i] >= large) {
                index = i;
                break;
            }
        }
        return index;
    }
    
    /**
    * Count the number of observations in the range min to max (all values x that satisfy min <= x < max)
    * @param min lower bound for selection range (inclusive)
    * @param max upper bound for selection range (exclusive)
    * @return int the number of values that lie in this range
    */
    public int countNumInRange(double min, double max) {
        int num = 0;
        for (int i = 0; i < observations.length; i++){
            if (observations[i] < max && observations[i] >= min) { num++; }
        }
        return num;
    }

    /**
    * Create a historgram array with numdivision categories
    * selecting reasonable integer range and step size for 
    * numdivisions to cover the whole range of observations
    * @param numdivisions to divide the observation range into, must be a positive integer
    * @return array histogram in which position i contains the 
    * number of observations in division i
    * @throws IllegalArgumentException if numdivisions is not a positive integer
    */
    public int[] histogram(int numdivisions) throws IllegalArgumentException{
        if (numdivisions > 0) {
            int[] hist = new int[numdivisions];
            int range;
            int min;
            if (observations.length > 0) {
                min = (int)(Math.floor(this.minimum()));
                range = (int)(Math.ceil(this.maximum() - this.minimum()));
            } else {
                range = 0;
                min = 0;
            }
            int stepsize = (range / numdivisions) + 1;
            for (int i = 0; i < numdivisions; i++) {
                hist[i] = countNumInRange(min + (stepsize * i), min + (stepsize * (i+1)));
            }
            return hist;
        } else { throw new IllegalArgumentException("pct must be between 0 and 100 inclusive"); }
    }

    /** 
    * Sorts the elements of the array into increasing order in a simple, but fairly inefficient way.
    * @return a new array with the elements of observations sorted in ascending order.
    */
    public double[] selectionSort() {
        if (sorted) {
            double[] returnarray = new double[sortedobs.length];
            System.arraycopy(sortedobs, 0, returnarray, 0, sortedobs.length);
            return returnarray;
        } else if(observations.length == 0) {
            double[] returnarray = {};
            return returnarray;
        } else {
            sortedobs = new double[observations.length];
            System.arraycopy(observations, 0, sortedobs, 0, observations.length);
            int minindex;
            double temp;
            for(int i = 0; i < observations.length; i++) {
                minindex = i;
                for(int j = i + 1; j < observations.length; j++) {
                    if (sortedobs[j] < sortedobs[minindex]) {minindex = j;}
                }
                temp = sortedobs[i];
                sortedobs[i] = sortedobs[minindex];
                sortedobs[minindex] = temp;
            }
            sorted = true;
            double[] returnarray = new double[sortedobs.length];
            System.arraycopy(sortedobs, 0, returnarray, 0, sortedobs.length);
            return returnarray;
        }
    }

    /**
    * Find the median (middle) value of a set of observations
    * @return double the median value of the observation set.
    * @throws NoSuchElementException when the observations array is a zero-length array, and therefore there is no median value
    */
    public double median() throws NoSuchElementException {
        return percentileValue(50);
    }

    /**
    * return the pct-th value in sequence  
    * For ex, percentileValue(50) is the median, percentileValue(100) is max
    * @param pct percentile to select must be a value between 0 and 100 inclusive
    * for observations with fewer than 100 values, round to nearest posn for percentile
    * @return value of the pct-th percentile of all observations
    * @throws NoSuchElementException when the observations array is a zero-length array, and therefore there is no value at any percentile
    * @throws IllegalArgumentException when pct is not between 0 and 100 inclusive
    */
    public double percentileValue(int pct) throws NoSuchElementException, IllegalArgumentException {
        if (0 <= pct && pct <= 100) {
            if (observations.length > 0) {
                int index = (int)Math.round((pct/100.0)*(observations.length));
                if (index > 0) {index--;}
                return selectionSort()[index];
            } else { 
                throw new NoSuchElementException("Attempting to find the " + pct + "-th percentile of a zero-length observations array");}
        } else {
            throw new IllegalArgumentException("pct must be between 0 and 100 inclusive");
        }
    }
    
    /** 
    * Create a new WeatherRecord by appending the observations of two existing ones.
    * Both the argument WeatherRecords must have the same typeofdata and station name 
    * otherwise return null
    * @param w1 WeatherRecord to be joined
    * @param w2 WeatherRecord to be joined
    * @return WeatherRecord for a new joined object or null if w1,w2 not compatible
    */
    public WeatherRecord join (WeatherRecord w1, WeatherRecord w2) {
        if (w1.stationname.equals(w2.stationname) && w1.typeofdata.equals(w2.typeofdata)) {
            double[] newobs = new double[w1.observations.length + w2.observations.length];
            System.arraycopy(w1.observations, 0, newobs, 0, w1.observations.length);
            System.arraycopy(w2.observations, 0, newobs, w1.observations.length, w2.observations.length);
            return new WeatherRecord(w1.stationname, w1.typeofdata, newobs);
        } else { return null; }
    }

    /**
    * Zoom out of a large set of observations by replacing subsequences of 
    * values with their average value
    * For example, {1,2,3, 8,6,4, 8,8,8} groupby(3) is {2,6,8}
    * @param groupsize must be a positive integer, and if it is not less than the observation length and 
    * also a divisor of obs length  null will be returned
    * @return new record with compressed observations or null
    * @throws IllegalArgumentException if groupsize is not a positive integer
    */
    public WeatherRecord groupby (int groupsize) throws IllegalArgumentException {
        if (groupsize > 0) {
            if (groupsize < observations.length && (observations.length % groupsize == 0)) {
                double[] grouped = new double[observations.length / groupsize];
                for(int i = 0; i < (observations.length/groupsize); i ++) {
                    for(int j = 0; j < groupsize; j++) {
                        grouped[i] += observations[ (i * groupsize) + j];
                    }
                    grouped[i] =  grouped[i] / groupsize;
                }
                return new WeatherRecord(stationname, typeofdata, grouped); 
            } else { return null; }
        } else { throw new IllegalArgumentException("groupsize must be a positive integer"); }
    }
    
    /**
    * Select the weather record with the highest average observation value
    * @param allrecords an array of WeatherRecords to select from
    * @return WeatherRecord the object with the highest average of its observations
    * @throws NoSuchElementException when the allrecord array is a zero-length array, and therefore has no extreme element, or if all elements of
    * allrecords have zero observations, and therefore cannot be averaged
    */ 
    public WeatherRecord selectExtreme(WeatherRecord[] allrecords) throws NoSuchElementException {
        if (allrecords.length > 0) {
            boolean valid = allrecords[0].getObservations().length > 0;
            int indexmax = 0;
            for(int i = 1; i < allrecords.length; i++) {
                if (allrecords[i].getObservations().length > 0) {
                    if (allrecords[i].average() > allrecords[indexmax].average()) { 
                        indexmax = i;
                        valid = true;
                    }
                }
            }
            if (valid) {
                return allrecords[indexmax];
            } else {
                throw new NoSuchElementException("Attempting to find the element with largest average of an array of WeatherRecords," +
                                                            "but no elements can be averaged, as they all have zero-length observation arrays");
            }
        } else  {
            throw new NoSuchElementException("Attempting to find the element with largest average of a zero-length array of WeatherRecords");
        }
    } 
    

}

