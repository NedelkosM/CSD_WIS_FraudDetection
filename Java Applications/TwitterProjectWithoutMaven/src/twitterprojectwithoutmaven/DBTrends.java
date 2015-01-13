/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package twitterprojectwithoutmaven;

import com.mongodb.DBObject;
import java.util.ArrayList;

/**
 *
 * @author efi
 */
public class DBTrends {
    
    private ArrayList<String> trends;
    private String as_of;
    
    public DBTrends (DBObject obj)
    {
        trends = new ArrayList<>();
        String trendsRaw = obj.get("trends").toString();
        String parts[] = trendsRaw.split(",");
        String trend[];
        for (String part : parts)
        {
            trend =  part.split(":");
            String trim = trend[1].trim();
            trends.add(trim);
        }
        this.as_of = obj.get("as_of").toString();
    }
    
    public ArrayList<String> getTrends()
    {
        return this.trends;
    }
}
