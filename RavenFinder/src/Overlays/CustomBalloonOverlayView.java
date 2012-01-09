/* Matthew Chou 100703266 Honours Project - RavenFinder. 2011. Professor: Tony White.
 * This CustomBalloonOverlayView is part of an open source code base that is part of the balloon overlay that shows a balloon image with customized buttons and objects on it.
 * This extends the BalloonOverlayView which only had a 2 views supported on it.
 * The source is from https://github.com/jgilfelt/android-mapviewballoons.
 */


/***

 * Copyright (c) 2011 readyState Software Ltd
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package Overlays;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import Mateh.Android.Finder_Main;
import Mateh.Android.MessagingPage;
import Mateh.Android.R;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.maps.OverlayItem;


public class CustomBalloonOverlayView<Item extends OverlayItem> extends BalloonOverlayView<CustomOverlayItem> {

	private TextView title;
	private TextView snippet;
	private ImageView image;
	private Finder_Main finder;
	
	public CustomBalloonOverlayView(Context context, int balloonBottomOffset, Finder_Main f) {
		super(context, balloonBottomOffset);
		finder=f;
	}
	
	@Override
	protected void setupView(Context context, final ViewGroup parent) {
		
		// inflate our custom layout into parent
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflater.inflate(R.layout.custom_balloon_overlay, parent);
		
		// setup our fields
		title = (TextView) v.findViewById(R.id.balloon_item_title);
		snippet = (TextView) v.findViewById(R.id.balloon_item_snippet);
		image = (ImageView) v.findViewById(R.id.balloon_item_image);

		// implement balloon close
		ImageView close = (ImageView) v.findViewById(R.id.balloon_close);
		close.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				parent.setVisibility(GONE);
			}
		});
		
		//messaging icon
		ImageView msg = (ImageView) v.findViewById(R.id.balloon_msg);
		msg.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
			/*	Toast.makeText(getContext(), "title is " + title.getText(),
						Toast.LENGTH_LONG).show();*/
				//gets the title of the helper connected to the bubble and finds the chatter number it is linked to so that it can be loaded upon activating the message panel
				Finder_Main.currentChatter= Finder_Main.chatterIdMapOppo.get(title.getText());
				finder.toMessengerPage();
				
	
				
			}
		});
		
	}

	@Override
	protected void setBalloonData(CustomOverlayItem item, ViewGroup parent) {
		
		// map our custom item data to fields
		title.setText(item.getTitle());
		snippet.setText(item.getSnippet());
		
		// get remote image from network.
		// bitmap results would normally be cached, but this is good enough for demo purpose.
		image.setImageResource(R.drawable.cu_raven);
		new FetchImageTask() { 
	        protected void onPostExecute(Bitmap result) {
	            if (result != null) {
	            	image.setImageBitmap(result);
	            }
	        }
	    }.execute(item.getImageURL());
		
	}

	private class FetchImageTask extends AsyncTask<String, Integer, Bitmap> {
	    @Override
	    protected Bitmap doInBackground(String... arg0) {
	    	Bitmap b = null;
	    	try {
				 b = BitmapFactory.decodeStream((InputStream) new URL(arg0[0]).getContent());
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} 
	        return b;
	    }	
	}
	
}
