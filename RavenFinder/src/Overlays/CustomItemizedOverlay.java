/* Matthew Chou 100703266 Honours Project - RavenFinder. 2011. Professor: Tony White.
 * This CustomItemzedOverlay is part of an open source code base that is part of the balloon overlay that shows a balloon image with customized buttons and objects on it.
 * The clear method was implimented since the original codebase did not support it, which was necessary when updating the balloons.
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

import java.util.ArrayList;

import Mateh.Android.Finder_Main;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.Toast;

import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class CustomItemizedOverlay<Item extends OverlayItem> extends BalloonItemizedOverlay<CustomOverlayItem> {

	private ArrayList<CustomOverlayItem> m_overlays = new ArrayList<CustomOverlayItem>();
	private Context c;
	private Finder_Main finder;
	
	public CustomItemizedOverlay(Drawable defaultMarker, MapView mapView, Finder_Main f) {
		super(boundCenter(defaultMarker), mapView);
		c = mapView.getContext();
		finder=f;
	}

	public void addOverlay(CustomOverlayItem overlay) {
	    m_overlays.add(overlay);
	    populate();
	}

	@Override
	protected CustomOverlayItem createItem(int i) {
		return m_overlays.get(i);
	}

	@Override
	public int size() {
		return m_overlays.size();
	}
	//tapping the ballon directly
	@Override
	protected boolean onBalloonTap(int index, CustomOverlayItem item) {
		/*Toast.makeText(c, "onBalloonTap for overlay index " + index,
				Toast.LENGTH_LONG).show();*/
		return true;
	}

	@Override
	protected BalloonOverlayView<CustomOverlayItem> createBalloonOverlayView() {
		// use our custom balloon view with our custom overlay item type:
		return new CustomBalloonOverlayView<CustomOverlayItem>(getMapView().getContext(), getBalloonBottomOffset(), finder);
	}

	public void clear() {
		// TODO Auto-generated method stub
		m_overlays.clear();
	}

}
