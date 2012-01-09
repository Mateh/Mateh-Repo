/* Matthew Chou 100703266 Honours Project - RavenFinder. 2011. Professor: Tony White.
 * 
 * The LoginPage class (Activity) is used to allow the user to enter in their carleton central credentials so that they can retrieve their course list for the current semester and add them to their groups
 * to seek or give help on. Upon successful login, this activity executes the finish() and returns to the previous activity, the MenuActivity. If the user incorrectly enters their login information, then
 * an error message will be shown, and the user may keep on attempting to log in, or select the "back" button to return to the MenuActivity. This class was developed by utilizing the same process in which the
 * Carleton Mobile android application developed by CollegeMobile retrieves the students info from Carleton University.
 */

package Mateh.Android;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LoginPage extends Activity{
	private	EditText userEditText;
	private EditText pwdEditText;
	private TextView loginerrorText;
	//the students account name used in myConnect, as well as the password
	private String username;
	private String userpwd;

	
	@Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.loginpagelayout);
	        
	        userEditText= (EditText)findViewById(R.id.username_editText);
	        pwdEditText= (EditText)findViewById(R.id.password_editText);
	        loginerrorText= (TextView) findViewById(R.id.loginerror);
	        
	        
	        //the login button
	        final Button button = (Button) findViewById(R.id.login_button);
	        button.setOnClickListener(new OnClickListener() {
	            public void onClick(View v) {
	            	//when the login button is clicked, the username and password entered in the fields are retrieved and the login() is called
	                username=userEditText.getText().toString();
	                userpwd=pwdEditText.getText().toString();
	                login();
	            }
	        });
	        
	        
	 }
/*this method creates a url connection to the website that Carleton University has set up for the Carleton Mobile to work with. Essentially, the user name and password the user enters is sent in and 
* the return of the post can be checked to see if it contains a substring that is expected if the login was successful, if it doesnt, then it means the user incorrectly logged in, and an error message is revealed.
* If the page is the correct successful login page, we can extract from the page source a key, this key is used with the username and some parameters to define exactly what we would like to request from the webpage.
* The return of this post is a Json object. Using this object, we can request from it the courses being taken this semester by the user. Then those values are added to the course list array stored in the MenuActivity.
	*
	*/
	 public void login()
	 {
		    URL anURL;
			HttpsURLConnection anHttpsURLConnection;
			try {
				
				anURL = new URL("https://mobileapps.carleton.ca:8443/iPhone/login.jsp");
				anHttpsURLConnection = (HttpsURLConnection) anURL.openConnection();
				anHttpsURLConnection.setRequestMethod("POST");
				
				
				
				anHttpsURLConnection.setDoOutput(true);
				String aString = "userid=" + username + "&userpwd=" + userpwd;
				anHttpsURLConnection.getOutputStream().write(aString.getBytes());
				
				BufferedInputStream aBufferedInputStream = new BufferedInputStream(anHttpsURLConnection.getInputStream());
				InputStreamReader anInputStreamReader = new InputStreamReader(aBufferedInputStream);
				BufferedReader aReader = new BufferedReader(anInputStreamReader);
				String aLine = null;
				int offset = -1;
				String match = "<input id=\"key\" type=\"hidden\" name=\"userid\" value=\"";
				while((aLine = aReader.readLine()) != null){
					offset = aLine.indexOf(match);
					if(offset != -1)
						break;
				}
				//if there is an incorrect login
				if(aLine==null)
				{
					loginerrorText.setText("Incorrect login information was submitted, please enter the information and submit it again.");
				}
				//else send another message with the key
				else{		
				loginerrorText.setText("");
				String key = aLine.substring(offset + match.length(), aLine.length() - 2);
				
				String query = "https://mobileapps.carleton.ca:8443/iPhone/protected/getClassesTerm?userid=" + username
						+ "&key=" + key + "&term=" + "now";
				anURL = new URL(query);
				anHttpsURLConnection = (HttpsURLConnection) anURL.openConnection();
				anHttpsURLConnection.setRequestMethod("GET");
				
				BufferedReader reader = new BufferedReader(new InputStreamReader(anHttpsURLConnection.getInputStream()));
				String json = reader.readLine();
				JSONObject aJSONObject = new JSONObject(new JSONTokener(json));
				JSONArray aJSONArray = aJSONObject.getJSONArray("terms");
				aJSONObject = aJSONArray.getJSONObject(0);
				aJSONArray = aJSONObject.getJSONArray("courses");
				
				//create array to send to the MenuActivity, defining the groups
				String temparray[]= new String[aJSONArray.length()+2];
				temparray[0]="Campus Events";
				temparray[1]="General Help";
				for(int i = 0; i < aJSONArray.length(); i++){
					aJSONObject = aJSONArray.getJSONObject(i);
					//System.out.println(aJSONObject.getString("courseTitle") + " :" + aJSONObject.getString("courseSection"));
					temparray[i+2]=aJSONObject.getString("courseTitle");
				}
				//Log.e("COURSES", groups);
				//set the array in MenuActivity
				MenuActivity.courses=temparray;
				MenuActivity.username=username;
				finish();
				}
				
				
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		
	 }
	        
	      


}
