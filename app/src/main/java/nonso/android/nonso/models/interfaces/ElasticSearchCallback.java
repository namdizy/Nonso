package nonso.android.nonso.models.interfaces;

import nonso.android.nonso.models.Result;
import nonso.android.nonso.models.elasticSearch.UserHitsPOJO;

public interface ElasticSearchCallback {

    void result(Result result);
    void users(UserHitsPOJO userHits);
    void password(String password);
    void authorization(String authorization);
}
