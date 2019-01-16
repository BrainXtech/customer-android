package com.kustomer.kustomer.BaseClasses;

import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.ProgressBar;

public class BaseFragment extends Fragment {

    //region properties
    protected ProgressBar progressBar;
    protected View backLayout;
    //endregion

    public BaseFragment() {
        // Required empty public constructor
    }

    public boolean onBackPressed() {
        return false;
    }

    public void onActive() {
    }


    protected BaseFragment getTopFragmentInBackStack() {
        try {
            int index = getChildFragmentManager().getBackStackEntryCount() - 1;
            FragmentManager.BackStackEntry backEntry = getChildFragmentManager().getBackStackEntryAt(index);
            String tag = backEntry.getName();
            return (BaseFragment) getChildFragmentManager().findFragmentByTag(tag);
        } catch (Exception e) {
            return null;
        }
    }

    protected BaseFragment getTopFragmentInBackStackByTag(String tag) {
        return (BaseFragment) getChildFragmentManager().findFragmentByTag(tag);
    }

    protected boolean popTopFragment(FragmentManager fragmentManager, int minFragmentCount) {
        if (fragmentManager.getBackStackEntryCount() > minFragmentCount) {
            return fragmentManager.popBackStackImmediate();
        } else
            return false;
    }

    protected void showSnackBar(View view, int textId, int colorId) {
        Snackbar snackbar = Snackbar
                .make(view, textId, Snackbar.LENGTH_LONG);

        snackbar.getView().setBackgroundColor(getResources().getColor(colorId));

        snackbar.show();
    }

    protected void showSnackBar(View view, String text, int colorId) {
        Snackbar snackbar = Snackbar
                .make(view, text, Snackbar.LENGTH_LONG);

        snackbar.getView().setBackgroundColor(getResources().getColor(colorId));

        snackbar.show();
    }

}
