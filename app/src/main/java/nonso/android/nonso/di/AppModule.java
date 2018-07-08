package nonso.android.nonso.di;

import android.app.Application;
import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(includes = ViewModelModule.class)
public class AppModule {

    public AppModule(){}

    @Provides
    @Singleton
    @ApplicationContext
    Context provideContext(Application application){
        return application;
    }


}
