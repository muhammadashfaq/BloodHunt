package bloodcafe.savelet.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import bloodcafe.savelet.R;
import bloodcafe.savelet.constants.BaseurlClass;
import bloodcafe.savelet.constants.SessionManager;

public class UserTrackActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private LocationManager locationManager;
    private String provider;
    Location location;

    List<String> spinner_list;
    String map_type;
    Spinner spinner;
    String userData[];

    ProgressDialog mProgressDialogue;
    LatLng mRequesterLatLng;

    CircularImageView mCircularImageView;
    TextView txtVuUserName, txtVuUserBlood,txtVuUserLastAddress;
    Button btnSMSUser,btnCallUser;
    SessionManager mSessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_track);
        spinner = findViewById(R.id.spinner);
        spinner_list = new ArrayList<String>();

        txtVuUserName = findViewById(R.id.txtVuUserName);
        txtVuUserBlood = findViewById(R.id.txtVuUserBlood);
        txtVuUserLastAddress = findViewById(R.id.txtVuUserLastAddress);
        mCircularImageView = findViewById(R.id.imgVuProfileThumbnail);
        btnSMSUser = findViewById(R.id.btnSMSUser);
        btnCallUser = findViewById(R.id.btnCallUser);
        mProgressDialogue = new ProgressDialog(this);
        mProgressDialogue.setTitle("Please wait");
        mProgressDialogue.setMessage("we are loading...");

        mSessionManager = new SessionManager(this);
        userData = getIntent().getStringArrayExtra("userData");
        //0 ->name
        //1 ->getUserContact
        //2 ->getUserProfilePic
        //3 ->getUserId
        //4 ->getUserPostTime
        //5 ->getUserBloodRequestType
        //6 ->getUserlats
        //7 ->getUserlong
        //8 ->getUserAddress
        //9 ->getUserContact


        txtVuUserName.setText(userData[0]);
        txtVuUserBlood.setText(userData[5]);
        txtVuUserLastAddress.setText(userData[8]);

        mRequesterLatLng = new LatLng(Double.valueOf(userData[6]), Double.valueOf(userData[7]));

        spinner_list.add("Select Map");
        spinner_list.add("Normal Map");
        spinner_list.add("Hybrid Map");
        spinner_list.add("Satellite Map");
        spinner_list.add("Terrain Map");
        setSpinneradapter(spinner_list, spinner);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        System.out.println("Provider " + provider + " has been selected.");
        isGpsEnabled();
        Picasso.get().load(BaseurlClass.mBaseURl+this.getResources().getString(R.string.ProfileImagePath)+userData[2]).into(mCircularImageView);



        btnSMSUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // The number on which you want to send SMS
                SmsManager sm = SmsManager.getDefault();
                sm.sendTextMessage(userData[9], null, "hie I saw you needed "+userData[5] +" blood! I am " +mSessionManager.getKeyuserName()+" Please get in touch I am Glad to Help", null, null);
            }
        });
        btnCallUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + userData[9]));
                if (ActivityCompat.checkSelfPermission(UserTrackActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    Toast.makeText(UserTrackActivity.this, "please provide permission to call", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    startActivity(i);
                }

            }
        });

    }

    private void isGpsEnabled() {
        LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean enabled = service
                .isProviderEnabled(LocationManager.GPS_PROVIDER);

// check if enabled and if not send user to the GSP settings
// Better solution would be to display a dialog and suggesting to
// go to the settings
        if (!enabled) {

            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        } else {
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
            System.out.println("Provider " + provider + " has been selected.");
            getLocationOfTheUser();

        }
    }


    private void getLocationOfTheUser() {
        // Get the location manager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // Define the criteria how to select the locatioin provider -> use
        // default
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            mProgressDialogue.dismiss();
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 2);
        } else {
            location = locationManager.getLastKnownLocation(provider);

            // Initialize the location fields
            if (location != null) {
                mProgressDialogue.dismiss();
                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);
                mapFragment.getMapAsync(this);
                System.out.println("Provider " + provider + " has been selected.");
//            onLocationChanged(location);
            } else {
                mProgressDialogue.dismiss();
                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);
                mapFragment.getMapAsync(this);
                System.out.println("Provider " + provider + " has been selected.");
            }
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        System.out.println("Provider " + provider + " has been selected.");

    }


    private void setSpinneradapter(List<String> list, Spinner spinner) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {


        mMap = googleMap;

        //region select spinner map type item
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                map_type = adapterView.getItemAtPosition(i).toString();
                spinner.setSelection(i);
                if (map_type.equals("Hybrid Map")) {
                    mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                } else if (map_type.equals("Normal Map")) {
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                } else if (map_type.equals("Satellite Map")) {
                    mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                } else if (map_type.equals("Terrain Map")) {
                    mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                } else {
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


//        LatLng hostel_location = new LatLng(lat, lon);
//        LatLng current_location = new LatLng(CLat, CLon);
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(hostel_location, 20));
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current_location, 20));
//        mMap.animateCamera(CameraUpdateFactory.zoomIn());
//        mMap.animateCamera(CameraUpdateFactory.zoomTo(12), 2000, null);

//        addMarker(hostel_location, getAddressFromLatLng(lat, lon), placeType + " : " + hostelnName);
//        addMarker(current_location, getAddressFromLatLng(CLat, CLon), "Your Current Location");


        //endregion

//        // Add a marker in Sydney, Australia,
//        // and move the map's camera to the same location.
//
        if (location != null) {
            LatLng mLatLng = new LatLng(location.getLatitude(), location.getLongitude());
//        System.out.println(location.getLatitude()+",");
            CameraPosition cp = new CameraPosition.Builder()
                    .target(mLatLng)
                    .zoom(15)
                    .build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cp));

            mMap.addMarker(new MarkerOptions().position(mLatLng)
                    .title("you"));

            CameraPosition cp2 = new CameraPosition.Builder()
                    .target(mRequesterLatLng)
                    .zoom(15)
                    .build();
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cp2));

            mMap.addMarker(new MarkerOptions().position(mLatLng)
                    .title(userData[0]));

            final PolylineOptions[] mPolylines = new PolylineOptions[1];
            mPolylines[0] = new PolylineOptions().geodesic(true)
                    .add(mRequesterLatLng)  // requester
                    .add(mLatLng)// you
                    .width(3.0f)
                    .color(Color.RED);

            mMap.addPolyline(mPolylines[0]);

        }else{

            LatLng mLatLng = new LatLng( 32.0737, 72.6804);
//        System.out.println(location.getLatitude()+",");
            CameraPosition cp = new CameraPosition.Builder()
                    .target(mLatLng)
                    .zoom(15)
                    .build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cp));

            mMap.addMarker(new MarkerOptions().position(mLatLng)
                    .title("you"));

            CameraPosition cp2 = new CameraPosition.Builder()
                    .target(new LatLng(32.075034,72.681699))
                    .zoom(15)
                    .build();
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cp2));

            mMap.addMarker(new MarkerOptions().position(new LatLng(32.075034,72.681699))
                    .title(userData[0]));

            final PolylineOptions[] mPolylines = new PolylineOptions[1];
            mPolylines[0] = new PolylineOptions().geodesic(true)
                    .add(mRequesterLatLng)  // requester
                    .add(mLatLng)// you
                    .width(3.0f)
                    .color(Color.RED);

            mMap.addPolyline(mPolylines[0]);

        }


        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setScrollGesturesEnabled(true);
        mMap.getUiSettings().setTiltGesturesEnabled(true);
        mMap.getUiSettings().setRotateGesturesEnabled(true);
    }
}
