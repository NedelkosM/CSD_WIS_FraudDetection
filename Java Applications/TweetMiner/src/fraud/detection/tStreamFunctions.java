/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

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
import java.lang.System;

/**
 *
 * @author Miltos
 */
public class tStreamFunctions{
    static String[] trends;
    static int totalTweets = 0;
    static int itterations;
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
    public static void startStream(String newTrends, int itteration){
        if(newTrends == null) return;
        updateTrends(newTrends);
        itterations = itteration;
        StatusListener listener = new StatusListener() {
            @Override
            public void onStatus(Status status) {
                for(int i=0;i<10;i++){
                    if(status.getText().contains(trends[i]))
                    System.out.println("@" + status.getUser().getScreenName() + " - " + status.getText());
                    
                    String json = DataObjectFactory.getRawJSON(status);
                    String filename = relPath+"\\Trends"+(itterations-1)+"\\Tweet"+(totalTweets++)+System.currentTimeMillis()+".json";
                    Miner.writeFile(filename, json);
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
    public static void stopStream(){
        if(twitterStream!=null){
            twitterStream.cleanUp();
        }
    }
}
