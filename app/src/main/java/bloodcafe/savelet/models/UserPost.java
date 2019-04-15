package bloodcafe.savelet.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserPost {

    @SerializedName("tens")
    @Expose
    private String tens;
    @SerializedName("user_profile_pic")
    @Expose
    private String userProfilePic;
    @SerializedName("u_name")
    @Expose
    private String uName;
    @SerializedName("userBloodRequestType")
    @Expose
    private String userBloodRequestType;
    @SerializedName("user_post_time")
    @Expose
    private String userPostTime;
    @SerializedName("userPostType")
    @Expose
    private String userPostType;
    @SerializedName("userAddress")
    @Expose
    private String userAddress;
    @SerializedName("userContact")
    @Expose
    private String userContact;
    @SerializedName("userlats")
    @Expose
    private String userlats;
    @SerializedName("userlong")
    @Expose
    private String userlong;
    @SerializedName("post_id")
    @Expose
    private String postId;
    @SerializedName("user_id")
    @Expose
    private String userId;

    public String getTens() {
        return tens;
    }

    public void setTens(String tens) {
        this.tens = tens;
    }

    public String getUserProfilePic() {
        return userProfilePic;
    }

    public void setUserProfilePic(String userProfilePic) {
        this.userProfilePic = userProfilePic;
    }

    public String getUName() {
        return uName;
    }

    public void setUName(String uName) {
        this.uName = uName;
    }

    public String getUserBloodRequestType() {
        return userBloodRequestType;
    }

    public void setUserBloodRequestType(String userBloodRequestType) {
        this.userBloodRequestType = userBloodRequestType;
    }

    public String getUserPostTime() {
        return userPostTime;
    }

    public void setUserPostTime(String userPostTime) {
        this.userPostTime = userPostTime;
    }

    public String getUserPostType() {
        return userPostType;
    }

    public void setUserPostType(String userPostType) {
        this.userPostType = userPostType;
    }

    public String getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(String userAddress) {
        this.userAddress = userAddress;
    }

    public String getUserContact() {
        return userContact;
    }

    public void setUserContact(String userContact) {
        this.userContact = userContact;
    }

    public String getUserlats() {
        return userlats;
    }

    public void setUserlats(String userlats) {
        this.userlats = userlats;
    }

    public String getUserlong() {
        return userlong;
    }

    public void setUserlong(String userlong) {
        this.userlong = userlong;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

}
