package com.armjld.rayashipping.Adapters;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.armjld.rayashipping.Captin.capAvillable;
import com.armjld.rayashipping.Captin.captinRecived;
import com.armjld.rayashipping.R;
import com.armjld.rayashipping.SuperVisor.SuperAvillable;
import com.armjld.rayashipping.SuperVisor.SuperRecived;
import com.armjld.rayashipping.models.UserInFormation;

public class SuperVisorAdapter extends FragmentPagerAdapter {

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.txtAvillable, R.string.txtRecived};
    private final Context mContext;

    public SuperVisorAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                if (UserInFormation.getAccountType().equals("Delivery Worker")) {
                    fragment = new capAvillable();
                } else {
                    fragment = new SuperAvillable();
                }
                break;
            case 1:
                if (UserInFormation.getAccountType().equals("Delivery Worker")) {
                    fragment = new captinRecived();
                } else {
                    fragment = new SuperRecived();

                }
                break;
        }
        assert fragment != null;
        return fragment;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
        return 2;
    }
}