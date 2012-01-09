/* Matthew Chou 100703266 Honours Project - RavenFinder. 2011. Professor: Tony White.
 * The MyCustomLocationOverlay was integrated from http://stackoverflow.com/questions/753793/how-can-i-use-a-custom-bitmap-for-the-you-are-here-point-in-a-mylocationoverla.
 * This particular overlay is the overlay that represents the users current position. The image being drawn is rotated based on the rotation of the users device.
 */

package Overlays;

import Mateh.Android.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;
import android.location.Location;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;

public class MyCustomLocationOverlay extends MyLocationOverlay {
    private Context mContext;
    private float   mOrientation;

    public MyCustomLocationOverlay(Context context, MapView mapView) {
        super(context, mapView);
        mContext = context;
    }

    @Override 
    protected void drawMyLocation(Canvas canvas, MapView mapView, Location lastFix, GeoPoint myLocation, long when) {
        // translate the GeoPoint to screen pixels
        Point screenPts = mapView.getProjection().toPixels(myLocation, null);

        // create a rotated copy of the marker
        Bitmap arrowBitmap = BitmapFactory.decodeResource( mContext.getResources(), R.drawable.arrow);
        Matrix matrix = new Matrix();
        matrix.postRotate(this.getOrientation());
        
        Bitmap rotatedBmp = Bitmap.createBitmap(
            arrowBitmap, 
            0, 0, 
            arrowBitmap.getWidth(), 
            arrowBitmap.getHeight(), 
            matrix, 
            true
        );
        // add the rotated marker to the canvas
        canvas.drawBitmap(
            rotatedBmp, 
            screenPts.x - (rotatedBmp.getWidth()  / 2), 
            screenPts.y - (rotatedBmp.getHeight() / 2), 
            null
        );
        mapView.postInvalidate();
    }

    public void setOrientation(float newOrientation) {
         mOrientation = newOrientation;
    }
}