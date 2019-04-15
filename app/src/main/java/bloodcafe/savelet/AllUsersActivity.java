package bloodcafe.savelet;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import bloodcafe.savelet.adapters.ListAllUsersRecyclerAdapter;
import bloodcafe.savelet.constants.SessionManager;
import bloodcafe.savelet.models.AllUsers;

import static bloodcafe.savelet.constants.BaseurlClass.mBaseURl;

public class AllUsersActivity extends AppCompatActivity implements ListAllUsersRecyclerAdapter.HomeItemClickCallback ,SwipeRefreshLayout.OnRefreshListener{
    RecyclerView rvAllUsers;
    ArrayList<AllUsers> mUserArrayList;
    SessionManager mSessionManager;
    SwipeRefreshLayout swipeLayout;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users);
        rvAllUsers = findViewById(R.id.rvAllUsers);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        swipeLayout = findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setColorSchemeColors(android.R.color.holo_green_dark,
                android.R.color.holo_red_dark,
                android.R.color.holo_blue_dark,
                android.R.color.holo_orange_dark);
        mSessionManager = new SessionManager(this);
        LinearLayoutManager mLinearLayoutManger = new LinearLayoutManager(this);
        rvAllUsers.setLayoutManager(mLinearLayoutManger);

        getAllUserListData();
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


    private void getAllUserListData() {
        String url = mBaseURl + this.getResources().getString(R.string.getAllUsers);
        mUserArrayList = new ArrayList<>();

        trimCache(this);
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
                        swipeLayout.setRefreshing(false);
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

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onRefresh() {
        getAllUserListData();

    }
}
