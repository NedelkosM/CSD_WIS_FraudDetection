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
        Integer k = Integer.getInteger(obj.get("RetweetCount").toString());

        if (k == null) {
            return 0;
        }
        return k;
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
        if (Text.contains("RT ")) {
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
        //String toR = this.removeUrl(t);
        //toR = this.removeMentions(toR);
        return removeMentions(this.getText());
        //return toR;
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

    private String removeMentions(String str) {
        String[] split = str.split(" ");
        String toReturn = "";
        for (String s : split) {
            if (s.length() > 1) {
                if (!(s.charAt(0) == '@' && s.length() > 1)) {
                    toReturn = toReturn + " " + s;
                }
            } else {
                toReturn = toReturn + " " + s;
            }
        }
        return toReturn.trim();

    }

    public ArrayList<String> getURLS() {
        BasicDBList list = ((BasicDBList) obj.get("URLs"));
        ArrayList<String> toReturn = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            BasicDBObject o = (BasicDBObject) list.get(i);
            String url = o.get("" + (i + 1)).toString();
            url = getExpandedUrl(url);
            toReturn.add(url);
        }

        return toReturn;

    }

    private String getExpandedUrl(String url) {

        String[] split = url.split(", ");
        String exteUrl = split[1].replaceAll("expandedURL=", "");
        if (exteUrl.contains("'")) {
            exteUrl = exteUrl.replace("'", "");
        }
        exteUrl = exteUrl.trim();

        return exteUrl;
    }

}
