/*
 * Author : Miltos Nedelkos
 *
 */
package fraud.detection;

import com.sun.corba.se.impl.protocol.giopmsgheaders.Message;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Miner implements Runnable{
    String relPath = "";
    int totalFiles = 0;
    int successfulFiles = 0;
    int targetFiles = 0;
    
    public Miner(int target, String relativePath ){
        targetFiles = target;
        relPath = relativePath;
        run();
    }
    public void run(){
        while(successfulFiles!=targetFiles){
            String newFile = getTrends();
            if(newFile!=null){
                System.out.println("File "+newFile+" created.");
                successfulFiles++;
            } else {
                System.out.println("Error : File not created.");
            }
            try {
                int seconds = 5;
                Thread.sleep(seconds * 1000);
            } catch(InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
    }
    private String getTrends(){
        String filename = null;
        try {
            tFunctions.AppAuth();
        } catch (Exception ex) {
            Logger.getLogger(Miner.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        String trends = tFunctions.getWorldTrends();
        if(trends == null){
            return null;
        }
        FileWriter outFile = null;
        try {
            filename = relPath+"Trends"+(totalFiles++)+".json";
            outFile = new FileWriter(filename);
        } catch (IOException ex) {
            Logger.getLogger(Miner.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        try (PrintWriter out = new PrintWriter(outFile)) {
            out.print(trends);
        }
        return filename;
    }
}