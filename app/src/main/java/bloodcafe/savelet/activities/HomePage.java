package bloodcafe.savelet.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import bloodcafe.savelet.Dialogues.CreateBloodRequestDialogue;
import bloodcafe.savelet.Fragment.HomeFragment;
import bloodcafe.savelet.R;
import bloodcafe.savelet.adapters.HomeRecyclerAdapter;
import bloodcafe.savelet.constants.BaseurlClass;
import bloodcafe.savelet.constants.SessionManager;
import bloodcafe.savelet.models.UserPost;

import static bloodcafe.savelet.constants.BaseurlClass.isNetworkAvailable;
import static bloodcafe.savelet.constants.BaseurlClass.mBaseURl;

public class HomePage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
//
//    FirebaseAuth mAuth;
//    FirebaseAuth.AuthStateListener mAuthStateListener;
//
//    protected void onSta rt()
//    {
//        super.onStart();
//        mAuth.addAuthStateListener(mAuthStateListener);
//    }


    String UserImage;

    SessionManager mSessionManager;
    ArrayList<UserPost> mUserPostsArrayList;
    LinearLayout linLayProgressContainer;
    HomeRecyclerAdapter mHomeRecyclerAdapter;
    CreateBloodRequestDialogue mCreateBloodRequestDialogue;
    CircularImageView userProfileImageView;
    TextView userName, userEmail;

    private LocationManager locationManager;
    private String provider;
    Location location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        linLayProgressContainer = findViewById(R.id.linLayProgressContainer);

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

        }


//        mAuth=FirebaseAuth.getInstance();
//        mAuthStateListener=new FirebaseAuth.AuthStateListener()
//        {
//            @Override
//            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth)
//            {
//                if(firebaseAuth.getCurrentUser()==null)
//                {
//                    startActivity(new Intent(HomePage.this,LoginActivity.class));
//                    finish();
//                }
//
//            }
//        };


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();

// showing the post dialog design

                    // Get the location manager
                    locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                    // Define the criteria how to select the locatioin provider -> use
                    // default
                    Criteria criteria = new Criteria();
                    provider = locationManager.getBestProvider(criteria, false);
                    if (ActivityCompat.checkSelfPermission(HomePage.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(HomePage.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        ActivityCompat.requestPermissions(HomePage.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 2);
                        return;
                    } else {
                        location = locationManager.getLastKnownLocation(provider);

                        // Initialize the location fields
                        if (location != null) {
                            System.out.println("Provider " + provider + " has been selected.");
//            onLocationChanged(location);
                        } else {
//            latituteField.setText("Location not available");
//            longitudeField.setText("Location not available");
                        }
                    }

                    mCreateBloodRequestDialogue = new CreateBloodRequestDialogue(HomePage.this,location);
                mCreateBloodRequestDialogue.show();
//                mCreateBloodRequestDialogue.setOnDismissListener(new DialogInterface.OnDismissListener() {
//                    @Override
//                    public void onDismiss(DialogInterface dialog) {
//                        recreate();
////                        rvHomePosts.getAdapter().
//                    }
//                });

            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
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
        if (id == R.id.action_logout) {
            mSessionManager.setLogin(false, "", "", "", "");
            startActivity(new Intent(this, LoginActivity.class));
            finish();
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

        }else if (id == R.id.sign_out) {
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
                            Picasso.get().load(BaseurlClass.mBaseURl + HomePage.this.getResources().getString(R.string.ProfileImagePath) + UserImage).into(userProfileImageView);

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
