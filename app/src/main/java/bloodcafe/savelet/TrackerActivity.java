package bloodcafe.savelet;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.DoubleUnaryOperator;

import bloodcafe.savelet.constants.BaseurlClass;
import bloodcafe.savelet.constants.SessionManager;

public class TrackerActivity extends AppCompatActivity implements OnMapReadyCallback {

    CircularImageView mCircularImageView;
    TextView txtVuUserName, txtVuUserBlood,txtVuUserLastAddress;
    Button btnSMSUser,btnCallUser;
    SessionManager mSessionManager;
    List<String> spinner_list;
    String map_type;
    Spinner spinner;
    String userData[];
    ProgressDialog mProgressDialogue;
    DatabaseReference dbRef;

    String userLat,userLong;

    GoogleMap googleMap;

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracker);


        //IntiazlizationStuff
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

        SessionManager sessionManager=new SessionManager(this);
        if(sessionManager.getKeyuserId()!=null){
            dbRef= FirebaseDatabase.getInstance().getReference("UsersLocation").child(sessionManager.getKeyuserId());
        }


        //Getting data
        mSessionManager = new SessionManager(this);
        userData = getIntent().getStringArrayExtra("userData");



        txtVuUserName.setText(userData[0]);
        txtVuUserBlood.setText(userData[5]);
        txtVuUserLastAddress.setText(userData[8]);

        userLat = userData[6];
        userLong = userData[7];
        Log.i("ARRAY",userLat+"\n"+userLong);

        spinner_list.add("Select Map");
        spinner_list.add("Normal Map");
        spinner_list.add("Hybrid Map");
        spinner_list.add("Satellite Map");
        spinner_list.add("Terrain Map");

        spinner.setBackgroundColor(getResources().getColor(R.color.black));

        setSpinneradapter(spinner_list, spinner);
//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);
        SupportMapFragment mapFragment = (SupportMapFragment) this.getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);//remember getMap() is deprecated!
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
                if (ActivityCompat.checkSelfPermission(TrackerActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(TrackerActivity.this, "please provide permission to call", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    startActivity(i);
                }
            }
        });
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        this.googleMap =  googleMap;
        String lat = userLat;
        String longi = userLong;
        googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        LatLng latLng = new LatLng(Double.valueOf(lat),Double.valueOf(longi));
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title(getAddressFromLatLng(TrackerActivity.this,latLng));
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        googleMap.addMarker(markerOptions);
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(markerOptions.getPosition(), 14));

    }



    public static String getAddressFromLatLng(Context context, LatLng latLng) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(context, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            return addresses.get(0).getAddressLine(0);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private void setSpinneradapter(List<String> list, Spinner spinner) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String selectMap = parent.getItemAtPosition(position).toString();
                if(selectMap.equalsIgnoreCase("Normal Map")){
                        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                } else if (selectMap.equalsIgnoreCase("Hybrid Map")) {
                    googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                }else if(selectMap.equalsIgnoreCase("Satellite Map")){
                    googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                }else if(selectMap.equalsIgnoreCase("Terrain Map")){
                    googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                }else{
                    googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }
}
