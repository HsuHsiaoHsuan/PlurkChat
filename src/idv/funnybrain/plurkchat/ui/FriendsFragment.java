package idv.funnybrain.plurkchat.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import idv.funnybrain.plurkchat.FunnyActivity;
import idv.funnybrain.plurkchat.R;

/**
 * Created by Freeman on 2014/4/2.
 */
public class FriendsFragment extends Fragment {
    FriendsFragment newInstance() {
        FriendsFragment friendsFragment = new FriendsFragment();
        return friendsFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_friends, container, false);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((FunnyActivity) getActivity()).doTest();

    }
}