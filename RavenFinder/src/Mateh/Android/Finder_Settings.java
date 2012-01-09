/* Matthew Chou 100703266 Honours Project - RavenFinder. 2011. Professor: Tony White.
 *This Finder_Settings class (Activity) displays a list of radio buttons for the user to select so that it can update on set intervals.
 *When changing the intervals of updating, the timer in Finder_Main needs to cancel the timer and create a new timer with the updated time to update at.
 */

package Mateh.Android;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioButton;
import android.widget.Toast;

public class Finder_Settings extends Activity {
	
	 public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.findersettingsmenu);
	           
	        //radio buttons for the update time
	        final RadioButton radio_1 = (RadioButton) findViewById(R.id.radio_1second2);
	        final RadioButton radio_5 = (RadioButton) findViewById(R.id.radio_5second2);
	        final RadioButton radio_10 = (RadioButton) findViewById(R.id.radio_10second2);
	        final RadioButton radio_60 = (RadioButton) findViewById(R.id.radio_60second2);
	        final RadioButton radio_none = (RadioButton) findViewById(R.id.radio_none2);
	        radio_1.setOnClickListener(radio_listener);
	        radio_5.setOnClickListener(radio_listener);
	        radio_10.setOnClickListener(radio_listener);
	        radio_60.setOnClickListener(radio_listener);
	        radio_none.setOnClickListener(radio_listener);
	        
	        if(Finder_Main.myFinder_main.getUpdateSeconds()==1)
	        {
	        	radio_1.setChecked(true);
	        }
	        else if(Finder_Main.myFinder_main.getUpdateSeconds()==5)
	        {
	        	radio_5.setChecked(true);
	        }
	        else if(Finder_Main.myFinder_main.getUpdateSeconds()==10)
	        {
	        	radio_10.setChecked(true);
	        }
	        else if(Finder_Main.myFinder_main.getUpdateSeconds()==60)
	        {
	        	radio_60.setChecked(true);
	        }
	        else if(Finder_Main.myFinder_main.getUpdateSeconds()==99)
	        {
	        	radio_none.setChecked(true);
	        }
	    }
	    //listener used to modifiy the update seconds in the Finder_Main class
	 	
	    private OnClickListener radio_listener = new OnClickListener() {
	        public void onClick(View v) {
	            // Perform action on clicks
	            RadioButton rb = (RadioButton) v;
	            Toast.makeText(getBaseContext(), rb.getText(), Toast.LENGTH_SHORT).show();
	            if(rb.getText().equals("1 Second"))
	            {
	            	Finder_Main.myFinder_main.setUpdateSeconds(1);
	            	Finder_Main.myFinder_main.scheduleTimer(Finder_Main.myFinder_main.getUpdateSeconds());
	            }
	            else if(rb.getText().equals("5 Seconds"))
	            {
	            	Finder_Main.myFinder_main.setUpdateSeconds(5);
	            	Finder_Main.myFinder_main.scheduleTimer(Finder_Main.myFinder_main.getUpdateSeconds());
	            }
	            else if(rb.getText().equals("10 Seconds"))
	            {
	            	Finder_Main.myFinder_main.setUpdateSeconds(10);
	            	Finder_Main.myFinder_main.scheduleTimer(Finder_Main.myFinder_main.getUpdateSeconds());
	            }
	            else if(rb.getText().equals("60 Seconds"))
	            {
	            	Finder_Main.myFinder_main.setUpdateSeconds(60);
	            	Finder_Main.myFinder_main.scheduleTimer(Finder_Main.myFinder_main.getUpdateSeconds());
	            }
	            else if(rb.getText().equals("Never Update"))
	            {
	            	Finder_Main.myFinder_main.setUpdateSeconds(99);
	            	Finder_Main.myFinder_main.stopUpdates();
	            }
	           
	           finish();
	        }
	    };
	    
		public void onPause() {
			super.onPause();
			
			  Finder_Main.myFinder_main.locCheck=0;
		        Finder_Main.myFinder_main.onPause();
			
		}
		 public void onResume() {
		        super.onResume();
		        Finder_Main.myFinder_main.onResume();
		        Finder_Main.myFinder_main.locCheck=1;
		        
		    }

}
