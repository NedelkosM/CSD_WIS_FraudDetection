/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package twitterprojectwithoutmaven;

import com.mongodb.BasicDBObject;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Chris
 */
public class DBUserStat {

    private long num_simple_tweets = 0;//number of user tweets
    private long num_reTweets = 0;//number of retweets
    private long num_replies = 0;//number of user's replies
    private long num_mentions = 0;//total user's mentions
    private long num_reTweets_recieved = 0;//total retweets user recieved
    private long num_hashTag = 0;// total number of hashTags
    private long num_Urls = 0;// total number of Ulrs contained at users tweets

    private long containsHastag = 0;
    private long containsUrl = 0;

    private ArrayList<TweetDist> sameTweets;
    private final HashMap<String, Integer> Sources;
    private final HashSet<String> domains;
    private final HashSet<String> uniqueUrls;

    private final String UserId;

    private double urlsPerCent;
    private double hashatagPerCent;
    private double avgHashTags;
    private double avgRetweets;
    private double urls;
    private double Uniquedomains;

    DBUserStat(String id) {
        UserId = id;
        Sources = new HashMap<>();
        domains = new HashSet<>();
        uniqueUrls = new HashSet<>();
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
            this.containsHastag++;
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
            this.containsUrl++;
        }
        this.num_Urls = num_Urls;
    }

    /**
     * @return the sameTweets
     */
    public ArrayList<TweetDist> getSameTweets() {
        return sameTweets;
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

    public void AddSources(String src) {
        if (Sources.containsKey(src)) {
            Sources.replace(src, Sources.get(src) + 1);
            return;
        }
        Sources.put(src, 1);

    }

    public void AddUrls(ArrayList<String> ur) {

        for (int i = 0; i < ur.size(); i++) {
            uniqueUrls.add(ur.get(i));
            String str = getDomain(ur.get(i));
            domains.add(str);
        }

    }

    public void CalculateStats() {
        avgRetweets = 1.0 * this.getNum_reTweets_recieved() / this.getNum_simple_tweets();
        avgHashTags = 1.0 * this.getNum_hashTag() / this.getNum_simple_tweets();
        hashatagPerCent = (1.0 * this.containsHastag / this.getNum_simple_tweets()) * 100;
        urlsPerCent = (1.0 * this.containsUrl / this.num_simple_tweets) * 100;
        urls = 1.0 * uniqueUrls.size() / this.getNum_Urls();
        Uniquedomains = 1.0 * domains.size() / this.getNum_Urls();
    }

    public BasicDBObject getDBObject() {

        //needs implementation!
        return null;

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

}
