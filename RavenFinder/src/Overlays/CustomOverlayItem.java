/* Matthew Chou 100703266 Honours Project - RavenFinder. 2011. Professor: Tony White.
 * This CustomOverlyItem is part of an open source code base that is part of the balloon overlay that shows an image given from a URL.
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

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

public class CustomOverlayItem extends OverlayItem {

	protected String mImageURL;
	
	public CustomOverlayItem(GeoPoint point, String title, String snippet, String imageURL) {
		super(point, title, snippet);
		mImageURL = imageURL;
	}

	public String getImageURL() {
		return mImageURL;
	}

	public void setImageURL(String imageURL) {
		this.mImageURL = imageURL;
	}
	
}
