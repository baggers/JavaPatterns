
/**
 * Weather record database and analysis assistant.
 * 
 * @author (Michelle Delfos 20149803) 
 * @version (Holy Thursday)
 */
public class WeatherRecord
{
    private String stationname;
    private String typeofdata;
    private double[] observations;
        
    /**
     * sum method helper.
     * @param set[] array
     * @return sum of all entries in set
     */
    private double sum (double[] set) {
        double sum;
        sum = 0;
        for (int i = 0; i < set.length; i++) {
            sum += set[i];
        }
        return sum;
    }
    
    /**.
    * Create a weather data record with a name, data type and array of 
    * observed values
    * @param stationname String for human-readable identification of weather 
    * station location
    * @param typeofdata String to characterise the data e.g. temperature, 
    * rainfall, humidity
    * @param observations an array of doubles representing weather observations 
    */
    public WeatherRecord(String stationname, String typeofdata, 
        double[] observations) {
        this.stationname = stationname;
        this.typeofdata = typeofdata;
        //make a new copy of the input observations
        this.observations = new double[observations.length]; 
        for (int i = 0; i < observations.length; i++) {
            this.observations[i] = observations[i];
        }
        
    }
    
    /**
     * get station name.
     * @return station name.
     */
    public String getStationname() {
        return stationname; 
    }
    
    /**
     * get type of data.
     * @return station name.
     */
    public String getTypeofdata() {
        return typeofdata; 
    }
    
    /**
     * get observations, by creating a copy.
     * @return copy of original array.
     */
    public double[] getObservations() { 
        double[] obscopy = new double[observations.length];
        for (int i = 0; i < observations.length; i++) {
            obscopy[i] = observations[i];
        }
        return obscopy;
    }
    
    /**
    * Find the minimum observation.
    * @return double the smallest value in the observations array
    */
    public double minimum() {
        double minimum;
        minimum = observations[0];
        
        for (int i = 0; i < observations.length; i++) {
            if (minimum > observations[i]) {
                minimum = observations[i];
            } 
            else {
                minimum = minimum;
            }
        }
        return minimum;
    }
    /**
    * Find the maximum observation.
    * @return double the largest value in the observations field
    */
    public double maximum() {
        double maximum;
        maximum = observations[0];
        for (int i = 0; i < observations.length; i++) {
          
            if (maximum < observations[i]) {
                maximum = observations[i];
            } 
            else {
                maximum = maximum;
            }
        }
        return maximum;
    }
    
 
     /**
     * Calculate the average of the elements in the observations field:
     * the sum of all elements divided by the number of elements.
     * @return double the average of all values in the observations array
     */
    public double average() {
        double average;
        average = sum(observations) / observations.length;
        return average;
    }
 
    /**
    * Find the standard deviation of weather observations
    * If {x(0),...,x(n-1)} is a set of values, 
    * then the standard deviation is given by
    * the square root of the variance, which is
    * ( (x1-x)2 + (x2-x)2 + ... + (xn-x)2 ) / n
    * where x is the average of the observations and (a)2 means a squared.
    @ see worked example at http://en.wikipedia.org/wiki/Standard_deviation
    * @return double standard deviation of the observations field
    */  
    public double stddeviation() {
        double var;
        double stdev;
        var = 0;
        for (int i = 0; i < observations.length; i++) {
            var += Math.pow(observations[i] - average(), 2);
        }    
        stdev = Math.sqrt(var / observations.length);
        return stdev;
    }

    /**
    * Create an array which contains the 
    * cumulative sums of the observation field.
    * For example, if the original array is {7,3,12,5}, 
    * then accumulate returns {7,10,22,27}.
    * @return double a new array containing the cummulative sums of the field
    */
    public double[] accumulate() {
        double[] acc = new double[observations.length];
        acc[0] = observations[0];
        for (int i = 1; i < observations.length; i++) {
            acc[i] = acc[i - 1] + observations[i];
        }
        return acc;
    }

    /**
    * Find the position in observation array of the first value 
    * greater than or equal to the argument, if such an element exists
    * For example, the first day of good rain would be one with rainfall >= 5mm.
    * @param large a double for the search condition value
    * @return position of element satisfying search criteria
    * or -1 if no value meets this condition 
    */
    public int findPosGreaterthan(double large) {
        int position;
        position = -1;
         
        for (int i = 0; i < observations.length; i++) {
            if (observations[observations.length - 1 - i] >= large) {
                position = observations.length - 1 - i;  
            }
        }
        return position;
    }
    
    /**
    * Count the number of observations in the range min to max.
    * @param min lower bound for selection range (inclusive)
    * @param max upper bound for selection range (exclusive)
    * @return int the number of values that lie in this range
    */
    public int countNumInRange(double min, double max) {
        int numInRange = 0;
        
        for (int i = 0; i < observations.length; i++) {
            if (min <= observations[i] && observations[i] < max) {
                numInRange = numInRange + 1;
            }
        }
        return numInRange;
    }

    /**
    * Create a historgram array with numdivision categories
    * selecting reasonable integer range and step size for 
    * numdivisions to cover the whole range of observations.
    * @param numdivisions to divide the observation range into
    * @return array histogram in which position i contains the 
    * number of observations in division i
    */
    public int[] histogram(int numdivisions) {
        double numRange = maximum() - minimum();
        int[] histogram = new int[numdivisions];
         
        for (int i = 0; i < numdivisions; i++) {
            histogram[i] = countNumInRange(minimum() + 
                (i * numRange / numdivisions), minimum() + 
                ((i + 1) * numRange / numdivisions));
            histogram[numdivisions - 1] = countNumInRange(maximum() - 
                (numRange / numdivisions), maximum() + 1);
        }
        return histogram;  
    }
   
    /** 
    * Sort the elements of the array into increasing order in a simple, 
    * but fairly inefficient way:
    * calculate the position of the smallest element in the array and 
    * swap the element there into position 0. 
    * Then calculate the position of the smallest element in the array starting 
    * from position 1, and swap the element there into position 2, 
    * and so on for each position.
    * Do not change the argument array, but create a new one that is sorted
    * @return a new array with the elements of observations sorted in 
    * ascending order.
    */
    public double[] selectionSort() {
        double[] sorted = new double[observations.length];
        double a = 0;
        sorted = getObservations();
          
        for (int i = 0; i < observations.length; i++) {
            for (int j = i; j < observations.length; j++) {
                if (sorted[i] > sorted[j]) {
                    a = sorted[i];
                    sorted[i] = sorted[j];
                    sorted[j] = a;
                }
            }
        }
       
        return sorted;
       
    }
    
    /**
    * Find the median (middle) value of a set of observations 
    * for odd array lengths
    * Finds the average of the two points either side of the (middle) 
    * of the array for even array lengths.
    * @return double the median value of the observation set.
    */
    public double median() {
        double median = 0.0;
        if ( observations.length % 2 == 1) {
            median = (double)(selectionSort()[observations.length / 2]);
        } 
        else if ( observations.length % 2 == 0) { 
            median = (double)((selectionSort()[observations.length / 2] + 
                selectionSort()[(observations.length / 2) - 1]) / 2);
        }
        return median;         
    }

    /**
    * return the pct-th value in sequence  
    * For ex, percentileValue(50) is the median, PercentilValue(100) is max.
    * @param pct percentile to select must be a 
    * value between 0 and 100 inclusive
    * for observations with fewer than 100 values, 
    * round to nearest posn for percentile
    * @return value of the pct-th percentile of all observations
    */
    public double percentileValue(int pct) {
        double percentile = 0.0;
        if ( pct == 100 ) {
            percentile = maximum();
        } 
        else {
            for (int i = 0; i < 100; i++) {
                if ( observations.length % 100 == 0) {
                    percentile = selectionSort()[observations.length 
                        * pct / 100];
                }
                else if ( observations.length % 100 != 0) { 
                    percentile = (selectionSort()[observations.length 
                        * pct / 100] + selectionSort()[(observations.length 
                            * pct / 100) - 1]) / 2;
                } 
          
            }
        }
    
        return percentile ;
    }
    
    /** Create a new WeatherRecord by appending 
     * the observations of two existing ones.
     * Both the argument WeatherRecords must have 
     * the same typeofdata and station name 
     * otherwise return null
     * @param w1 WeatherRecord to be joined
     * @param w2 WeatherRecord to be joined
     * @return WeatherRecord for a new joined 
     * object or null if w1,w2 not compatible
     */
    public WeatherRecord join (WeatherRecord w1, WeatherRecord w2) {
       
        if ( w1.stationname == w2.stationname && 
            w1.typeofdata == w2.typeofdata) {
            double[] observationsw12 = new double[w1.observations.length + 
                w2.observations.length];
            for ( int i = 0; i < w1.observations.length + 
                w2.observations.length; i++) {
                if (i >= 0 && i < w1.observations.length) {
                    observationsw12[i] = w1.observations[i];
                } 
                else if ( i >= w1.observations.length && 
                    i < w1.observations.length + w2.observations.length) {
                    observationsw12[i] = w2.observations[i - 
                        w1.observations.length];
                }
            }
            WeatherRecord w12 = new WeatherRecord(w1.stationname, 
                w1.typeofdata, observationsw12);
            return w12;
        } 
        else {
            return null;
        }
    }
 

    /**
    * Zoom out of a large set of observations by replacing subsequences of 
    * values with their average value.
    * For example, {1,2,3, 8,6,4, 8,8,8} groupby(3) is {2,6,8}
    * @param groupsize must be less than the observation length and 
    * also a divisor of obs length otherwise return null
    * @return new record with compressed observations or null
    */
    public WeatherRecord groupby (int groupsize) {
        if ( groupsize > observations.length || 
            observations.length % groupsize != 0) {
            return null;
        }
        
        double[] groupby = new double[observations.length / groupsize];
        double[][] set = new double[observations.length / groupsize][groupsize];
        for (int i = 0; i < observations.length / groupsize; i++) {
            for (int j = 0; j < groupsize; j++) {                    
                set[i][j] = observations[i + j + i];
            }
            groupby[i] = setaverage(set[i]); 
        }
        WeatherRecord group = new WeatherRecord(this.stationname, 
            this.typeofdata, groupby);
        return group;
    }
     
    /**
     * average method helper.
     * @param set[] array
     * @return average of given set[]
     */
    private double setaverage(double[] set) {
        double average;
        average = sum(set) / set.length;
        return average;
    }
    
    /**
    * Select the weather record with the highest average observation value.
    * @param allrecords an array of WeatherRecords to select from
    * @return WeatherRecord the object with the highest 
    * average of its observations
    */
    public WeatherRecord selectExtreme(WeatherRecord[] allrecords) {
        double[] exaverages = new double[allrecords.length];
        int maxavepos = 0;
        for (int j = 0; j < allrecords.length; j++) {
            
            exaverages[j] = setaverage(allrecords[j].observations);
            
            int maxpos = 0;
            double maximum = exaverages[0];
            for (int i = 0; i < exaverages.length; i++) {
          
                if (maximum < exaverages[i]) {
                    maxpos = i;
                } 
                else {
                    maxpos = maxpos;
                }
            }
            maxavepos = maxpos;
        }
        return allrecords[maxavepos];
    } 
}

       

