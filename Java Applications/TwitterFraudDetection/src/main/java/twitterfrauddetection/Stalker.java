package twitterfrauddetection;

import static twitterfrauddetection.tStreamFunctions.startStream;
import static twitterfrauddetection.tStreamFunctions.stopStream;
import static twitterfrauddetection.App.relPath;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
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
public class Stalker {
    Date dateStarted;
    /**
     * Constructor for Stalker class. Will also initialize the Stalker.
     * @param duration The duration (in days).
     */
    public Stalker(int duration){
        dateStarted = new Date();
        System.out.println(dateStarted);
    }
}
