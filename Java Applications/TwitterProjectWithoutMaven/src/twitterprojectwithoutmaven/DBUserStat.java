/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package twitterprojectwithoutmaven;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Chris
 */
public class DBUserStat {

    public static Object[] header = new String[]{
        "UserID",
        "Simple Tweets",
        "User's Replies",
        "User's Retweets",
        "Retweets Recieved",
        "Mentions",
        "HashTags",
        "Tweets Containing HashTag",
        "Total Urls",
        "Unique Urls",
        "Unique Domains",
        "Tweets Containing Url",
        "Similar Tweets"};
    private static String User_ID="UserID";
    private static String simple_Tweets="Simple Tweets";
    private static String Users_retweets="Users's Retweets";
    private static String replies="User's Replies";
    private static String mentions="Mentions";
    private static String retweets_Recieved="Retweets Recieved";
    private static String total_Urls="Total Urls";
    private static String hash_Tags="Hashtags";
    private static String containing_HashTag="Tweets Containing Hashtags";
    private static String unique_Urls="Unique Urls";
    private static String unique_Domains="Unique Domains";
    private static String similar_Tweets="SimilarTweets";
    private static String sources_of_Tweets="Sources";
    private static String contains_Url="Contains Url";
    private static String Key="key";
    private static String Value="value";
    private static String Tweet1="tweet1";
    private static String Tweet2="tweet2";
    private static String Distance="distance";

    private long num_simple_tweets = 0;//number of user tweets
    private long num_reTweets = 0;//number of retweets
    private long num_replies = 0;//number of user's replies
    private long num_mentions = 0;//total user's mentions
    private long num_reTweets_recieved = 0;//total retweets user recieved
    private long num_hashTag = 0;// total number of hashTags
    private long num_Urls = 0;// total number of Ulrs contained at users tweets
    private int Unique_Urls=0;
    private int Unique_domains=0;
    private long containsHastag = 0;
    private long containsUrl = 0;

    private ArrayList<TweetDist> sameTweets;
    private final HashMap<String, Integer> Sources;
    private final HashSet<String> domains;
    private final HashSet<String> uniqueUrls;

    private String UserId;

    
    
    

    DBUserStat(String id) {
        UserId = id;
        Sources = new HashMap<>();
        domains = new HashSet<>();
        uniqueUrls = new HashSet<>();
        sameTweets = new ArrayList<>();
    }

    DBUserStat() {
        Sources = new HashMap<>();
        domains = new HashSet<>();
        uniqueUrls = new HashSet<>();
        sameTweets = new ArrayList<>();
    }

    DBUserStat(DBObject obj) {
        Sources = new HashMap<>();
        domains = new HashSet<>();
        uniqueUrls = new HashSet<>();
        sameTweets = new ArrayList<>();
        this.ReadDBObject2(obj,true);
    }
    
    
    
    public Object[] getExelRow() {
        Object[] row;
        row = new Object[]{
            this.getUserId(),
            this.getNum_simple_tweets(),
            this.getNum_replies(),
            this.getNum_reTweets(),
            this.getNum_reTweets_recieved(),
            this.getNum_mentions(),
            this.getNum_hashTag(),
            this.getContainsHastag(),
            this.getNum_Urls(),
            this.getUniqueUrls(),
            this.getDomains(),
            this.getContainsUrl(),
            this.sameTweets.size()
        };
        return row;
    }

    /**
     * @return the num_simple_tweets
     */
    public long getNum_simple_tweets() {
        return num_simple_tweets;
    }

    /**
     * @param num_simple_tweets the num_simple_tweets to set
     */
    public void setNum_simple_tweets(long num_simple_tweets) {
        this.num_simple_tweets = num_simple_tweets;
    }

    /**
     * @return the num_reTweets
     */
    public long getNum_reTweets() {
        return num_reTweets;
    }

    /**
     * @param num_reTweets the num_reTweets to set
     */
    public void setNum_reTweets(long num_reTweets) {
        this.num_reTweets = num_reTweets;
    }

    /**
     * @return the num_replies
     */
    public long getNum_replies() {
        return num_replies;
    }

    /**
     * @param num_replies the num_replies to set
     */
    public void setNum_replies(long num_replies) {
        this.num_replies = num_replies;
    }

    /**
     * @return the num_mentions
     */
    public long getNum_mentions() {
        return num_mentions;
    }

    /**
     * @param num_mentions the num_mentions to set
     */
    public void setNum_mentions(long num_mentions) {
        this.num_mentions = num_mentions;
    }

    /**
     * @return the num_reTweets_recieved
     */
    public long getNum_reTweets_recieved() {
        return num_reTweets_recieved;
    }

    /**
     * @param num_reTweets_recieved the num_reTweets_recieved to set
     */
    public void setNum_reTweets_recieved(long num_reTweets_recieved) {
        this.num_reTweets_recieved = num_reTweets_recieved;
    }

    /**
     * @return the num_hashTag
     */
    public long getNum_hashTag() {
        return num_hashTag;
    }

    /**
     * @param num_hashTag the num_hashTag to set
     */
    public void setNum_hashTag(long num_hashTag) {
        if (this.num_hashTag < num_hashTag) {
            this.setContainsHastag(this.getContainsHastag() + 1);
        }
        this.num_hashTag = num_hashTag;

    }

    /**
     * @return the num_Urls
     */
    public long getNum_Urls() {
        return num_Urls;
    }

    /**
     * @param num_Urls the num_Urls to set
     */
    public void setNum_Urls(long num_Urls) {
        if (this.num_Urls < num_Urls) {
            this.setContainsUrl(this.getContainsUrl() + 1);
        }
        this.num_Urls = num_Urls;
    }

    /**
     * @param sameTweets the sameTweets to set
     */
    public void setSameTweets(ArrayList<TweetDist> sameTweets) {
        this.sameTweets = sameTweets;
    }

    /**
     * @return the Sources
     */
    public ArrayList<String> getSources() {
        return new ArrayList(Sources.keySet());
    }
    
    public int getNumofTweetsofSource(String str){
        if(Sources.containsKey(str)){
            return Sources.get(str);
        }
        return 0;
    }

    /**
     *
     * @param src
     */
    public void AddSources(String src) {
        if (Sources.containsKey(src)) {
            Sources.put(src, Sources.get(src) + 1);
            return;
        }
        Sources.put(src, 1);

    }

    /**
     *
     * @param ur
     */
    public void AddUrls(ArrayList<String> ur) {

        for (String url : ur) {
            uniqueUrls.add(url);
            String str = getDomain(url);
            domains.add(str);
        }
        this.Unique_Urls=uniqueUrls.size();
        this.Unique_domains=domains.size();

    }

   

   
   

    public BasicDBObject getDBObject2(){
        
        BasicDBObject o = new BasicDBObject();
        o.append(DBUserStat.User_ID, getUserId());
        

        o.append(DBUserStat.simple_Tweets, this.num_simple_tweets);
        
        o.append(DBUserStat.Users_retweets, this.num_reTweets);
        o.append(DBUserStat.replies, this.num_replies);
        o.append(DBUserStat.mentions, this.num_mentions);
        o.append(DBUserStat.retweets_Recieved, this.num_reTweets_recieved);
        
        o.append(DBUserStat.hash_Tags, this.num_hashTag);
        o.append(DBUserStat.containing_HashTag, this.containsHastag);
        
        o.append(DBUserStat.total_Urls, this.num_Urls);
        o.append(DBUserStat.unique_Domains, this.Unique_domains);
        o.append(DBUserStat.unique_Urls, this.Unique_Urls);
        o.append(DBUserStat.contains_Url, this.containsUrl);
        
        
        
        
        BasicDBList sourceList = new BasicDBList();

        Set<String> keySet = Sources.keySet();
        Iterator<String> iterator = keySet.iterator();
        while (iterator.hasNext()) {
            String str = iterator.next();
            int value = Sources.get(str);

            BasicDBObject lo = new BasicDBObject();
            lo.append(DBUserStat.Key, str);
            lo.append(DBUserStat.Value, value);
            sourceList.add(lo);
        }

        o.append(DBUserStat.sources_of_Tweets, sourceList);

        BasicDBList sameList = new BasicDBList();

        for (int i = 0; i < sameTweets.size(); i++) {
            BasicDBObject t = new BasicDBObject();
            String t1, t2;
            int dist;
            t1 = sameTweets.get(i).getTweet1();
            t2 = sameTweets.get(i).getTweet2();
            dist = (int) sameTweets.get(i).getDist();

            t.append(DBUserStat.Tweet1, t1);
            t.append(DBUserStat.Tweet2, t2);
            t.append(DBUserStat.Distance, dist);

            sameList.add(t);
        }

        o.append(DBUserStat.similar_Tweets, sameList);
        return o;
    }
    
   
    
    private void ReadDBObject2(DBObject obj,boolean b) {
        
        setUserId((String) obj.get(DBUserStat.User_ID));
        

        this.num_simple_tweets = (long) obj.get(DBUserStat.simple_Tweets);
        this.num_reTweets = (long) obj.get(DBUserStat.Users_retweets);
        this.num_replies = (long) obj.get(DBUserStat.replies);
        this.num_mentions = (long) obj.get(DBUserStat.mentions);
        this.num_reTweets_recieved = (long) obj.get(DBUserStat.retweets_Recieved);
        
        this.num_hashTag= (long) obj.get(DBUserStat.hash_Tags);
        this.containsHastag= (long) obj.get(DBUserStat.containing_HashTag);
        
         this.num_Urls=(long) obj.get(DBUserStat.total_Urls);
         this.Unique_domains=(int) obj.get(DBUserStat.unique_Domains);
         this.Unique_Urls=(int) obj.get(DBUserStat.unique_Urls);
         this.containsUrl=(long) obj.get(DBUserStat.contains_Url);
         
        
       
        
        BasicDBList list = (BasicDBList) obj.get(DBUserStat.sources_of_Tweets);
        for (Object list1 : list) {
            BasicDBObject o = (BasicDBObject) list1;
            String str = (String) o.get(DBUserStat.Key);
            Integer value = (Integer) o.get(DBUserStat.Value);
            Sources.put(str, value);
        }

        BasicDBList list2 = (BasicDBList) obj.get(DBUserStat.similar_Tweets);
        for (Object list1 : list2) {
            BasicDBObject o = (BasicDBObject) list1;
            String str1 = (String) o.get(DBUserStat.Tweet1);
            String str2 = (String) o.get(DBUserStat.Tweet2);
            int dist = (int) o.get(DBUserStat.Distance);
            TweetDist tweetDist = new TweetDist(str1, str2, dist);
            sameTweets.add(tweetDist);
        }

    }

    private String getDomain(String url) {
        URI uri = null;
        try {
            uri = new URI(url);
        } catch (URISyntaxException ex) {
            Logger.getLogger(DBUserStat.class.getName()).log(Level.SEVERE, null, ex);
        }
        String domain = uri.getHost();
        return domain.startsWith("www.") ? domain.substring(4) : domain;
    }

    /**
     * @return the containsHastag
     */
    public long getContainsHastag() {
        return containsHastag;
    }

    /**
     * @param containsHastag the containsHastag to set
     */
    public void setContainsHastag(long containsHastag) {
        this.containsHastag = containsHastag;
    }

    /**
     * @return the containsUrl
     */
    public long getContainsUrl() {
        return containsUrl;
    }

    /**
     * @param containsUrl the containsUrl to set
     */
    public void setContainsUrl(long containsUrl) {
        this.containsUrl = containsUrl;
    }

    /**
     * @return the domains
     */
    public int getDomains() {
        
        return this.Unique_domains;
    }

    
    public int getUniqueUrls() {
       
        return this.Unique_Urls;
    }

    /**
     * @return the UserId
     */
    public String getUserId() {
        return UserId;
    }

    /**
     * @param UserId the UserId to set
     */
    public void setUserId(String UserId) {
        this.UserId = UserId;
    }

    

    
}