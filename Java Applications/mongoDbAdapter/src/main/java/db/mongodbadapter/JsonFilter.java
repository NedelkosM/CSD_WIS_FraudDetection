package db.mongodbadapter;

import java.io.File;
import java.io.FileFilter;

/**
 *
 * @author efi
 */
public class JsonFilter implements FileFilter {

    public boolean accept(File pathname) {
        return pathname.toString().endsWith(".json");
    }
    
}
