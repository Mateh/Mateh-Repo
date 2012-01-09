/* Matthew Chou 100703266 Honours Project - RavenFinder. 2011. Professor: Tony White.
 * This is the main activity for the path of being a finder of help. The user can choose to go from this activity to the MessagingPage class (Activity), which has the chatting functionality,
 * or there is the Finder_Settings class (Activity) that displays the update settings the user can choose from. This class uses the Finder_Messenger class to send and receive messages, the Finder_Messenger class
 * is a runnable class.
 */

package Mateh.Android;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import MessageData.HelperInfo;
import Overlays.CustomItemizedOverlay;
import Overlays.CustomOverlayItem;
import Overlays.MyCustomLocationOverlay;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
//main activity for the finder option, for users who are trying to find a helper	
//location pinpoint and zoom from http://joshclemm.com/blog/?p=148
public class Finder_Main extends MapActivity {
	
	private MyCustomLocationOverlay myLocationOverlay; //an overlay that uses a custom icon, this overlay displays the users current position and direction
	private MapView mapView; //the main view for this activity
	private MapController mc;//map controller can be used to control the view
	private List<Overlay> mapOverlays; //the list of overlays that will be displayed on the map
	public static Finder_Main myFinder_main;//current instance of class to pass to other classes
	public static ConcurrentHashMap<String,HelperInfo> clientHelperList;//hash map of the helpers that are in the current users selected group
	public static Timer updateTimer;//timer for updating the location and chat
	private int updateSeconds=10;//time interval between updates
	public int locCheck=0;//integer to check for when the activity is put on pause, so that the updates are stopped when the app is closed

	
	//balloon overlay variables
	public Drawable drawable;
	public CustomItemizedOverlay<CustomOverlayItem> itemizedOverlay;
	
	//chatter update variables
	    public static int currentChatter=0;
	    public static int numberOfChatters=0;
	    public static HashMap<Integer, String> chatterIdMap;//hash map that maps the index in the MessagingPage activity for current visible chat (the chat log of the helper)
	    public static HashMap<String, Integer> chatterIdMapOppo;//a hash map which is the opposite of chatterIdMap, maps the Helper name to the integer

	  
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fmainlayout);
		clientHelperList= new ConcurrentHashMap<String,HelperInfo>();	
	    chatterIdMap= new HashMap<Integer, String>();
	    chatterIdMapOppo= new HashMap<String, Integer>() ;
	    myFinder_main=this;
	    
		mapView = (MapView) findViewById(R.id.finderMapView);
		mapView.setBuiltInZoomControls(true);
		myLocationOverlay = new MyCustomLocationOverlay(this, mapView);
		myLocationOverlay.enableCompass();//enable the compass so that MyCustomLocationOverlay can rotate accordingly
		
		mc = mapView.getController();

		
		mapOverlays =Collections.synchronizedList(mapView.getOverlays());//retrieve the overlay list
		mapOverlays.add(myLocationOverlay);//add the overlay that represents the current user
		
		
		
		drawable = getResources().getDrawable(R.drawable.cu_raven);//setting the icon to draw for the helpers
		itemizedOverlay = new CustomItemizedOverlay<CustomOverlayItem>(drawable, mapView,this);
		//calling updateChatterNumbers() so that the helpers are mapped correctly to the indexs
		updateChatterNumbers();
		//set up timer and make it run by default, the timer will invoke a method every updateSeconds
		updateTimer= new Timer();
		scheduleTimer(updateSeconds);
		//this timer is used only on creation, so that the view will be zoomed in on the current user position, as long as a gps position can be found within 5 seconds
		Timer t= new Timer();
		t.schedule(new zoomTimer(), 5000);
	
	}
	//timer only used to set the zoom after they connect
	 class zoomTimer extends TimerTask {
	        public void run() {    
	          zoomToMyLocation();          
	        }
	    }
	 
	
	 //update the timer with selected time
    public void scheduleTimer(int seconds)
    {
    	updateTimer.cancel();
    	updateTimer=new Timer();
    	updateTimer.scheduleAtFixedRate(new timerUpdate(), 0,seconds*1000);
    	
    }
    
  //the TimerTask that fetches for updates based on user input
    class timerUpdate extends TimerTask {
        public void run() {
        //Log.e("TESTING", "updating");
          sendUpdate();
          
        }
    }
    
    //to stop the timer from updating
    public void stopUpdates()
    {
    	updateTimer.cancel();
    	//updateSeconds set to 99 just for settings menu to update the settings menu
    	updateSeconds=99;
    	//this runOnUiThread invokation must be made since this method can be called from another activity that is not in control of this activity's main GUI thread
    	Finder_Main.this.runOnUiThread(new Runnable() {
    	    public void run() {
    	    
    	    	Toast.makeText(getBaseContext(), "Turning off automatic updates.", Toast.LENGTH_LONG).show();
    	    	
    	    }
    	});
    	
    	//Log.e("TESTING", "Cancel timer and change update seconds to 99");
    }

    
    
	// when our activity resumes, we want to register for location updates
	public void onResume() {
		super.onResume();
		
		if(locCheck==0){
		myLocationOverlay.enableMyLocation();
		updateTimer.cancel();
		scheduleTimer(updateSeconds);
		}
		locCheck=0;
	}

	// when our activity pauses, we want to remove listening for location updates
	public void onPause() {
		super.onPause();
		
		if(locCheck==0){
		myLocationOverlay.disableMyLocation();
		updateTimer.cancel();
		}
		
	}
	
	//when activity is no longer available
	public void onStop(){
		super.onStop();
		// Log.e("STOPPING", "STOPPPPING");
	
	}
	
	//this method gets the users latest position and zooms the view onto it using the map controller
	private void zoomToMyLocation() {
		GeoPoint myLocationGeoPoint = myLocationOverlay.getMyLocation();
		if(myLocationGeoPoint != null) {
			mc.animateTo(myLocationGeoPoint);
			mc.setZoom(65);
		}
		else {
			//Toast.makeText(this, "Cannot determine location", Toast.LENGTH_SHORT).show();
		}
	} 
	

	//updates the groups positions and overlays
	public synchronized void updateGroup() {
		if (!clientHelperList.isEmpty()) {
			
			//clearing the containers of overlays so that updated ones can be added
			mapOverlays.clear();
			itemizedOverlay.clear(); //modified CustomItemizedOverlay to support clear() method
			
			//loop through all helpers added into clientHelperList
			for (String key : clientHelperList.keySet()) {
				HelperInfo hinfo= clientHelperList.get(key);
				//create a geopoint from the helpers information
				GeoPoint point = new GeoPoint((int) (hinfo.getLat() * 1E6), (int) (hinfo.getLongs() * 1E6));

				String tname= hinfo.getName();
				
				//if a groupname was not set in the helper info, then it was because they did not post a group message, the if/else is for setting the group name and setting nothing in the bubble overlay
				if(hinfo.getInfo().get(MenuActivity.groupName)!=null)
				{
					CustomOverlayItem overlayItem = new CustomOverlayItem(point, hinfo.getName(), 
							hinfo.getInfo().get(MenuActivity.groupName), 
							"http://www.studytoabroad.com/wp-content/uploads/2011/03/carleton-logo.jpg");
					itemizedOverlay.addOverlay(overlayItem);
				}
				
				else{
				CustomOverlayItem overlayItem = new CustomOverlayItem(point, hinfo.getName(), 
						"", 
						"http://www.studytoabroad.com/wp-content/uploads/2011/03/carleton-logo.jpg");
				itemizedOverlay.addOverlay(overlayItem);
				
				}
				
			

				// if the helper is not part of the chatlog already, add the helpers
			
				if (!MenuActivity.chatLog.containsKey(key)) {
					MenuActivity.chatLog.put(key, new ArrayList<String>());
					if (MenuActivity.chatLog.containsKey("empty")) {
						MenuActivity.chatLog.remove("empty");
					}
				}

			}
		
	
						// add the helpers to the overlay
						mapOverlays.add(itemizedOverlay);
	
						// add yourself as well
						mapOverlays.add(myLocationOverlay);
						mapView.postInvalidate();
		
			
			
		}
		//update chatter
		updateChatterNumbers();

	
	} 
	
	//update method to determine which chatters are available so that the view in MessagingPage is showed correctly	
	public void updateChatterNumbers()
	 {
		 int j=0;
	    	numberOfChatters=(MenuActivity.chatLog.size()-1);
	    	//loop through chatlog to find current focused chatter
	    	for(String key: MenuActivity.chatLog.keySet())
			{
			chatterIdMap.put(j, key);
			chatterIdMapOppo.put(key,j);
			j++;
			}
	 }
	
	//this method creates a thread that runs the FinderMessenger class with an "FUPDATE" message and returns a message, gets handled, then the thread dies
	public synchronized void sendUpdate()
	{
		FinderMessenger mess=new FinderMessenger("FUPDATE", this,null);
		Thread t= new Thread(mess);
		t.start();
	}
	
	//menu that pops up when menu button is pushed
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.finder_menu, menu);
	    return true;
	}
	
	//options that happen when the menu buttons are selected
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	   //if the Update button is pressed
	    case R.id.fupdate:
	        	sendUpdate();			
	        return true;
	    //if Zoom on Me button is pressed    
	    case R.id.fzoom:
	    	zoomToMyLocation();
	        return true;
	     //if the Messenger button is pressed
	     //starts the MessagingPage Activity
	    case R.id.messenger:
	    	locCheck=1;
	    	 Intent messangerPage = new Intent(this, MessagingPage.class);
	    	 startActivity(messangerPage);
	    	 return true;
	    //if the Settings button is pressed
	    //starts the Finder_Settings activity
	    case R.id.settings:
	    	locCheck=1;
	    	Intent intent = new Intent(this, Finder_Settings.class);
  	    	 startActivity(intent);
	        return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
	//starting MessengerPage helper method
	public void toMessengerPage()
	{
			 Intent messangerPage = new Intent(myFinder_main, MessagingPage.class);
		     startActivity(messangerPage);	
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
	
	//methods used for timer changes from settings menu
	public void setUpdateSeconds(int i)
	{
		updateSeconds=i;
	}
	public int getUpdateSeconds()
	{
		return updateSeconds;
	}
	
	
	
}