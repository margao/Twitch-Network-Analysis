package twitchNetworkAnalysis;

import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;

import org.json.JSONObject;
import org.json.JSONTokener;

public class User {
	int numFollows;
	int numFollowers;
	String userID;
	String username;
	// Hashmaps correspond to <String user_id, String username>
	Map<String, String> followsMap = new HashMap<String, String>(); // who this user follows
	Map<String, String> followersMap = new HashMap<String, String>(); // who follows this user

	// Constructor
	public User(String clientID, String user) throws Exception {
		userID = fetchUserIDandUsername(clientID, user)[0];
		username = fetchUserIDandUsername(clientID, user)[1];
	}

	//
	public void buildFollows(String clientID, String user, int limit) throws Exception {
		JSONObject data = fetchFollowsData(clientID, user, limit);
		numFollows = data.getInt("_total"); // get total number of follows
		
		System.out.println("Building follows for: " + user);
		
		// Add follows to hashmap until hashmap size matches numFollows size
		// HOWEVER, during runtime users may unfollow channels and thus dynamically change the true size of numFollows
		// To account for this we make sure that the current page of follows is NOT empty
		// If it is empty, this means we have reached the end of the list and we terminate the while loop
		// To reflect this possible change in numFollows we update its value to what our current followsMap.size() is.
		while (followsMap.size() < numFollows && !data.get("follows").toString().equals("[]")) {
			System.out.println("Progress: " + followsMap.size() + "/" + numFollows);
			
			for (int i = 0; i < data.getJSONArray("follows").length(); i++) {
				String username = data.getJSONArray("follows").getJSONObject(i).getJSONObject("channel").get("name")
						.toString();
				String id = data.getJSONArray("follows").getJSONObject(i).getJSONObject("channel").get("_id").toString();
				followsMap.put(id, username);
			}
			if (followsMap.size() < numFollows) {
				data = fetchNextPage(clientID, data);
			}
		}
		numFollows = followsMap.size();
	}

	public void buildFollowers(String clientID, String user, int limit) throws Exception {
		JSONObject data = fetchFollowersData(clientID, user, limit);
		numFollowers = data.getInt("_total");
		
		System.out.println("Building followers for: " + user);
		
		// Add follows to hashmap until hashmap size matches numFollowers size
		// HOWEVER, during runtime users may unfollow channels and thus dynamically change the true size of numFollowers
		// To account for this we make sure that the current page of follows is NOT empty
		// If it is empty, this means we have reached the end of the list and we terminate the while loop
		// To reflect this possible change in numFollows we update its value to what our current followersMap.size() is.
		while (followersMap.size() < numFollowers && !data.get("follows").toString().equals("[]")) {
			System.out.println("Progress: " + followersMap.size() + "/" + numFollowers);
			
			for (int i = 0; i < data.getJSONArray("follows").length(); i++) {
				String username = data.getJSONArray("follows").getJSONObject(i).getJSONObject("user").get("name")
						.toString();
				String id = data.getJSONArray("follows").getJSONObject(i).getJSONObject("user").get("_id").toString();
				followersMap.put(id, username);
			}
			if (followersMap.size() < numFollowers) {
				data = fetchNextPage(clientID, data);
			}
			
		}
		numFollows = followersMap.size();
		System.out.println("Progress: " + followersMap.size() + "/" + numFollowers);
	}
	
	// Returns next page JSON Object, given the previous page
	public JSONObject fetchNextPage(String clientID, JSONObject prev) throws Exception {
		Thread.sleep(200); // As this function is called many times for each user we pause execution slightly to alleviate load on Twitch API
		StringBuilder buildURL = new StringBuilder(prev.getJSONObject("_links").get("next").toString());
		buildURL.append("&client_id=" + clientID);
		URL url = new URL(buildURL.toString());
		return inputStreamToJSON(url);
	}
	
	// Helper function that parses text from URL and returns text as JSONObject
	public JSONObject inputStreamToJSON(URL url) throws Exception{
		InputStreamReader reader;
		reader = new InputStreamReader(url.openStream());
		JSONTokener tokener = new JSONTokener(reader);
		JSONObject data = new JSONObject(tokener);
		reader.close();
		return data;
	}
	
	// Helper function to construct appropriate URL syntax for user's follows JSON Object
	public JSONObject fetchFollowsData(String clientID, String user, int limit) throws Exception {
		StringBuilder buildURL = new StringBuilder("https://api.twitch.tv/kraken/users/");
		buildURL.append(user + "/follows/channels/?client_id=" + clientID + "&limit=" + Integer.toString(limit));
		URL url = new URL(buildURL.toString());
		return inputStreamToJSON(url);
	}

	// Helper function to construct appropriate URL syntax for user's followers JSON Object
	public JSONObject fetchFollowersData(String clientID, String user, int limit) throws Exception {
		StringBuilder buildURL = new StringBuilder("https://api.twitch.tv/kraken/channels/");
		buildURL.append(user + "/follows/?client_id=" + clientID + "&limit=" + Integer.toString(limit));
		URL url = new URL(buildURL.toString());
		return inputStreamToJSON(url);
	}
	
	// Fetch this user's user_ID and username 
	public String[] fetchUserIDandUsername(String clientID, String user) throws Exception {
		StringBuilder buildURL = new StringBuilder("https://api.twitch.tv/kraken/channels/");
		buildURL.append(user + "?client_id=" + clientID);
		URL url = new URL(buildURL.toString());
		String[] output = { inputStreamToJSON(url).get("_id").toString(), inputStreamToJSON(url).get("name").toString() };
		return output;
	}

}