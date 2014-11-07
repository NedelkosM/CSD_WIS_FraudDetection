package db.mongodbadapter;

public class App {

    public static void main(String[] args) {

        if (args.length < 1) 
        {
            System.out.println("Proper use: <category_directory>");
        }
        else 
        {
        //Paths of directories for getting from and moving to the json files
            //The directory containing the new json files that need to be inserted in the db
            final String new_folder_path = "/usr/lib/twitter-project/" + args[0] + "/new/";
            //the directory the new json will be moved into after they are inserted
            final String backup_folder_path = "/usr/lib/twitter-project/" + args[0] + "/backup/";
        }

    }
}
