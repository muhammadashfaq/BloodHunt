package bloodcafe.savelet.Fragment;


import android.annotation.SuppressLint;
import android.support.v4.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.io.File;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;

import bloodcafe.savelet.R;
import bloodcafe.savelet.TrackerActivity;
import bloodcafe.savelet.UserTrackActivity;
import bloodcafe.savelet.adapters.HomeRecyclerAdapter;
import bloodcafe.savelet.models.UserPost;

import static bloodcafe.savelet.constants.BaseurlClass.mBaseURl;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */


public class HomeFragment extends Fragment implements HomeRecyclerAdapter.HomeItemClickCallback, SwipeRefreshLayout.OnRefreshListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private boolean FLAG = false;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public static RecyclerView rvHomePosts;
    ArrayList<UserPost> mUserPostsArrayList;
private ProgressDialog mProgressDialogue;
    SwipeRefreshLayout swipeLayout;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        swipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setColorSchemeColors(android.R.color.holo_green_dark,
                android.R.color.holo_red_dark,
                android.R.color.holo_blue_dark,
                android.R.color.holo_orange_dark);
        rvHomePosts = view.findViewById(R.id.rvHomePosts);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LinearLayoutManager mLinearLayoutManger = new LinearLayoutManager(getActivity());
        rvHomePosts.setLayoutManager(mLinearLayoutManger);
        mProgressDialogue = new ProgressDialog(getActivity());
        mProgressDialogue.setTitle("Please wait");
        mProgressDialogue.setMessage("we are loading...");
        getUserHomePostsData();


    }

    public   void refreshTimeline(){
        FLAG=true;

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



    private void getUserHomePostsData() {
            mProgressDialogue.show();

        String url = mBaseURl + getActivity().getResources().getString(R.string.getAllHomePosts);
        mUserPostsArrayList = new ArrayList<>();

        trimCache(getActivity());
        JsonArrayRequest mJsonArrayRequest = new JsonArrayRequest(
                1,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
//                        Toast.makeText(getActivity(), response.toString(), Toast.LENGTH_SHORT).show();
                        mProgressDialogue.dismiss();
                        Gson mGson = new Gson();
                        Type listType = new TypeToken<ArrayList<UserPost>>() {
                        }.getType();
                        mUserPostsArrayList = mGson.fromJson(response.toString(), listType);
                        HomeRecyclerAdapter mHomeRecyclerAdapter = new HomeRecyclerAdapter(getActivity(), mUserPostsArrayList);
                        rvHomePosts.setAdapter(mHomeRecyclerAdapter);
                        mHomeRecyclerAdapter.notifyDataSetChanged();
                        mHomeRecyclerAdapter.setItemClickCallback(HomeFragment.this);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialogue.dismiss();
                Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        }
        );

        Volley.newRequestQueue(getActivity()).add(mJsonArrayRequest);
    }

    @Override
    public void onUserNameClick(int p) {
        Intent i = new Intent(getActivity(), TrackerActivity.class);
       UserPost mUser =  mUserPostsArrayList.get(p);
        String userData[] = {
                mUser.getUName(),
                mUser.getUserContact(),
                mUser.getUserProfilePic(),
                mUser.getUserId(),
                mUser.getUserPostTime(),
                mUser.getUserBloodRequestType(),
                mUser.getUserlats(),
                mUser.getUserlong(),
                mUser.getUserAddress(),
                mUser.getUserContact()
        };
        i.putExtra("userData",userData);
        startActivity(i);
    }


    @Override
    public void onRefresh() {
        getUserHomePostsData();

        new Handler().postDelayed(new Runnable() {
            @Override public void run() {
               // getUserHomePostsData();
                swipeLayout.setRefreshing(false);
            }
        }, 5000);

    }


}
