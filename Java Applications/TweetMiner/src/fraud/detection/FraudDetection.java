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
        try {
            System.out.println(requestBearerToken("https://api.twitter.com/oauth2/token"));
        } catch (IOException ex) {
            Logger.getLogger(FraudDetection.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            System.out.println(fetchTrends("https://api.twitter.com/1.1/trends/available.json"));
        } catch (IOException ex) {
            Logger.getLogger(FraudDetection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
