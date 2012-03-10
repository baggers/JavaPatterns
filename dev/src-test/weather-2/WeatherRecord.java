
/**
 * This is a description of class WeatherRecord. It is here.
 * 
 * @author (Faraz Ahmed) 
 * @version (Math.sqrt(-1))
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
    public double minimum(){
           double min = observations[0];
        for (int i=1; i<observations.length; i++){
            if (observations[i] < min) {
                min = observations[i];}
            } return min;
        }
        
        /**
    * Find the maximum observation
    * @return double the largest value in the observations field
    */
    public double maximum(){
           double max = observations[0];
        for (int i=1; i<observations.length; i++){
            if (observations[i] > max) {
                max = observations[i];}
            } return max;
        }
     
    /**
    * Calculate the average of the elements in the observations field:
    * the sum of all elements divided by the number of elements
    * @return double the average of all values in the observations array
    */
    public double average(){
        double total = 0;
        for (int i=0; i<observations.length; i++) {
            total = total + observations[i];
        }
        return total / observations.length;
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
    public double stddeviation(){
        double total = 0;
        double ave = 0;
        double devi = 0;
        double devf = 0;
        for (int i=0; i<observations.length; i++) {
            total = total + observations[i];
            ave = total / observations.length;}
            
        for (int i=0; i<observations.length; i++){
            devi = devi + (observations[i] - ave)*(observations[i] - ave);
            devf = java.lang.Math.sqrt(devi / observations.length);
        }
        return devf;
    } 
    
    /**
    * Create an array which contains the cumulative sums of the observation field.
    * For example, if the original array is {7,3,12,5}, then accumulate returns {7,10,22,27}.
    * @return double a new array containing the cummulative sums of the field
    */
    public double[] accumulate(){
        double[] a;
        a = new double[observations.length];
        a[0] = observations[0];
        for (int i=1; i<observations.length; i++) {
            a[i] = observations[i] + a[i-1];
        }
            return a;
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
        for (int i=0; i<observations.length; ++i) {
		if (observations[i] == large) {
			return i;
		}
	}
	return -1;
    }
}
