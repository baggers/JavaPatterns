
/**
 * Write a description of class WeatherRecord here.
 * 
 * @author Ben Deeks 
 * @version 21/4/2011
 */
public class WeatherRecord
{
    private String stationname;
    private String typeofdata; 
    private double[] observations;

    /**
    * Create a weather data record with a name, data type and array of observed values
    * @param stationname String for human-readable identification of weather station location
    * @param typeofdata String to characterise the data e.g. temperature, rainfall, humidity
    * @param observations an array of doubles representing weather observations 
    */
    public WeatherRecord(String stationname, String typeofdata, double [] observations) {
        this.stationname = stationname;
        this.typeofdata = typeofdata;
        //make a new copy of the input observations
        this.observations = new double[observations.length]; 
        for (int i=0; i < observations.length; i++) {
            this.observations[i] = observations[i];
        }
    }
    
    public String getStationname() { 
        return stationname; 
    }
    
    public String getTypeofdata() { 
        return typeofdata; 
    }
    
    /**
     * Simple variable retriever, returns a copy for security reasons
     * @return A copy of the observations array
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
    */
    public double minimum() {
        double minob = observations[0];
        int i;
        for (i=1; i<observations.length; i++) {
            if (observations[i] < minob) {
                minob = observations[i];
            }
        }
        return minob;
    }
    

    /**
    * Find the maximum observation
    * @return double the largest value in the observations field
    */
   public double maximum(){
       double maxob = observations[0];
       int i;
       for (i=1; i<observations.length; i++) {
            if (observations[i] > maxob) {
                maxob = observations[i];
            }
        }
        return maxob;
    }
  
   /**
   * Calculate the average of the elements in the observations field:
   * the sum of all elements divided by the number of elements
   * @return double the average of all values in the observations array
   */
   public double average(){
        double total = observations[0];
        int i;
        for (i=1; i<observations.length; i++) {
            total = total + observations[i];
        }
        return (total/observations.length);
    }
 
    /**
    * Find the standard deviation of weather observations
    * If {x(0),...,x(n-1)} is a set of values, then the standard deviation is given by
    * the square root of the variance, which is
    * ( (x1-x)2 + (x2-x)2 + ... + (xn-x)2 ) / n
    * where x is the average of the observations and (a)2 means a squared.
    * see worked example at http://en.wikipedia.org/wiki/Standard_deviation
    * @return double standard deviation of the observations field
    */  
    public double stddeviation() {
        double ave = this.average();
        double variance = 0;
        int i;
        for (i=0; i<observations.length; i++) {
            variance = variance + java.lang.Math.pow((observations[i] - ave), 2);
        }
        variance = variance / observations.length;
        return java.lang.Math.sqrt(variance);
    }

    /**
    * Create an array which contains the cumulative sums of the observation field.
    * For example, if the original array is {7,3,12,5}, then accumulate returns {7,10,22,27}.
    * @return double a new array containing the cummulative sums of the field
    */
    public double[] accumulate(){
        double[] accum = new double[observations.length];
        accum[0] = observations[0];
        int i;
        for (i=1; i<observations.length; i++) {
            accum[i] = accum[i-1] + observations[i];
        }
        return accum;
    }

    /**
    * Find the position in observation array of the first value 
    * greater than or equal to the argument, if such an element exists
    * For example, the first day of good rain would be one with rainfall >= 5mm
    * @param large a double for the search condition value
    * @return position of element satisfying search criteria
    * or -1 if no value meets this condition
    */
    public int findPosGreaterthan(double large){
        int i;
        int index = -1;
        for(i=0; (i<observations.length); i++) {
            if (observations[i] >= large) {
                index = i;
                i = observations.length;
            }
        }
        return index;
    }
    
    /**
    * Count the number of observations in the range min to max
    * @param min lower bound for selection range (inclusive)
    * @param max upper bound for selection range (exclusive)
    * @return int the number of values that lie in this range
    */
   public int countNumInRange(double min, double max){
       int i;
       int count = 0;
       for (i=0; i<observations.length; i++){
           if ((observations[i] >= min) && (observations[i] < max)){
               count++;
            }
        }
       return count;
    }

   /**
   * Create a historgram array with numdivision categories
   * selecting reasonable integer range and step size for 
   * numdivisions to cover the whole range of observations
   * @param numdivisions to divide the observation range into
   * @return array histogram in which position i contains the 
   * number of observations in division i
   */
   public int[] histogram(int numdivisions) {
       int stepsize;
       double range;
       int i;
       int divisions = numdivisions;
       if (divisions <= 0) {
           divisions = 1;
        }
       int[] hist = new int[divisions];
       double min = this.minimum();
       range = (this.maximum() - min);
       stepsize = (int)(java.lang.Math.ceil(range / divisions));
       if (divisions == 1) {
           stepsize++;
        }
       for (i=0; i<divisions; i++){
           hist[i] = countNumInRange(java.lang.Math.floor(min) + (i*stepsize), min + ((i+1) * stepsize));
        }
        return hist;
    }
    
   /** 
   * Sort the elements of the array into increasing order in a simple, but fairly inefficient way:
   * calculate the position of the smallest element in the array and swap the element there 
   * into position 0. Then calculate the position of the smallest element in the array starting 
   * from position 1, and swap the element there into position 2, and so on for each position.
   * Do not change the argument array, but create a new one that is sorted
   * @return a new array with the elements of observations sorted in ascending order.
   */
   public double[] selectionSort(){
       //method
       double[] sorted = new double[observations.length];
       int n = 1;
       double temp = 0;
       int i;
       //copy the observations
       for (i=0; i < observations.length; i++){
           sorted[i] = observations[i];
       }
       while (n>0){
           n=0;
           for (i=0; i < (observations.length-1); i++) {
               if(sorted[i] > sorted [i+1]){
                   temp = sorted[i+1];
                   sorted[i+1] = sorted[i];
                   sorted[i] = temp;
                   n = i; //Sorting will end if only the first 2 numbers are swapped
                }
            }
        }
        return sorted;
    }
    
   /**
   * Find the median (middle) value of a set of observations
   * @return double the median value of the observation set.
   */
   public double median(){
       //method
       double med = 0;
       int position = (int)java.lang.Math.floor((double)observations.length/2.0);
       double[] sortedList = selectionSort();
       if (observations.length%2 == 0){
           med = (sortedList[position-1] + sortedList[position])/2;
        }
        else{
            med = sortedList[position];
        }
       return med;
    }
   
   /**
   * return the pct-th value in sequence  
   * For ex, percentileValue(50) is the median, PercentilValue(100) is max
   * @param pct percentile to select must be a value between 0 and 100 inclusive
   * for observations with fewer than 100 values, round to nearest posn for percentile
   * @return value of the pct-th percentile of all observations
   */
   public double percentileValue(int pct){
       int posn;
       double[] sortedList = selectionSort();
       if (pct == 50){
           return median();
        }
       else{
           posn = (int)java.lang.Math.round(observations.length * (0.01 * pct)-1);
           if (posn == -1){ posn++; }
           return sortedList[posn];
        }
    }
   /** Create a new WeatherRecord by appending the observations of two existing ones.
   * Both the argument WeatherRecords must have the same typeofdata and station name 
   * otherwise return null
   * @param w1 WeatherRecord to be joined
   * @param w2 WeatherRecord to be joined
   * @return WeatherRecord for a new joined object or null if w1,w2 not compatible
   */
   public WeatherRecord join (WeatherRecord w1, WeatherRecord w2){
       WeatherRecord compiled;
       double[] firstObs = w1.getObservations();
       double[] secondObs = w2.getObservations();
       double[] list = new double[firstObs.length + secondObs.length];
       int i;
       if (w1.getStationname() == w2.getStationname() && w1.getTypeofdata() == w2.getTypeofdata()){
           for (i = 0; i < firstObs.length; i++){
               list[i] = firstObs[i];
            }
           for (i = firstObs.length; i < list.length; i++){
               list[i] = secondObs[i - firstObs.length];
            }
           compiled = new WeatherRecord(w1.getStationname(), w1.getTypeofdata(), list);
           return compiled;
        }
       else{
           return null;
        }
    }

   /**
   * Zoom out of a large set of observations by replacing subsequences of 
   * values with their average value
   * For example, {1,2,3, 8,6,4, 8,8,8} groupby(3) is {2,6,8}
   * @param groupsize must be less than the observation length and 
   * also a divisor of obs length otherwise return null
   * @return new record with compressed observations or null
   */
   public WeatherRecord groupby (int groupsize){
       int i;
       int j;
       double total;
       double[] list = new double[observations.length/groupsize];
       if (observations.length%groupsize == 0){
           list = new double[observations.length/groupsize];
           for (i=0; i < list.length; i++){
               total = 0;
               for (j= i * groupsize; j < (i+1) * groupsize; j++){
                   total = total + observations[j];
                }
               list[i] = total/groupsize;
            }
           return new WeatherRecord(stationname, typeofdata, list);
        }
       else {
           return null;
        }
    }
   
   /**
   * Select the weather record with the highest average observation value
   * @param allrecords an array of WeatherRecords to select from
   * @return WeatherRecord the object with the highest average of its observations
   */
   public WeatherRecord selectExtreme(WeatherRecord[] allrecords) {
       int i;
       int extreme = 0;
       double maximum = allrecords[0].maximum();
       for (i = 1; i < allrecords.length; i++){
           if (maximum < allrecords[i].maximum()){
               maximum = allrecords[i].maximum();
               extreme = i;
            }
        }
       return allrecords[extreme];
    }
}
