package bloodcafe.savelet.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Response;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;

import java.util.ArrayList;

import bloodcafe.savelet.R;
import bloodcafe.savelet.adapters.ViewHolders.HomeRecyclerViewHolder;
import bloodcafe.savelet.adapters.ViewHolders.ListAllUsersRecyclerViewHolder;
import bloodcafe.savelet.constants.BaseurlClass;
import bloodcafe.savelet.constants.SessionManager;
import bloodcafe.savelet.models.AllUsers;
import bloodcafe.savelet.models.UserPost;

public class ListAllUsersRecyclerAdapter extends RecyclerView.Adapter<ListAllUsersRecyclerViewHolder> {

    private Context context;
    private ArrayList<AllUsers> mPostArrayList;
    private LayoutInflater mLayoutInflater;
    private SessionManager mSessionManager;
    public static ListAllUsersRecyclerAdapter.HomeItemClickCallback itemClickCallback;



    //an interface for making this recyclerView ClickAble
    public interface HomeItemClickCallback {
        void onUserNameClick(int p);
    }

    public void setItemClickCallback(final HomeItemClickCallback itemClickCallback) {
        this.itemClickCallback = itemClickCallback;
    }


    public ListAllUsersRecyclerAdapter(Context context, ArrayList<AllUsers> mPostArrayList) {
        this.context = context;
        this.mPostArrayList = mPostArrayList;
        mLayoutInflater = LayoutInflater.from(context);
        mSessionManager = new SessionManager(context);
    }


    @NonNull
    @Override
    public ListAllUsersRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.all_user_model_card_design, parent, false);
        return new ListAllUsersRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListAllUsersRecyclerViewHolder holder, int position) {
        final AllUsers mUser = mPostArrayList.get(position);
//        final String number = mUser.get();
        holder.txtVuUserName.setText(mUser.getUName());
        holder.txtVuUserPostCity.setText(mUser.getUAddress());
        holder.txtVuBloodType.setText(mUser.getUserBloodGroup());
        String imageAddress = BaseurlClass.mBaseURl + context.getResources().getString(R.string.ProfileImagePath);
        Picasso.get().load(imageAddress + mUser.getUserProfilePic()).into(holder.imgVuProfileThumbnail);


    }

    @Override
    public int getItemCount() {
        return mPostArrayList.size();
    }
}
