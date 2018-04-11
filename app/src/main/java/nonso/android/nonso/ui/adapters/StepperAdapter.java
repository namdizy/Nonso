package nonso.android.nonso.ui.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;

import com.stepstone.stepper.Step;
import com.stepstone.stepper.adapter.AbstractFragmentStepAdapter;
import com.stepstone.stepper.viewmodel.StepViewModel;

import nonso.android.nonso.models.Journey;
import nonso.android.nonso.ui.fragments.DescriptionStepFragment;
import nonso.android.nonso.ui.fragments.MediaStepFragment;
import nonso.android.nonso.ui.fragments.SettingsStepFragment;

public class StepperAdapter extends AbstractFragmentStepAdapter {

    private Journey journey = new Journey();

    public StepperAdapter(FragmentManager fm, Context context){
        super(fm, context);
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public Step createStep(int position) {

        switch (position){
            case 0:
                final DescriptionStepFragment descriptionStepFragment = new DescriptionStepFragment().newInstance(position, journey);
                return descriptionStepFragment;
            case 1:
                final SettingsStepFragment settingsStepFragment = new SettingsStepFragment().newInstance(position, journey);
                return  settingsStepFragment;
        }
        return null;
    }


}
