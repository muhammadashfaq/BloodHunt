package bloodcafe.savelet.constants;
/**
 * Created by Haseeb.
 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.util.Log;

import java.util.Set;

public class SessionManager {
    // LogCat tag
    private static String TAG = SessionManager.class.getSimpleName();

    // Shared Preferences
    static SharedPreferences pref;
    static Editor editor;
    private static String STORE_VAR_SOCIAL_LOGIN;

    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "MyLoginPref";
    // Pref Index name
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USEREMAIL = "email";
    private static final String KEY_USERPASS = "pass";
    private static final String KEY_USERID = "id";
    private static final String KEY_USERName = "userName";


    private static String user_profile_name = "";
    private static String user_profile_email = "";
    private static Uri user_profile_photo = null;

    public static String getUser_profile_name() {
        return pref.getString("profile_name", "empty");
    }

    public static String getUser_profile_email() {
        return pref.getString("profile_email", "empty");
    }

    public static String getUser_profile_photo() {
        return pref.getString("profile_photo", "empty");
    }

    @SuppressLint("WrongConstant")
    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }



    public static void storeUserProfileData(String name, String email, Uri uri){
        user_profile_name=name;
        user_profile_email=email;
        user_profile_photo=uri;


        editor.putString("profile_name",user_profile_name);
        editor.putString("profile_email",user_profile_email);
        editor.putString("profile_photo", String.valueOf(user_profile_photo));

        editor.commit();

    }




    public static String getStoreVarSocialLogin() {
        return pref.getString("social_key", "empty");
    }

    public void setStoreVarSocialLogin(String storeVarSocialLogin) {
        editor.putString("social_key",storeVarSocialLogin);
        editor.commit();
    }

    public void setLogin(boolean isLoggedIn, String email, String KEY_USERPass, String UserDbid, String userName) {
        editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn);
        editor.putString(KEY_USEREMAIL, email);
        editor.putString(KEY_USERPASS, KEY_USERPass);
        editor.putString(KEY_USERID, UserDbid);
        editor.putString(KEY_USERName, userName);

        // commit changes
        editor.commit();

        Log.d(TAG, "User login session modified!");
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public String getKeyUserEmail() {
        return pref.getString(KEY_USEREMAIL, "");
    }

    public String getKeyUserPass() {
        return pref.getString(KEY_USERPASS, "");
    }

    public String getKeyuserName() {
        return pref.getString(KEY_USERName, "");
    }
    public String getKeyuserId() {
        return pref.getString(KEY_USERID, "");
    }


    public void setKeyUserName(String userName) {
        editor.putString(KEY_USERName, userName);
        editor.commit();
    }   public void setKeyUserEmail(String userEmail) {
        editor.putString(KEY_USEREMAIL, userEmail);
        editor.commit();
    }


}
