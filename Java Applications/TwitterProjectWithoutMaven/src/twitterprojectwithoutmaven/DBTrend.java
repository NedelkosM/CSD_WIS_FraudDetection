package twitterprojectwithoutmaven;

import com.mongodb.DBObject;

/**
 *
 * @author efi
 */
public class DBTrend {
    
    private final String id;
    private final String trend;
    
    public DBTrend (DBObject obj)
    {
        this.id = obj.get("ID").toString();
        this.trend = obj.get("Name").toString();
    }

    public String getId() {
        return id;
    }

    public String getTrend() {
        return trend;
    }
    
}
