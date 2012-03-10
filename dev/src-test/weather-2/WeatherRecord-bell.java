
/**
 * In this lab you will create a WeatherRecord class in Java to store and process environmental data.
 * 
 * @author Daniel Bell 
 * @version 16/04/2011
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
    double minimum;
    minimum = observations[0];
    for (int i=1; i < observations.length; i++) {
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
public double maximum() {
    double maximum;
    maximum = observations[0];
    for (int i=1; i < observations.length; i++) {
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
public double average() {
    double average = 0;
    for (int i=0; i < observations.length; i++) {
            average = average + observations[i];
        }
    return (average / observations.length);
}

        }