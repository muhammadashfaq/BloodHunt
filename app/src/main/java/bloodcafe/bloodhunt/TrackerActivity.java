package bloodcafe.bloodhunt;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
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

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import bloodcafe.bloodhunt.constants.BaseurlClass;
import bloodcafe.bloodhunt.constants.SessionManager;

public class TrackerActivity extends AppCompatActivity implements OnMapReadyCallback {

    CircularImageView mCircularImageView;
    TextView txtVuUserName, txtVuUserBlood, txtVuUserLastAddress;
    Button btnSMSUser, btnCallUser;
    SessionManager mSessionManager;
    List<String> spinner_list;
    String map_type;
    boolean PERMISSION_STATUS = false;
    private int SMS_PERMISSION = 100;
    private int CALL_PERMISSION = 200;
    Spinner spinner;
    String userData[];
    ProgressDialog mProgressDialogue;
    DatabaseReference dbRef;

    String userLat, userLong;

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

        SessionManager sessionManager = new SessionManager(this);
        if (sessionManager.getKeyuserId() != null) {
            dbRef = FirebaseDatabase.getInstance().getReference("UsersLocation").child(sessionManager.getKeyuserId());
        }


        //Getting data
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
        LatLng latLng = new LatLng(Double.valueOf(userData[6]), Double.valueOf(userData[7]));
        String address = getAddressFromLatLng(this, latLng);
        txtVuUserLastAddress.setText(address);

        userLat = userData[6];
        userLong = userData[7];
        Log.i("ARRAY", userLat + "\n" + userLong);

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
        Picasso.get().load(BaseurlClass.mBaseURl + this.getResources().getString(R.string.ProfileImagePath) + userData[2]).into(mCircularImageView);


        btnSMSUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // The number on which you want to send SMS
                if (ActivityCompat.checkSelfPermission(TrackerActivity.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                    takeSMSPermission();
                    return;
                } else {

                }

                SmsManager sm = SmsManager.getDefault();
                sm.sendTextMessage(userData[9], null, "Hi I saw you needed " + userData[5] + " blood! I am " + mSessionManager.getKeyuserName() + " Please get in touch, I am very glad to help you", null, null);
                Toast.makeText(TrackerActivity.this, "Message Sent", Toast.LENGTH_SHORT).show();

            }
        });

        btnCallUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + userData[9]));
                if (ActivityCompat.checkSelfPermission(TrackerActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    takeCallPermission();
                    Toast.makeText(TrackerActivity.this, "Please provide permission to call", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    startActivity(i);
                }
            }
        });
    }

    private void takeSMSPermission() {
        int permissionsSMS = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.SEND_SMS);

        if (permissionsSMS != PackageManager.PERMISSION_GRANTED)
            setUpSMSPermissions();
        else {
            PERMISSION_STATUS = true;
        }

    }


    private void takeCallPermission() {
        int permissionsAccess = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE);

        if (permissionsAccess != PackageManager.PERMISSION_GRANTED)
            setUpCallPermissions();
        else {
            PERMISSION_STATUS = true;
        }

    }

    private void setUpSMSPermissions() {
        // Toast.makeText(getApplicationContext(), "setup permission mn aya", Toast.LENGTH_SHORT).show();
        ActivityCompat.requestPermissions(TrackerActivity.this,
                new String[]{Manifest.permission.SEND_SMS},
                SMS_PERMISSION);

    }

    private void setUpCallPermissions() {
        // Toast.makeText(getApplicationContext(), "setup permission mn aya", Toast.LENGTH_SHORT).show();
        ActivityCompat.requestPermissions(TrackerActivity.this,
                new String[]{Manifest.permission.CALL_PHONE},
                CALL_PERMISSION);

    }

//    private static void showSnack(String snackText, int snackColor) {
//        Snackbar snackbar;
//        snackbar = Snackbar.make(frameLayout, snackText, Snackbar.LENGTH_LONG);
//        View snackBarView = snackbar.getView();
//        snackBarView.setBackgroundColor(snackColor);
//        snackbar.show();
//    }

    private void messageSMS() {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setMessage("Please allow Location Permission to post your request");
        builder.setTitle("Alert");
        builder.setPositiveButton("Go to Settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (!PERMISSION_STATUS) {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivityForResult(intent, SMS_PERMISSION);

                } else {
                    takeSMSPermission();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                Toast.makeText(TrackerActivity.this, "Allow Permission to proceed", Toast.LENGTH_SHORT).show();
                // showSnack("Allow Locaiton Permission to Post your request", getResources().getColor(R.color.snackRed));

            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    private void messageCALL() {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setMessage("Please allow Location Permission to post your request");
        builder.setTitle("Alert");
        builder.setPositiveButton("Go to Settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (!PERMISSION_STATUS) {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivityForResult(intent, CALL_PERMISSION);

                } else {
                    takeSMSPermission();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                Toast.makeText(TrackerActivity.this, "Allow Permission to proceed", Toast.LENGTH_SHORT).show();
                // showSnack("Allow Locaiton Permission to Post your request", getResources().getColor(R.color.snackRed));

            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        this.googleMap = googleMap;
        String lat = userLat;
        String longi = userLong;
        googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        LatLng latLng = new LatLng(Double.valueOf(lat), Double.valueOf(longi));
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title(getAddressFromLatLng(TrackerActivity.this, latLng));
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


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == SMS_PERMISSION) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                PERMISSION_STATUS = true;

            } else {
                PERMISSION_STATUS = false;
                messageSMS();

            }
        }
        if (requestCode == CALL_PERMISSION) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                PERMISSION_STATUS = true;

            } else {
                PERMISSION_STATUS = false;
                messageCALL();

            }
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

                if (googleMap != null) {
                    if (selectMap.equalsIgnoreCase("Normal Map")) {
                        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    } else if (selectMap.equalsIgnoreCase("Hybrid Map")) {
                        googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                    } else if (selectMap.equalsIgnoreCase("Satellite Map")) {
                        googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                    } else if (selectMap.equalsIgnoreCase("Terrain Map")) {
                        googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                    } else {
                        googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                    }
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }
}
