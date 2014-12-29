package twitterfrauddetection;

import static twitterfrauddetection.tStreamFunctions.startStream;
import static twitterfrauddetection.tStreamFunctions.stopStream;
import static twitterfrauddetection.App.relPath;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Miltos Nedelkos, nedelkosm at gmail com
 * gihub dot com/nedelkosm
 * nedelkos dot me
 * 
 * Miner Class
 * Pulls and saves (as .json) Twitter trends every 5 minutes.
 * Between the 5 minute intervals it will record all relative tweets at the same folder.
 */
public class Miner implements Runnable{
    /**
     * Used to store the trends.
     */
    String trends;
    /**
     * Counts the number of all trend file attempts.
     */
    int totalFiles = 0;
    /**
     * Counts the number of successful trends files.
     */
    int successfulFiles = 0;
    /**
     * Amounts for the total files of trends needed.
     */
    int targetFiles = 0;
    /**
     * Constructor for Miner object. Will also initialize the Miner.
     * @param target Total times the Miner class will update trends.
     */
    public Miner(int target){
        targetFiles = target; // Total trends
        run();
    }
    /**
     * Initializes the Miner
     */
    public void run(){
        while(successfulFiles!=targetFiles){
            String newFile = getTrends();
            if(newFile!=null){
                System.out.println("File "+newFile+" created.");
                successfulFiles++;
                listenTrends(); // Listen to tweets bewteen intervals
            } else {
                System.out.println("Error : File not created.");
            }
            try {
                // Twitter updates trends every 5 minutes
                int seconds = 300; // 5-minute intervals 
                Thread.sleep(seconds * 1000);
                stopStream();
            } catch(InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
    }
    /**
     * Calls twitter API to retrieve trends. Also creates folder Trends* and saves trends as a Trends*.json file
     * @return Returns current trends response from twitter API. Format .JSON. Will return null if cannot connect to twitter API.
     */
    private String getTrends(){
        String filename = null;
        trends = tFunctions.getWorldTrends();
        if(trends == null){
            return null;
        }
        filename = relPath+File.separator+"Trends"+successfulFiles+File.separator+"Trends"+(totalFiles++)+".json";
        writeFile(filename,trends);
        
        return filename;
    }
    /**
     * Calls streaming API functions if the trends request was successful.
     */
    public void listenTrends(){
        if(trends!=null){
            startStream(trends,successfulFiles);
        }
    }
    /**
     * Creates a new file and writes a String in it.
     * @param filename Target filename. Will create parent folder if not existing.
     * @param content The String which will be written at target file.
     */
    public static void writeFile(String filename, String content){
        FileWriter outFile = null;
        try {
            File myfile = new File(filename);
            if(!myfile.getParentFile().exists()){
                myfile.getParentFile().mkdir();
            }
            outFile = new FileWriter(filename);
        } catch (IOException ex) {
            Logger.getLogger(Miner.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        try (PrintWriter out = new PrintWriter(outFile)) {
            out.print(content);
        }
    }
}