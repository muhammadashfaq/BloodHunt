package bloodcafe.savelet.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import bloodcafe.savelet.R;
import bloodcafe.savelet.constants.BaseurlClass;
import bloodcafe.savelet.constants.SessionManager;

import static bloodcafe.savelet.constants.BaseurlClass.mBaseURl;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    private String UserImage;
    private CircularImageView img_vu_userProfile;
    private SessionManager mSessionManager;
    private ImageView img_vu_uploadNewProfilePic;
    private int PICK_IMAGE_REQUEST = 1;
    String image;
    ProgressDialog mProgressDialogue;
    Uri filePath;
    String imageName = "image";
    Random rand;
    int imageRand;

    EditText edtTxtUserName, edtTxtUserEmail, edtTxtUserAddress, edtTxtUserCity;
    String UserName, UserEmail, UserAddress, UserCity, UserBlood;

    Button btnCancelUpdate, btnSaveUpdates;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar3);
        setSupportActionBar(toolbar);
        img_vu_userProfile = findViewById(R.id.img_vu_userProfile);
        img_vu_uploadNewProfilePic = findViewById(R.id.img_vu_uploadNewProfilePic);
        edtTxtUserName = findViewById(R.id.edtTxtUserName);
        edtTxtUserEmail = findViewById(R.id.edtTxtUserEmail);
        edtTxtUserAddress = findViewById(R.id.edtTxtUserAddress);
        edtTxtUserCity = findViewById(R.id.edtTxtUserCity);

        btnCancelUpdate = findViewById(R.id.btnCancelUpdate);
        btnSaveUpdates = findViewById(R.id.btnSaveUpdates);

        mSessionManager = new SessionManager(this);
        mProgressDialogue = new ProgressDialog(this);
        rand = new Random();
        mProgressDialogue = new ProgressDialog(this);
        mProgressDialogue.setTitle("Please wait");
        mProgressDialogue.setMessage("we are loading...");


        img_vu_uploadNewProfilePic.setOnClickListener(this);
        getUserProfileDetails();
        btnSaveUpdates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserName = edtTxtUserName.getText().toString();
                UserEmail = edtTxtUserEmail.getText().toString();
                UserAddress = edtTxtUserAddress.getText().toString();
                UserCity = edtTxtUserCity.getText().toString();
                validationPlease();

            }
        });
        btnCancelUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void validationPlease() {
        if (!UserName.isEmpty()
                && !UserEmail.isEmpty()
                && !UserAddress.isEmpty()
                && !UserCity.isEmpty()
                ) {
            mProgressDialogue.show();
            updateTheDataOnServer();

        }

    }

    private void updateTheDataOnServer() {
        String url = mBaseURl + getApplicationContext().getResources().getString(R.string.UpdateUserProfile);


        StringRequest mStringRequest = new StringRequest(
                1,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        mProgressDialogue.dismiss();
                        mSessionManager.setKeyUserEmail(UserEmail);
                        mSessionManager.setKeyUserName(UserName);
                        recreate();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mProgressDialogue.dismiss();
                        Toast.makeText(SettingsActivity.this, error.toString(), Toast.LENGTH_SHORT).show();

                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("UserName",UserName);
                params.put("UserEmail",UserEmail);
                params.put("UserAddress",UserAddress);
                params.put("UserCity",UserCity);
                params.put("UserID",mSessionManager.getKeyuserId());
                return params;
            }
        };


        //     this is the qeue that will run the request
        RequestQueue mRequestQueue = Volley.newRequestQueue(this);
        mRequestQueue.add(mStringRequest);
    }

    public void getUserProfileDetails() {
        mProgressDialogue.show();
        String url = mBaseURl + getApplicationContext().getResources().getString(R.string.getUserProfileDetails);


        StringRequest mStringRequest = new StringRequest(
                1,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            mProgressDialogue.dismiss();
                            JSONArray mJsonArray = new JSONArray(response);
                            JSONObject mJsonObject = mJsonArray.getJSONObject(0);
                            UserImage = mJsonObject.getString("user_profile_pic");
                            edtTxtUserName.setText(mJsonObject.getString("u_name"));
                            edtTxtUserEmail.setText(mJsonObject.getString("u_email"));
                            edtTxtUserCity.setText(mJsonObject.getString("user_city"));
                            edtTxtUserAddress.setText(mJsonObject.getString("u_address"));
//                            userName.setText(mJsonObject.getString("u_name"));
//                            userEmail.setText(mJsonObject.getString("u_email"));
                            Picasso.get().load(BaseurlClass.mBaseURl + SettingsActivity.this.getResources().getString(R.string.ProfileImagePath) + UserImage).into(img_vu_userProfile);

                        } catch (JSONException e) {
                            mProgressDialogue.dismiss();
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mProgressDialogue.dismiss();
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_vu_uploadNewProfilePic: {
                imageRand = rand.nextInt();

                showFileChooser();
                break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            mProgressDialogue.show();
            filePath = data.getData();
            try {

                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);

                Bitmap lastBitmap = null;
                lastBitmap = bitmap;
//                encoding image to string
                image = getStringImage(lastBitmap);
                Log.d("image", image);
                //passing the image to volley
                SendImage();
                img_vu_userProfile.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }

//            for cover changing
        }
    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 70, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;

    }


    //Gallery openers for Application
    private void showFileChooser() {
        Intent pickImageIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickImageIntent.setType("image/*");
        pickImageIntent.putExtra("aspectX", 1);
        pickImageIntent.putExtra("aspectY", 1);
        pickImageIntent.putExtra("scale", true);
        pickImageIntent.putExtra("outputFormat",
                Bitmap.CompressFormat.JPEG.toString());
        startActivityForResult(pickImageIntent, PICK_IMAGE_REQUEST);
    }


    private void SendImage() {
        mProgressDialogue.show();
//        Toast.makeText(this, ""+msSessionManager.getKeyUserId()+"\n"+image+"\n"+imageName + imageRand, Toast.LENGTH_SHORT).show();
        StringRequest mStringRequest = new StringRequest(1, BaseurlClass.mBaseURl + getApplicationContext().getResources().getString(R.string.AddNewProfilePicUrl), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mProgressDialogue.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialogue.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id", mSessionManager.getKeyuserId());
                params.put("image", image);
                params.put("imageName", imageName + imageRand);
                return params;
            }
        };
        mStringRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(this).add(mStringRequest);

    }
}
