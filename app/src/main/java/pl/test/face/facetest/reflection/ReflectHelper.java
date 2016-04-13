package pl.test.face.facetest.reflection;

import android.app.Activity;
import android.util.ArrayMap;
import android.util.Log;
import android.view.View;

import com.facebook.FacebookActivity;

import java.lang.reflect.Field;
import java.util.HashMap;

import pl.test.face.facetest.App;
import pl.test.face.facetest.Clickerbot;
import pl.test.face.facetest.FBActivity;
import pl.test.face.facetest.utils.Utils;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * @author Lukasz Marczak
 * @since 13.04.2016.
 */
public class ReflectHelper {
    public static final String TAG = ReflectHelper.class.getSimpleName();


//    public static com.facebook.internal.WebDialog getWebDialog() throws Exception{
//
//        Class activityThreadClass = Class.forName("com.facebook.internal.WebDialog");
//    }

    public static Activity getActivity() throws Exception {
        Class activityThreadClass = Class.forName("android.app.ActivityThread");
        Object activityThread = activityThreadClass.getMethod("currentActivityThread").invoke(null);
        Field activitiesField = activityThreadClass.getDeclaredField("mActivities");
        activitiesField.setAccessible(true);
        ArrayMap activities = (ArrayMap) activitiesField.get(activityThread);
        for (Object activityRecord : activities.values()) {
            Class activityRecordClass = activityRecord.getClass();
            Field pausedField = activityRecordClass.getDeclaredField("paused");
            pausedField.setAccessible(true);
            if (!pausedField.getBoolean(activityRecord)) {
                Field activityField = activityRecordClass.getDeclaredField("activity");
                activityField.setAccessible(true);
                Activity activity = (Activity) activityField.get(activityRecord);
                return activity;
            }
        }
        return null;
    }
}
