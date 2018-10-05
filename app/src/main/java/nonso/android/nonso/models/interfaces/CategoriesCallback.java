package nonso.android.nonso.models.interfaces;

import java.util.ArrayList;

import nonso.android.nonso.models.Result;

public interface CategoriesCallback {

    public void result(Result result);
    public void categories(String[] categories);
}
