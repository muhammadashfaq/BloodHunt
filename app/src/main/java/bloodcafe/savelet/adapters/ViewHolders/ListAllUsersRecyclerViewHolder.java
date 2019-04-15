package bloodcafe.savelet.adapters.ViewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import bloodcafe.savelet.R;
import me.biubiubiu.justifytext.library.JustifyTextView;

import static bloodcafe.savelet.adapters.HomeRecyclerAdapter.itemClickCallback;

public class ListAllUsersRecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public ImageView imgVuProfileThumbnail;
    public TextView txtVuUserName;
    public TextView txtVuUserPostCity;
    public TextView txtVuBloodType;


    public ListAllUsersRecyclerViewHolder(View itemView) {
        super(itemView);
        imgVuProfileThumbnail = itemView.findViewById(R.id.imgVuProfileThumbnail);
        txtVuUserName = itemView.findViewById(R.id.txtVuUserName);
        txtVuUserPostCity = itemView.findViewById(R.id.txtVuUserPostCity);
        txtVuBloodType = itemView.findViewById(R.id.txtVuBloodType);


        txtVuUserName.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txtVuUserName: {
                itemClickCallback.onUserNameClick(getAdapterPosition());
                break;
            }

        }
    }
}
