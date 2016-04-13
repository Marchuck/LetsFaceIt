package pl.test.face.facetest;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * @author Lukasz Marczak
 * @since 13.04.2016.
 */
public class ConnectionDetector {

    /**
     * Checking for all possible internet providers
     * **/
    public static boolean isConnectingToInternet(Context _context){
        ConnectivityManager connectivity = (ConnectivityManager)
                _context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null)
        {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for(android.net.NetworkInfo netInfo : info){
                    if(netInfo.getState() == NetworkInfo.State.CONNECTED)
                        return true;
                }
//                for (int i = 0; i < info.length; i++)
//                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
//                    {
//                        return true;
//                    }

        }
        return false;
    }
}
