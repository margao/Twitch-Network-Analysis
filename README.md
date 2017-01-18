# Twitch Network Analysis
This program analyzes multiple twitch.tv channels and collects follower/follows data. Data is then processed and analyzed using Jaccardian and Cosine similarity measures to compute channel similarity. Graphs can be visualized by importing the .GML file to the graph analysis software of your choice. Examples shown below were taken via Gephy. 

## Examples
![Example 1](/Example1.png?raw=true)
![Example 2](/Example2.png?raw=true)

### Pseudo-code

Twitch API is comprised of two parts: the REST API and a JavaScript SDK. Using the API, we sent requests via the base URL https://api.twitch.tv/kraken and with our own client-ID authentication to ensure that data we send and receive is not rate limited. The basic pseudocode for our mining algorithm is as follows:
```
Prompt user input for # of channels to compare
Initialize usernames array of size #
FOR i = 0 to #
	usernames[i] = next line of input
	Initialize User object with usernames[i]
	Add User to array list
END FOR

FOR i = 0 to #
	Build follower data for usernames[i]
END FOR
Write channels and follower nodes to .GML file
Write directed edges pointing from follower to respective channels to .GML file
```
