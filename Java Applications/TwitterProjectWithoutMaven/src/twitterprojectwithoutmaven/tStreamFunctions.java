package twitterprojectwithoutmaven;

import static twitterprojectwithoutmaven.tFunctions.twitterStream;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import twitter4j.FilterQuery;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.Trend;
import twitter4j.Trends;
import twitter4j.TwitterException;
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
    static ArrayList<TimedTrend> trends;
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
    static int iterations;
    /**
     * Counts the total number of tweets gathered from user being tracked
     */
    static int userTweets = 0;
    /**
     * Tracked users
     */
    static ArrayList trackedUsers; 
    
    /**
     * Updates trends array, which is used to filter which tweets will be saved to file. Must be called before streaming listener is initialized.
     * @param newTrends Top 10 Twitter trends. In JSON form.
     */
    private static void updateTrends(String newTrends){
        trends = new ArrayList<>();
        try {
            Trends tr = createTrends(newTrends);
            Trend[] trendsObj;
            trendsObj = tr.getTrends();
            TimedTrend[] tempTTrends = new TimedTrend[trendsObj.length];
            for(int i=0;i<trendsObj.length;i++){
                tempTTrends[i] = new TimedTrend(trendsObj[i]);
            }
            for(TimedTrend t:tempTTrends){
                boolean placed = false;
                for (int i=0;i<trends.size();i++){
                    TimedTrend temp = (TimedTrend) trends.get(i);
                    String tempName = temp.getTrend().getName();
                    String tName = t.getTrend().getName();
                    if(tempName.equals(tName)){
                        t.refresh();
                        placed = true;
                        break;
                    }
                }
                if(!placed){
                    trends.add(t);
                }
            }
            for(int i =0;i<trends.size();i++){
                TimedTrend t = (TimedTrend) trends.get(i);
                if(t.expired(120)){
                    trends.remove(i);
                }
            }
        } catch (TwitterException ex) {
            Logger.getLogger(tStreamFunctions.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    /**
     * Initializes the streaming API listeners. Will parse all incoming tweets and write to file all tweets that use a hashtag from the top trends.
     * @param newTrends Updates top trends
     * @param iteration Counts the number of iterations since the program started. Used to determine in which folder the tweets will be written.
     */
    public static void startStream(String newTrends, int iteration){
        if(newTrends == null) return;
        updateTrends(newTrends);
        iterations = iteration;
        
        StatusListener listener = new StatusListener() {
            @Override
            public void onStatus(Status status) {
                //System.out.println("@" + status.getUser().getScreenName() + " - " + status.getText());
                dbAdapter.getInstance().insertTweet(status);
                totalTweets++;
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
         // Filter
        FilterQuery filter = new FilterQuery();
        String[] keywordsArray = new String[trends.size()];
        TimedTrend temp;
        for(int i=0;i<keywordsArray.length;i++){
            temp = (TimedTrend) trends.get(i);
            keywordsArray[i] = temp.getTrend().getName();
        }
        filter.track(keywordsArray);
        twitterStream.filter(filter);
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
    /**
     * Initializes the streaming API listeners. Will parse all incoming tweets and write to file all tweets that use a hashtag from the top trends.
     * @param newUsers Updates tracked users
     */
    public static void startStalkerStream(ArrayList newUsers){
        if(newUsers != null && newUsers.size()>0){
            trackedUsers = newUsers;
        } else {
            System.out.println("No users to track. Exiting stream.");
            return;
        }
        
        StatusListener listener = new StatusListener() {
            @Override
            public void onStatus(Status status) {
                long userID = status.getUser().getId();
                for(int i=0;i<trackedUsers.size();i++){
                    if(trackedUsers.contains(userID)){
                        //System.out.println("@" + status.getUser().getScreenName() + " - " + status.getText());
                        dbAdapter.getInstance().insertUserTweet(status);
                        userTweets++;
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
         // Filter
        FilterQuery filter = new FilterQuery();
        long[] users = new long[trackedUsers.size()];
        for(int i=0;i<users.length;i++){
            users[i] = (long) trackedUsers.get(i);
        }
        filter.follow(users);
        twitterStream.filter(filter);
    }
    
    /**
     * Stops listening to streaming API and reports results for the last 5 minutes.
     */
    public static void stopStalkerStream(){
        if(twitterStream!=null){
            twitterStream.cleanUp();
            System.out.println("Total tracked users tweets "+userTweets);
            tweetsPerRun = 0;
        }
    }
}