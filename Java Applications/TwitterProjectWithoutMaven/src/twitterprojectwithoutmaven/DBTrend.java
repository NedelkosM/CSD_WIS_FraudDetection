package twitterprojectwithoutmaven;

import com.mongodb.DBObject;

/**
 *
 * @author efi
 */
public class DBTrend {
    
    private final String ID;
    private final String trend;
    
    public DBTrend (DBObject obj)
    {
        this.ID = obj.get("ID").toString();
        this.trend = obj.get("Name").toString();
    }

    public String getID() {
        return ID;
    }

    public String getTrend() {
        return trend;
    }
    
}
