package nonso.android.nonso;

import android.app.Activity;
import android.app.Application;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;
import nonso.android.nonso.di.AppInjector;
import nonso.android.nonso.di.ApplicationComponent;

public class App extends Application implements HasActivityInjector {

    protected static App sInstance;

    protected static ApplicationComponent sAppComponent;

    @Inject
    DispatchingAndroidInjector<Activity> dispatchingAndroidInjector;


    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        sAppComponent = AppInjector.init(this);
    }

    @Override
    public AndroidInjector<Activity> activityInjector() {
        return dispatchingAndroidInjector;
    }

    public static App getInstance() {
        return sInstance;
    }
}
