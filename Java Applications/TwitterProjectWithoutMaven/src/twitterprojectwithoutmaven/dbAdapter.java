package twitterprojectwithoutmaven;


import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
import com.mongodb.WriteConcern;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.Properties;
import twitter4j.Status;
import twitter4j.Trend;
import twitter4j.Trends;
import twitter4j.User;

/**
 *
 * @author efi
 */
public class dbAdapter {
    
    private String host;
    private int port;
    private String databaseName;
    private final int trendID = 1;
    private int trendNum = 2740;
    
    
    private MongoClient mongoClient;
    private DB db;
    private DBCollection TrendsColl;
    private DBCollection TweetsColl;
    private DBCollection UsersColl;
    private DBCollection TrendColl;
    private DBCollection SelectedUsersColl;
    private BasicDBObject status_json;
    private BasicDBObject trends_names;
    private BasicDBObject trends_json;
    private BasicDBObject user_json;
    private BasicDBObject trend_json;

    /**
     * 
     * 
     * INITIALIZATION METHODS
     * 
     * 
    */
    
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
       File file = new File("mongo.properties");
       Properties prop = new Properties();
       InputStream is = null;
       
       try {
            // check if properties file exists
            if (file.exists()) {
                is = new FileInputStream(file);
                prop.load(is);
                this.host = prop.getProperty("hostname");
                this.port = Integer.parseInt(prop.getProperty("port"));
                this.databaseName = prop.getProperty("databaseName");
                
                this.mongoClient = new MongoClient(host, port);

                this.db = this.mongoClient.getDB(databaseName);

                TrendsColl = db.getCollection("trends");
                TweetsColl = db.getCollection("tweets");
                UsersColl = db.getCollection("users");
                TrendColl = db.getCollection("trend");
                SelectedUsersColl = db.getCollection("selectedUsers");
            }
        } catch (IOException ioe) {
            System.exit(-1);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ignore) {
                }
            }
        }
    }
    
    /**
     * Close the connections.
     */
    public void closeConnections()
    {
        this.mongoClient.close();
    }
    
   /**
     * 
     * 
     * INSERT METHODS
     * 
     * 
    */
    
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
            trend_json = new BasicDBObject();
            
            trend_json.append("ID", trendNum);
            trend_json.append("Name", t.getName());
            
            trends_names.append("ID", trendNum);
            trends_names.append("trend"+trendNum, t.getName());
            trendNum++;
            
            if(trendNum%50 == 0)
            {
                System.out.println("Garbage collector called.");
                System.gc();
            }
            this.TrendColl.insert(trend_json, new WriteConcern(0, 0, false, false, true));
        }
        trends_json.append("trends", trends_names);
        trends_json.append("as_of", trends.getAsOf());
        
        this.TrendsColl.insert(trends_json,new WriteConcern(0, 0, false, false, true));
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
        
        this.TweetsColl.insert(status_json,new WriteConcern(0, 0, false, false, true));
        this.insertUser(status.getUser());
    }
    
    /**
     * Creates a BasicDBObject for this status and inserts it into the  
     * collection of the User who tweeted it.
     * @param status 
     */
    public void insertUserTweet(Status status)
    {
        mongoClient.setWriteConcern(WriteConcern.JOURNALED);
        
        status_json = new BasicDBObject();
        
        status_json.append("ID", status.getId());
        status_json.append("Text", status.getText());
        status_json.append("UserID", status.getUser().getId());
        status_json.append("UserName", status.getUser().getName());
        status_json.append("created_at", status.getCreatedAt());
        
        String coll = "User" + status.getUser().getId();
        this.db.getCollection(coll).insert(status_json,new WriteConcern(0, 0, false, false, true));
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
        
        this.UsersColl.insert(user_json,new WriteConcern(0, 0, false, false, true));
    }
    
    /**
     * Creates a BasicDBObject for this selected user and inserts it into the 
     * selectedUsers collection.
     * @param user 
     */
    public void insertStalkedUser(DBUser user)
    {
        mongoClient.setWriteConcern(WriteConcern.JOURNALED);
        
        user_json = new BasicDBObject();
        
        user_json.append("ID", user.getID());
        user_json.append("UserName", user.getUserName());
        user_json.append("Friends", user.getFriends());
        user_json.append("Followers", user.getFollowers());
        user_json.append("Description", user.getDescription());
        user_json.append("created_at", user.getCreated_at());
        
        this.SelectedUsersColl.insert(user_json,new WriteConcern(0, 0, false, false, true));
    }
    
    /**
     * 
     * 
     * GET METHODS
     * 
     * 
    */
    
    /**
     * Returns a cursor pointing to every entry in the Trends collection.
     * You can get every item calling the hasNext() method of the cursor.
     * IMPORTANT: After you are done call cursor.close to close the connection.
     * @return 
     */
    public DBCursor getTrends()
    {
        DBCursor cursor = TrendColl.find();
        return cursor;
    }
    
    /**
     * Returns a cursor pointing to every entry in the Tweets collection.
     * You can get every item calling the hasNext() method of the cursor.
     * IMPORTANT: After you are done call cursor.close to close the connection.
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
     * IMPORTANT: After you are done call cursor.close to close the connection.
     * @return 
     */
    public DBCursor getUsers()
    {
        DBCursor cursor = UsersColl.find();
        return cursor;
    }
    
    /**
     * Returns a cursor pointing to every entry in the selectedUsers collection.
     * You can get every item calling the hasNext() method of the cursor.
     * IMPORTANT: After you are done call cursor.close to close the connection.
     * @return 
     */
    public DBCursor getStalkedUsers()
    {
        DBCursor cursor = SelectedUsersColl.find();
        return cursor;
    }
    
    /**
     * Returns a cursor pointing to every tweet of the user with this id.
     * The users are from the selectedUsers the program has stalked for a period of time.
     * The tweets are the ones retrieved during that period.
     * You can get every item calling the hasNext() method of the cursor.
     * IMPORTANT: After you are done call cursor.close to close the connection.
     * @param id
     * @return 
     */
    public DBCursor getStalkedUserTweets(int id)
    {
        String collection = "User"+id;
        DBCollection dbColl = db.getCollection(collection);
        DBCursor cursor = dbColl.find();
        return cursor;
    }
    
    /**
     * 
     * 
     * QUERY METHODS
     * 
     * 
    */
    
    /**
     * Returns a cursor pointing to every entry in the Trend collection that 
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
    public DBCursor queryTrends(String field, String value)
    {
        BasicDBObject query = new BasicDBObject(field, value);

        DBCursor cursor = TrendColl.find(query);
        return cursor;
    }
    
    /**
     * Returns a cursor pointing to every entry in the Tweets collection that 
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
    
    /**
     * Returns a cursor pointing to every entry in the selectedUsers collection that 
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
    public DBCursor queryStalkedUsers(String field, String value)
    {
        BasicDBObject query = new BasicDBObject(field, value);

        DBCursor cursor = SelectedUsersColl.find(query);
        return cursor;
    }
    
     /**
     * Returns a cursor pointing to every entry in the User<id> collection that 
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
    public DBCursor queryStalkedUsersTweets(String field, String value, int userID)
    {
        BasicDBObject query = new BasicDBObject(field, value);
        
        String collection = "User" + userID;
        DBCollection dbColl = db.getCollection(collection);
        
        DBCursor cursor = dbColl.find(query);
        return cursor;
    }
}
