/* Matthew Chou 100703266 Honours Project - RavenFinder. 2011. Professor: Tony White.
 * This is the main Activity for the Helper who is giving help to the finders. The user can post messages to each group, and set the update time intervals.
 * There is a timer that sends makes the activity retrieve the geolocation of the user and sends and update message to the server.
 */

package Mateh.Android;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import MessageData.SendMessage;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

public class Helper_Main extends Activity  {
    /** Called when the activity is first created. */
	private EditText editV;
	private String focusedGroup;
	private LocationManager lm;
	private LocationListener locationListener;
	private int minUpdateTime;
	private int minUpdateDist;
	private double currentLat;
	private double currentLong;
	private String groupList;
	private ArrayList<String> groupnames;
	private Timer updateTimer;//timer for updating the location and chat
	private int updateSeconds=5;
	public static Helper_Main myHelper_Main;
	private ConcurrentHashMap<String,String> groupMsgs;
	private RadioButton radio_1;
	private RadioButton radio_5;
	private RadioButton radio_10;
	private RadioButton radio_60;
	private RadioButton radio_none;
	private Handler handler;
	public int locCheck=0;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.helpermainlayout);
        groupList="";
        //hash map of the message that is posted for each group
        groupMsgs= new ConcurrentHashMap<String,String>();
        myHelper_Main=this;
        handler=new Handler();
        //the group message edittext field for group messages
        editV=(EditText) findViewById(R.id.post_box);
        //location manager to determine the location of the current user
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		locationListener = new MyLocationListener();
		//use the gps and network provider to retrieve geolocation
		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000,1,
				locationListener);
		lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000,1,
				locationListener);
        

		
		groupnames= new ArrayList<String>();
        for(String key: Clist_Helper.groups.keySet())
		{
        	groupnames.add(key);
        	groupList=groupList+key+":";
		}
        
        //spinner used to show users the groups they decided to give help on, switches the messages for the groups
        Spinner spinner = (Spinner) findViewById(R.id.coursespin);
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item , groupnames);
              
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        
        spinner.setOnItemSelectedListener(new MyOnItemSelectedListener());
        
       
        
        //Submit button that toasts the user with what group they submitted on, and sets the group message, then calls an update
        final Button button = (Button) findViewById(R.id.button_Post);
        button.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // Perform action on clicks
                Toast.makeText(Helper_Main.this, "Submitted text for group " + focusedGroup, Toast.LENGTH_SHORT).show();
                groupMsgs.put(focusedGroup, editV.getText().toString());
                updateMethod();
            }
        });
        //messaging button that creates the MessagingPage_Helper activity used for chatting
        final Button button2 = (Button) findViewById(R.id.HelperMessage_Button);
        button2.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // Perform action on clicks
            	locCheck=1;
            	Intent messangerPageHelper = new Intent(Helper_Main.myHelper_Main, MessagingPage_Helper.class);
   	    	 startActivity(messangerPageHelper);
   	    	
                
            }
        });
        
      
        //radio buttons for the update time
        radio_1 = (RadioButton) findViewById(R.id.radio_1second);
        radio_5 = (RadioButton) findViewById(R.id.radio_5second);
        radio_10 = (RadioButton) findViewById(R.id.radio_10second);
        radio_60 = (RadioButton) findViewById(R.id.radio_60second);
        radio_none=(RadioButton) findViewById(R.id.radio_noupdate);
        radio_1.setOnClickListener(radio_listener);
        radio_5.setOnClickListener(radio_listener);
        radio_10.setOnClickListener(radio_listener);
        radio_60.setOnClickListener(radio_listener);
        radio_none.setOnClickListener(radio_listener);
        
        if(updateSeconds==1)
        {
        	radio_1.setChecked(true);
        }
        else if(updateSeconds==5)
        {
        	radio_5.setChecked(true);
        }
        else if(updateSeconds==10)
        {
        	radio_10.setChecked(true);
        }
        else if(updateSeconds==60)
        {
        	radio_60.setChecked(true);
        }
        else if(updateSeconds==99)
        {
        	radio_none.setChecked(true);
        }
        
        //start the update timer
        updateTimer= new Timer();
        scheduleTimer(updateSeconds);
    }
    
    private OnClickListener radio_listener = new OnClickListener() {
        public void onClick(View v) {
            // Perform action on clicks
            RadioButton rb = (RadioButton) v;
            Toast.makeText(getBaseContext(), rb.getText(), Toast.LENGTH_SHORT).show();
            if(rb.getText().equals("1 Second"))
            {
            	updateSeconds=1;
            	//radio_1.setChecked(true);
            	 scheduleTimer(updateSeconds);
            }
            else if(rb.getText().equals("5 Seconds"))
            {
            	updateSeconds=5;
            	//radio_5.setChecked(true);
            	 scheduleTimer(updateSeconds);
            }
            else if(rb.getText().equals("10 Seconds"))
            {
            	updateSeconds=10;
            	//radio_10.setChecked(true);
            	 scheduleTimer(updateSeconds);
            }
            else if(rb.getText().equals("60 Seconds"))
            {
            	updateSeconds=60;
            	//radio_60.setChecked(true);
            	 scheduleTimer(updateSeconds);
            }
            else if(rb.getText().equals("Never Update"))
            {
            	
            	stopUpdates();
            }
       
        }
    };
    //update the timer with selected time
    public void scheduleTimer(int seconds)
    {
    	updateTimer.cancel();
    	//Log.e("TESTING", "update seconds are " + updateSeconds);
    	updateTimer=new Timer();
    	updateTimer.scheduleAtFixedRate(new timerUpdate(), 0,seconds*1000);
    	
    }
    //stops the timer so that no more messages are sent out
    public void stopUpdates()
    {
    	updateTimer.cancel();
    	updateSeconds=99;
    	Helper_Main.this.runOnUiThread(new Runnable() {
    	    public void run() {
    	    	Toast.makeText(getBaseContext(), "Turning off automatic updates.", Toast.LENGTH_LONG).show();
    	    	radio_none.setChecked(true);
    	    }
    	});
    	
    	//Log.e("TESTING", "Cancel timer and change update seconds to 99");
    }
    
    //selection listener for the spinner, so that when the user changes the group, it will show up with the message that they previously posted with, so that they can change it
    public class MyOnItemSelectedListener implements OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent,
            View view, int pos, long id) {
         /* Toast.makeText(parent.getContext(), "Course selected: " +
              parent.getItemAtPosition(pos).toString(), Toast.LENGTH_SHORT).show();*/
          	focusedGroup= parent.getItemAtPosition(pos).toString();
          	
          	if(groupMsgs.containsKey(focusedGroup))
          	editV.setText(groupMsgs.get(focusedGroup));
        }

        public void onNothingSelected(AdapterView parent) {
          // Do nothing.
        }
    } 
    
    //the TimerTask
    class timerUpdate extends TimerTask {
        public void run() {
        	//Log.e("TESTING", "updating");
           updateMethod();
          
        }
    }
    //get method for the update time
    public int getUpdateSeconds()
    {
    	return updateSeconds;
    }
    
  //update method that sends the updated location and receives any new chat messages
    public void updateMethod()
    {	
    	//Toast.makeText(getBaseContext(),"Updating" ,Toast.LENGTH_SHORT).show();
    	SendMessage msg=new SendMessage("HUPDATE", MenuActivity.username, groupList);
			msg.setLoc(currentLat, currentLong);
			msg.setGroupList(groupnames);
			msg.setGroupMsg(groupMsgs);
      	HelperMessenger mess=new HelperMessenger("HUPDATE", this,msg);
			Thread t= new Thread(mess);
			t.start();
    }
  //menu that pops up when menu button is pushed
  	@Override
  	public boolean onCreateOptionsMenu(Menu menu) {
  	    MenuInflater inflater = getMenuInflater();
  	    inflater.inflate(R.menu.helper_menu, menu);
  	    return true;
  	}
  	
  	//options that happen when the menu buttons are selected
  	@Override
  	public boolean onOptionsItemSelected(MenuItem item) {
  	
  		// Handle item selection
  	    switch (item.getItemId()) {
  	    //updating server with geolocation
  	    case R.id.hupdate:
  	  	//Toast.makeText(getBaseContext()," Lat: " + currentLat+ " Lng: " +currentLong ,Toast.LENGTH_SHORT).show();	    
  	  			updateMethod();
  	        return true;
  	    default:
  	        return super.onOptionsItemSelected(item);
  	    }
  	}

  	//location listener class that retrieves the current latitude and longitude of the device
	private class MyLocationListener implements LocationListener {
		public void onLocationChanged(Location loc) {
			if (loc != null) {
				/*Toast.makeText(
						getBaseContext(),
						"Location changed : Lat: " + loc.getLatitude()+ " Lng: " + loc.getLongitude(),Toast.LENGTH_SHORT).show();*/
						currentLat=loc.getLatitude();
						currentLong=loc.getLongitude();
			
				
			}
		}

		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
			
		}

		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
		}
		public void onDestroy(){
	    	
			lm.removeUpdates(this);
	    }
	}

	//callled when view is paused, stops updating the chatlog screen
	public void onPause() {
		super.onPause();
		// when our activity pauses, we want to remove listening for location updates
		if(locCheck==0){
		lm.removeUpdates(locationListener);
		updateTimer.cancel();
		}
		
		
	}

	public void onResume() {
		super.onResume();
		// when our activity resumes, we want to register for location updates
		if(locCheck==0){
		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000,1,
				locationListener);
		lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000,1,
				locationListener);
		}
		locCheck=0;
		updateTimer.cancel();
		scheduleTimer(updateSeconds);
		
	}
}		