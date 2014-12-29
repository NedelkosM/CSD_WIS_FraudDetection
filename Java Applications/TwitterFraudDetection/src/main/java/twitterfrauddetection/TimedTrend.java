package twitterfrauddetection;

import twitter4j.Trend;

/**
 * @author Miltos Nedelkos, nedelkosm at gmail com
 * gihub dot com/nedelkosm
 * nedelkos dot me
 * 
 * TimedTrend class.
 * Enhances a Thread object by adding lifespan.
 */
public class TimedTrend {
    Trend trendObj;
    long lastRefresh;
    TimedTrend(Trend trendIni){
        trendObj = trendIni;
        lastRefresh = System.currentTimeMillis();
    }
    /**
     * Checks if the Trend has expired.
     * @param minutes Sets the lifespan of the Trend (since last refresh).
     * @return Will return true if Trend has expired.
     */
    public boolean expired(long minutes){
        long now = System.currentTimeMillis();
        int minutesSinceBirth = (int) (now - lastRefresh)/(6000);
        return minutesSinceBirth >= minutes;
    }
    /**
     * Refreshes the Trend's time of birth to current system time.
     */
    public void refresh(){
        lastRefresh = System.currentTimeMillis();
    }
    /**
     * Get method for Trend object
     * @return Returns the Trend object.
     */
    public Trend getTrend(){
        return trendObj;
    }
}
