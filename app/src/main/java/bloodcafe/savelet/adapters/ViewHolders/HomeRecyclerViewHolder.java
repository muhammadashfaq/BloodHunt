package bloodcafe.savelet.adapters.ViewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import bloodcafe.savelet.R;
import me.biubiubiu.justifytext.library.JustifyTextView;

import static bloodcafe.savelet.adapters.HomeRecyclerAdapter.itemClickCallback;

public class HomeRecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public ImageView imgVuProfileThumbnail;
    public TextView txtVuUserName;
    public JustifyTextView txtVuUserPostText;
    public TextView txtVuUserPostTime;
    public TextView txtVuBloodType;
    public Button btnSMSUser, btnCallUser;

    public HomeRecyclerViewHolder(View itemView) {
        super(itemView);
        imgVuProfileThumbnail = itemView.findViewById(R.id.imgVuProfileThumbnail);
        txtVuUserName = itemView.findViewById(R.id.txtVuUserName);
        txtVuUserPostText = itemView.findViewById(R.id.txtVuUserPostText);
        txtVuUserPostTime = itemView.findViewById(R.id.txtVuUserPostTime);
        txtVuBloodType = itemView.findViewById(R.id.txtVuBloodType);
        btnSMSUser = itemView.findViewById(R.id.btnSMSUser);
        btnCallUser = itemView.findViewById(R.id.btnCallUser);

        txtVuUserName.setOnClickListener(this);
        btnCallUser.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txtVuUserName: {
                itemClickCallback.onUserNameClick(getAdapterPosition());
                break;
            }
            case R.id.btnCallUser: {
                itemClickCallback.onUserNameClick(getAdapterPosition());
                break;
            }
        }
    }
}
