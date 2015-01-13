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
public class DBTweet {
    
    private final String ID;
    private final String Text;
    private final String UserID;
    private final String UserName;
    private final String created_at;
    
    public DBTweet (DBObject obj)
    {
       this.ID = obj.get("ID").toString();
       this.Text = obj.get("ID").toString();
       this.UserID = obj.get("UserID").toString();
       this.UserName = obj.get("UserName").toString();
       this.created_at = obj.get("created_at").toString();
    }

    public String getID() {
        return ID;
    }

    public String getText() {
        return Text;
    }

    public String getUserID() {
        return UserID;
    }

    public String getUserName() {
        return UserName;
    }

    public String getCreated_at() {
        return created_at;
    }
}
