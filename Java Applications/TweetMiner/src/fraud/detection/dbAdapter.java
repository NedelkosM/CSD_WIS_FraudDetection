/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package fraud.detection;

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
import java.net.UnknownHostException;
import java.util.Arrays;

/**
 *
 * @author efi
 */
public class dbAdapter {
    
    private final String host = "localhost";
    private final int port  = 27017;
    private final String databaseName  = "twitter";
    private final String userName = "admin";
    private final char password[] = {'a','d','m','i','n'};
    private final String trendsColl = "trends";
    private final String tweetsColl = "tweets";
    private final String usersColl = "users";
    
    
    private final MongoClient mongoClient;
    private final DB db;
    private final MongoCredential credential = MongoCredential.createMongoCRCredential(userName, databaseName, password);
    
    private dbAdapter() {
    }
    
    public static dbAdapter getInstance() {
        return dbAdapterHolder.INSTANCE;
    }
    
    private static class dbAdapterHolder {

        private static final dbAdapter INSTANCE = new dbAdapter();
    }
    
    public dbAdapter() throws UnknownHostException
    {
       this.mongoClient = new MongoClient(new ServerAddress(), Arrays.asList(credential));
       
       this.db = this.mongoClient.getDB(databaseName);
    }
    
    public void closeConnections()
    {
        this.mongoClient.close();
    }
    
    public void insertTrend(String trend)
    {
        
    }
}
