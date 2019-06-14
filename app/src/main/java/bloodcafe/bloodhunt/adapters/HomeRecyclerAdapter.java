package bloodcafe.bloodhunt.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import bloodcafe.bloodhunt.R;
import bloodcafe.bloodhunt.adapters.ViewHolders.HomeRecyclerViewHolder;
import bloodcafe.bloodhunt.constants.BaseurlClass;
import bloodcafe.bloodhunt.constants.SessionManager;
import bloodcafe.bloodhunt.models.UserPost;

public class HomeRecyclerAdapter extends RecyclerView.Adapter<HomeRecyclerViewHolder> {

    private Context context;
    private ArrayList<UserPost> mPostArrayList;
    private LayoutInflater mLayoutInflater;
    private SessionManager mSessionManager;
    public static HomeRecyclerAdapter.HomeItemClickCallback itemClickCallback;

    //an interface for making this recyclerView ClickAble
    public interface HomeItemClickCallback {
        void onUserNameClick(int p);

//        void onLikeItemClick(int p, View view, View DislikeView, View LikesCount, View disLikesCount);
//
//        void onDislikeItemClick(int p, View view, View likeView, View disLikesCount, View LikesCount);


    }

    public void setItemClickCallback(final HomeItemClickCallback itemClickCallback) {
        HomeRecyclerAdapter.itemClickCallback = itemClickCallback;
    }


    public HomeRecyclerAdapter(Context context, ArrayList<UserPost> mPostArrayList) {
        this.context = context;
        this.mPostArrayList = mPostArrayList;
        mLayoutInflater = LayoutInflater.from(context);
        mSessionManager = new SessionManager(context);
    }


    @NonNull
    @Override
    public HomeRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.user_post_card_design, parent, false);
        return new HomeRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeRecyclerViewHolder holder, int position) {
        final UserPost mUserPost = mPostArrayList.get(position);
        final String number = mUserPost.getUserContact();
        holder.txtVuUserName.setText(mUserPost.getUName());
        holder.txtVuUserPostText.setText(mUserPost.getTens());
        holder.txtVuUserPostTime.setText(mUserPost.getUserPostTime());
        holder.txtVuBloodType.setText(mUserPost.getUserBloodRequestType());
        String imageAddress = BaseurlClass.mBaseURl + context.getResources().getString(R.string.ProfileImagePath);
        Picasso.get().load(imageAddress + mUserPost.getUserProfilePic()).placeholder(R.drawable.default_user).into(holder.imgVuProfileThumbnail);


    }

    @Override
    public int getItemCount() {
        return mPostArrayList.size();
    }
}
