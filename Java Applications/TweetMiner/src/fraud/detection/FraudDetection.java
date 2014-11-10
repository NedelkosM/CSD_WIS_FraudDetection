package fraud.detection;

import java.io.File;

/**
 *
 * @author Miltos
 */
public class FraudDetection {
    public static String relPath = "\\TweetMiner";
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