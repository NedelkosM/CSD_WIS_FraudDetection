/*
 * Twitter 4j library developed by Yusuke Yamamoto
 *
 */

package fraud.detection;

import java.util.Map;
import java.util.logging.Level;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import twitter4j.*;
import twitter4j.auth.OAuth2Token;
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.json.DataObjectFactory;


public final class tFunctions {
    private static ConfigurationBuilder builder;
    private static Twitter twitter;
    
    public static void AppAuth() throws TwitterException, Exception{
        builder = new ConfigurationBuilder();
        builder.setApplicationOnlyAuthEnabled(true);
        builder.setJSONStoreEnabled(true);
        
        builder.setOAuthConsumerKey("SJI7mwsTho3A4YzGpqtjIYOap").setOAuthConsumerSecret("FVqF7cYVY6kXhLSJgdhQq9fTw5S04uzaW8wNjK5K6ZHxT3oSE5");
        twitter = new TwitterFactory(builder.build()).getInstance();

        // exercise & verify
        OAuth2Token token = twitter.getOAuth2Token();
        assertEquals("bearer", token.getTokenType());

        Map<String, RateLimitStatus> rateLimitStatus = twitter.getRateLimitStatus("search");
        RateLimitStatus searchTweetsRateLimit = rateLimitStatus.get("/search/tweets");
        assertNotNull(searchTweetsRateLimit);
        assertEquals(searchTweetsRateLimit.getLimit(), 450);
    }
    
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
        return json;
    }
}