package nonso.android.nonso.di;


import android.arch.lifecycle.ViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;
import nonso.android.nonso.viewModel.UserProfileViewModel;

@Module
public abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(UserProfileViewModel.class)
    abstract ViewModel bindUserViewModel(UserProfileViewModel userViewModel);
}
