/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package fraud.detection;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.HttpsURLConnection;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.omg.CORBA.NameValuePair;
import org.omg.CORBA.portable.ResponseHandler;
import sun.net.www.http.HttpClient;

/**
 *
 * @author Miltos
 */
public class tFunctions {
    private static String bearerToken;
    public static void updateBearerToken(){
        String url = "https://api.twitter.com/oauth2/token";
        try {
            bearerToken = requestBearerToken(url);
        } catch (IOException ex) {
            Logger.getLogger(tFunctions.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    // Encodes the consumer key and secret to create the basic authorization key
    private static String encodeKeys(String consumerKey, String consumerSecret) {
            try {
                    String encodedConsumerKey = URLEncoder.encode(consumerKey, "UTF-8");
                    String encodedConsumerSecret = URLEncoder.encode(consumerSecret, "UTF-8");

                    String fullKey = encodedConsumerKey + ":" + encodedConsumerSecret;
                    return fullKey;  
            }
            catch (UnsupportedEncodingException e) {
                    return new String();
            }
    }
    // Constructs the request for requesting a bearer token and returns that token as a string
    public static String requestBearerToken(String endPointUrl) throws IOException {
            HttpsURLConnection connection = null;
            String encodedCredentials = encodeKeys("1qGSkkPnP04cus9Az0GVC2vGD","Re7SSSv3En2tRVH7U9rOUtfAKuB8jBsmtIetE3lemZgEBs6uRJ");
           
            try {
                    URL url = new URL(endPointUrl); 
                    connection = (HttpsURLConnection) url.openConnection();
                    connection.setDoOutput(true);
                    connection.setDoInput(true);
                    connection.setRequestMethod("POST"); 
                    connection.setRequestProperty("Host", "api.twitter.com");
                    connection.setRequestProperty("User-Agent", "Fraud Detector 2014");
                    connection.setRequestProperty("Authorization", "Bearer " + encodedCredentials);
                    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8"); 
                    connection.setRequestProperty("Content-Length", "29");
                    connection.setRequestProperty("Accept-Encoding", "gzip");
                    connection.setUseCaches(false);
                    
                    writeRequest(connection, "grant_type=client_credentials");
                    String response = readResponse(connection);
                    System.out.println("Server Response: "+response);
                    // Parse the JSON response into a JSON mapped object to fetch fields from.
                    JSONObject obj = (JSONObject)JSONValue.parse(response);
                    if (obj != null) {
                            String tokenType = (String)obj.get("token_type");
                            String token = (String)obj.get("access_token");

                            return ((tokenType.equals("bearer")) && (token != null)) ? token : "";
                    }
                    return new String();
            }
            catch (MalformedURLException e) {
                    throw new IOException("Invalid endpoint URL specified.", e);
            }
            finally {
                    if (connection != null) {
                            connection.disconnect();
                    }
            }
    }
    // Fetches the first tweet from a given user's timeline
    public static String fetchTrends(String endPointUrl) throws IOException {
	HttpsURLConnection connection = null;
				
	try {
		URL url = new URL(endPointUrl); 
		connection = (HttpsURLConnection) url.openConnection();           
		connection.setDoOutput(true);
		connection.setDoInput(true); 
		connection.setRequestMethod("GET"); 
		connection.setRequestProperty("Host", "api.twitter.com");
		connection.setRequestProperty("User-Agent", "Fraud Detector 2014");
		connection.setRequestProperty("Authorization", "Bearer " + bearerToken);
		connection.setUseCaches(false);
			
			
		// Parse the JSON response into a JSON mapped object to fetch fields from.
		JSONArray obj = (JSONArray)JSONValue.parse(readResponse(connection));
			
		if (obj != null) {
			String tweet = ((JSONObject)obj.get(0)).get("text").toString();

			return (tweet != null) ? tweet : "";
		}
		return new String();
	}
	catch (MalformedURLException e) {
		throw new IOException("Invalid endpoint URL specified.", e);
	}
	finally {
		if (connection != null) {
			connection.disconnect();
		}
	}
    }
    // Writes a request to a connection
    private static boolean writeRequest(HttpsURLConnection connection, String textBody) {
            try {
                    BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
                    wr.write(textBody);
                    wr.flush();
                    wr.close();

                    return true;
            }
            catch (IOException e) { return false; }
    }
	
	
// Reads a response for a given connection and returns it as a string.
private static String readResponse(HttpsURLConnection connection) {
	try {
		StringBuilder str = new StringBuilder();
			
		BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String line = "";
		while((line = br.readLine()) != null) {
			str.append(line).append(System.getProperty("line.separator"));
		}
		return str.toString();
	}
	catch (IOException e) { return new String(); }
}
}
