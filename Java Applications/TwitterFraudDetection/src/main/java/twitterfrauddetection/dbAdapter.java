/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package twitterfrauddetection;


import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
import com.mongodb.WriteConcern;
import java.net.UnknownHostException;
import twitter4j.Status;
import twitter4j.Trend;
import twitter4j.Trends;
import twitter4j.User;

/**
 *
 * @author efi
 */
public class dbAdapter {
    
    private final String host = "localhost";
    private final int port  = 27017;
    private final String databaseName  = "twitter";
    private final String trendID = "trend";
    private int trendNum = 1320;
    
    
    private MongoClient mongoClient;
    private DB db;
    private DBCollection TrendsColl;
    private DBCollection TweetsColl;
    private DBCollection UsersColl;
    private BasicDBObject status_json;
    private BasicDBObject trends_names;
    private BasicDBObject trends_json;
    private BasicDBObject user_json;

    
    private dbAdapter() {  
    }
    
    public static dbAdapter getInstance() {
        return dbAdapterHolder.INSTANCE;
    }
    
    private static class dbAdapterHolder {

        private static final dbAdapter INSTANCE = new dbAdapter();
    }
    
    /**
     * Create connection with the database and the collections
     * @throws UnknownHostException 
     */
    public void initialize() throws UnknownHostException
    {
       this.mongoClient = new MongoClient(host, port);
       
       this.db = this.mongoClient.getDB(databaseName);
       
       TrendsColl = db.getCollection("trends");
       TweetsColl = db.getCollection("tweets");
       UsersColl = db.getCollection("users");
    }
    
    /**
     * Close the connections.
     */
    public void closeConnections()
    {
        this.mongoClient.close();
    }
    
    /**
     * Creates a BasicDBObject for this trend and inserts it into the Trends 
     * collection.
     * @param trends 
     */
    public void insertTrend(Trends trends)
    {
        mongoClient.setWriteConcern(WriteConcern.JOURNALED);
        
        trends_names = new BasicDBObject();
        trends_json = new BasicDBObject();
        for(Trend t : trends.getTrends())
        {
            String field = trendID + trendNum;
            trends_names.append(field, t.getName());
            trendNum++;
            
            if(trendNum%50 == 0)
            {
                System.gc();
            }
        }
        trends_json.append("trends", trends_names);
        trends_json.append("as_of", trends.getAsOf());
        
        this.TrendsColl.insert(trends_json);
    }
    
    /**
     * Creates a BasicDBObject for this status and inserts it into the Tweets 
     * collection.
     * @param status 
     */
    public void insertTweet(Status status)
    {
        mongoClient.setWriteConcern(WriteConcern.JOURNALED);
        
        status_json = new BasicDBObject();
        
        status_json.append("ID", status.getId());
        status_json.append("Text", status.getText());
        status_json.append("UserID", status.getUser().getId());
        status_json.append("UserName", status.getUser().getName());
        status_json.append("created_at", status.getCreatedAt());
        
        this.TweetsColl.insert(status_json);
    }
    
    /**
     * Creates a BasicDBObject for this user and inserts it into the Users 
     * collection.
     * @param user 
     */
    public void insertUser(User user)
    {
        mongoClient.setWriteConcern(WriteConcern.JOURNALED);
        
        user_json = new BasicDBObject();
        
        user_json.append("ID", user.getId());
        user_json.append("UserName", user.getName());
        user_json.append("Friends", user.getFriendsCount());
        user_json.append("Followers", user.getFollowersCount());
        user_json.append("Description", user.getDescription());
        user_json.append("created_at", user.getCreatedAt());
        
        this.UsersColl.insert(user_json);
    }
    
    /**
     * Returns a cursor pointing to every entry in the Trends collection.
     * You can get every item calling the hasNext() method of the cursor.
     * Important: After you are done call cursor.close to close the connection.
     * @return 
     */
    public DBCursor getTrends()
    {
        DBCursor cursor = TrendsColl.find();
        return cursor;
    }
    
    /**
     * Returns a cursor pointing to every entry in the Tweets collection.
     * You can get every item calling the hasNext() method of the cursor.
     * Important: After you are done call cursor.close to close the connection.
     * @return 
     */
    public DBCursor getTweets()
    {
        DBCursor cursor = TweetsColl.find();
        return cursor;
    }
    
    /**
     * Returns a cursor pointing to every entry in the Users collection.
     * You can get every item calling the hasNext() method of the cursor.
     * Important: After you are done call cursor.close to close the connection.
     * @return 
     */
    public DBCursor getUsers()
    {
        DBCursor cursor = UsersColl.find();
        return cursor;
    }
    
    /**
     * Returns a cursor pointing to every entry in the Trends collection that 
     * matches the criteria given as parameters.
     * 
     * Available field options: ID, Text, UserID, UserName, created_at .
     * 
     * You can get every item calling the hasNext() method of the cursor.
     * 
     * IMPORTANT: After you are done call cursor.close to close the connection.
     * @param field
     * @param value
     * @return 
     */
    public DBCursor queryTrends(String field, String value)
    {
        BasicDBObject query = new BasicDBObject(field, value);

        DBCursor cursor = TrendsColl.find(query);
        return cursor;
    }
    
    /**
     * Returns a cursor pointing to every entry in the Tweets collection that 
     * matches the criteria given as parameters.
     * 
     * Available field options: trends, as_of .
     * 
     * You can get every item calling the hasNext() method of the cursor.
     * 
     * IMPORTANT: After you are done call cursor.close to close the connection.
     * @param field
     * @param value
     * @return 
     */
    public DBCursor queryTweets(String field, String value)
    {
        BasicDBObject query = new BasicDBObject(field, value);

        DBCursor cursor = TweetsColl.find(query);
        return cursor;
    }
    
    /**
     * Returns a cursor pointing to every entry in the Users collection that 
     * matches the criteria given as parameters.
     * 
     * Available field options: ID, UserName, Friends, Followers, Description, created_at .
     * 
     * You can get every item calling the hasNext() method of the cursor.
     * 
     * IMPORTANT: After you are done call cursor.close to close the connection.
     * @param field
     * @param value
     * @return 
     */
    public DBCursor queryUsers(String field, String value)
    {
        BasicDBObject query = new BasicDBObject(field, value);

        DBCursor cursor = UsersColl.find(query);
        return cursor;
    }
}
