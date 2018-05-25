package nonso.android.nonso.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import nonso.android.nonso.R;
import nonso.android.nonso.models.Step;
/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link JourneyTimelineFragment.OnJourneyTimelineListener} interface
 * to handle interaction events.
 * create an instance of this fragment.
 */
public class JourneyTimelineFragment extends Fragment{



    private OnJourneyTimelineListener mListener;

    private Step mStep;

    public JourneyTimelineFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_journey_timeline, container, false);

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnJourneyTimelineListener) {
            mListener = (OnJourneyTimelineListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


//    private void createText(){
//        if (mListener != null) {
//            Step step = new Step();
//            step.setStepType(StepType.TEXT);
//            mListener.onJourneyTimelineInteraction(step);
//        }
//    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnJourneyTimelineListener {
        // TODO: Update argument type and name
        void onJourneyTimelineInteraction(Step step);
    }
}
