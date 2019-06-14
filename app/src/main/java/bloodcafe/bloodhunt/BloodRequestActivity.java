package bloodcafe.bloodhunt;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import bloodcafe.bloodhunt.constants.SessionManager;

import static bloodcafe.bloodhunt.constants.BaseurlClass.isNetworkAvailable;
import static bloodcafe.bloodhunt.constants.BaseurlClass.mBaseURl;

public class BloodRequestActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener , ResultCallback<LocationSettingsResult> {
    private static final String TAG = BloodRequestActivity.class.getSimpleName();
    private Button btnCreateTextPost, btnDiscardTextPost;
    private EditText edt_txt_user_name,
            edt_txt_user_hospital,
            edt_txt_user_address,
            edt_txt_user_city,
            edt_txt_user_relation,
            edt_txt_user_disease,
            edt_txt_user_contact;

    static FrameLayout frameLayout;
    HashMap<String, String> hashMap;
    ArrayList<LatLng> MarkerPoints;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;

    private TextView txt_vu_userName_newTextPost;
    String currentLatitude,currentLongitude;

    private String userName,
            userHospital,
            userAddress,
            userCity,
            userRelation,
            userDisease,
            userBloodGroup,
            userContact;

    private Spinner spnrBloodGroup;
    public static final int LOCATION_REQUEST_CODE = 100;
    public static final int LOCATION_PERMISSION_CODE = 101;
    //Splash Screen Timer
    private static final int SPLASH_SCREEN_TIMER = 1000;
    private LocationRequest mCurrentLocationRequest;
    private String mCurrentLocation = "";
    private SharedPreferences mLocationSharedPreferences;
    boolean gps_enabled = false;
    boolean network_enabled = false;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;

    private ProgressDialog mProgressDialogue;

    boolean PERMISSION_STATUS = false;
    private int LOCATION_PERMISSION = 100;
    private SessionManager mSessionManager;

    private LocationManager locationManager;
    private String provider;
    Location location;
    Activity activity;

    String userLat, userLang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blood_request);
        init();

        mProgressDialogue = new ProgressDialog(this);
        mProgressDialogue.setTitle("Please wait");
        mProgressDialogue.setMessage("Post uploading...");
        mProgressDialogue.setCancelable(false);


        buildGoogleApiClient();

        mSessionManager = new SessionManager(this);
////        userLat =  mSessionManager.getUserLat();
////        userLang =  mSessionManager.getUserLang();

        spnrBloodGroup.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                TextView incomingTextView = (TextView) view;
                userBloodGroup = incomingTextView.getText().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        btnDiscardTextPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnCreateTextPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initTheValues();
                validateTheValuesAndSend();
            }
        });
    }
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }


    private void messageLocationDailog() {


        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(BloodRequestActivity.this);
        builder.setMessage("Please allow location permission >  TURNON LOCATION");
        builder.setTitle("Location Services");
        builder.setPositiveButton("Go to Settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                Toast.makeText(BloodRequestActivity.this, "Please allow the following Permissions ", Toast.LENGTH_LONG).show();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    private void requestLocationUpdates() {

        LocationRequest request = new LocationRequest();
        request.setInterval(1000);
        request.setFastestInterval(1000);
        request.setNumUpdates(1);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);
        //final String path = "https://tracking-app-81674.firebaseio.com/";
        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);


        if (permission == PackageManager.PERMISSION_GRANTED) {
            // Request location updates and when an update is
            // received, store the location in Firebase
            if (isLocationEnabled(BloodRequestActivity.this)) {
                showSnack("Google Location Enabled", getResources().getColor(R.color.snackGreen));
            } else {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    messageLocationDailog();
                }
            }
            client.requestLocationUpdates(request, new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    SessionManager sessionManager = new SessionManager(BloodRequestActivity.this);
                    Location location = locationResult.getLastLocation();
                    if (location != null) {
                        hashMap = new HashMap<>();
                        hashMap.put("Longitude", String.valueOf(location.getLongitude()));
                        hashMap.put("Latitude", String.valueOf(location.getLatitude()));
                    }
                }
            }, null);
        }
    }

    public boolean checkLocationServices() {

        try {
            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }

        try {
            network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        return gps_enabled || network_enabled;
    }

    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        }else{
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }


    }

    private void takePermission() {
        int permissionsAccess = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION);
        int permissionsCourse = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION);

        if (permissionsAccess != PackageManager.PERMISSION_GRANTED && permissionsCourse != PackageManager.PERMISSION_GRANTED)
            setUpPermissions();
        else {
            PERMISSION_STATUS = true;
        }

    }

    private void setUpPermissions() {
        // Toast.makeText(getApplicationContext(), "setup permission mn aya", Toast.LENGTH_SHORT).show();
        ActivityCompat.requestPermissions(BloodRequestActivity.this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                LOCATION_PERMISSION);

    }

    private void message() {


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
                    startActivityForResult(intent, LOCATION_PERMISSION);

                } else {
                    takePermission();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                showSnack("Allow Locaiton Permission to Post your request", getResources().getColor(R.color.snackRed));

            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }


    private void init() {
        btnCreateTextPost = findViewById(R.id.btnCreateTextPost);
        btnDiscardTextPost = findViewById(R.id.btnDiscardTextPost);
        txt_vu_userName_newTextPost = findViewById(R.id.txt_vu_userName_newTextPost);
        edt_txt_user_name = findViewById(R.id.edt_txt_user_name);
        edt_txt_user_hospital = findViewById(R.id.edt_txt_user_hospital);
        edt_txt_user_address = findViewById(R.id.edt_txt_user_address);
        edt_txt_user_city = findViewById(R.id.edt_txt_user_city);
        edt_txt_user_relation = findViewById(R.id.edt_txt_user_relation);
        edt_txt_user_disease = findViewById(R.id.edt_txt_user_disease);
        edt_txt_user_contact = findViewById(R.id.edt_txt_user_contact);
        spnrBloodGroup = findViewById(R.id.spnrBloodGroup);
        frameLayout = findViewById(R.id.main_layout);
    }

    private void initTheValues() {

        userName = edt_txt_user_name.getText().toString();
        userHospital = edt_txt_user_hospital.getText().toString();
        userAddress = edt_txt_user_address.getText().toString();
        userCity = edt_txt_user_city.getText().toString();
        userRelation = edt_txt_user_relation.getText().toString();
        userDisease = edt_txt_user_disease.getText().toString();
        userContact = edt_txt_user_contact.getText().toString();


    }


    private void validateTheValuesAndSend() {
        if (userName.isEmpty()
                && userHospital.isEmpty()
                && userAddress.isEmpty()
                && userCity.isEmpty()
                && userRelation.isEmpty()
                && userDisease.isEmpty()
                && userContact.isEmpty()
        ) {
            edt_txt_user_name.setError("Fill the form");
            edt_txt_user_hospital.setError("Fill the form");
            edt_txt_user_address.setError("Fill the form");
            edt_txt_user_city.setError("Fill the form");
            edt_txt_user_relation.setError("Fill the form");
            edt_txt_user_disease.setError("Fill the form");
            edt_txt_user_contact.setError("Fill the form");
        } else if (userName.isEmpty()
                || userHospital.isEmpty()
                || userAddress.isEmpty()
                || userCity.isEmpty()
                || userRelation.isEmpty()
                || userDisease.isEmpty()
                || userContact.isEmpty()
        ) {

            if (userName.isEmpty())
                edt_txt_user_name.setError("Fill the form");
            if (userHospital.isEmpty())
                edt_txt_user_hospital.setError("Fill the form");
            if (userAddress.isEmpty())
                edt_txt_user_address.setError("Fill the form");
            if (userCity.isEmpty())
                edt_txt_user_city.setError("Fill the form");
            if (userRelation.isEmpty())
                edt_txt_user_relation.setError("Fill the form");
            if (userDisease.isEmpty())
                edt_txt_user_disease.setError("Fill the form");
            if (userContact.isEmpty())
                edt_txt_user_contact.setError("Fill the form");

        } else if (userBloodGroup.equals("Blood")) {
            Toast.makeText(this, "Select a Blood Group", Toast.LENGTH_SHORT).show();
        } else {
            //            send ther request to server with data


//            LocationManager locationManager = (LocationManager)
//                    getSystemService(Context.LOCATION_SERVICE);
//            LocationListener locationListener = new MyLocationListner();
//            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                // TODO: Consider calling
//                //    ActivityCompat#requestPermissions
//                // here to request the missing permissions, and then overriding
//                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                //                                          int[] grantResults)
//                // to handle the case where the user grants the permission. See the documentation
//                // for ActivityCompat#requestPermissions for more details.
//                return;
//            }
//            locationManager.requestLocationUpdates(
//                    LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
            createUserRequestToServer();
        }
    }

    private void createUserRequestToServer() {
        if (isNetworkAvailable(this)) {
            mProgressDialogue.show();
            String url = mBaseURl + this.getResources().getString(R.string.AddNewPostUrl);
            trimCache(this);
            StringRequest mStringRequest = new StringRequest(
                    1,
                    url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            mProgressDialogue.dismiss();
                            Toast.makeText(BloodRequestActivity.this, "Your Request is added", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(BloodRequestActivity.this, HomePage.class);
                            startActivity(intent);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            mProgressDialogue.dismiss();
                            Toast.makeText(BloodRequestActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }) {

                @Override
                protected Map<String, String> getParams() throws AuthFailureError {

                    Map<String, String> params = new HashMap<>();
                    params.put("userName", userName);
                    params.put("userHospital", userHospital);
                    params.put("userAddress", userAddress);
                    params.put("userCity", userCity);
                    params.put("userRelation", userRelation);
                    params.put("userDisease", userDisease);
                    params.put("userBloodGroup", userBloodGroup);
                    params.put("userContact", userContact);
                    params.put("userlats",currentLatitude);
                    params.put("userlong",currentLongitude);
                    params.put("userId", mSessionManager.getKeyuserId());
                    params.put("PostTimeData", new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date()));
//                    if (hashMap != null && !hashMap.isEmpty()) {
//                        Log.i("POST", "in if");
//                        params.put("userlats", hashMap.get("Latitude"));
//                        params.put("userlong", hashMap.get("Longitude"));
//                        params.put("userId", mSessionManager.getKeyuserId());
//                        params.put("PostTimeData", new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date()));
//                    } else {
//                        Log.i("POST", "in else");
//                        params.put("userlats", "32.07");
//                        params.put("userlong", "72.68");
//                        params.put("userId", mSessionManager.getKeyuserId());
//                        params.put("PostTimeData", new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date()));
//                    }

                    return params;
                }

                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {

                    if (response.headers == null) {
                        // cant just set a new empty map because the member is final.
                        response = new NetworkResponse(
                                response.statusCode,
                                response.data,
                                Collections.<String, String>emptyMap(), // this is the important line, set an empty but non-null map.
                                response.notModified,
                                response.networkTimeMs);


                    }
                    return super.parseNetworkResponse(response);
                }
            };


            //     this is the qeue that will run the request
            RequestQueue mRequestQueue = Volley.newRequestQueue(this);
            mRequestQueue.add(mStringRequest);

        } else {
            showSnack("Seems no Internet connection", getResources().getColor(R.color.snackRed));
        }

    }

    private static void showSnack(String snackText, int snackColor) {
        Snackbar snackbar;
        snackbar = Snackbar.make(frameLayout, snackText, Snackbar.LENGTH_LONG);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(snackColor);
        snackbar.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    public static void trimCache(Context context) {
        try {
            File dir = context.getCacheDir();
            if (dir != null && dir.isDirectory()) {
                deleteDir(dir);
            }
        } catch (Exception e) {

        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }

// The directory is now empty so delete it
        return dir.delete();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "onConnected");
        mCurrentLocationRequest = LocationRequest.create();
        mCurrentLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mCurrentLocationRequest.setInterval(50000);
        /**
         * Check runtime permission for Android M and high level SDK
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                if (shouldShowRequestPermissionRationale(
                        Manifest.permission.ACCESS_FINE_LOCATION)) {
                    new AlertDialog.Builder(BloodRequestActivity.this)
                            .setTitle(R.string.location_permission_title)
                            .setMessage(R.string.location_permission_message)
                            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    ActivityCompat.requestPermissions(BloodRequestActivity.this, new String[]{
                                                    Manifest.permission.ACCESS_FINE_LOCATION},
                                            LOCATION_PERMISSION_CODE);
                                }

                            })
                            .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            }).show();
                } else
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            LOCATION_PERMISSION_CODE);
            } else{
                getGPSPermission();
            }

        } else
            getGPSPermission();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        if (LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient) != null) {
            currentLatitude=String.valueOf(
                    LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient).getLatitude());
            currentLongitude=String.valueOf(
                    LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient).getLongitude());

           // Toast.makeText(this, currentLatitude+"\n"+currentLongitude, Toast.LENGTH_SHORT).show();


            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient).getLatitude()
                    + "," + LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient).getLongitude();
        } else {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient,
                    mCurrentLocationRequest,
                    this);
        }
    }
    private void getGPSPermission() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mCurrentLocationRequest);
        builder.setAlwaysShow(true);
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(
                        mGoogleApiClient,
                        builder.build()
                );
        result.setResultCallback(BloodRequestActivity.this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location.getLatitude() + "," +
                location.getLongitude();
        currentLatitude= String.valueOf(location.getLatitude());
        currentLongitude= String.valueOf(location.getLongitude());
       // Toast.makeText(this, currentLatitude+"\n"+currentLongitude, Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onLocationChange");
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!mGoogleApiClient.isConnecting() || !mGoogleApiClient.isConnected())
            mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnecting() || mGoogleApiClient.isConnected())
            mGoogleApiClient.disconnect();
    }

    @Override
    public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
        Status status = locationSettingsResult.getStatus();

        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                // NO need to show the dialog all permission are correct;
                break;

            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                //  Location settings are not satisfied. Show the user a dialog
                try {
                    // Show the dialog by calling startResolutionForResult(), and check the result
                    // in onActivityResult().
                    status.startResolutionForResult(BloodRequestActivity.this, LOCATION_REQUEST_CODE);
                } catch (IntentSender.SendIntentException e) {
                    //failed to show
                    e.printStackTrace();
                }
                break;

            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                // Location settings are unavailable so not possible to show any dialog now
                break;
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                LocationServices.FusedLocationApi.requestLocationUpdates(
                        mGoogleApiClient,
                        mCurrentLocationRequest,
                        this);
            } else {
                new AlertDialog.Builder(BloodRequestActivity.this)
                        .setTitle(R.string.gps_permission_title)
                        .setMessage(R.string.gps_permission_message)
                        .setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Retry for GPS Permission
                                getGPSPermission();
                            }
                        })
                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Kill the application
                                finish();
                            }
                        }).show();
            }
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this,HomePage.class));
    }
}
