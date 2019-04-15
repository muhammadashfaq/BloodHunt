package bloodcafe.savelet;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static bloodcafe.savelet.constants.BaseurlClass.mBaseURl;

public class SignUp extends AppCompatActivity {
    private EditText etName, etMail, etHomeArea, etCity, etPass, etConPass;
    TextView tvDOB;
    private String userName, userMail, userGender, userBloodGroup, userDOB, userHomeArea, userCity, userPass, userConfPass;
    private int genderArrayPos, bloodArrayPos;
    Spinner spinSex, spinBGroup;
    private int mYear, mMonth, mDay;
    static final int DATE_DIALOG_ID = 0;
    private String monthName;
    int age;
    private DatePickerDialog.OnDateSetListener dPickerDialog;
    Button signUpBtn;
    ArrayAdapter<CharSequence> adapter;
    private ConnectivityManager connectivityManager;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private TextView tvLogin;
    private ProgressDialog progressDialog;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        etName = findViewById(R.id.etname);
        etMail = findViewById(R.id.etmail);
        tvDOB = findViewById(R.id.tvBirthDay);
        etHomeArea = findViewById(R.id.etaddress);
        etCity = findViewById(R.id.etcity);
        etPass = findViewById(R.id.etpass);
        etConPass = findViewById(R.id.etcpass);
        spinSex = findViewById(R.id.select_sex);
        spinBGroup = findViewById(R.id.select_BloodGroup);
        signUpBtn = findViewById(R.id.btn_signup);


        progressDialog = new ProgressDialog(this);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();


        tvLogin = findViewById(R.id.tv_login);
        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SignUp.this, LoginActivity.class);
                startActivity(i);
            }
        });
        tvDOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DisplayDOB();
                showDialog(DATE_DIALOG_ID);

            }
        });

    }

    public void signupMethod(View view) {

        userName = etName.getText().toString().trim();
        userMail = etMail.getText().toString().trim();
        userGender = spinSex.getSelectedItem().toString();
        userBloodGroup = spinBGroup.getSelectedItem().toString();
        userHomeArea = etHomeArea.getText().toString().trim();
        userCity = etCity.getText().toString().trim();
        userPass = etPass.getText().toString().trim();
        userConfPass = etConPass.getText().toString().trim();
        genderArrayPos = spinSex.getSelectedItemPosition();
        bloodArrayPos = spinBGroup.getSelectedItemPosition();

        FormValidation();


    }

    private void WriteNewUser(String userId, String name, String gender, String bloodGroup, String homeArea, String city) {
        UserInfo userInfo = new UserInfo(name, gender, bloodGroup, homeArea, city);
        databaseReference.child("users").child(userId).setValue(userInfo);
        //databaseReference.child("users").child(userId).child("name").setValue(name);
    }

    private boolean IsNetworkConnected() {
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return connectivityManager.getActiveNetworkInfo() != null;
    }

    private void FormValidation()   //to ensure input validation,connection & then register
    {
        if (
                !userName.isEmpty()
                        && !userMail.isEmpty()
                        && !userHomeArea.isEmpty()
                        && !userCity.isEmpty()
                        && !userPass.isEmpty()
                        && !userConfPass.isEmpty()
                        && genderArrayPos != 0
                        && bloodArrayPos != 0
                        && userConfPass.length() > 5
                        && userPass.equals(userConfPass)
                        && IsNetworkConnected())

        {

            progressDialog.setMessage("Registering...");
            progressDialog.show();
            sendNewUserToServer();


        } else if (userName.isEmpty()) {
            etName.setError("Empty ");
            etName.requestFocus();
        } else if (userMail.isEmpty()) {
            etMail.setError("Empty");
            etMail.requestFocus();
        } else if (genderArrayPos == 0) {

            Toast.makeText(getApplicationContext(), "Select Gender plaese", Toast.LENGTH_LONG).show();
        } else if (bloodArrayPos == 0) {
            Toast.makeText(getApplicationContext(), "Select Blood Group plaese", Toast.LENGTH_LONG).show();
        } else if (userHomeArea.isEmpty()) {
            etHomeArea.setError("Empty");
            etHomeArea.requestFocus();
        } else if (userCity.isEmpty()) {
            etCity.setError("Empty");
            etCity.requestFocus();
        } else if (userPass.isEmpty() || userPass.length() < 6) {
            etPass.setError("Enter atleast 6 characters' password ");
            etPass.requestFocus();
        } else if (userConfPass.isEmpty()) {
            etConPass.setError("confirm the password plz");
            etConPass.requestFocus();
        } else if (!userPass.equals(userConfPass)) {
            etConPass.requestFocus();
            etConPass.setError("password doesn't match");
        } else if (!IsNetworkConnected()) {
            Toast.makeText(getApplicationContext(),
                    "Sorry! You are not connected", Toast.LENGTH_LONG).show();
        }
    }

    private void sendNewUserToServer() {
        String url = mBaseURl + getApplicationContext().getResources().getString(R.string.registerNewUserUrl);


        StringRequest mStringRequest = new StringRequest(
                1,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.equalsIgnoreCase("Registered")){
                            startActivity(new Intent(SignUp.this,LoginActivity.class));
                            finish();
                        }else{
                            progressDialog.dismiss();
                            Toast.makeText(SignUp.this, response, Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(SignUp.this, error.toString(), Toast.LENGTH_SHORT).show();

                    }
                }) {
//            private String ,,,,,userCity,,userConfPass;

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("incomingUserName", userName);
                params.put("incomingUserEmail", userMail);
                params.put("incomingUserGender", userGender);
                params.put("incomingUserBloodGroup", userBloodGroup);
                params.put("incomingUserDOB", userDOB);
                params.put("incomingUserAddress", userHomeArea);
                params.put("incomingUserCity", userCity);
                params.put("incomingUserPassword", userPass);

                return params;
            }
        };


        //     this is the qeue that will run the request
        RequestQueue mRequestQueue = Volley.newRequestQueue(this);
        mRequestQueue.add(mStringRequest);
    }

    public void DisplayDOB() {

        dPickerDialog = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                mYear = year;
                mMonth = month;
                mDay = dayOfMonth;
                monthName = Integer.toString(mMonth + 1);
                switch (monthName) {
                    case "1":
                        monthName = "Jan";
                        setDOB(mYear, monthName, mDay);
                        break;
                    case "2":
                        monthName = "Feb";
                        setDOB(mYear, monthName, mDay);
                        break;
                    case "3":
                        monthName = "Mar";
                        setDOB(mYear, monthName, mDay);
                        break;
                    case "4":
                        monthName = "Apr";
                        setDOB(mYear, monthName, mDay);
                        break;
                    case "5":
                        monthName = "May";
                        setDOB(mYear, monthName, mDay);
                        break;
                    case "6":
                        monthName = "Jun";
                        setDOB(mYear, monthName, mDay);
                        break;
                    case "7":
                        monthName = "Jul";
                        setDOB(mYear, monthName, mDay);
                        break;
                    case "8":
                        monthName = "Aug";
                        setDOB(mYear, monthName, mDay);
                        break;
                    case "9":
                        monthName = "Sep";
                        setDOB(mYear, monthName, mDay);
                        break;
                    case "10":
                        monthName = "Oct";
                        setDOB(mYear, monthName, mDay);
                        break;
                    case "11":
                        monthName = "Nov";
                        setDOB(mYear, monthName, mDay);
                        break;
                    case "12":
                        monthName = "Dec";
                        setDOB(mYear, monthName, mDay);
                        break;
                    default:
                        tvDOB.setText("Sorry");


                }
            }
        };
    }

    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == DATE_DIALOG_ID) {
            return new DatePickerDialog(this, dPickerDialog, mYear, mMonth, mDay);
        } else
            return null;
    }

    private void setDOB(int mYear, String monthName, int mDay) {
        userDOB = new StringBuilder().append(monthName).append(" ")
                .append(mDay).append(" ,").append(mYear).append(" ").toString();
        tvDOB.setText(userDOB);

        Date currentDate = new Date();
        age = currentDate.getYear() - mYear;
    }
}

