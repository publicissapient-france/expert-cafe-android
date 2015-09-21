package fr.xebia.expertcafe.common;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;

public abstract class BaseFragment extends Fragment {

    public static final String BUNDLE_LAYOUT_ID = "BUNDLE_LAYOUT_ID";
    public static final String BUNDLE_BIND = "BUNDLE_BIND";
    public static final String STRING = "string";

    private int layoutId;
    private boolean bind;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle != null) {
            layoutId = bundle.getInt(BUNDLE_LAYOUT_ID);
            bind = bundle.getBoolean(BUNDLE_BIND, true);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(layoutId, container, false);
        if (bind) {
            ButterKnife.bind(this, view);
        }
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (bind) {
            ButterKnife.unbind(this);
        }
    }

    public int getStringByName(String name) {
        return getResources().getIdentifier(name, STRING, getActivity().getPackageName());
    }
}
