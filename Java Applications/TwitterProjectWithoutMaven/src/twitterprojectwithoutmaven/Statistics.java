/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package twitterprojectwithoutmaven;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

/**
 *
 * @author Chris
 */
public class Statistics {

    private ArrayList<TweetDist> list;
    private ArrayList<TweetDist> dublist;
    public boolean testMode = false;
    public int MaxTweets = 100;
    private int lastrow = 0;
    private final String fileName = "UserStats.xls";
    private final String sheet1Name = "allUsers";
    private final String sheet2Name = "Stalked users";
    private final String sheet3Name = "Sources";
    private int start;

    public Statistics(boolean test, int startFrom) {
        testMode = test;

        this.start = startFrom;
        if (start == 0) {
            this.CreateFile(fileName);
            this.writeRow(fileName, 1, lastrow++, DBUserStat.header);
        }
    }

    public void StatisticsA() {

        //This is actually ready, its  the DBUser.
    }

    public void StatisticsB() {
        //for each stalked user
        DBCursor users = dbAdapter.getInstance().getStalkedUsers();
        System.out.println("users: " + users.size());

        int c = 0;//userCounter
        while (c < start && users.hasNext()) {
            users.next();
            lastrow++;
            c++;//Start analyzing from start User (start 0:users.count)
        }

        while (users.hasNext()) {
            DBObject obj = (DBObject) users.next();
            DBUser user = new DBUser(obj);// Get User obj
            //get user's tweets
            DBUserStat stat = new DBUserStat(user.getID());

            dublist = new ArrayList<>();// simmilar tweets per tweet

            //for each user's tweet
            DBCursor userTweets = dbAdapter.getInstance().getStalkedUserTweets(user.getID());
            System.out.println("user: " + user.getID() + " tweets:" + userTweets.size());
            int count = 0, count2 = 0;// test Counter, and tweet counter
            while (userTweets.hasNext()) {

                DBObject next = userTweets.next();
                DBTweet tweet = new DBTweet(next);

                if (testMode) {
                    //if(tweet.getUrls()>0){
                    //System.out.println("sText: " + tweet.getText());
                    //System.out.println("lText: " + tweet.getLevText());
                    //System.out.println();
                    //}

                    if (count >= MaxTweets) {
                        break;
                    }
                    count++;
                }
                //make get data from the tweet and add it to userStats
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
                    //if it's a imple tweet find the tweets tha are similar to this.
                    if (list == null) {
                        list = new ArrayList<>();
                    }

                    list.clear();
                    findDublicates(tweet, user.getID(), count2);
                }
                count2++;

            }
            userTweets.close();

            stat.setSameTweets(dublist);
            stat.CalculateStats();
            
            BasicDBObject dbObject = stat.getDBObject();
            dbAdapter.getInstance().insertUserStats(dbObject);
            
            this.writeRow(fileName, 1, lastrow++, stat.getExelRow());

        }
        users.close();
    }

    private void findDublicates(DBTweet tweet, String id, int c) {
        
        if(testMode){
            return;
        }
        
        //for each  user's tweet
        DBCursor tweets = dbAdapter.getInstance().getStalkedUserTweets(id);
        int max = -1;
        int count = 0;
        while (tweets.hasNext()) {

            if (count >= c) {
                break;
            }

            DBObject obj = tweets.next();
            DBTweet tweet2 = new DBTweet(obj);

            if (tweet.getID() == null ? tweet2.getID() != null : (!(tweet.getID().equals(tweet2.getID()) && tweet2.isASimpleTweet() == 1))) {

                int dist = distance(tweet.getLevText(), tweet2.getLevText());
                //distance bettwen tweets
                if (max < dist) {
                    max = dist;//max distance
                }
                
                list.add(new TweetDist(tweet.getID(), tweet2.getID(), dist));

            }

            count++;
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
            list1.normalize(max);// based on max distance each distance forim this tweet is now %

            if (list1.getDist() <= 10) {//get add to final list tweets that the distance between them is lower than 10/100
                if (!dublist.contains(list1)) {
                    dublist.add(list1);
                }
            }
        }

    }

    private void CreateFile(String name) {
        try {
            Workbook wb = new HSSFWorkbook();  // or new XSSFWorkbook();
            Sheet sheet1 = wb.createSheet(sheet1Name);//create sheets
            Sheet sheet2 = wb.createSheet(sheet2Name);
            Sheet sheet3 = wb.createSheet(sheet3Name);

            FileOutputStream fileOut = null;
            try {
                fileOut = new FileOutputStream(name); //save file
            } catch (FileNotFoundException ex) {
                Logger.getLogger(Statistics.class.getName()).log(Level.SEVERE, null, ex);
            }
            wb.write(fileOut);
            wb.close();

        } catch (IOException ex) {
            Logger.getLogger(Statistics.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        

    }

    private void writeRow(String name, int sh, int rowCounter, Object[] obj) {
        InputStream inp = null;
        try {
            inp = new FileInputStream(name);//load file
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Statistics.class.getName()).log(Level.SEVERE, null, ex);
        }

        Workbook wb = null;
        try {
            wb = WorkbookFactory.create(inp);
        } catch (IOException | InvalidFormatException ex) {
            Logger.getLogger(Statistics.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        Sheet sheet = wb.getSheetAt(sh);//load sheet
        if (sheet == null) {
            return;
        }
        Row row = sheet.getRow(rowCounter);//create row at line rowCounter
        if (row == null) {
            row = sheet.createRow((short) rowCounter);
        }
        int col = 0;
        for (Object o : obj) {//write row

            Cell cell = row.createCell(col);
            col++;
            if (o instanceof String) {
                cell.setCellValue((String) o);
            } else if (o instanceof Integer) {
                cell.setCellValue((Integer) o);
            } else if (o instanceof Double) {
                cell.setCellValue((double) o);
            } else if (o instanceof Long) {
                cell.setCellValue((long) o);
            } else {
                cell.setCellValue((double) o);
            }
        }

        FileOutputStream fileOut = null;
        try {
            fileOut = new FileOutputStream(name);//save file
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Statistics.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            wb.write(fileOut);
        } catch (IOException ex) {
            Logger.getLogger(Statistics.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            fileOut.close();//close file
        } catch (IOException ex) {
            Logger.getLogger(Statistics.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void WriteSources(String name, int sh) {
        
        DBCursor cursor = dbAdapter.getInstance().getAllStalkedUserStats();
        HashSet<String> sources = new HashSet<>();
        //Get all unique  tweet's sources from all users
        while (cursor.hasNext()) {
            DBObject next = cursor.next();

            DBUserStat stat = new DBUserStat(next);
            ArrayList<String> s = stat.getSources();
            for (String str : s) {
                
                sources.add(str);
            }

        }
        cursor.close();
        
        Object[] header = new Object[sources.size()+1];
        Iterator<String> iterator = sources.iterator();
        header[0]="User ID";
        int h = 1;
        while (iterator.hasNext()) {

            String[] split= iterator.next().split("\"");
            header[h] = split[1];
            h++;
        }
        //header created
        
        
        int TheRow=0;
        this.writeRow(name, sh, TheRow++, header);//wite header to file
        cursor = dbAdapter.getInstance().getAllStalkedUserStats();

        while (cursor.hasNext()) {

            DBObject next = cursor.next();

            DBUserStat stat = new DBUserStat(next);
            
            Object[] row = new Object[sources.size()+1];
            
            row[0]=stat.getUserId();
            //wite for each user a row with the number of tweets per source 
            int i=1;
            for (String s: sources) {
                row[i] = stat.getNumofTweetsofSource((String) s);
                i++;
            }
            this.writeRow(name, sh, TheRow++, row);
           
        }
        cursor.close();

    }

    private void WriteUserStats(String name, int sh) {
        //write for alla stalked user their stats in a file
        DBCursor cursor = dbAdapter.getInstance().getAllStalkedUserStats();
        
        int row=0;
        
        this.writeRow(name, sh, row++, DBUserStat.header);

        while (cursor.hasNext()) {
            DBObject next = cursor.next();
            DBUserStat stat = new DBUserStat(next);
            
            this.writeRow(name, sh, row++, stat.getExelRow());
        }
        cursor.close();
    }

    public void ExtractXLS(String name) {
        this.CreateFile(name);
        this.WriteUserStats(name, 1);
        this.WriteSources(name, 2);
    }
}
