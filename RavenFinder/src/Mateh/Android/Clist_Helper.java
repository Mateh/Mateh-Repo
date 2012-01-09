/* Matthew Chou 100703266 Honours Project - RavenFinder. 2011. Professor: Tony White.
 * This Clist_Helper class (Activity) shows the groups that the user can choose to give help on, by selecting multiple groups the user will be telling all group finders their location.
 * The groups will be in a list. Submitting the groups creates the Helper_Main class (Activity)
 */

package Mateh.Android;

// filtered list view referenced from http://www.androidpeople.com/android-listview-searchbox-sort-items
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class Clist_Helper extends Activity {
	private static final int CHOICE_MODE_MULTIPLE = 2;
	/** Called when the activity is first created. */
	private ListView listV;
	private EditText editV;

	//hashmap of groups
	public static HashMap<String, String> groups;
	private ArrayList<String> arr_sort = new ArrayList<String>();
	int textlength = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		groups = new HashMap<String, String>();
		final Intent helpintent = new Intent(this, Helper_Main.class);

		super.onCreate(savedInstanceState);
		setContentView(R.layout.clisthelperlayout);
		listV = (ListView) findViewById(R.id.ListView02);
		editV = (EditText) findViewById(R.id.EditText02);
		// By using setAdpater method in listview we an add string array in a list.
		//set choice mode so that the user can select multiple groups
		listV.setChoiceMode(CHOICE_MODE_MULTIPLE);
		listV.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_multiple_choice, MenuActivity.courses));
		editV.addTextChangedListener(new TextWatcher() {
			
			//not used
			public void afterTextChanged(Editable s) {
			}
			//not used
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			//filters the list
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
				listV.setAdapter(new ArrayAdapter<String>(Clist_Helper.this,android.R.layout.simple_list_item_1, arr_sort));
			}
		});

		listV.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				// When clicked, show a toast with the TextView text
	/*			Toast.makeText(getApplicationContext(),
						((TextView) view).getText(), Toast.LENGTH_SHORT).show();*/

			}
		});

		// submit button, puts the groups selected into the groups hash map
		final Button button = (Button) findViewById(R.id.Button_GroupSubmit);
		button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// Perform action on clicks
				// startActivity(finderIntent);
				// loop through selections and add to groupname
				SparseBooleanArray bo = listV.getCheckedItemPositions();
				for (int i = 0; i < MenuActivity.courses.length; i++) {
					// if true, then it is selected
					if (bo.get(i)) {
						groups.put(MenuActivity.courses[i], "");
					}
				}
				startActivity(helpintent);
			}
		});

	}

}