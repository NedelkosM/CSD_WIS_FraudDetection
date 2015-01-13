package twitterfrauddetection;

import java.io.File;
import java.net.UnknownHostException;

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
     * @throws java.net.UnknownHostException
     */
    public static void main(String[] args) throws UnknownHostException 
    {
        String correct_usage = "Wrong input.Correct usage is: java -jar TwitterFraudDetection-1.0.jar <directory_name> <Miner/Stalker>";
        
        if(args.length > 1){
            relPath = args[0];
        }
        else
        {
            System.out.println(correct_usage);
        }
        //initialize dbAdapter
        dbAdapter.getInstance().initialize();
        
        switch (args[1])
        {
            case "Miner":
                // PHASE 1 
                File myfile = new File(relPath + File.separator + "temp.txt");
                if (!myfile.getParentFile().exists()) 
                {
                    myfile.getParentFile().mkdir();
                }
                tFunctions.UserAuth(args);
                Miner miner = new Miner(864); // This amounts to a total of 3 days for 5-minute itterations.
                break;
            case "Stalker":
                tFunctions.UserAuth(args);
                // Creates a stalker with a starting duration of 7 days
                Stalker stalker = new Stalker(7);
                //stalker.addUser(USERID or twitter4j.User class);
                stalker.initialize();
                break;
            default:
                System.out.println(correct_usage);
                break;

        }
        //close db connections
        dbAdapter.getInstance().closeConnections();
    }
}
