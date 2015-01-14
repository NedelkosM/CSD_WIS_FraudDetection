package tfd.miner;

import java.io.File;
import java.net.UnknownHostException;

/**
 * @author Miltos Nedelkos, nedelkosm at gmail com gihub dot com/nedelkosm
 nedelkos dot me

 App Class
 */
public class App {

    /**
     * Relative path in which the files will be shared. Slash at the end is not
     * needed.
     */
    public static String relPath = File.separator + "TweetMiner";

    /**
     * App class. Will parse parameters and initialize the Miner for a 3-day
     * period.
     *
     * @param args [relative path, Miner/Stalker, consumer key, consumer key secret]
     * @throws java.net.UnknownHostException
     */
    public static void main(String[] args) throws UnknownHostException 
    {
        String correct_usage = "Wrong input.Correct usage is: java -jar TwitterFraudDetection-1.0.jar <directory_name> <Miner/Stalker> <consumer_key> <consumer_secret>";
        
        if (args.length > 1) {
            relPath = args[0];

            switch (args[1]) {
                case "Miner":
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
                    //stalker.addUser((64-bit long)id);
                    //stalker.addUser((twitter4j.User)user);
                    stalker.initialize();
                    break;
                default:
                    System.out.println(correct_usage);
                    break;

            }
        }
        else
        {
            System.out.println(correct_usage);
        }
    }
}
