
/**
 * Write a description of class WeatherRecord here.
 * 
 * @author (Ghazi Abujabeen) 
 * @version (20/04/2011)
 */
public class WeatherRecord
{
    private String stationname;
    private String typeofdata; 
    private double[] observations;
    private double tempminimum;
    private double tempmaximum;
    private double sum;
    private double variance;
    private double accumulateSum;
    private double[] accumulateArray;
    
    /**
   * Create a weather data record with a name, data type and array of observed values
   * @param stationname String for human-readable identification of weather station location
   * @param typeofdata String to characterise the data e.g. temperature, rainfall, humidity
   * @param observations an array of doubles representing weather observations
   * @param tempminimum stores the the temporary minimum value in an array for the minimum() method
   * @param tempmaximum stores the the temporary maximum value in an array for the maximum() method
   * @param sum stores the total sum of the a
    */
   public WeatherRecord(String stationname, String typeofdata, double [] observations) {
        this.stationname = stationname;
        this.typeofdata = typeofdata;
        //make a new copy of the input observations
        this.observations = new double[observations.length]; 
        for (int i=0; i < observations.length; i++) {
            this.observations[i] = observations[i];
        }
        this.accumulateArray = new double[observations.length];
        this.tempminimum = tempminimum;
        this.sum = 0;
        this.variance = variance;
        this.accumulateSum =accumulateSum;
    }
    
   public String getStationname() { return stationname; }
   
   public String getTypeofdata() { return typeofdata; }
    
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
      for (int i=1; i< observations.length; i++) {
           if (this.observations[i] < observations[i-1]) {
               tempminimum = observations[i]; }
         }
         return tempminimum;           
        }
        
   /**
   * Find the maximum observation
   * @return double the largest value in the observations field
   */
   public double maximum() {
       for (int i=1; i< observations.length; i++) {
           if (this.observations[i] > observations[i-1]) {
                 tempmaximum = observations[i]; 
        }
       } 
       return tempmaximum;       
   }
       
   /**
   * Calculate the average of the elements in the observations field:
   * the sum of all elements divided by the number of elements
   * @return double the average of all values in the observations array
   */
   public double average() {
     for (int i=0; i< observations.length; i++) {
          sum = observations[i] + sum;
         }
         return (sum/observations.length);     
        }
  
   /**
   * Find the standard deviation of weather observations
   * If {x(0),...,x(n-1)} is a set of values, then the standard deviation is given by
   * the square root of the variance, which is
   * ( (x1-x)2 + (x2-x)2 + ... + (xn-x)2 ) / n
   * where x is the average of the observations and (a)2 means a squared.
   @ see worked example at http://en.wikipedia.org/wiki/Standard_deviation
   * @return double standard deviation of the observations field
   */  
   public double stddeviation() {
       for (int i=0; i< observations.length; i++) {
           variance = Math.pow((observations[i] - this.average()),2) + variance;
        }
       return Math.sqrt(variance/observations.length);
    }     

   /**
   * Create an array which contains the cumulative sums of the observation field.
   * For example, if the original array is {7,3,12,5}, then accumulate returns {7,10,22,27}.
   * @return double a new array containing the cummulative sums of the field
   */
   public double[] accumulate() {
       for (int i=0; i< observations.length; i++){ 
             for (int a=0; a< i-1; a++){ 
               accumulateSum = observations[a] + accumulateSum;
            }            
            accumulateArray[i] = observations[i] + accumulateSum;
        }
       return accumulateArray;
        }

   /**
   * Find the position in observation array of the first value 
   * greater than or equal to the argument, if such an element exists
   * For example, the first day of good rain would be one with rainfall >= 5mm
   * @param large a double for the search condition value
   * @return position of element satisfying search criteria
   * or -1 if no value meets this condition
   */
   public double findPosGreaterthan(double large) {
      for (int i=0; i< observations.length; i++) {
           if (large <= observations[i]) {
               return i; 
        }
     }
     return -1;
    }
}      
   
