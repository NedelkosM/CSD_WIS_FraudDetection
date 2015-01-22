/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package twitterprojectwithoutmaven;

import com.mongodb.DBCursor;
import java.util.ArrayList;

/**
 *
 * @author Chris
 */
public class Statistics {

    private ArrayList<TweetDist> list;
    ArrayList<TweetDist> dublist;

    public Statistics() {

    }

    public void StatisticsA() {

        //This is actually ready, its  the DBUser.
    }

    public void StatisticsB() {
        //for each stalked user
        DBCursor users = dbAdapter.getInstance().getStalkedUsers();
        while (users.hasNext()) {
            DBUser user = (DBUser) users.next();
            //get user's tweets

            long num_simple_tweets = 0;//number of user tweets
            long num_reTweets = 0;//number of retweets
            long num_replies = 0;//number of user's replies
            long num_mentions = 0;//total user's mentions
            long num_reTweets_recieved = 0;//total retweets user recieved
            long num_hashTag = 0;// total number of hashTags
            long num_Urls = 0;// total number of Ulrs contained at users tweets
            dublist = new ArrayList<TweetDist>();
            //for each user's tweet
            DBCursor userTweets = dbAdapter.getInstance().queryTweets("UserID", user.getID());
            while (userTweets.hasNext()) {
                DBTweet tweet = (DBTweet) userTweets.next();
                num_simple_tweets += tweet.isASimpleTweet();
                num_reTweets += tweet.getReTweets();
                num_replies += tweet.isAReply();
                num_mentions += tweet.getMentions();
                num_reTweets_recieved += tweet.isAreTweet();
                num_hashTag += tweet.getHasTags();
                num_Urls += tweet.getUrls();

                if (tweet.isASimpleTweet() == 1) {
                    //if it's a imple tweet find the tweets tha are same to this.
                    if (list == null) {
                        list = new ArrayList<TweetDist>();
                    }
                    list.clear();
                    findDublicates(tweet, user.getID());
                }

            }
            userTweets.close();

        }
        users.close();
    }

    private void findDublicates(DBTweet tweet, String id) {
        //for each 
        DBCursor tweets = dbAdapter.getInstance().queryTweets("UserId", id);
        int max = -1;
        while (tweets.hasNext()) {
            DBTweet tweet2 = (DBTweet) tweets.next();
            if (tweet.getID() == null ? tweet2.getID() != null : (!(tweet.getID().equals(tweet2.getID()) && tweet2.isASimpleTweet() == 1))) {

                int dist = distance(tweet.getText(), tweet2.getText());

                if (max < dist) {
                    max = dist;
                }

                list.add(new TweetDist(tweet.getID(), tweet2.getID(), dist));

            }

        }
        tweets.close();
        NormalizeAndCut(list, max);

    }

    public static int distance(String a, String b) {
        a = a.toLowerCase();
        b = b.toLowerCase();
        // i == 0
        int[] costs = new int[b.length() + 1];
        for (int j = 0; j < costs.length; j++) {
            costs[j] = j;
        }
        for (int i = 1; i <= a.length(); i++) {
            // j == 0; nw = lev(i - 1, j)
            costs[0] = i;
            int nw = i - 1;
            for (int j = 1; j <= b.length(); j++) {
                int cj = Math.min(1 + Math.min(costs[j], costs[j - 1]), a.charAt(i - 1) == b.charAt(j - 1) ? nw : nw + 1);
                nw = costs[j];
                costs[j] = cj;
            }
        }
        return costs[b.length()];
    }

    private void NormalizeAndCut(ArrayList<TweetDist> list, int max) {

        for (TweetDist list1 : list) {
            list1.normalize(max);
            TweetDist td = list1;
            if (td.getDist() >= 10) {
                dublist.add(td);
            }
        }

    }

}
