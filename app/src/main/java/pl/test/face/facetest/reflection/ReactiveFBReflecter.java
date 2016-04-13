package pl.test.face.facetest.reflection;

import android.app.Activity;

import com.facebook.FacebookActivity;

import pl.test.face.facetest.utils.Utils;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

/**
 * @author Lukasz Marczak
 * @since 13.04.2016.
 */
public class ReactiveFBReflecter {
    public static final String TAG = ReactiveFBReflecter.class.getSimpleName();

    public static rx.Observable<FacebookActivity> getFacebookActivity() {
        return Observable.create(new Observable.OnSubscribe<FacebookActivity>() {
            @Override
            public void call(Subscriber<? super FacebookActivity> subscriber) {
                FacebookActivity fbActivity = null;
                try {
                    Activity activity = ReflectHelper.getActivity();
                    if (activity instanceof FacebookActivity) {
                        fbActivity = (FacebookActivity) activity;
                        subscriber.onNext(fbActivity);
                    }
                } catch (Exception x) {
                    Utils.logErrors(TAG, x);
                    subscriber.onNext(null);
                }
            }
        }).onErrorReturn(new Func1<Throwable, FacebookActivity>() {
            @Override
            public FacebookActivity call(Throwable throwable) {
                Utils.logErrors(TAG, throwable);
                return null;
            }
        });
    }
}
