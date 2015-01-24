/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package twitterprojectwithoutmaven;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author efi
 */
public class DBTweet {

    private String ID;
    private String Text;
    private String UserID;
    private String UserName;
    private String created_at;
    private DBObject obj;

    public DBTweet() {

    }

    public DBTweet(DBObject obj) {
        this.ID = obj.get("ID").toString();
        this.Text = obj.get("Text").toString();
        this.UserID = obj.get("UserID").toString();
        this.UserName = obj.get("UserName").toString();
        this.created_at = obj.get("created_at").toString();
        this.obj = obj;
    }

    public void reset(DBObject obj) {
        this.ID = obj.get("ID").toString();
        this.Text = obj.get("Text").toString();
        this.UserID = obj.get("UserID").toString();
        this.UserName = obj.get("UserName").toString();
        this.created_at = obj.get("created_at").toString();
        this.obj = obj;
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

    long getReTweets() {
        return Integer.getInteger(obj.get("RetweetCount").toString());
    }

    long isAReply() {
        String s = (String) obj.get("in_reply_to_screen_name");
        if (s != null) {
            return 1;
        }
        return 0;
    }

    long getMentions() {
        return ((BasicDBList) obj.get("Mentions")).size();
    }

    long isaReTweet() {
        if (Boolean.getBoolean(obj.get("Retweeted").toString())) {
            return 1;
        }
        return 0;
    }

    long getHashTags() {
        return ((BasicDBList) obj.get("Hashtags")).size();
    }

    long getUrls() {
        return ((BasicDBList) obj.get("URLs")).size();
    }

    long isASimpleTweet() {
        if (this.isaReTweet() + this.isAReply() > 0) {
            return 0;
        }
        return 1;
    }

    String getSource() {
        return obj.get("Source").toString();
    }

    String getLevText() {
        String t = this.getText();
        String toR = this.removeUrl(t);
        toR = this.removeMentions(Text);

        return toR;
    }

    private String removeUrl(String commentstr) {
        String urlPattern = "((https?|ftp|gopher|telnet|file|Unsure|http):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
        Pattern p = Pattern.compile(urlPattern, Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(commentstr);
        int i = 0;
        while (m.find()) {
            commentstr = commentstr.replaceAll(m.group(i), "").trim();
            i++;
        }
        return commentstr;
    }

    private String removeMentions(String commentstr) {
        String urlPattern = "@([^@ ]+)";
        Pattern p = Pattern.compile(urlPattern, Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(commentstr);
        int i = 0;
        while (m.find()) {
            commentstr = commentstr.replaceAll(m.group(i), "").trim();
            i++;
        }
        return commentstr;

    }

    public ArrayList<String> getURLS() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
