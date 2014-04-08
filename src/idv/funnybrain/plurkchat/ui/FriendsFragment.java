package idv.funnybrain.plurkchat.ui;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import idv.funnybrain.plurkchat.FunnyActivity;
import idv.funnybrain.plurkchat.PlurkOAuth;
import idv.funnybrain.plurkchat.R;
import idv.funnybrain.plurkchat.RequestException;
import idv.funnybrain.plurkchat.data.Friend;
import idv.funnybrain.plurkchat.data.Language;
import idv.funnybrain.plurkchat.data.Me;
import idv.funnybrain.plurkchat.data.Qualifier;
import idv.funnybrain.plurkchat.modules.Mod_FriendsFans;
import idv.funnybrain.plurkchat.modules.Mod_Timeline;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Freeman on 2014/4/2.
 */
public class FriendsFragment extends Fragment {
    // ---- constant variable START ----
    private static final boolean D = true;
    private static final String TAG = "FriendsFragment";
    // ---- constant variable END ----

    // ---- local variable START ----
    private PlurkOAuth plurkOAuth;
    private Me me;
    private ListView list;
    // ---- local variable END ----

    FriendsFragment newInstance() {
        if(D) { Log.d(TAG, "newInstance"); }
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
        list = (ListView) v.findViewById(R.id.list);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //((FunnyActivity) getActivity()).doTest();
        plurkOAuth = ((FunnyActivity) getActivity()).getPlurkOAuth();
        me = ((FunnyActivity) getActivity()).getMe();
        //new PlurkTmpAsyncTask().execute("");
        new Mod_FriendsFans_getFriendsByOffset_AsyncTask().execute(me.getId());
    }

    private class Mod_FriendsFans_getFriendsByOffset_AsyncTask extends AsyncTask<String, Void, List<Friend>> {
        @Override
        protected List<Friend> doInBackground(String... params) {
            if(D) { Log.d(TAG, "Mod_FriendsFans_getFriendsByOffset_AsyncTask, doInBackground"); }
            JSONArray result = null;
            List<Friend> friends = new ArrayList<Friend>();
            int round = 0;

            try {
                do {
                    if(D) { Log.d(TAG, "Mod_FriendsFans_getFriendsByOffset_AsyncTask, while: " + round); }
                    result = plurkOAuth.getModule(Mod_FriendsFans.class).getFriendsByOffset(me.getId(), 0 + 100 * round, 100);
                    // CWT   7014485
                    // kero  4373060
                    // 6880391
                    for (int x = 0; x < result.length(); x++) {
                        friends.add(new Friend(result.getJSONObject(x)));
                    }
                    round++;
                } while (result.length() > 0);
                if (D) {
                    Log.d(TAG, result.toString());
                }
            } catch (JSONException je) {
                Log.e(TAG, je.getMessage());
            } catch (RequestException e) {
                Log.e(TAG, e.getMessage());
            }

            for(int x=0; x<friends.size(); x++) {
                String tmp = friends.get(x).getDisplay_name();
                if (tmp.equals("")) {
                    tmp = "!!!!!!!!!!!!";
                }
                System.out.println(x + " " + tmp + " " +
                                             friends.get(x).getId() + " " +
                                             friends.get(x).getNick_name() + " " +
                                             friends.get(x).getFull_name());
            }

            return friends;
        }

        @Override
        protected void onPostExecute(List<Friend> friends) {
            super.onPostExecute(friends);
            list.setAdapter(new FriendsListAdapter(getActivity().getLayoutInflater(), friends));
        }
    }

    private class PlurkTmpAsyncTask extends AsyncTask<String, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(String... params) {
            JSONObject result = null;
            try {
                result = plurkOAuth.getModule(Mod_Timeline.class).plurkAdd("(wave)(wave)(wave)"+ Math.random(), Qualifier.SAYS, null, 0, Language.TR_CH);
            } catch (RequestException e) {
                e.printStackTrace();
            }
            return result;
        }
    }
}