package fraud.detection;

import java.io.File;

/**
 * @author Miltos Nedelkos, nedelkosm at gmail com
 * gihub dot com/nedelkosm
 * nedelkos dot me
 * 
 * Main Class
 */
public class FraudDetection {
    /**
     * Relative path in which the files will be shared. Slash at the end is not needed.
     */
    public static String relPath = "\\TweetMiner";
    
    /**
     * Main class. Will parse parameters and initialize the Miner for a 3-day period.
     * @param args [relative path, consumer key, consumer key secret]
     */
    public static void main(String[] args) {
        if(args.length > 0){
            relPath = args[0];
        }
        File myfile = new File(relPath+"\\temp.txt");
        if(!myfile.getParentFile().exists()){
            myfile.getParentFile().mkdir();
        }
        tFunctions.UserAuth(args);
        new Miner(864);  // This amounts to a total of 3 days for 5-minute itterations.
        return;
    }
}