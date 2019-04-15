package bloodcafe.savelet.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import bloodcafe.savelet.R;
import bloodcafe.savelet.adapters.ListAllUsersRecyclerAdapter;
import bloodcafe.savelet.constants.SessionManager;
import bloodcafe.savelet.models.AllUsers;

import static bloodcafe.savelet.constants.BaseurlClass.mBaseURl;

public class AllUsersActivity extends AppCompatActivity implements ListAllUsersRecyclerAdapter.HomeItemClickCallback {
    RecyclerView rvAllUsers;
    ArrayList<AllUsers> mUserArrayList;
    SessionManager mSessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users);
        rvAllUsers = findViewById(R.id.rvAllUsers);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        mSessionManager = new SessionManager(this);
        LinearLayoutManager mLinearLayoutManger = new LinearLayoutManager(this);
        rvAllUsers.setLayoutManager(mLinearLayoutManger);

        getAllUserListData();
    }

    private void getAllUserListData() {
        String url = mBaseURl + this.getResources().getString(R.string.getAllUsers);
        mUserArrayList = new ArrayList<>();

        StringRequest mStringRequest = new StringRequest(
                1,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Gson mGson = new Gson();
                        Type listType = new TypeToken<ArrayList<AllUsers>>() {
                        }.getType();
                        mUserArrayList = mGson.fromJson(response, listType);
                        ListAllUsersRecyclerAdapter mListAllUsersRecyclerAdapter = new ListAllUsersRecyclerAdapter(AllUsersActivity.this, mUserArrayList);
                        rvAllUsers.setAdapter(mListAllUsersRecyclerAdapter);
                        mListAllUsersRecyclerAdapter.setItemClickCallback(AllUsersActivity.this);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(AllUsersActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("userId", mSessionManager.getKeyuserId());
                return params;
            }
        };

        Volley.newRequestQueue(this).add(mStringRequest);
    }

    @Override
    public void onUserNameClick(int p) {
        Toast.makeText(this, "qwertyuiasdfghxcvbnkjhgfkijuhygtfdkjhg", Toast.LENGTH_SHORT).show();
    }
}
