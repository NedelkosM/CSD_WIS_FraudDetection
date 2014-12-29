/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package twitterfrauddetection;


import com.mongodb.BasicDBObject;
import com.mongodb.BulkWriteOperation;
import com.mongodb.BulkWriteResult;
import com.mongodb.Cursor;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ParallelScanOptions;
import com.mongodb.ServerAddress;
import com.mongodb.WriteConcern;
import java.net.UnknownHostException;
import java.util.Arrays;
import twitter4j.Status;
import twitter4j.Trend;
import twitter4j.Trends;

/**
 *
 * @author efi
 */
public class dbAdapter {
    
    private final String host = "localhost";
    private final int port  = 27017;
    private final String databaseName  = "twitter";
    private String trendID = "trend";
    private int trendNum = 1;
    
    
    private MongoClient mongoClient;
    private DB db;
    DBCollection TrendsColl;
    DBCollection TweetsColl;

    
    private dbAdapter() {  
    }
    
    public static dbAdapter getInstance() {
        return dbAdapterHolder.INSTANCE;
    }
    
    private static class dbAdapterHolder {

        private static final dbAdapter INSTANCE = new dbAdapter();
    }
    
    public void initialize() throws UnknownHostException
    {
       this.mongoClient = new MongoClient(host, port);
       
       this.db = this.mongoClient.getDB(databaseName);
       
       TrendsColl = db.getCollection("trends");
       TweetsColl = db.getCollection("tweets");
    }
    
    public void closeConnections()
    {
        this.mongoClient.close();
    }
    
    public void insertTrend(Trends trends)
    {
        mongoClient.setWriteConcern(WriteConcern.JOURNALED);
        
        BasicDBObject trends_names = new BasicDBObject();
        BasicDBObject trends_json = new BasicDBObject();
        for(Trend t : trends.getTrends())
        {
            String field = trendID + trendNum;
            trends_names.append(field, t.getName());
            trendNum++;
        }
        trends_json.append("trends", trends_names);
        trends_json.append("as_of", trends.getAsOf());
        
        //System.out.println(trends_json);
        this.TrendsColl.insert(trends_json);
    }
    
    public void insertTweet(Status status)
    {
        mongoClient.setWriteConcern(WriteConcern.JOURNALED);
        
        BasicDBObject status_json = new BasicDBObject();
        
        status_json.append("ID", status.getId());
        status_json.append("Text", status.getText());
        status_json.append("UserID", status.getUser().getId());
        status_json.append("UserName", status.getUser().getName());
        status_json.append("created_at", status.getCreatedAt());
        //System.out.println(status_json);
        this.TweetsColl.insert(status_json);
    }
}

