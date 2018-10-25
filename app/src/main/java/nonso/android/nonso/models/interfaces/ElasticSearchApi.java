package nonso.android.nonso.models.interfaces;

import java.util.Map;

import nonso.android.nonso.models.elasticSearch.UserHitsPOJO;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.Query;

public interface ElasticSearchApi {


    @GET("_search/")
    Call<UserHitsPOJO> search(
            @HeaderMap Map<String, String> headers,
            @Query("default_operator") String operator,
            @Query("q") String query
        );
}
