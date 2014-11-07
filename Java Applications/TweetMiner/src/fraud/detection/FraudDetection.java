package fraud.detection;

import static fraud.detection.tFunctions.*;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Miltos
 */
public class FraudDetection {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {  
        updateBearerToken();
        try{
            System.out.println(fetchTrends("https://api.twitter.com/1.1/trends/available.json"));
        } catch (IOException ex) {
            Logger.getLogger(FraudDetection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
