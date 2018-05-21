package nonso.android.nonso.ui.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import nonso.android.nonso.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class DiscoverNewFragment extends Fragment {


    public DiscoverNewFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_discover_new, container, false);

        return view;
    }

}
