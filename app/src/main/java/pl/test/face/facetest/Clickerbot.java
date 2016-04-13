package pl.test.face.facetest;

import android.app.Activity;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.FacebookActivity;

import java.util.List;
import java.util.concurrent.TimeUnit;

import pl.test.face.facetest.reflection.ReflectHelper;
import pl.test.face.facetest.utils.Utils;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * @author Lukasz Marczak
 * @since 13.04.2016.
 */
public class Clickerbot {
    static final String TAG = Clickerbot.class.getSimpleName();
    Subscription subscription;

    public void stop() {
        Log.d(TAG, "stop: ");
        if (subscription != null) subscription.unsubscribe();
    }

    public void start(final Activity activity) {
        stop();
        Log.d(TAG, "start: ");
        subscription = Observable.timer(Utils.DELAY, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Long>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: " + e.getMessage());
                        e.printStackTrace();
                        start(null);
                    }

                    @Override
                    public void onNext(Long aLong) {
                        Log.i(TAG, "onNext: " + aLong);

                        Log.i(TAG, "onNext: fire action");
                        fireAction();
                        // Log.i(TAG, "onNext: not yet");

/**                        if (aLong % 10 == 0) {
 Log.e(TAG, "onNext: fire action!");
 fireAction(activity);
 } else {
 Log.i(TAG, "onNext: waiting");
 }**/
                    }
                });
    }

    public static synchronized void aaaa() {

        Activity activity = null;
        try {
            activity = ReflectHelper.getActivity();

        } catch (Exception x) {
            Utils.logErrors(TAG, x);
        } finally {
            if (activity instanceof FacebookActivity) {
                App.INSTANCE.facebookActivity = (FacebookActivity) activity;
            }
            doOnFBReady();
        }
    }

    public static synchronized void doOnFBReady() {
        Log.i(TAG, "fireAction: on  current fb activity");
        FacebookActivity facebookActivity = App.INSTANCE.facebookActivity;
        View currentFocus = facebookActivity.getCurrentFocus();
        if (currentFocus != null) {
            currentFocus.performClick();
            Log.i(TAG, "click performed");

        } else {

            Log.i(TAG, "cannot fire action null view,try on fragment ");

            FragmentManager manager = facebookActivity.getSupportFragmentManager();
            Log.i(TAG, "doOnFBReady: manager = " + (manager != null));

            List<Fragment> fragmentList = manager.getFragments();
            if (fragmentList != null)
                Log.i(TAG, "doOnFBReady: " + fragmentList.size());
            else Log.i(TAG, "doOnFBReady: fragment list is null");

            final Fragment fragment = manager.findFragmentByTag("SingleFragment");
            Log.i(TAG, "doOnFBReady: fragment = " + (fragment != null));

            View fragmentRootView = fragment.getView();
            Log.i(TAG, "doOnFBReady: fragmentRootView = " + (fragmentRootView != null));
            checkViewgroupFromView(fragmentRootView);
            FragmentManager childFragmentManager = fragment.getChildFragmentManager();
            Log.i(TAG, "doOnFBReady: childFragmentManager = " + (childFragmentManager != null));

            List<Fragment> fragmentList2 = childFragmentManager.getFragments();
            if (fragmentList2 != null)
                Log.i(TAG, "doOnFBReady: fragment childfragments count: " + fragmentList2.size());
            else
                Log.i(TAG, "doOnFBReady: no fragment list inside child");
            String tag = fragment.getTag();
            Log.d(TAG, "doOnFBReady: tag of fragment is " + tag);
            fr = fragment;
            if (shouldTry)
                tryAgain();
        }
    }

    static Fragment fr;

    static void tryAgain() {
        Log.i(TAG, "tryAgain: ");
        if (shouldTry) {
            new android.os.Handler(Looper.myLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    FacebookActivity activity = App.INSTANCE.facebookActivity;
                    if (activity != null) {
                        FragmentManager manager = activity.getSupportFragmentManager();
                        Fragment fr = manager.findFragmentByTag("SingleFragment");
                        if (fr.isVisible()) {
                            if (fr.getView() != null) {
                                Log.i(TAG, "run: perform click");
                                fr.getView().performClick();

                            } else Log.i(TAG, "run: null view");
                        }
                    }
//                    if (fr.getView() == null) {
//                        tryAgain();
//                    } else {
//                        shouldTry = false;
//                        fr.getView().performClick();
//                        Log.i(TAG, "click performed");
//                    }
                }
            }, 1000);
        }
    }

    static boolean shouldTry = true;

    static void checkViewgroupFromView(View fragmentRootView) {
        if ((fragmentRootView != null) && (fragmentRootView instanceof ViewGroup)) {
            for (int i = 0; i < ((ViewGroup) fragmentRootView).getChildCount(); ++i) {
                View nextChild = ((ViewGroup) fragmentRootView).getChildAt(i);
                Log.d(TAG, "fireAction on next child: " + nextChild.getId());
            }
        } else {
            Log.i(TAG, "checkViewgroupFromView: nullable view");
        }
    }

    private synchronized void fireAction() {
        Log.i(TAG, "fireAction: gggg");
        if (App.INSTANCE.facebookActivity == null) {
            Log.i(TAG, "fireAction: reflect start");
            try {
                aaaa();
                // activity.getCurrentFocus().performClick();
                // ReflectHelper.reflectSafetly();
            } catch (Exception x) {
                Log.e(TAG, "fireAction failed " + x.getMessage());
                x.printStackTrace();
            }
        } else {

            doOnFBReady();
        }
    }
}
