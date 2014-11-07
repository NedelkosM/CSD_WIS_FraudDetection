package db.mongodbadapter;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import static java.util.concurrent.TimeUnit.DAYS;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 *
 * @author efi
 */
public class dbScheduler {
    
   /************************************************************
    * Fields
    *************************************************************/
    
    //scheduler to run the routine for fixed period of time
    private final ScheduledExecutorService scheduler;
    //The directory containing the new json files that need to be inserted in the db
    private final File new_dir;
    //the directory the new json will be moved into after they are inserted
    private final File backup;
    //Minutes for every routine
    private final int minutes = 5;
    //Days the program will run in total
    private final int days = 3;
    //filter to recognise the json files from any other files
    private JsonFilter filter;
    
    
    /************************************************************
    * Methods
    *************************************************************/
    
    /**
     * Constructor with the paths for the directories
     * @param new_directory_path
     * @param backup_directory_path 
     */
    public dbScheduler(String new_directory_path, String backup_directory_path )
    {
        new_dir = new File(new_directory_path);
        backup = new File(backup_directory_path);
        filter = new JsonFilter();
        scheduler = Executors.newScheduledThreadPool(1);
    }
    
    /**
     * Routine
     * Gets the files contained in the new_directory_path
     * Adds one by one the files for as long as it runs in the db
     * Moves every file it adds to the backup_directory
     */
    public void runRoutineForFixedTime() {
     final Runnable checker = new Runnable() {
       
         public void run() 
       { 
           //get list of json files in the directory
           File[] files = new_dir.listFiles(filter);
           
           //run for every file in directory, may not finish all the files in the directory
           //depends on the fixed time and the number of files
           for (File file : files)
           {
               //add file to the db
               
               //move file to the backup directory
           }
       }
     };
     
     
     /* Correct scheduler for the 3 days <--------------------------------------
     final ScheduledFuture<?> beeperHandle =
       scheduler.scheduleAtFixedRate(checker, minutes, minutes, MINUTES);
     scheduler.schedule(new Runnable() {
       public void run() { beeperHandle.cancel(true); }
     }, days, DAYS);
             */
     
     //Scheduler for testing,runs for a minute, every 10 seconds
          final ScheduledFuture<?> beeperHandle =
       scheduler.scheduleAtFixedRate(checker, 10, 10, SECONDS);
     scheduler.schedule(new Runnable() {
       public void run() { beeperHandle.cancel(true); }
     }, 60, SECONDS);
    }
}
