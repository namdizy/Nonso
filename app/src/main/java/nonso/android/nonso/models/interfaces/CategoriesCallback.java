package nonso.android.nonso.models.interfaces;


import nonso.android.nonso.models.Result;

public interface CategoriesCallback {

    void result(Result result);
    void categories(String[] categories);
}
