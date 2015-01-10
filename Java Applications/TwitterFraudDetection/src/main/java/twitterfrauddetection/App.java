package twitterfrauddetection;

import java.io.File;
import java.net.UnknownHostException;
import twitter4j.User;

/**
 * @author Miltos Nedelkos, nedelkosm at gmail com
 * gihub dot com/nedelkosm
 * nedelkos dot me
 * 
 * Main Class
 */
public class App 
{

    /**
     * Relative path in which the files will be shared. Slash at the end is not needed.
     */
    public static String relPath = File.separator+"TweetMiner";
    
    /**
     * Main class. Will parse parameters and initialize the Miner for a 3-day period.
     * @param args [relative path, consumer key, consumer key secret]
     */
    public static void main(String[] args) throws UnknownHostException {
        if(args.length > 0){
            relPath = args[0];
        }
        
        //initialize dbAdapter
        //dbAdapter.getInstance().initialize();
        
        /* PHASE 1 
        File myfile = new File(relPath+File.separator+"temp.txt");
        if(!myfile.getParentFile().exists()){
            myfile.getParentFile().mkdir();
        }
        tFunctions.UserAuth(args);
        Miner miner = new Miner(864); // This amounts to a total of 3 days for 5-minute itterations.
        */
        
        tFunctions.UserAuth(args);
        // Creates a stalker with a starting duration of 7 days
        Stalker stalker = new Stalker(7);
        //stalker.addUser(USERID or twitter4j.User class);
        stalker.initialize();
        
        //close db connections
        //dbAdapter.getInstance().closeConnections();
    }
}
