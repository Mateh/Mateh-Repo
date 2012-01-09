/* Matthew Chou 100703266 Honours Project - RavenFinder. 2011. Professor: Tony White.
 * 
 * This class is the menu page where the user can choose between logging into Carleton Central to retrieve their course information, finding help by selecting "Find Help" button, and giving help by selecting
 * "Give help". This class also contains the group name and username that the finder is currently seeking help on, as well as the an array of strings that contain the courses that are available 
 * for the user to choose to find help or help on. The username of the user is displayed, and is randomized by the application unless the user logs into Carleton Central. The chat log of the user is also contained
 * in this class, which is modified by other classes. Selecting "Login" starts the LoginPage class (Activity), "Find Help" starts the Clist_Finder class (Activity), "Give Help" starts the Clist_Helper class (Activity)
 * 
 */


package Mateh.Android;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import MessageData.ChatMessage;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
//this menu is used to select which path the user would take, in terms of login,finder, and helper
//this class also stands as the middle way between being a helper and finder, so it will hold many static methods and variables that either a helper of finder would use
public class MenuActivity extends Activity {
	
	public static String username;
	public static String groupName;
	//course list used in the Clist_.. classes to show the list of available groups to join, Campus Events and General Help are set as default groups so that non-Carleton users can use those groups
	public static String[] courses={"Campus Events","General Help"};
	// IP to server
	public static  String HOST; 
	public static  int HOSTPORT;
	//this hashmap contains the chat messages sent between users, the key is the usernames, and the value is an array of messages
	public static ConcurrentHashMap<String, ArrayList<String>> chatLog;
	
	private TextView username_Textview;
	private EditText serverip;
	private EditText serverport;
	private TextView serveraddress;
	public MenuActivity()
	{ 
		 
	}
	
		 @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.menulayout);
	        HOST = "0.0.0.0"; 
	        HOSTPORT= 15000;
	        //setting the intents for the selection of an option
	        final Intent finderIntent= new Intent(this, Clist_Finder.class);
	        final Intent helperIntent= new Intent(this, Clist_Helper.class);
	        final Intent loginIntent= new Intent(this, LoginPage.class);
	        //Finder button
	        
	        username_Textview= (TextView) findViewById(R.id.client_username_text);
	        serverip= (EditText) findViewById(R.id.editserverip);
	        serverport= (EditText) findViewById(R.id.editserverport);
	        serveraddress= (TextView) findViewById(R.id.serveraddresslabel);
	        serveraddress.setText("Server address: "+ HOST +":"+ HOSTPORT);
	        
	        //upon creation, the user has not logged in as of yet, so a random userid is created, it is possible that clients may coincidentally have the same userid because of random number generation
	        Random gen= new Random(System.currentTimeMillis());
		      username= "user"+(gen.nextInt());			
		      //initialize the chat log and add an empty entry into it  
		      chatLog= new ConcurrentHashMap<String, ArrayList<String>>();
		      chatLog.put("empty", new ArrayList<String>());
		        
		      //find help selected 
		      final Button button = (Button) findViewById(R.id.Button_FindHelp); 
	          button.setOnClickListener(new OnClickListener() {
	            public void onClick(View v) {
	            	//Perform action on clicks
	                startActivity(finderIntent);
	            }
	        });
	        //help button selected
	        final Button button2 = (Button) findViewById(R.id.Button_GiveHelp);
	        button2.setOnClickListener(new OnClickListener() {
	            public void onClick(View v) {
	                // Perform action on clicks
	            	startActivity(helperIntent);
	                
	                
	            }
	        });
	        //login button pressed
	        final Button button3 = (Button) findViewById(R.id.Button_Login);
	        button3.setOnClickListener(new OnClickListener() {
	            public void onClick(View v) {
	                // Perform action on clicks
	            	startActivity(loginIntent);
	                
	                
	            }
	        });
	        
	        final Button button4 = (Button) findViewById(R.id.serverbutton);
	        button4.setOnClickListener(new OnClickListener() {
	            public void onClick(View v) {
	                // Perform action on click
	            	
	            	HOST= serverip.getText().toString();
	                
	            	String port= serverport.getText().toString();
	            	try{
	            		HOSTPORT= Integer.parseInt(port);
	            	}
	            	catch(Exception e)
	            	{
	            		//invalid input
	            		MenuActivity.this.runOnUiThread(new Runnable() {
		            	    public void run() {
		            	    
		            	    	Toast.makeText(getBaseContext(), "Improper format for port number, default set.", Toast.LENGTH_SHORT).show();
		            	    	HOSTPORT=15000;
		            	    }
		            	});
	            	}
	            	
	            	
	            	MenuActivity.this.runOnUiThread(new Runnable() {
	            	    public void run() {
	            	    	serveraddress.setText("Server address: "+ HOST +":"+ HOSTPORT);
	            	    	Toast.makeText(getBaseContext(), "Server address set.", Toast.LENGTH_SHORT).show();
	            	    	
	            	    }
	            	});
	                
	            	
	            	
	            }
	        });
	        
	  
	      
}
	//updates the chatLog with new messages
	public static void updateChat(ArrayList<ChatMessage> m)
	{	
		//loop through the new chat messages received
		for(ChatMessage msg : m)
		{
			//if the sender already exists then add message to the log
			if(chatLog.containsKey(msg.getHeader()))
					{
					String t= msg.getHeader() + ": " + msg.getBody();
					chatLog.get(msg.getHeader()).add(t);
					}
			//if the sender doesn't exist, add new entry and message
			else
			{	//Log.e("Menu", "adding new person to chatlog");
				ArrayList<String> ch= new ArrayList<String>();
				//adding the sender name to the message string for formating
				String t= msg.getHeader() + ": " + msg.getBody();
				ch.add(t);
				chatLog.put(msg.getHeader(), ch);
				//removing the empty chatter which was placed in the initialization as a place holder
				chatLog.remove("empty");
			}
		}
	}
	
	//when this view resumes, the user name is set, this is mainly used for after the login page returns the user name is changed in the view
	 @Override
		protected void onResume() {
			super.onResume();
			 username_Textview.setText(username);
			 
		}
}
