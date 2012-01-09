/* Matthew Chou 100703266 Honours Project - RavenFinder. 2011. Professor: Tony White.
 * 
 * This class contains the first view that the user will see, the RavenFinderActivity simply splits up the choices between selecting RavenFinder or RavenEye.
 * By selecting RavenFinder, the user will create the MenuActivity class and the view will change.
 * The RavenEye application created by Michael Du Plessis is not joined into this application yet.
 * 
 * THROUGHOUT  the comments, "group" and "courses" might be used interchangeably.
 * Future modifications should be : notification on new messages, deregister message sent when helpers disconnect so that finder dont see them, timestamp for helpers.
 */

package Mateh.Android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

 

//this activity is the first activity activated upon application initialization
public class RavenFinderActivity extends Activity {
    /** Called when the activity is first created. */
	RavenFinderActivity rf;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        rf=this;
        final Intent rfintent= new Intent(this, MenuActivity.class);
        //RavenFinder button
        final Button button = (Button) findViewById(R.id.Button_RF);
        button.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                startActivityForResult(rfintent,1);
            }
        });
        //RavenEye button
        final Button button2 = (Button) findViewById(R.id.Button_RE);
        button2.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {    
                Toast.makeText(RavenFinderActivity.this, "RavenEye not yet loaded", Toast.LENGTH_SHORT).show();
                
            }
        });
      
    }
}		