package bloodcafe.savelet.Dialogues;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.SupportMapFragment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import bloodcafe.savelet.R;
import bloodcafe.savelet.constants.SessionManager;

import static android.content.Context.LOCATION_SERVICE;
import static bloodcafe.savelet.constants.BaseurlClass.isNetworkAvailable;
import static bloodcafe.savelet.constants.BaseurlClass.mBaseURl;

public class CreateBloodRequestDialogue extends Dialog implements View.OnClickListener {

    private Button btnCreateTextPost, btnDiscardTextPost;
    private EditText edt_txt_user_name,
            edt_txt_user_hospital,
            edt_txt_user_address,
            edt_txt_user_city,
            edt_txt_user_relation,
            edt_txt_user_disease,
            edt_txt_user_contact;



    private TextView txt_vu_userName_newTextPost;

    private String userName,
            userHospital,
            userAddress,
            userCity,
            userRelation,
            userDisease,
            userBloodGroup,
            userContact;

    private Spinner spnrBloodGroup;

    private ProgressDialog mProgressDialogue;

    private Context mContext;

    private SessionManager mSessionManager;

    private LocationManager locationManager;
    private String provider;
    Location location;
    Activity activity;

    public CreateBloodRequestDialogue(@NonNull Context mContext, Location location) {
        super(mContext);
        this.mContext = mContext;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mProgressDialogue = new ProgressDialog(mContext);
        this.location = location;
        mProgressDialogue.setTitle("Please wait");
        mProgressDialogue.setMessage("Image Uploading...");

    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_blood_request_post_design);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

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


        btnCreateTextPost.setOnClickListener(this);
        btnDiscardTextPost.setOnClickListener(this);

        mSessionManager = new SessionManager(mContext);
        txt_vu_userName_newTextPost.setText(mSessionManager.getKeyuserName());
        // filling up the basic value in string of blood group from spinner
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

    }


    // all clicks will be handled here
    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.btnDiscardTextPost: {
                dismiss();
                break;
            }
            case R.id.btnCreateTextPost: {
                initTheValues();
                validateTheValuesAndSend();

                break;
            }

        }

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
            Toast.makeText(mContext, "Select a Blood Group", Toast.LENGTH_SHORT).show();
        } else {
            //            send ther request to server with data
            mProgressDialogue.show();
            createUserRequestToServer();
        }
    }


    private void createUserRequestToServer() {
        if (isNetworkAvailable(mContext)) {

            String url = mBaseURl + mContext.getResources().getString(R.string.AddNewPostUrl);


            SessionManager session =new SessionManager(getContext());
            Toast.makeText(mContext, session.getKeyuserId(), Toast.LENGTH_SHORT).show();

            StringRequest mStringRequest = new StringRequest(
                    1,
                    url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if (response.equalsIgnoreCase("true")) {
                                mProgressDialogue.dismiss();
                                Toast.makeText(mContext, "your request is added", Toast.LENGTH_SHORT).show();
                                dismiss();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            mProgressDialogue.dismiss();
                            Toast.makeText(mContext, error.toString(), Toast.LENGTH_SHORT).show();

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
                    if(location !=null){
                        params.put("userlats", String.valueOf(location.getLatitude()));
                        params.put("userlong", String.valueOf(location.getLongitude()));
                        params.put("userId", mSessionManager.getKeyuserId());
                        params.put("PostTimeData", new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date()));
                    }
                    else{
                        params.put("userlats", "32.0737");
                        params.put("userlong", "72.6804");
                        params.put("userId", mSessionManager.getKeyuserId());
                        params.put("PostTimeData", new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date()));
                    }


                    return params;
                }
            };


            //     this is the qeue that will run the request
            RequestQueue mRequestQueue = Volley.newRequestQueue(mContext);
            mRequestQueue.add(mStringRequest);

        } else {
            Toast.makeText(mContext, "No internet found please recheck", Toast.LENGTH_SHORT).show();
        }

    }

}
