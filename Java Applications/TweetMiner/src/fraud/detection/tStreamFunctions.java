package fraud.detection;

import static fraud.detection.FraudDetection.relPath;
import static fraud.detection.tFunctions.twitterStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.Trend;
import twitter4j.Trends;
import twitter4j.TwitterException;
import twitter4j.json.DataObjectFactory;
import static twitter4j.json.DataObjectFactory.createTrends;
/**
 * @author Miltos Nedelkos, nedelkosm at gmail com
 * gihub dot com/nedelkosm
 * nedelkos dot me
 * 
 * tStreamFunctions class.
 * Implements all functions that are needed for twitter Streaming API calls.
 */
public class tStreamFunctions{
    /**
     * Trends array. Used to filter which tweets contain a popular hashtag.
     */
    static String[] trends;
    /**
     * Counts the number of tweets recored since the program started.
     */
    static int totalTweets = 0;
    /**
     * Counts the number of tweets recored between intervals.
     */
    static int tweetsPerRun = 0;
    /**
     * Counts the number of 5min iterations.
     */
    static int itterations;
    /**
     * Updates trends array, which is used to filter which tweets will be saved to file. Must be called before streaming listener is initialized.
     * @param newTrends Top 10 Twitter trends. In JSON form.
     */
    private static void updateTrends(String newTrends){
        trends = new String[10];
        try {
            Trends tr = createTrends(newTrends);
            Trend[] trendsObj;
            trendsObj = tr.getTrends();
            int count = 0;
            for(Trend t:trendsObj){
                trends[count++] = t.getName();
            }
        } catch (TwitterException ex) {
            Logger.getLogger(tStreamFunctions.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    /**
     * Initializes the streaming API listeners. Will parse all incoming tweets and write to file all tweets that use a hashtag from the top trends.
     */
    public static void startStream(String newTrends, int itteration){
        if(newTrends == null) return;
        updateTrends(newTrends);
        itterations = itteration;
        StatusListener listener = new StatusListener() {
            @Override
            public void onStatus(Status status) {
                for(int i=0;i<10;i++){
                    if(status.getText().contains(trends[i])){
                        //System.out.println("@" + status.getUser().getScreenName() + " - " + status.getText());
                        String json = DataObjectFactory.getRawJSON(status);
                        String filename = relPath+"\\Trends"+(itterations-1)+"\\Tweet"+(tweetsPerRun++)+"-"+System.currentTimeMillis()+".json";
                        totalTweets++;
                        Miner.writeFile(filename, json);
                    }
                }
            }

            @Override
            public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
                //System.out.println("Got a status deletion notice id:" + statusDeletionNotice.getStatusId());
            }

            @Override
            public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
                //System.out.println("Got track limitation notice:" + numberOfLimitedStatuses);
            }

            @Override
            public void onScrubGeo(long userId, long upToStatusId) {
                //System.out.println("Got scrub_geo event userId:" + userId + " upToStatusId:" + upToStatusId);
            }

            @Override
            public void onStallWarning(StallWarning warning) {
                //System.out.println("Got stall warning:" + warning);
            }

            @Override
            public void onException(Exception ex) {
                ex.printStackTrace();
            }
        };
        twitterStream.addListener(listener);
        twitterStream.sample();
    }
    
    /**
     * Stops listening to streaming API and reports results for the last 5 minutes.
     */
    public static void stopStream(){
        if(twitterStream!=null){
            twitterStream.cleanUp();
            System.out.println("Tweets for last trend : "+tweetsPerRun);
            System.out.println("Total Tweets : "+totalTweets);
            // Reset tweets counter
            tweetsPerRun = 0;
        }
    }
}