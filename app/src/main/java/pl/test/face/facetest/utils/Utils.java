package pl.test.face.facetest.utils;

import android.util.Log;

/**
 * @author Lukasz Marczak
 * @since 13.04.2016.
 */
public class Utils {
    public static int DELAY = 20;
    public static void logErrors(String TAG,Throwable throwable){
        Log.d(TAG, "onError "+throwable.getMessage());
        throwable.printStackTrace();
    }
}
