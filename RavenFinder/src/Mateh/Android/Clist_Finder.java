/* Matthew Chou 100703266 Honours Project - RavenFinder. 2011. Professor: Tony White.
 * This activity is created when the MenuActivity chooses the "Find Help" button, it contains the list of groups that the user can choose from to find help for, one can only be selected at a time.
 * Once the user selects a group, the group is set in the MenuActivity class and the Finder_Main class is created and viewed.
 */

package Mateh.Android;

// filtered list view referenced from http://www.androidpeople.com/android-listview-searchbox-sort-items
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class Clist_Finder extends Activity {
	/** Called when the activity is first created. */
	private ListView listV;
	private EditText editV;
	 

	
	private ArrayList<String> arr_sort = new ArrayList<String>();
	int textlength = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		final Intent findintent = new Intent(this, Finder_Main.class);

		super.onCreate(savedInstanceState);
		setContentView(R.layout.clistfinder);
		listV = (ListView) findViewById(R.id.ListView01);
		editV = (EditText) findViewById(R.id.EditText01);
		// By using setAdpater method in listview we an add string array in the list
		listV.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, MenuActivity.courses));
		editV.addTextChangedListener(new TextWatcher() {
			//unused
			public void afterTextChanged(Editable s) {
			}
			//unused
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			//when the text is changed, the list is filtered according to the letters you input
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

				textlength = editV.getText().length();
				arr_sort.clear();
				for (int i = 0; i < MenuActivity.courses.length; i++) {
					if (textlength <= MenuActivity.courses[i].length()) {
						if (editV.getText().toString().equalsIgnoreCase((String) MenuActivity.courses[i].subSequence(0,textlength))) {
							arr_sort.add(MenuActivity.courses[i]);
						}
					}
				}
					
				listV.setAdapter(new ArrayAdapter<String>(Clist_Finder.this,
						android.R.layout.simple_list_item_1, arr_sort));
				}
		});

		listV.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				// set group name
				MenuActivity.groupName = (String) ((TextView) view).getText();
				// When clicked, show a toast with the TextView text
				Toast.makeText(getApplicationContext(),
						((TextView) view).getText(), Toast.LENGTH_SHORT).show();

				// starting GPS map activity
				startActivity(findintent);
			}
		});
		
		

	}

	
	
}