package nonso.android.nonso.ui.adapters;

import android.content.Context;
import android.support.v4.app.FragmentManager;

import com.stepstone.stepper.Step;
import com.stepstone.stepper.adapter.AbstractFragmentStepAdapter;

import nonso.android.nonso.models.Journey;
import nonso.android.nonso.ui.fragments.createJourneys.DescriptionStepperFragment;
import nonso.android.nonso.ui.fragments.createJourneys.SettingsStepperFragment;

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
                final DescriptionStepperFragment descriptionStepperFragment = new DescriptionStepperFragment().newInstance(position, journey);
                return descriptionStepperFragment;
            case 1:
                final SettingsStepperFragment settingsStepperFragment = new SettingsStepperFragment().newInstance(position, journey);
                return settingsStepperFragment;
        }
        return null;
    }


}
