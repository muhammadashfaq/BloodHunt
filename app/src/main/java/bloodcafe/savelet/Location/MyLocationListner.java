package bloodcafe.savelet.Location;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import bloodcafe.savelet.Common.Common;

public class MyLocationListner implements LocationListener {

    @Override
    public void onLocationChanged(Location location) {
        Log.i("location",location.getLongitude()+"\n"+location.getLatitude());
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
