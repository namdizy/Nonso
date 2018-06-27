package nonso.android.nonso.viewModel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import javax.inject.Inject;

import nonso.android.nonso.data.UserRepository;
import nonso.android.nonso.models.User;

public class UserProfileViewModel extends ViewModel {

    private UserRepository mUserRepository;
    private LiveData<User> mObservableUser;

    @Inject
    public UserProfileViewModel(@NonNull UserRepository userRepo){
        this.mUserRepository = userRepo;
    }


    public void init(String userId){
        if (this.mObservableUser != null) {
            return;
        }
        mObservableUser = mUserRepository.getUser(userId);
    }

    public LiveData<User> getUser() {
        return this.mObservableUser;
    }

}
