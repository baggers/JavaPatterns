
/**
 * This program can be used to keeps records of Weather observations and to analyse the datain different ways..
 * 
 * @author Caroline Caly 
 * @version 20/04/2011
 */
public class WeatherRecord
{
    // instance variables
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
    
/**
* Returns a copy of observations
* @return double[] 
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
public double minimum(){
    double minimum;
    minimum = observations[0];
    for (int i = 1; i<observations.length; i++) {
        if (observations[i] < minimum) {
            minimum = observations[i];
        }
    } 
    return minimum;
}

/**
* Find the maximum observation
* @return double the largest value in the observations field
*/
public double maximum(){
    double maximum;
    maximum = observations[0];
    for (int i = 1; i<observations.length; i++) {
        if (observations[i] > maximum) {
            maximum = observations[i];
        }
    } 
    return maximum;
}
 



/**
* Calculate the average of the elements in the observations field:
* the sum of all elements divided by the number of elements
* @return double the average of all values in the observations array
*/
public double average(){
    double total = 0;
        for (int i = 0; i <observations.length; i++) {
            total = total + observations[i];
        }   
    return total/observations.length;
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
    double v = 0;
    double ave = this.average();
        for ( int i = 0; i < observations.length; i++) {
            v = v + Math.pow((observations[i] - ave), 2.0);
        }
    return Math.sqrt(v/observations.length);
}

/**
* Create an array which contains the cumulative sums of the observation field.
* For example, if the original array is {7,3,12,5}, then accumulate returns {7,10,22,27}.
* @return double a new array containing the cummulative sums of the field
*/
public double[] accumulate() {
    double [] acc = new double[observations.length];
    acc[0] = observations[0];
        for ( int i = 1; i < observations.length; i++) {
            acc[i] = observations[i] + acc[i-1];
        } 
    return acc;
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
    int i = 0;
    
   while (i < observations.length){
      if(observations[i] >= large) {
           return i;
      }
      i++;
    }
    return -1;
}

    /**
* Helper method that finds and returns the position of the minimum observation and then deletes it from the array
* @return position of the smallest value in the observations array
*/
//public double minimumpos(){
  //  double minimumpos;
    //minimumpos = observations[0];
    //for (int i = 1; i<observations.length; i++) {
      //  if (observations[i] < minimumpos) {
     //       minimumpos = i;
        //}
   // } 
    //return minimumpos;
    //delete minimumpos[
//}



/** 
* Sort the elements of the array into increasing order in a simple, but fairly inefficient way:
* calculate the position of the smallest element in the array and swap the element there 
* into position 0. Then calculate the position of the smallest element in the array starting 
* from position 1, and swap the element there into position 2, and so on for each position.
* Do not change the argument array, but create a new one that is sorted
* @return a new array with the elements of observations sorted in ascending order.
*/
public double[] selectionSort(){
    
//Make a new array which has the same values as observations[]
double[] sortedObs = new double [observations.length];
for (int k=0; k< observations.length; ++k){
sortedObs[k] = observations [k];
}
    
    //Find minimum value in array
    for ( int i = 0; i < sortedObs.length-1; i++){
        for (int j = i + 1; j< sortedObs.length; j++) {
            //Check which element is larger and swap values so smaller is first
            if (sortedObs[i] > sortedObs[j]) {
               double swappy = sortedObs[i];
                sortedObs[i] = sortedObs [j];
                sortedObs[j] = swappy;
            }
    }
}
return sortedObs;
}
 
/**
* Find the median (middle) value of a set of observations
* @return double the median value of the observation set.
*/
public double median(){

double[] sortedObs2 = new double [observations.length];
//First need to sort observations into ascending order
sortedObs2= selectionSort();
//Then find position of centre
int centre = (sortedObs2.length)/2;
//Check if array has odd number of elements
    if (sortedObs2.length%2 == 1) {
        return sortedObs2[centre];
    }
    //this deals with case where the array has an even number of elements
    else {
        return (sortedObs2[centre - 1] + sortedObs2[centre])/2.0;
    }
}

/**
* return the pct-th value in sequence  
* For ex, percentileValue(50) is the median, PercentilValue(100) is max
* @param pct percentile to select must be a value between 0 and 100 inclusive
* for observations with fewer than 100 values, round to nearest posn for percentile
* @return value of the pct-th percentile of all observations
*/
public double percentileValue(int pct) {
    //Make a new array so that original array remains unaltered
    double[] sortedObs3 = new double [observations.length];
    //Sort observations into ascending order
    sortedObs3= selectionSort();    
    //Only find percentile if 0<=pct<=100. Otherwise return error.
    if (pct >= 0 && pct <=100){
    
    double factor = pct/(double)100;
    double pos = sortedObs3.length*factor -1;
    if (pos >=1){
        return sortedObs3[(int)Math.round(pos)];
    }
    else {
        return sortedObs3[0]; 
    }
  
    }
    else 
    {
        String error1 = "Error. Percentile must be between 0 and 100";
        System.out.println(error1);    
        return -1;
    }
}
}



