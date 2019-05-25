package com.bsecure.apha.fragments;



import android.support.v4.app.Fragment;
import android.view.View;

import com.bsecure.apha.R;


public class ParentFragment extends Fragment {

	public boolean back() {
		return false;
	}

	public String getFragmentName() {
		return getString(R.string.app_name);
	}

	public void onFragmentChildClick(View view) {

	}

	public int getFragmentActionBarColor() {
		return R.color.colorPrimary;
	}

	public int getFragmentStatusBarColor() {
		return R.color.colorPrimaryDark;
	}

	public interface OnFragmentInteractionListener {
		void onFragmentInteraction(int colorId, int secondaryColorId, int stringid, boolean blockMenu);
	}

}
