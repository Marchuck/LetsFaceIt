package pl.test.face.facetest.thirdPartyApis.swapi;

import android.util.Log;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Subscriber;

/**
 * @author Lukasz Marczak
 * @since 18.04.2016.
 */
public class SwapiClient {

    public static final String TAG = SwapiClient.class.getSimpleName();


    public static Swapi getSwapiClient() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Swapi.endpoint)
                .addConverterFactory(GsonConverterFactory.create()).build();
        return retrofit.create(Swapi.class);
    }

    public static void preformCall(int id) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Swapi.endpoint)
                .addConverterFactory(GsonConverterFactory.create()).build();

        retrofit.create(Swapi.class).getCharacter(id).enqueue(new Callback<SwapiCharacter>() {
            @Override
            public void onResponse(Call<SwapiCharacter> call, Response<SwapiCharacter> response) {
                Log.d(TAG, "onResponse: " + response.body());
            }

            @Override
            public void onFailure(Call<SwapiCharacter> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    public static Observable<SwapiCharacter> getSwapiCharacterById(final int id) {

        return Observable.create(new Observable.OnSubscribe<SwapiCharacter>() {
            @Override
            public void call(final Subscriber<? super SwapiCharacter> subscriber) {
                getSwapiClient().getCharacter(id).enqueue(new Callback<SwapiCharacter>() {
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

}
