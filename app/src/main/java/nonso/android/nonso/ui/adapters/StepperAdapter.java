package nonso.android.nonso.ui.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;

import com.stepstone.stepper.Step;
import com.stepstone.stepper.adapter.AbstractFragmentStepAdapter;
import com.stepstone.stepper.viewmodel.StepViewModel;

import nonso.android.nonso.ui.fragments.DescriptionStepFragment;
import nonso.android.nonso.ui.fragments.MediaStepFragment;
import nonso.android.nonso.ui.fragments.SettingsStepFragment;

public class StepperAdapter extends AbstractFragmentStepAdapter {



    public StepperAdapter(FragmentManager fm, Context context){
        super(fm, context);
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public Step createStep(int position) {

        switch (position){
            case 0:
                final DescriptionStepFragment descriptionStepFragment = new DescriptionStepFragment().newInstance(position, "temp");
                return descriptionStepFragment;
            case 1:
                final SettingsStepFragment settingsStepFragment = new SettingsStepFragment().newInstance(position, "temp");
                return  settingsStepFragment;
            case 2:
                final MediaStepFragment mediaStepFragment = new MediaStepFragment().newInstance(position, "temp");
                return mediaStepFragment;
        }
        return null;
    }


}
