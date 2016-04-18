package pl.test.face.facetest.thirdPartyApis.pokemon;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * @author Lukasz Marczak
 * @since 18.04.2016.
 */
public interface PokemonApi {
    String endpoint=  "http://pokeapi.co/api/v2/";

    @GET("pokemon/{id}/")
    Call<Pokemon> getPokemonById(@Path("id") int id);
}
