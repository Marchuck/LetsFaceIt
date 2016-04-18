package pl.test.face.facetest.thirdPartyApis.pokemon;

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
public class PokemonClient {


    public static PokemonApi createApi() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(PokemonApi.endpoint)
                .addConverterFactory(GsonConverterFactory.create()).build();
        return retrofit.create(PokemonApi.class);
    }


    public static Observable<Pokemon> getPokemonById(final int id) {
        return Observable.create(new Observable.OnSubscribe<Pokemon>() {
            @Override
            public void call(final Subscriber<? super Pokemon> subscriber) {
                createApi().getPokemonById(id).enqueue(new Callback<Pokemon>() {
                    @Override
                    public void onResponse(Call<Pokemon> call, Response<Pokemon> response) {
                        subscriber.onNext(response.body());
                    }

                    @Override
                    public void onFailure(Call<Pokemon> call, Throwable t) {
                        subscriber.onError(t);
                    }
                });
            }
        });
    }
}
