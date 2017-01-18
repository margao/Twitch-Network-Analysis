package twitchNetworkAnalysis;

import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.*;

public class Main {
	final static String clientID = "jj8yi3c0dqxb632vuwx16fxbd4s741z";

	public static void main(String[] args) throws Exception {

		Scanner in = new Scanner(System.in);
		ArrayList<User> al = new ArrayList<User>();
//		System.out.println("Enter Twitch API Client ID: ");
//		String clientID = in.nextLine();
		System.out.println("Enter # of users to compare: ");
		String[] usernames = new String[Integer.parseInt(in.nextLine())];
		
		System.out.println("Enter Twitch.tv user ID: (Press return after each username)");
		for (int i = 0; i < usernames.length; i++){
			usernames[i] = in.nextLine();
			al.add(new User(clientID, usernames[i]));
		}
		in.close();
		
		for (int i = 0; i < usernames.length; i++){
			al.get(i).buildFollowers(clientID, usernames[i], 100);
		}
		
//		User[] bag = new User[7];
//
//		// Build follower data for each twich account
//		User skd = new User(clientID, "superkawaiidesu");
//		skd.buildFollowers(clientID, "superkawaiidesu", 100);
//		bag[0] = skd;
//		User lk = new User(clientID, "lordknight");
//		lk.buildFollowers(clientID, "lordknight", 100);
//		bag[1] = lk;
//		User hbox = new User(clientID, "hungrybox");
//		hbox.buildFollowers(clientID, "hungrybox", 100);
//		bag[2] = hbox;
//		User jiyuna = new User(clientID, "animeilluminati");
//		jiyuna.buildFollowers(clientID, "animeilluminati", 100);
//		bag[3] = jiyuna;
//		User cirnotv = new User(clientID, "cirno_tv");
//		cirnotv.buildFollowers(clientID, "cirno_tv", 100);
//		bag[4] = cirnotv;
//		User nairomk = new User(clientID, "nairomk");
//		nairomk.buildFollowers(clientID, "nairomk", 100);
//		bag[5] = nairomk;
//		User adrentv = new User(clientID, "adren_tv");
//		adrentv.buildFollowers(clientID, "adren_tv", 100);
//		bag[6] = adrentv;

		// Write to .gml file
		createGML(al);
	}

	// Debugging function. Not used.
	public static void writeFollowersID(Set<Integer> followers, String filename) throws Exception {
		PrintWriter writer = new PrintWriter(filename + ".txt", "UTF-8");
		for (int i : followers) {
			writer.println(i);
		}
		writer.close();
	}

	// Iterates through input user array and outputs all nodes and edges present
	// for user.
	public static void createGML(ArrayList<User> al) throws Exception {
		PrintWriter writer = new PrintWriter("twitch.gml", "UTF-8");
		String singleIndent = "     ";
		String doubleIndent = singleIndent + singleIndent;

		// Keep track of existing nodes written to file to prevent unnecessary
		// node duplicates.
		Set<Integer> existingNodes = new HashSet<Integer>();

		writer.println("graph [");
		writer.println(singleIndent + "directed 1");

		// Iterate over every element of input bag
		for (int i = 0; i < al.size(); i++) {
			// First we insert the parent node (the user who's followers we are
			// gathering)
			// Check to see if parent node was already inserted
			if (!existingNodes.contains(Integer.parseInt(al.get(i).userID))) {
				writer.println(singleIndent + "node [");
				writer.println(doubleIndent + "id " + al.get(i).userID);
				writer.println(doubleIndent + "label \"" + al.get(i).username + "\"");
				writer.println(singleIndent + "]");
				existingNodes.add(Integer.parseInt(al.get(i).userID));
			}

			// Get all IDs of followers
			Set<String> keySet = al.get(i).followersMap.keySet();
			// For each ID in the set, create a node corresponding to that ID
			for (String s : keySet) {
				if (!existingNodes.contains(Integer.parseInt(s))) {
					writer.println(singleIndent + "node [");
					writer.println(doubleIndent + "id " + s);
					writer.println(doubleIndent + "label \"" + al.get(i).followersMap.get(s) + "\"");
					writer.println(singleIndent + "]");
					existingNodes.add(Integer.parseInt(s));
				}
			}

			// For each ID in the set, create directed edge from that node to
			// the parent node
			for (String e : keySet) {
				writer.println(singleIndent + "edge [");
				writer.println(doubleIndent + "source " + e);
				writer.println(doubleIndent + "target " + al.get(i).userID);
				writer.println(doubleIndent + "weight 1");
				writer.println(singleIndent + "]");
			}
		}
		// End graph
		writer.println("]");
		writer.close();
	}

}