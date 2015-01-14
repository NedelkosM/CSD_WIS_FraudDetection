package twitterprojectwithoutmaven;

import static twitterprojectwithoutmaven.tStreamFunctions.startStalkerStream;
import java.util.ArrayList;
import java.util.Date;
import twitter4j.User;
import static twitterprojectwithoutmaven.tStreamFunctions.userTweets;

/**
 * @author Miltos Nedelkos, nedelkosm at gmail com
 * gihub dot com/nedelkosm
 * nedelkos dot me
 * 
 * Miner Class
 * Pulls and saves (as .json) Twitter trends every 5 minutes.
 * Between the 5 minute intervals it will record all relative tweets at the same folder.
 */
public class Stalker {
    /**
     * Starting point
     */
    Date dateStarted;
    /*
    * Default duration is 7 days.
    */
    int duration = 7;
    /*
    * Every how many minutes the Stalker will report results
    */
    int reportTimer = 60;
    /*
    * List of tracked users
    */
    ArrayList trackedUsers = null;
    /**
     * Counts the total number of tweets gathered from user being tracked
     */
    int gatheredTweets = 0;
    /**
     * Constructor for Stalker class.
     * @param duration The duration (in days).
     */
    public Stalker(int duration){
        dateStarted = new Date();
        System.out.println("Stalker - New Stalker. Created at: "+dateStarted);
        System.out.println("Stalker - Set stalker duration at: "+duration+" days.");
        this.duration = duration;
        trackedUsers = new ArrayList<>();
    }
    /**
     * Constructor for Stalker class.
     */
    public Stalker(){
        dateStarted = new Date();
        System.out.println("Stalker - New stalker class. Created at: "+dateStarted);
        trackedUsers = new ArrayList<>();
    }
    /**
     * Setter function for total duration
     * @param duration New duration (in days)
     */
    public void setDuration(int duration){
        this.duration = duration;
    }
    /**
     * Resets starting point
     */
    public void resetDate(){
        dateStarted = new Date();
    }
    /**
     * Initializes the stalker
     */
    public void initialize(){
        if(trackedUsers != null && trackedUsers.size()>0){
        startStalkerStream(trackedUsers);
        } else {
            System.out.println("Stalker - No users to track.");
            return;
        }
        System.out.println("--- Initializing Stalker ---");
        System.out.println("Stalker - Users tracked : "+trackedUsers.size());
        System.out.println("Stalker - Will report every "+reportTimer+" minutes");
        System.out.println("--- Stalker Initialized ---");
        
        Date currentDate = new Date();
        long lifetime = currentDate.getTime() - dateStarted.getTime();
        while(lifetime < (duration*24*60*60*1000)){
            try{
                Thread.sleep(reportTimer * 60 * 1000);
            } catch(InterruptedException ex){
                System.out.println(ex);
            }
            reportResults();
            currentDate = new Date();
            lifetime = currentDate.getTime() - dateStarted.getTime();
        }
    }
    /**
     * Adds a new user at tracked users list. Note that Twitter uses 64-bit long IDs.
     * If you want to insert an ID manually you have to add l suffix to your number.
     * for example : 3847015174 is 32-bit but 3847015174l is 64-bit long.
     * @param userID The new user's ID (must be 64-bit).
     */
    public void addUser(long userID){
        if(trackedUsers.contains(userID)){
            System.out.println("Stalker - User already tracked.");
        } else {
            trackedUsers.add(userID);
            System.out.println("Stalker - Tracking user "+userID);
        }
    }
    /**
     * Adds a new user at tracked users list.
     * @param newUser The new user.
     */
    public void addUser(User newUser){
        if(newUser == null){
            System.out.println("Stalker - No new user given.");
            return;
        }
        long userID = newUser.getId();
        addUser(userID);
    }
    /**
     * Removes a user from tracked users list.
     * @param userID The user's ID.
     */
    public void removeUser(long userID){
        if(!trackedUsers.contains(userID)){
            System.out.println("Stalker - User was not on tracked list.");
        } else {
            trackedUsers.remove(userID);
            System.out.println("Stalker - User "+userID+" is no longer tracked.");
        }
    }
    /**
     * Controls how often  the stalker will report results
     * @param minutes Number of minutes between each interval.
     */
    public void setReportIntervals(int minutes){
        reportTimer = minutes;
    }
    /**
     * Reports Stalker results
     */
    private void reportResults() {
        int totalUserTweets = userTweets;
        int lastRun = userTweets - gatheredTweets; 
        gatheredTweets = userTweets;
        Date currentDate = new Date();
        long lifetime = currentDate.getTime() - dateStarted.getTime();
        long targetLifetime = duration*24*60*60*1000;
        long remainingLifetime = targetLifetime - lifetime;
        int remainingMinutes = (int) ((remainingLifetime/1000)/60);
        
        System.out.println("--- Stalker Results ---");
        System.out.println("Stalker - Users tracked : "+trackedUsers.size());
        System.out.println("Stalker - User tweets last run : "+lastRun);
        System.out.println("Stalker - Total tweets : "+totalUserTweets);
        System.out.println("Stalker - Next results :"+reportTimer+" min.");
        System.out.println("Stalker - Remaining Lifetime : "+remainingMinutes+" minutes");
        System.out.println("--- Stalker Results ---");
    }
    /**
     * Get tracked users
     * @return Tracked users
     */
    public ArrayList getTrackedUsers(){
        return trackedUsers;
    }
}
