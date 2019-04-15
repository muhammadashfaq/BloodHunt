package bloodcafe.savelet.constants;

import android.Manifest;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.ActivityCompat;

public class  BaseurlClass {
//    public static String mBaseURl = "http://cc9f8334.ngrok.io/";
   // public static String mBaseURl = "http://10.0.2.2/";
    public static String mBaseURl ="http://rfbasolutions.com/clients/ashfaq/savelet_blood_cafe/";


    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }


}
