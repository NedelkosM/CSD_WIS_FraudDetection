/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package twitterprojectwithoutmaven;

import com.mongodb.DBObject;

/**
 *
 * @author efi
 */
public class DBUser {
    
    private final String ID;
    private final String UserName;
    private final int Friends;
    private final int Followers;
    private final String Description;
    private final String created_at;
    
    public DBUser (DBObject obj)
    {
        this.ID = obj.get("ID").toString();
        if (obj.get("Description") != null)
        {
            this.Description = obj.get("Description").toString();
        }
        else
        {
            this.Description = "null";
        }
        this.created_at = obj.get("created_at").toString();
        this.UserName = obj.get("UserName").toString();
        this.Friends = Integer.parseInt(obj.get("Friends").toString());
        this.Followers = Integer.parseInt(obj.get("Followers").toString());
    }

    public String getID() {
        return ID;
    }

    public String getUserName() {
        return UserName;
    }

    public int getFriends() {
        return Friends;
    }

    public int getFollowers() {
        return Followers;
    }

    public String getDescription() {
        return Description;
    }

    public String getCreated_at() {
        return created_at;
    }
    
    
}
