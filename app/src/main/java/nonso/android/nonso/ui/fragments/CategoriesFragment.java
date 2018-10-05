package nonso.android.nonso.ui.fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.chip.Chip;
import android.support.design.chip.ChipGroup;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import nonso.android.nonso.R;
import nonso.android.nonso.models.Result;
import nonso.android.nonso.models.interfaces.CategoriesCallback;
import nonso.android.nonso.viewModel.CategoriesViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CategoriesFragment.OnCategoriesFragmentInteraction} interface
 * to handle interaction events.
 * Use the {@link CategoriesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CategoriesFragment extends Fragment {

    @BindView(R.id.categories_chip_group) ChipGroup mChipGroup;
    private String[] mCategories;

    CategoriesViewModel mViewModel;

    private OnCategoriesFragmentInteraction mListener;

    public CategoriesFragment() {

    }

    public static CategoriesFragment newInstance() {
        return new CategoriesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_categories, container, false);

        ButterKnife.bind(this, view);

        mViewModel = ViewModelProviders.of(this).get(CategoriesViewModel.class);

        mViewModel.getCategories(new CategoriesCallback() {
            @Override
            public void result(Result result) {

            }

            @Override
            public void categories(String[] categories) {
                updateUI(categories);
            }
        });


        return view;
    }

    public void updateUI(String[] categories){

        mCategories = categories;

        for(String st: categories){

            Chip chip = new Chip(getContext());
            chip.setText(st);
            chip.setElevation(15);
            chip.setChipBackgroundColorResource(R.color.colorBlueLight);
            chip.setTextAppearance(R.style.ChipTextStyle);
            chip.setTextAppearanceResource(R.style.Widget_MaterialComponents_Chip_Filter);
            chip.setCheckable(true);
            mChipGroup.addView(chip);
        }
    }

    public void chipClick(View view){

    }

    public void onChipSelect(String st) {
        if (mListener != null) {
            mListener.OnChipSelect(st);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnCategoriesFragmentInteraction) {
            mListener = (OnCategoriesFragmentInteraction) context;
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
    public interface OnCategoriesFragmentInteraction {
        void OnChipSelect(String st);
    }
}
