
/**
 * WeatherRecord.....Record your weather
 * 
 * @author James Barrett 
 * @version 15/04/2011
 */
public class WeatherRecord
{

    private String perthstation;
    private String temperature; 
    private double[] observations;
    private double minimum;
    private double maximum;
    private double average;
    private double stddeviation;
    private double[] accumulate;
    private int findPosGreaterthan;
    private double totalAvg;
    
    /**
     * Create a weather data record with a name, data type and array of observed values
     * @param perthstation String for human-readable identification of weather station location
     * @param temperature String to characterise the data e.g. temperature, rainfall, humidity
     * @param observations an array of doubles representing weather observations 
     */
    public WeatherRecord(String perthstation, String temperature, double [] observations) {
        this.perthstation = perthstation;
        this.temperature = temperature;
        this.observations = new double[observations.length]; 
        for (int i=0; i < observations.length; i++) {
            this.observations[i] = observations[i];
        }
    }


    public String getStationname() { return perthstation; }
    
    public String getTypeofdata() { return temperature; }
    
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
         minimum = observations[0];
         for (int i=1; i<observations.length; i++) {
             if (observations[i] < minimum) {
                 minimum = observations[i]; }
                }
         return minimum;
        }
    
    /**
     * Find the maximum observation
     * @return double the largest value in the observations field
     */
    public double maximum() {
        maximum = observations[0];
         for (int i = 1; i<observations.length; i++) {
             if (observations[i] > maximum) {
                 maximum = observations[i]; }
                }
        return maximum;
    }

    /**
     * Calculate the average of the elements in the observations field:
     * the sum of all elements divided by the number of elements
     * @return double the average of all values in the observations array
     */
    public double average() {
        double totalAvg = 0;
        for (int i=0; i<observations.length; i++) {
            totalAvg = totalAvg + observations[i]; }
            average = totalAvg / observations.length;
        return average;
    }
 
    /**
     * Find the standard deviation of weather observations
     * If {x1,...,xn} is a set of values, then the standard deviation is given by
     * the square root of the variance, which is
     * ( (x1-x)2 + (x2-x)2 + ... + (xn-x)2 ) / n
     * where x is the average of {x1,...,xn}.
     * @return double standard deviation of the observations field
     */  
    public double stddeviation() {
        double total = 0;
        double totalAvg = 0;
        for (int i=0; i<observations.length; i++) {
            totalAvg = totalAvg + observations[i]; }
            average = totalAvg / observations.length;
            
        for (int i = 0; i < observations.length; i++) {
            double variance = observations[i] - average;
            total = total + (variance * variance);
        }
        stddeviation = Math.sqrt(total / observations.length);
        return stddeviation;
    }

    /**
     * Create an array which contains the cumulative sums of the observation field.
     * For example, if the original array is {7,3,12,5}, then accumulate returns {7,10,22,27}.
     * @return double a new array containing the cummulative sums of the field
     */
    public double[] accumulate() {
       double[] accumulate = new double [observations.length];
       accumulate[0] = observations[0];
       for (int i=1; i < observations.length; i++){
           accumulate[i] = accumulate[i-1] + observations[i];
       }
        return accumulate;
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
   
       double PosGreaterthan = 0;
       int arraypos = 0;
       int meetcondition = 0;
      for (int i = 0; i < observations.length; i++) {
       if (observations[i] < large) {
          meetcondition = 0;
          }
       else {
            meetcondition = 1;
           }
        }
       if (meetcondition == 1) {
        for (int i = 0; i < observations.length; i++) {    
           if (observations[i] < large) {
               arraypos = arraypos + 1;
               PosGreaterthan = arraypos;
           }
        }
      }
       else {
           PosGreaterthan = -1;
       }
       return (int)PosGreaterthan;
    }

}
