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
    private ArrayList<TweetDist> dublist;

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
            DBUserStat stat = new DBUserStat(user.getID());

            dublist = new ArrayList<>();

            //for each user's tweet
            DBCursor userTweets = dbAdapter.getInstance().queryTweets("UserID", user.getID());

            while (userTweets.hasNext()) {

                DBTweet tweet = (DBTweet) userTweets.next();

                stat.setNum_simple_tweets(stat.getNum_simple_tweets() + tweet.isASimpleTweet());
                stat.setNum_reTweets(stat.getNum_reTweets() + tweet.isaReTweet());
                stat.setNum_replies(stat.getNum_replies() + tweet.isAReply());
                stat.setNum_mentions(stat.getNum_mentions() + tweet.getMentions());
                stat.setNum_reTweets_recieved(stat.getNum_reTweets_recieved() + tweet.getReTweets());
                stat.setNum_hashTag(stat.getNum_hashTag() + tweet.getHashTags());
                stat.setNum_Urls(stat.getNum_Urls() + tweet.getUrls());
                stat.AddSources(tweet.getSource());
                stat.AddUrls(tweet.getURLS());

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

            stat.setSameTweets(dublist);

            stat.CalculateStats();

            dbAdapter.getInstance().insertUserStats(stat.getDBObject());
        }
        users.close();
    }

    private void findDublicates(DBTweet tweet, String id) {
        //for each 
        DBCursor tweets = dbAdapter.getInstance().queryTweets("UserID", id);
        int max = -1;
        while (tweets.hasNext()) {
            DBTweet tweet2 = (DBTweet) tweets.next();
            if (tweet.getID() == null ? tweet2.getID() != null : (!(tweet.getID().equals(tweet2.getID()) && tweet2.isASimpleTweet() == 1))) {

                int dist = distance(tweet.getLevText(), tweet2.getLevText());

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
                if (!dublist.contains(td)) {
                    dublist.add(td);
                }
            }
        }

    }

}
