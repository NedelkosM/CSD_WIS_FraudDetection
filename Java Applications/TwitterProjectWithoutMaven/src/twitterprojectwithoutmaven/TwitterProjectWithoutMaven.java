/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package twitterprojectwithoutmaven;

import com.mongodb.DBCursor;
import java.io.File;
import java.net.UnknownHostException;

/**
 *
 * @author efi
 */
public class TwitterProjectWithoutMaven {

    
    /**
     * Relative path in which the files will be shared. Slash at the end is not
     * needed.
     */
    public static String relPath = File.separator + "TweetMiner";
    
        /**
     * Main class. Will parse parameters and initialize the Miner for a 3-day
     * period.
     *
     * @param args [relative path, consumer key, consumer key secret]
     * @throws java.net.UnknownHostException
     */
    public static void main(String[] args) throws UnknownHostException 
    {
        
        String correct_usage = "Wrong input.Correct usage is: java -jar TwitterProjectWithoutMaven.jar <Miner/Stalker/Find Users/Statistics/ExtractXLS>";

        if (args.length > 0) {

            //initialize dbAdapter
            dbAdapter.getInstance().initialize();

            switch (args[0]) {
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
                    //add stalked users to stalker
                    DBCursor cursor = dbAdapter.getInstance().getStalkedUsers();
                    while(cursor.hasNext())
                    {
                        stalker.addUser(Long.parseLong(new DBUser(cursor.next()).getID()));
                    }
                    cursor.close();
                    //start
                    stalker.initialize();
                    break;
                case "Find Users":
                    Choose40Users choose = new Choose40Users();
                    choose.doTheJob();
                    break;
                case "Statistics":
                    System.out.println("Running Statistics...");
                    int num = 0;
                    if (args.length > 1)
                    {
                        num =  Integer.parseInt(args[1]);
                    }
                    Statistics st=new Statistics(false, num);
                    st.StatisticsB();
                    break;
                case "ExtractXLS":
                    System.out.println("Running ExtractXLS...");
                    Statistics st1=new Statistics(false, 0);
                    st1.ExtractXLS("UserStats.xls");
                    break;
                default:
                    System.out.println(correct_usage);
                    break;

            }
            //close db connections
            dbAdapter.getInstance().closeConnections();
        }
        else
        {
            System.out.println(correct_usage);
        }
    }
    
}
