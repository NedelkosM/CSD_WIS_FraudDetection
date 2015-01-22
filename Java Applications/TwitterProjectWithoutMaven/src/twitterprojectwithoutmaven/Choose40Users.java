/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package twitterprojectwithoutmaven;

import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 *
 * @author kiki__000
 */
public class Choose40Users {
    
    private ArrayList<Integer> allUsers;
    private ArrayList<Integer> trendyTopics;
    private ArrayList<Integer>  frequenciesByUser;
    private ArrayList<Integer>  uniqueFrequencies;
    private ArrayList<Double> quartiles;
    private ArrayList<Integer> users40;
    //4 clusters: C1 <=Q1, C2 >Q1 AND <=Q2, C3 >Q2 AND <=Q3, C4 >Q3 
    private ArrayList<ArrayList<Integer>> clustersOfUsers; 
       
    
    public Choose40Users(){
       
        DBCursor temp;
        
        //take users
        temp = dbAdapter.getInstance().getUsers();
        while (temp.hasNext()){
                DBObject object = temp.next();
                DBUser user = new DBUser(object);
                allUsers.add(Integer.parseInt((user.getID())));  
        }
        
        //take trendy topics
        temp = dbAdapter.getInstance().getTrends();
        while (temp.hasNext()){
                DBObject object = temp.next();
                DBTrend trend = new DBTrend(object);
                trendyTopics.add(Integer.parseInt((trend.getID())));  
        }
        
        temp.close();
    }
    
    
     /**
     * find the frequencies
     */
    public void findFrequencies(){
        
        int sum,sizeU = allUsers.size(), sizeTd = trendyTopics.size();
        DBCursor temp = dbAdapter.getInstance().getTweets();
        
        for (int i=0; i<sizeU; i++){
            sum = 0;
            for (int j=0; j<sizeTd; j++){
                while (temp.hasNext()){
                        DBObject object = temp.next();
                        DBTweet tweet = new DBTweet(object);
                        
                      /** Ο δεύτερος ελεγχος που θέλω να κανει μετα το && είναι αν χρηστης allUsers.get(i)
                       * εχει κανει tweet στο trendyTopics.get(j)
                        
                       if (tweet.getText().contains(trendyTopics.get(j)) && ){
                                    sum++;
                                  
                        }
                      */
                }
            }
            frequenciesByUser.set(i, sum);
        }
        
        temp.close();
    }
    
    
    
    /**
     * sort ascending the frequencies
     */
    public void sortFrequencies(){
        
        int size = frequenciesByUser.size();
        
        //sort
        Collections.sort(frequenciesByUser);
        
        //delete the multiple and keep the unique frequencies
        for (int i=0; i<size; i++){
            if (uniqueFrequencies.contains(frequenciesByUser.get(i))){
                uniqueFrequencies.add(frequenciesByUser.get(i));
            }
        }
    }
    
    /**
     *find the quartiles
     */
    public void quartiles(){
        
        int sum1=0, sum2=0, count1=0, count2=0, size = uniqueFrequencies.size();
        double q1, q2, q3;
        
        //find q2
        if (size%2 == 0){
            q2 = (uniqueFrequencies.get(size/2) + uniqueFrequencies.get(1 + size/2 )) / 2.0;   
        }
        else{
            q2 = uniqueFrequencies.get(1 + size/2);
        }
        
        //find q1,q3
        for (int i=0; i<size; i++){
            if (uniqueFrequencies.get(i) < q2){
                sum1+= uniqueFrequencies.get(i);
                count1++;
            }
            else{
                sum2+= uniqueFrequencies.get(i);
                count2++;
            }           
        }
        
        q1 = sum1/count1;
        q3 = sum2/count2;
    
        quartiles.add(q1);
        quartiles.add(q2);
        quartiles.add(q3);
    
    }
    /**
     * take 10 users from each quartiles
     */
    public void find40Users(){
        
        int size = frequenciesByUser.size();
        Random rand = new Random();
        
        //find users for each cluster
        for (int i=0; i<size; i++){
            int temp = frequenciesByUser.get(i);
            if (temp <= quartiles.get(0)){
                clustersOfUsers.get(0).add(allUsers.get(i));
            }
            else if (temp > quartiles.get(0) && temp <= quartiles.get(1)){
                 clustersOfUsers.get(1).add(allUsers.get(i));
            }
            else if (temp > quartiles.get(1) && temp <= quartiles.get(2)){
                 clustersOfUsers.get(2).add(allUsers.get(i));
            }
            else{
                 clustersOfUsers.get(3).add(allUsers.get(i));
            }
        }
        
        //take random 10 users of each cluster and finally find the 40 Users
        for (int i=0; i< clustersOfUsers.size(); i++){
            if (clustersOfUsers.get(i).size() < 10){
                users40.addAll(clustersOfUsers.get(i));
            }
            else{
                for (int j=0; j<10; j++){
                        int  num = rand.nextInt(clustersOfUsers.get(i).size());
                        users40.add(clustersOfUsers.get(i).get(num)); 
                }
            }
        }  
        
    }
    
    /**
     * save 40 users in db
     */
    public void save40users(){
        
        int size = users40.size();
        DBCursor temp = null;
        
        for (int i=0; i<size; i++){
            String userToString = users40.get(i).toString();
            temp = dbAdapter.getInstance().queryUsers("ID", userToString);
            DBObject object = temp.next();
            DBUser user = new DBUser(object);
            dbAdapter.getInstance().insertStalkedUser(user);
        }
        
        temp.close();
    
    }
    
     /**
     *get the 40 users
     * @return users40
     */
    public ArrayList<Integer> qetUsers40(){
        
        return users40;
    }
    
     /**
     *get the quartiles
     * @return quartiles
     */
    public ArrayList<Double> qetQuartiles(){
        
        return quartiles;
    }
    
    /**
     *get the 4 clustersOfUsers
     * @return clustersOfUsers
     */
    public ArrayList<ArrayList<Integer>> qetClusters(){
        
        return clustersOfUsers;
    }
    
    
    /**
     * do all the job of part3 and take the 40 users
     *
     * @return users40
     */
    public ArrayList<Integer> doTheJob(){
        
        findFrequencies();
        sortFrequencies();
        quartiles();
        find40Users();
        save40users();
       
        return users40;
    }
    
    
    
   

    
}
