package idv.funnybrain.plurkchat.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import idv.funnybrain.plurkchat.R;

/**
 * Created by Freeman on 2014/4/3.
 */
public class ChatRoomsFragment extends Fragment {
    ChatRoomsFragment newInstance() {
        ChatRoomsFragment chatRoomsFragment = new ChatRoomsFragment();
        return chatRoomsFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_chatrooms, container, false);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }
}