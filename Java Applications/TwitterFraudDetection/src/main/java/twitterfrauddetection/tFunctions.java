package twitterfrauddetection;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.Properties;
import java.util.logging.Level;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import twitter4j.*;
import twitter4j.auth.AccessToken;
import twitter4j.auth.OAuth2Token;
import twitter4j.auth.RequestToken;
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.json.DataObjectFactory;
/**
 * @author Miltos Nedelkos, nedelkosm at gmail com
 * gihub dot com/nedelkosm
 * nedelkos dot me
 * 
 * tFunctions class.
 * Implements all functions that are needed for twitter API calls.
 */
public final class tFunctions {
    /**
     * Configuration Builder for twitter connection. Used in both App-Only and User authentication.
     */
    private static ConfigurationBuilder builder;
    /**
     * Twitter object used for twitter connections. Used in both App-Only and User authentication and tFunctions. Must be unique and accessible to other classes.
     */
    public static Twitter twitter;
    /**
     * Twitter object used for twitter streaming API connections. Must be unique and accessible to other classes.
     */
    public static TwitterStream twitterStream;
    /**
     * Authendicates the application and creates an OAuth2Token.
     * @throws twitter4j.TwitterException Throws exception when a connection to twitter cannot be made or authendication was not successful.
     */
    public static void AppAuth() throws TwitterException, Exception{
        builder = new ConfigurationBuilder();
        builder.setApplicationOnlyAuthEnabled(true);
        builder.setJSONStoreEnabled(true);
        builder.setOAuthConsumerKey("consumerkey").setOAuthConsumerSecret("comsumerkeysecret");
        twitter = new TwitterFactory(builder.build()).getInstance();
        // exercise & verify
        OAuth2Token token = twitter.getOAuth2Token();
        assertEquals("bearer", token.getTokenType());

        Map<String, RateLimitStatus> rateLimitStatus = twitter.getRateLimitStatus("search");
        RateLimitStatus searchTweetsRateLimit = rateLimitStatus.get("/search/tweets");
        assertNotNull(searchTweetsRateLimit);
        assertEquals(searchTweetsRateLimit.getLimit(), 450);
    }
    /**
     * Authenticates the application and creates an OAuthToken. If access token is not already saved, it will ask user for authentication and save the token at twitter4j.properties.
     * @param args The same parameters from Main. Use args[1] for consumerKey and args[2] for consumerKeySecret.
     */
    public static void UserAuth(String[] args){
        /**
        * Properties file that saves tokens and other configuration settings.
        * Name of the file kept original to honor twitter4j developer, Yusuke Yamamoto, 2009
        */
        File file = new File("twitter4j.properties");
        Properties prop = new Properties();
        InputStream is = null;
        OutputStream os = null;
        try {
            // check if properties file exists
            if (file.exists()) {
                is = new FileInputStream(file);
                prop.load(is);
            }
            // if enough arguements are given to main. Will overwrite the properties file
            if (args.length < 3) {
                // if the arguements were not enough and the properties file was not found
                if (null == prop.getProperty("oauth.consumerKey")
                        && null == prop.getProperty("oauth.consumerSecret")) {
                    // consumer key/secret are not set in twitter4j.properties
                    System.out.println(
                            "Usage: java twitter4j.examples.oauth.GetAccessToken [consumer key] [consumer secret]");
                    // Exit with failure
                    System.exit(-1);
                }
            } else {
                // Get settings from main
                prop.setProperty("oauth.consumerKey", args[1]);
                prop.setProperty("oauth.consumerSecret", args[2]);
                os = new FileOutputStream("twitter4j.properties");
                // Store token to properties
                prop.store(os, "twitter4j.properties");
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
            if (os != null) {
                try {
                    os.close();
                } catch (IOException ignore) {
                }
            }
        }
        try {
            builder = new ConfigurationBuilder();
            builder.setJSONStoreEnabled(true);
            twitter = new TwitterFactory(builder.build()).getInstance();
            RequestToken requestToken;
            AccessToken accessToken = null;
            if(!prop.containsKey("oauth.accessToken")){
                requestToken = twitter.getOAuthRequestToken();
                System.out.println("Got request token.");
                System.out.println("Request token: " + requestToken.getToken());
                System.out.println("Request token secret: " + requestToken.getTokenSecret());

                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                while (null == accessToken) {
                    System.out.println("Open the following URL and grant access to your account:");
                    System.out.println(requestToken.getAuthorizationURL());
                    try {
                        Desktop.getDesktop().browse(new URI(requestToken.getAuthorizationURL()));
                    } catch (UnsupportedOperationException | IOException ignore) {
                    } catch (URISyntaxException e) {
                        throw new AssertionError(e);
                    }
                    System.out.print("Enter the PIN(if available) and hit enter after you granted access.[PIN]:");
                    String pin = br.readLine();
                    try {
                        if (pin.length() > 0) {
                            accessToken = twitter.getOAuthAccessToken(requestToken, pin);
                        } else {
                            accessToken = twitter.getOAuthAccessToken(requestToken);
                        }
                    } catch (TwitterException te) {
                        if (401 == te.getStatusCode()) {
                            System.out.println("Unable to get the access token.");
                        }
                    }
                }
            } else {
                accessToken = new AccessToken(prop.getProperty("oauth.accessToken"),prop.getProperty("oauth.accessTokenSecret"));
            }
            System.out.println("Got access token.");
            System.out.println("Access token: " + accessToken.getToken());
            System.out.println("Access token secret: " + accessToken.getTokenSecret());

            try {
                prop.setProperty("oauth.accessToken", accessToken.getToken());
                prop.setProperty("oauth.accessTokenSecret", accessToken.getTokenSecret());
                os = new FileOutputStream(file);
                prop.store(os, "twitter4j.properties");
                os.close();
            } catch (IOException ioe) {
                System.exit(-1);
            } finally {
                if (os != null) {
                    try {
                        os.close();
                    } catch (IOException ignore) {
                    }
                }
            }
            System.out.println("Successfully stored access token to " + file.getAbsolutePath() + ".");
            twitterStream = new TwitterStreamFactory().getInstance();
        } catch (TwitterException te) {
            System.out.println("Failed to get accessToken: " + te.getMessage());
            System.exit(-1);
        } catch (IOException ioe) {
            System.out.println("Failed to read the system input.");
            System.exit(-1);
        }
    }
    
    /**
     * Calls twitter API to retrieve top world trends.
     * @return Will return top 10 trends in a single string formated in JSON. Will return null if unsuccessful.
     */
    public static String getWorldTrends(){
        /* All available trends
        ResponseList<Location> locations = null;
        try {
            locations = twitter.getAvailableTrends();
        } catch (TwitterException ex) {
            java.util.logging.Logger.getLogger(tFunctions.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("Showing available trends");
        for (Location location : locations) {
            System.out.println(location.getName() + " (woeid:" + location.getWoeid() + ")");
        }
        */
        Trends trends = null;
        try {
            trends = twitter.getPlaceTrends(1);
        } catch (TwitterException ex) {
            java.util.logging.Logger.getLogger(tFunctions.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        String json = DataObjectFactory.getRawJSON(trends);
        dbAdapter.getInstance().insertTrend(trends);
        return json;
    }
}