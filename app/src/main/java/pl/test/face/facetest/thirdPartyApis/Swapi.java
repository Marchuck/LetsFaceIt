package pl.test.face.facetest.thirdPartyApis;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/**
 * @author Lukasz Marczak
 * @since 18.04.2016.
 */
public interface Swapi {

    String endpoint = "http://swapi.co/api/";


    @GET("people/{id}")
    Call<SwapiCharacter> getCharacter(@Path("id") int id);

    @GET("people/{id}")
    Observable<SwapiCharacter> getCharacterForId(@Path("id") int id);


}
