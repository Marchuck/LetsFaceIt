package pl.test.face.facetest;

import android.app.Application;

import com.facebook.FacebookActivity;

/**
 * Created by lukasz on 13.04.2016.
 */
public class App extends Application {
    public FacebookActivity facebookActivity;
    public static App INSTANCE;

    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;
    }

}
