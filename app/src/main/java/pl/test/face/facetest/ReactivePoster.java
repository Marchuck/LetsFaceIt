package pl.test.face.facetest;

import android.os.Bundle;
import android.util.Log;
import android.util.Range;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import pl.test.face.facetest.thirdPartyApis.Swapi;
import pl.test.face.facetest.thirdPartyApis.SwapiCharacter;
import pl.test.face.facetest.thirdPartyApis.swapi.SwapiClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Observable;
import rx.Observer;
import rx.Scheduler;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * @author Lukasz Marczak
 * @since 18.04.2016.
 */
public class ReactivePoster {

    public static final String TAG = ReactivePoster.class.getSimpleName();
    public static final String TOKEN = "CAACEdEose0cBAM1BH6tCWA3h4xtY5GwZA1OzQxYQD40pyqp15ICSBXjif8097ZBZCZAEPwc2xZB0XrjjVCdyghoqtuNqk6YBWhlr45gv3olZCgWnnZBwJblig5ytwIf1nmZA2fZBXEt5upYUOSeNtXVf6g9gxrkjIA5IFfzR9OO5LcXMYOgxSZCwkdZAlfdKHO3RiaJkHdcBneaLwZDZD";

    String token;
    Bundle bundle;
    List<String> preparedMessages = new ArrayList<>();

    private ReactivePoster() {

        bundle = new Bundle();
    }

    public ReactivePoster withMessage(String message) {
        bundle.putString("message", message);
        return ReactivePoster.this;
    }

    public static ReactivePoster create() {
        return new ReactivePoster();
    }


    public ReactivePoster withToken(String token) {
        this.token = token;
        return this;
    }

    public static Observable<Boolean> postMessage(final String message) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(final Subscriber<? super Boolean> subscriber) {
                Bundle bundle = new Bundle();
                bundle.putString("message", message);
                bundle.putString("access_token", TOKEN);
                new GraphRequest(AccessToken.getCurrentAccessToken(),
                        "/me/feed",
                        bundle,
                        HttpMethod.POST,
                        new GraphRequest.Callback() {
                            public void onCompleted(GraphResponse response) {
                                Log.d(TAG, "onCompleted: " + response.toString());
                                subscriber.onNext(true);

                            }
                        }).executeAsync();
            }
        });
    }

    public void createSingle() {

        bundle.putString("access_token", token);
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me/feed",
                bundle,
                HttpMethod.POST,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        Log.d(TAG, "onCompleted: " + response.toString());

                    }
                }).executeAsync();
    }

    public Observable<SwapiCharacter> getCharacterForId(final int id) {
        return Observable.create(new Observable.OnSubscribe<SwapiCharacter>() {
            @Override
            public void call(final Subscriber<? super SwapiCharacter> subscriber) {
                SwapiClient.getSwapiClient().getCharacter(id).enqueue(new Callback<SwapiCharacter>() {
                    @Override
                    public void onResponse(Call<SwapiCharacter> call, Response<SwapiCharacter> response) {
                        subscriber.onNext(response.body());
                    }

                    @Override
                    public void onFailure(Call<SwapiCharacter> call, Throwable t) {
                        subscriber.onError(t);
                    }
                });
            }
        });
    }

    public Observable<Boolean> runPostAsync(final int id) {
        return getCharacterForId(id).flatMap(new Func1<SwapiCharacter, Observable<Boolean>>() {
            @Override
            public Observable<Boolean> call(SwapiCharacter swapiCharacter) {
                return postMessage("new Star Wars character #" + id + " discovered: " + swapiCharacter);
            }
        });
    }

    public void rolex() {
        Observable.range(6, 16).flatMap(new Func1<Integer, Observable<Boolean>>() {
                    @Override
                    public Observable<Boolean> call(Integer aLong) {
                        return runPostAsync(aLong + 5);
                    }
                }).subscribe(new Subscriber<Boolean>() {
            @Override
            public void onCompleted() {
                Log.i(TAG, "onCompleted: ddd");
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError: " + e.getMessage());
            }

            @Override
            public void onNext(Boolean aBoolean) {
                Log.w(TAG, "onNext: " + aBoolean);
            }
        });
    }

    public void run() {
        Log.d(TAG, "run: ");

        Observable.range(1, 3).flatMap(new Func1<Integer, Observable<SwapiCharacter>>() {
            @Override
            public Observable<SwapiCharacter> call(Integer integer) {
                Log.i(TAG, "call: " + integer);
                return getCharacterForId(integer);
            }
        }).flatMap(new Func1<SwapiCharacter, Observable<Boolean>>() {
            @Override
            public Observable<Boolean> call(SwapiCharacter swapiCharacter) {
                return postMessage("new Star Wars character discovered: " + swapiCharacter.name);
            }
        }).subscribeOn(Schedulers.trampoline()).subscribe(new Subscriber<Boolean>() {
            @Override
            public void onCompleted() {
                Log.i(TAG, "onCompleted: ");
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "onError: " + e.getMessage());
            }

            @Override
            public void onNext(Boolean aBoolean) {
                Log.d(TAG, "onNext: " + aBoolean);
            }
        });
    }
}
