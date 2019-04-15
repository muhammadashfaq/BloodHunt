package bloodcafe.savelet;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.flags.Flag;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import bloodcafe.savelet.constants.SessionManager;

import static bloodcafe.savelet.constants.BaseurlClass.mBaseURl;

public class LoginActivity extends AppCompatActivity {

    EditText etMail, etPass;
    Button btnSignIn;
    ImageView signInGoogleImg,signInFacebookImg;
    TextView tvSignUp, tvForgotPassword;
    FirebaseAuth mAuth;
    ImageView logo_iv;
    private static final int RC_SIGN_IN = 2;
    ProgressDialog progressDialog;
    private ConnectivityManager connectivityManager;
    private NetworkInfo networkInfo;

    GoogleApiClient mGoogleApiClient;
    FirebaseAuth.AuthStateListener mAuthListener;
    SignInButton signInButton;

    CallbackManager callbackManager;
    LoginButton loginButton;
    String email,name,gender,dateofbirth,Picid;
    SessionManager mSessionManager;
    String l_name,profile_image,full_name;

    String FLAG="";
    String personName,personEmail;

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        tvSignUp = findViewById(R.id.tv_signUp);

        signInButton=findViewById(R.id.sign_in_button);
        loginButton = findViewById(R.id.login_button);
        etMail = findViewById(R.id.etmail);
        etPass = findViewById(R.id.etpass);
        btnSignIn = findViewById(R.id.btnSignIn);
        tvForgotPassword = findViewById(R.id.tv_forgotpassword);
        logo_iv = findViewById(R.id.logo_iv);

        //
        printHashKey(this);
        callbackManager  = CallbackManager.Factory.create();
        loginButton.setReadPermissions("email","public_profile");
        logo_iv.requestFocus();

        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        mSessionManager = new SessionManager(this);


        //facebook login

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {


            @Override
            public void onSuccess(LoginResult loginResult) {

                SessionManager sessionManager=new SessionManager(LoginActivity.this);
                sessionManager.setStoreVarSocialLogin("facebook");
                FLAG="Facebook";


                Profile profile= Profile.getCurrentProfile();
                if (profile != null) {
                    String facebook_id=profile.getId();
                    String f_name=profile.getFirstName();
                    String m_name=profile.getMiddleName();
                   l_name=profile.getLastName();
                   full_name=profile.getName();
                   profile_image=profile.getProfilePictureUri(400, 400).toString();
                   SessionManager.storeUserProfileData(full_name,l_name, Uri.parse(profile_image));
                   sendNewUserToServer();
                   logTheUserThroughSocail();
                }

                GraphRequest graphRequest= GraphRequest.newMeRequest(loginResult.getAccessToken(),new GraphRequest.GraphJSONObjectCallback(){

                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                     //   getFacebookProfileData(object);
                    }
                });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender, birthday");
                graphRequest.setParameters(parameters);
                graphRequest.executeAsync();

                Log.i("Facebook","facebook:onSuccess:"+ loginResult);
                handleAccessFacebookToken(loginResult.getAccessToken());

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
                Log.i("FAcebook","facebook:failed:"+ error.getMessage());
            }
        });


//check session
        if(mSessionManager.isLoggedIn()){
            Intent i = new Intent(this, HomePage.class);
            startActivity(i);
            finish();
        }

        tvSignUp.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this, SignUp.class);
                startActivity(i);
            }
        });
        tvForgotPassword.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this, ForgotPassword.class);
                startActivity(i);

            }
        });

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setMessage("Plz wait...");
                progressDialog.show();
                signIn();
            }
        });



        mAuthListener = new FirebaseAuth.AuthStateListener() {

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                    startActivity(new Intent(LoginActivity.this, HomePage.class));
                    progressDialog.dismiss();
                    finish();
                }

            }
        };

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "something wemt wrong", Toast.LENGTH_SHORT).show();
            }
        }).addApi(Auth.GOOGLE_SIGN_IN_API, gso).build();


    }

    private void getFacebookProfileData(JSONObject jsonObject){
        try{
            email= jsonObject.getString("email");
            name = jsonObject.getString("name");
            gender = jsonObject.getString("gender");
            dateofbirth = jsonObject.getString("birthday");
            Toast.makeText(this, email+name+gender+dateofbirth, Toast.LENGTH_SHORT).show();


        }catch (JSONException e){
            e.printStackTrace();
        }
    }
    // Configure Google Sign In
    //GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
    //.requestIdToken(getString(R.string.default_web_client_id))
    // .requestEmail()
    //.build();
    public static void printHashKey(Context pContext) {
        try {
            PackageInfo info = pContext.getPackageManager().getPackageInfo(pContext.getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String hashKey = new String(Base64.encode(md.digest(), 0));
                Log.i("Hashkey", "printHashKey() Hash Key: " + hashKey);
            }
        } catch (NoSuchAlgorithmException e) {
            Log.e("Haskey", "printHashKey()", e);
        } catch (Exception e) {
            Log.e("Hashkey", "printHashKey()", e);
        }
    }
    private void signIn() {
        SessionManager sessionManager=new SessionManager(LoginActivity.this);
        sessionManager.setStoreVarSocialLogin("google");
        FLAG="Google";
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
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
                            Toast.makeText(LoginActivity.this, "Regsitered", Toast.LENGTH_SHORT).show();
                        }else{
                         //   progressDialog.dismiss();
                           // Toast.makeText(LoginActivity.this, response, Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                     //   progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this, error.toString(), Toast.LENGTH_SHORT).show();

                    }
                }) {
//            private String ,,,,,userCity,,userConfPass;

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();

                if(SessionManager.getStoreVarSocialLogin().equals("google")){

                    params.put("incomingUserName", "");
                    params.put("incomingUserEmail", personEmail);
                    params.put("incomingUserGender", "");
                    params.put("incomingUserBloodGroup", "");
                    params.put("incomingUserDOB", "");
                    params.put("incomingUserAddress", "");
                    params.put("incomingUserCity", "");
                    params.put("incomingUserPassword", "");
                   // return params;
                }


                if(SessionManager.getStoreVarSocialLogin().equals("facebook")){

                    params.put("incomingUserName", "");
                    params.put("incomingUserEmail", full_name);
                    params.put("incomingUserGender", "");
                    params.put("incomingUserBloodGroup", "");
                    params.put("incomingUserDOB", "");
                    params.put("incomingUserAddress", "");
                    params.put("incomingUserCity", "");
                    params.put("incomingUserPassword", "");
                //    return params;
                }

                return params;

            }
        };


        //     this is the qeue that will run the request
        RequestQueue mRequestQueue = Volley.newRequestQueue(this);
        mRequestQueue.add(mStringRequest);
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(callbackManager!=null){
            callbackManager.onActivityResult(requestCode,resultCode,data);
        }


        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("TAG", "Google sign in failed", e);
                // ...
            }
        }
    }

    private void handleAccessFacebookToken(AccessToken accessToken){
        Log.d("FAceboook", "handleFacebookAccessToken:" + accessToken);

        AuthCredential authCredential= FacebookAuthProvider.getCredential(accessToken.getToken());

        mAuth.signInWithCredential(authCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser currentUser = mAuth.getCurrentUser();

                    //updateUI(currentUser);
                }else{
                    Log.w("FAcebook", "signInWithCredential:failure", task.getException());
                    Toast.makeText(LoginActivity.this, "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                    // updateUI(null);
                }
            }
        });


    }


    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if (acct != null) {
            personName = acct.getDisplayName();
            String personGivenName = acct.getGivenName();
            String personFamilyName = acct.getFamilyName();
            personEmail = acct.getEmail();
            String personId = acct.getId();
            Uri personPhoto = acct.getPhotoUrl();

            SessionManager.storeUserProfileData(personName,personEmail,personPhoto);
           // Toast.makeText(this, personName+personEmail+personPhoto, Toast.LENGTH_SHORT).show();
            sendNewUserToServer();
            logTheUserThroughSocail();
        }

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "signInWithCredential:failure", task.getException());
                            Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                        // ...
                    }
                });
    }

    public void loginMethod(View view)
    {
        String mail = etMail.getText().toString().trim();
        String pass = etPass.getText().toString().trim();

        if (!mail.isEmpty() && !pass.isEmpty() && IsNetworkConnected()) {


            LogTheUser(mail, pass);
//            mAuth.signInWithEmailAndPassword(mail,pass)
//                    .addOnCompleteListener(new OnCompleteListener<AuthResult>()
//                    {
//                        @Override
//                        public void onComplete(@NonNull Task<AuthResult> task)
//                        {
//                            if(task.isSuccessful())
//                            {
//                                progressDialog.dismiss();
//                                Toast.makeText(getApplicationContext(),"Logged in",Toast.LENGTH_LONG).show();
//                                startActivity(new Intent(LoginActivity.this,HomePage.class));
//                                finish();
//                            }
//                            else
//                                {
//                                progressDialog.dismiss();
//                                String errorCode= ((FirebaseAuthException)task.getException()).getErrorCode();
//                                switch (errorCode)
//                                {
//                                    case "ERROR_USER_NOT_FOUND":
//                                        Toast.makeText(LoginActivity.this,
//                                                "No user registered against this email. The user may have been deleted.",
//                                                Toast.LENGTH_LONG).show();
//                                        break;
//                                    case "ERROR_WRONG_PASSWORD":
//                                        Toast.makeText(LoginActivity.this,
//                                                "The password is invalid or the user does not have a password.",
//                                                Toast.LENGTH_LONG).show();
//                                        etPass.setError("password is incorrect ");
//                                        etPass.requestFocus();
//                                        etPass.setText("");
//                                        break;
//                                }
//
//                            }
//                        }
//                    });
        } else if (mail.isEmpty()) {
            etMail.setError("Email is empty ");
            etMail.requestFocus();
        } else if (pass.isEmpty()) {
            etPass.setError("Password is empty ");
            etPass.requestFocus();
        } else if (!IsNetworkConnected()) {
            Toast.makeText(getApplicationContext(), "Not Connected", Toast.LENGTH_LONG).show();
        } else
            Toast.makeText(getApplicationContext(), "Sorry! Unexpected error", Toast.LENGTH_LONG).show();

    }



    private void logTheUserThroughSocail(){
        String url = mBaseURl + getApplicationContext().getResources().getString(R.string.LoginUserUrlSocail);

        StringRequest mStringRequest = new StringRequest(1, url, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                //Toast.makeText(LoginActivity.this, response, Toast.LENGTH_LONG).show();
                if(!response.equals("no user present")){

                    try {

                        JSONObject mJsonObject = new JSONObject(response);
                        String userId = mJsonObject.getString("u_id");
                        Toast.makeText(LoginActivity.this, userId, Toast.LENGTH_SHORT).show();
                        mSessionManager.setLogin(
                                true,
                                "",
                                "",
                                userId,
                                ""
                        );

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Log.i("error",error.getMessage());
                        //progressDialog.dismiss();
                       // Toast.makeText(LoginActivity.this, "check your connection and retry  "+error.getMessage(), Toast.LENGTH_LONG).show();

                    }
                })
        {

            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<>();

                if(SessionManager.getStoreVarSocialLogin().equals("google")){
                    // Toast.makeText(LoginActivity.this, "in", Toast.LENGTH_SHORT).show();
                    params.put("incomingUserEmail",personEmail);
                }


                if(SessionManager.getStoreVarSocialLogin().equals("facebook")){
                   // Toast.makeText(LoginActivity.this, "in", Toast.LENGTH_SHORT).show();
                    params.put("incomingUserEmail",full_name);
                }

                return params;
            }
        };


//     this is the qeue that will run the request
        RequestQueue mRequestQueue = Volley.newRequestQueue(this);
        mRequestQueue.add(mStringRequest);
    }

    private void LogTheUser(final String mail, final String pass) {

        String url = mBaseURl + getApplicationContext().getResources().getString(R.string.LoginUserUrl);



        progressDialog.setMessage("Plz wait...");
        progressDialog.show();


        StringRequest mStringRequest = new StringRequest(1, url, new Response.Listener<String>()
        {
                    @Override
                    public void onResponse(String response) {

//                        Toast.makeText(LoginActivity.this, response, Toast.LENGTH_LONG).show();
                        if(!response.equals("no user present")){
                            progressDialog.dismiss();
                            try {
                                JSONObject mJsonObject = new JSONObject(response);
                                String userName = mJsonObject.getString("u_name");
                                String userId = mJsonObject.getString("u_id");
                                Toast.makeText(LoginActivity.this, userId, Toast.LENGTH_SHORT).show();

                                Intent mIntent = new Intent(LoginActivity.this,HomePage.class);
                                mSessionManager.setLogin(
                                        true,
                                        mail,
                                        pass,
                                        userId,
                                        userName
                                );
                                startActivity(mIntent);
                                finish();




                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }else{
                            progressDialog.dismiss();
                            Toast.makeText(LoginActivity.this, "Error", Toast.LENGTH_SHORT).show();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this, "check your connection and retry"+error.getMessage(), Toast.LENGTH_LONG).show();

                    }
                })
        {

            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<>();
                params.put("incomingUserEmail",mail);
                params.put("incomingUserPass",pass);

                return params;
            }
        };


//     this is the qeue that will run the request
        RequestQueue mRequestQueue = Volley.newRequestQueue(this);
        mRequestQueue.add(mStringRequest);

    }


    //    -------------------------------------  this is network checking method    ------------  //
    private boolean IsNetworkConnected() {


        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connectivityManager.getActiveNetworkInfo();
        return (connectivityManager.getActiveNetworkInfo() != null && networkInfo.isConnected());
    }


}
