/*
 * Author : Miltos Nedelkos
 *
 */
package fraud.detection;

import static fraud.detection.FraudDetection.relPath;
import static fraud.detection.tStreamFunctions.startStream;
import static fraud.detection.tStreamFunctions.stopStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Miner implements Runnable{
    String trends;
    int totalFiles = 0;
    int successfulFiles = 0;
    int targetFiles = 0;
    
    public Miner(int target){
        targetFiles = target;
        run();
    }
    public void run(){
        while(successfulFiles!=targetFiles){
            String newFile = getTrends();
            if(newFile!=null){
                System.out.println("File "+newFile+" created.");
                successfulFiles++;
                listenTrends();
            } else {
                System.out.println("Error : File not created.");
            }
            try {
                int seconds = 300;
                Thread.sleep(seconds * 1000);
                stopStream();
            } catch(InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
    }
    private String getTrends(){
        String filename = null;
        trends = tFunctions.getWorldTrends();
        System.out.println("Trends: "+trends);
        if(trends == null){
            return null;
        }
        filename = relPath+"\\Trends"+successfulFiles+"\\Trends"+(totalFiles++)+".json";
        writeFile(filename,trends);
        
        return filename;
    }
    public void listenTrends(){
        if(trends!=null){
            startStream(trends,successfulFiles);
        }
    }
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