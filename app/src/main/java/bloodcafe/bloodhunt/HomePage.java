package bloodcafe.bloodhunt;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.login.LoginManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import bloodcafe.bloodhunt.Fragment.HomeFragment;
import bloodcafe.bloodhunt.adapters.HomeRecyclerAdapter;
import bloodcafe.bloodhunt.constants.BaseurlClass;
import bloodcafe.bloodhunt.constants.SessionManager;
import bloodcafe.bloodhunt.models.UserPost;

import static bloodcafe.bloodhunt.constants.BaseurlClass.isNetworkAvailable;
import static bloodcafe.bloodhunt.constants.BaseurlClass.mBaseURl;

public class HomePage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    boolean  PERMISSION_STATUS = false;
    private int LOCATION_PERMISSION = 100;
    DatabaseReference dbRef;
    String UserImage;
    static CoordinatorLayout coordinatorLayout;

    SessionManager mSessionManager;
    ArrayList<UserPost> mUserPostsArrayList;
    LinearLayout linLayProgressContainer;
    HomeRecyclerAdapter mHomeRecyclerAdapter;
    CircularImageView userProfileImageView;
    TextView userName, userEmail;

    private LocationManager locationManager;
    private String provider;
    Location location;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        linLayProgressContainer = findViewById(R.id.linLayProgressContainer);
        coordinatorLayout = findViewById(R.id.main_layout);

        mSessionManager = new SessionManager(this);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        userName = navigationView.getHeaderView(0).findViewById(R.id.userName);
        userEmail = navigationView.getHeaderView(0).findViewById(R.id.userEmail);
        userProfileImageView = navigationView.getHeaderView(0).findViewById(R.id.userProfileImageView);


        if (isNetworkAvailable(this)) {
            getUserProfileDetails();
            linLayProgressContainer.setVisibility(View.GONE);
            HomeFragment mHomeFragment = new HomeFragment();
            loadFragmentWithOutBackStack(mHomeFragment);
//            getUserHomePostsData(mSessionManager.getKeyuserId());
        } else {
            linLayProgressContainer.setVisibility(View.GONE);
            showSnack("Seems no Internet connection.",getResources().getColor(R.color.snackRed));
        }


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomePage.this,BloodRequestActivity.class));
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


    }

    private static void showSnack(String snackText, int snackColor) {
        Snackbar snackbar;
        snackbar = Snackbar.make(coordinatorLayout, snackText, Snackbar.LENGTH_LONG);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(snackColor);
        snackbar.show();
    }

    private void requestLocationUpdates() {
        LocationRequest request = new LocationRequest();
        request.setInterval(10000);
        request.setFastestInterval(5000);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);
        //final String path = "https://tracking-app-81674.firebaseio.com/";
        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);


        if (permission == PackageManager.PERMISSION_GRANTED)
        {
            // Request location updates and when an update is
            // received, store the location in Firebase
            client.requestLocationUpdates(request, new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    SessionManager sessionManager=new SessionManager(HomePage.this);
                    Location location = locationResult.getLastLocation();
                    if (location != null) {
                        HashMap<String,String> hashMap=new HashMap<>();
                        hashMap.put("Longitude", String.valueOf(location.getLongitude()));
                        hashMap.put("Latitude",String.valueOf(location.getLatitude()));
                        Log.d("Location", "location update " + location);
                    }
                }
            }, null);
        }
    }

    private void takePermission() {
        int permissionsAccess = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION);
        int permissionsCourse = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION);

        if(permissionsAccess != PackageManager.PERMISSION_GRANTED && permissionsCourse != PackageManager.PERMISSION_GRANTED)
            setUpPermissions();
        else  {
            PERMISSION_STATUS = true;
        }

    }

    private void setUpPermissions() {
       // Toast.makeText(getApplicationContext(), "setup permission mn aya", Toast.LENGTH_SHORT).show();
        ActivityCompat.requestPermissions(HomePage.this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                LOCATION_PERMISSION);

    }

    private void message() {


        android.support.v7.app.AlertDialog.Builder builder= new android.support.v7.app.AlertDialog.Builder(this);
        builder.setMessage("Please Allow All the Permissions For APP its required in this Application");
        builder.setTitle("Alert");
        builder.setPositiveButton("Go to Settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (!PERMISSION_STATUS){
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivityForResult(intent, LOCATION_PERMISSION);

                }else {
                    takePermission();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                Toast.makeText(HomePage.this,"Please allow the following Permissions ", Toast.LENGTH_LONG).show();
                HomePage.this.finish();
            }
        });

        AlertDialog alertDialog=builder.create();
        alertDialog.show();

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==LOCATION_PERMISSION){
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                PERMISSION_STATUS=true;

            } else {
                PERMISSION_STATUS = false;
                message();

            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

//        if(id == R.id.action_refresh){
//            HomeFragment fragment = (HomeFragment) getFragmentManager().findFragmentById(R.id.example_fragment);
//            fragment.refreshTimeline();
//        }
        if (id == R.id.action_logout) {

        getUserProfileDetails();
//            String respose = SessionManager.getStoreVarSocialLogin();
//            if(respose.equalsIgnoreCase("facebook")){
//                if(FirebaseAuth.getInstance().getCurrentUser()!=null){
//                    FirebaseAuth.getInstance().signOut();
//                    LoginManager.getInstance().logOut();
//                    mSessionManager.setStoreVarSocialLogin("");
//                    startActivity(new Intent(this, LoginActivity.class));
//                }
//
//
//            }
//
//            if(respose.equalsIgnoreCase("google")){
//                if(FirebaseAuth.getInstance().getCurrentUser()!=null){
//                    FirebaseAuth.getInstance().signOut();
//                    mSessionManager.setStoreVarSocialLogin("");
//                    startActivity(new Intent(this, LoginActivity.class));
//                }
//            }
//
//            mSessionManager.setLogin(false, "", "", "", "");
//            startActivity(new Intent(this, LoginActivity.class));
//            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();




        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_slideshow) {
            startActivity(new Intent(this,AllUsersActivity.class));

        } else if (id == R.id.nav_manage) {

            startActivity(new Intent(this,SettingsActivity.class));

        }else if(id == R.id.nav_nearby_hospitals){
            startActivity(new Intent(this,NearbyHospitals.class));
        }else if(id == R.id.nav_nearby_blood_banks){
            startActivity(new Intent(this,NearbyBloodbanks.class));
        }else if (id == R.id.sign_out) {

            String respose = SessionManager.getStoreVarSocialLogin();
            if(respose.equalsIgnoreCase("facebook")) {
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    FirebaseAuth.getInstance().signOut();
                    LoginManager.getInstance().logOut();
                    mSessionManager.setStoreVarSocialLogin("");
                    startActivity(new Intent(this, LoginActivity.class));
                }
            }

            if(respose.equalsIgnoreCase("google")){
                if(FirebaseAuth.getInstance().getCurrentUser()!=null){
                    FirebaseAuth.getInstance().signOut();
                    mSessionManager.setStoreVarSocialLogin("");
                    startActivity(new Intent(this, LoginActivity.class));
                }
            }

            mSessionManager.setLogin(false, "", "", "", "");
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void loadFragmentWithOutBackStack(Fragment mFragment) {

        FragmentManager mFragmentManager = getSupportFragmentManager();
        FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentTransaction.replace(R.id.fragContainer, mFragment);
        mFragmentTransaction.commit();
    }


    public void getUserProfileDetails() {
        String url = mBaseURl + getApplicationContext().getResources().getString(R.string.getUserProfileDetails);
        String response = SessionManager.getStoreVarSocialLogin();
        Toast.makeText(this, response, Toast.LENGTH_SHORT).show();
        if(response.equalsIgnoreCase("google")){
            SessionManager sessionManager=new SessionManager(this);
          //  Toast.makeText(this, sessionManager.getKeyuserId(), Toast.LENGTH_SHORT).show();
           userName.setText(SessionManager.getUser_profile_name());
            userEmail.setText(SessionManager.getUser_profile_email());
            Picasso.get().load(SessionManager.getUser_profile_photo()).into(userProfileImageView);
            return;
        }else if(response.equalsIgnoreCase("facebook")){
            Toast.makeText(this, "facebook", Toast.LENGTH_SHORT).show();
            SessionManager sessionManager=new SessionManager(this);
            userName.setText(SessionManager.getUser_profile_name());
            Picasso.get().load(SessionManager.getUser_profile_photo()).into(userProfileImageView);
           // return;
        }else{

        }

        StringRequest mStringRequest = new StringRequest(
                1,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray mJsonArray = new JSONArray(response);
                            JSONObject mJsonObject = mJsonArray.getJSONObject(0);
                            UserImage = mJsonObject.getString("user_profile_pic");
                            userName.setText(mJsonObject.getString("u_name"));
                            userEmail.setText(mJsonObject.getString("u_email"));
                            Picasso.get().load(BaseurlClass.mBaseURl + HomePage.this.getResources().getString(R.string.ProfileImagePath) + UserImage).placeholder(R.drawable.default_user).into(userProfileImageView);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put("userId", mSessionManager.getKeyuserId());
                return params;
            }
        };


        //     this is the qeue that will run the request
        RequestQueue mRequestQueue = Volley.newRequestQueue(this);
        mRequestQueue.add(mStringRequest);


    }

}
