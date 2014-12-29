nedelkosm (at) gmail (dot) com
github project : https://github.com/NedelkosM/CSD_WIS_FraudDetection

Developers : Kaltirimidou Efi, Nedelkos Miltos, Paniskaki Kiki, Papazoglou Chris

Language(s) : Java, JSON

Date: 2014, Nov

Usage : Assignment for course "Web Information Systems", Computer Science Department, Aristotle University of Thessaloniki

Development Tools : Java IDE NetBeans, OS Linux, Data Gathering VMs

Description :

The assignment was to create an application that detected spam accounts in Twitter.

It was developed in five main stages:


1) Create a Java application that will run for 3 days on a VM, gathering data from twitter.

2) Create a MongoDB that will load the data gathered in step 1, in JSON format.

3) Observe certain suspicious accounts for patterned actions.

4) Detect certain characteristics in those accounts.

5) Statistical analysis and presentation

>>>> (TweetMiner.jar implements stage 1) <<<<<

arguments[2]=
arguments[0] - relative path (without \ at the end)
arguments[1] - consumerKey (will overwrite twitter4j.properties)
arguments[2] - consumerKeySecret () (will overwrite twitter4j.properties)

Example run : java -jar TweetMiner.jar Tweets

Example : it will create files at Tweets\trends*\tweet*-*.json
*note : it may fail to create the folder if more than two subfolders.
* eg. if Miner/Tweets is given as arguments[0].
* to solve this create empty the requested folder manually

If access token settings are included in twitter4j.properties it will load them up and not require user authendication.
Main arguments, if any, will overwrite these properties.

