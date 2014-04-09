package idv.funnybrain.plurkchat.ui;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ExpandableListView;
import com.actionbarsherlock.app.SherlockFragment;
import idv.funnybrain.plurkchat.FunnyActivity;
import idv.funnybrain.plurkchat.PlurkOAuth;
import idv.funnybrain.plurkchat.R;
import idv.funnybrain.plurkchat.RequestException;
import idv.funnybrain.plurkchat.data.Me;
import idv.funnybrain.plurkchat.data.Plurk_Users;
import idv.funnybrain.plurkchat.data.Plurks;
import idv.funnybrain.plurkchat.modules.Mod_Timeline;
import idv.funnybrain.plurkchat.utils.ImageCache;
import idv.funnybrain.plurkchat.utils.ImageFetcher;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Freeman on 2014/4/3.
 */
public class ChatRoomsFragment extends SherlockFragment {
    // ---- constant variable START ----
    private static final boolean D = true;
    private static final String TAG = "FriendsFragment";

    private static final String IMAGE_CACHE_DIR = "thumbnails";

    protected boolean mPause = false;
    private final Object mPauseLock = new Object();
    // ---- constant variable END ----

    // ---- local variable START ----
    private PlurkOAuth plurkOAuth;
    private Me me;
    private ExpandableListView list;
    private Button bt_more;
    private ChatRoomExpandableListAdapter mAdapter;

    private ImageFetcher mImageFetcher;
    // ---- local variable END ----

    ChatRoomsFragment newInstance() {
        if(D) { Log.d(TAG, "newInstance"); }
        ChatRoomsFragment chatRoomsFragment = new ChatRoomsFragment();
        return chatRoomsFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ImageCache.ImageCacheParams cacheParams =
                new ImageCache.ImageCacheParams(getSherlockActivity(), IMAGE_CACHE_DIR);

        mImageFetcher = new ImageFetcher(getSherlockActivity(), 100);
        mImageFetcher.setLoadingImage(R.drawable.default_plurk_avatar);
        mImageFetcher.addImageCache(getFragmentManager(), cacheParams);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_chatrooms, container, false);
        // View v = inflater.inflate(R.layout.fragment_friends, container, false);
        list = (ExpandableListView) v.findViewById(R.id.elv_list);
        list.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if(scrollState == SCROLL_STATE_FLING) {
                    if(!idv.funnybrain.plurkchat.utils.Utils.hasHoneycomb()) {
                        mImageFetcher.setPauseWork(true);
                    }
                } else {
                    mImageFetcher.setPauseWork(false);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });

        bt_more = (Button) v.findViewById(R.id.bt_more);

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        plurkOAuth = ((FunnyActivity) getActivity()).getPlurkOAuth();

        HashMap<String, String> params = new HashMap<String, String>();
        new Mod_Timeline_getPlurks_AsyncTask().execute(params);
    }

    @Override
    public void onResume() {
        super.onResume();
        mImageFetcher.setExitTasksEarly(false);
        if(mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mImageFetcher.setPauseWork(false);
        mImageFetcher.setExitTasksEarly(true);
        mImageFetcher.flushCache();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mImageFetcher.closeCache();
    }

    private class Mod_Timeline_getPlurks_AsyncTask extends AsyncTask<HashMap<String, String>, Void, JSONObject> {
//        //private HashMap<String, Plurk_Users> plurk_users;
//        private List<Plurks> plurks;
//
//        private List<Plurk_Users> group_plurk_users;
//        private List<List<Plurks>> child_plurks;

        @Override
        protected JSONObject doInBackground(HashMap<String, String>... params) {
//            //plurk_users = new HashMap<String, Plurk_Users>();
//            plurks = new ArrayList<Plurks>();
//            group_plurk_users = new ArrayList<Plurk_Users>();
//            child_plurks = new ArrayList<List<Plurks>>();

            JSONObject result = null;

            String offset = null;
            int limit = 30;
            String filter = null;
            boolean favorers_detail = false;
            boolean limited_detail = false;
            boolean replurkers_detail = false;

            HashMap<String, String> param = params[0];
            if(param.containsKey("offset")) { offset = param.get("offset"); }
            if(param.containsKey("limit")) {
                String tmp = param.get("offset");
                limit = Integer.valueOf(tmp);
            }
            if(param.containsKey("filter")) { filter = param.get("filter"); }
            if(param.containsKey("favorers_detail")) {
                String tmp = param.get("favorers_detail");
                if(tmp.equals("true")) { favorers_detail = true; }
            }
            if(param.containsKey("limited_detail")) {
                String tmp = param.get("limited_detail");
                if(tmp.equals("true")) { limited_detail = true; }
            }
            if(param.containsKey("replurkers_detail")) {
                String tmp = param.get("replurkers_detail");
                if(tmp.equals("true")) { replurkers_detail = true; }
            }

            try {
                result = plurkOAuth.getModule(Mod_Timeline.class).getPlurks(offset, limit, filter, favorers_detail, limited_detail, replurkers_detail);

                return result;
//                JSONObject obj_plurk_users = result.getJSONObject("plurk_users");
//                Iterator<String> iterator = obj_plurk_users.keys();
//                while(iterator.hasNext()) {
//                    String idx = iterator.next();
//                    if(D) { Log.d(TAG, "plurk_users: " + idx); }
//                    if(D) { System.out.println(obj_plurk_users.getJSONObject(idx)); }
//                    //plurk_users.put(idx, new Plurk_Users(obj_plurk_users.getJSONObject(idx)));
//
//                    group_plurk_users.add(new Plurk_Users(obj_plurk_users.getJSONObject(idx)));
//                }
//
//                JSONArray obj_plurks = result.getJSONArray("plurks");
//                for(int x=0; x<obj_plurks.length(); x++) {
//                    plurks.add(new Plurks(obj_plurks.getJSONObject(x)));
//                    if(false) { Log.d(TAG, "" + obj_plurks.getJSONObject(x)); }
//                }
//
//                for(int x=0; x<group_plurk_users.size(); x++) {
//                    ArrayList<Plurks> childList = new ArrayList<Plurks>();
//                    String groupt_id = group_plurk_users.get(x).getId();
//
//                }
//
//                Log.d(TAG, "plurks: " + obj_plurks.length());
            } catch (RequestException e) {
                Log.e(TAG, e.getMessage());
                // e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject object) {
            super.onPostExecute(object);

            //private HashMap<String, Plurk_Users> plurk_users = new HashMap<String, Plurk_Users>();
            List<Plurks> plurks = new ArrayList<Plurks>();

            List<Plurk_Users> group_plurk_users = new ArrayList<Plurk_Users>();
            List<List<Plurks>> child_plurks = new ArrayList<List<Plurks>>();

            try {
                JSONObject obj_plurk_users = object.getJSONObject("plurk_users");
                Iterator<String> iterator = obj_plurk_users.keys();
                while (iterator.hasNext()) {
                    String idx = iterator.next();
                    if (D) {
                        Log.d(TAG, "plurk_users: " + idx);
                    }
                    if (false) {
                        System.out.println(obj_plurk_users.getJSONObject(idx));
                    }
                    //plurk_users.put(idx, new Plurk_Users(obj_plurk_users.getJSONObject(idx)));

                    group_plurk_users.add(new Plurk_Users(obj_plurk_users.getJSONObject(idx)));
                }

                JSONArray obj_plurks = object.getJSONArray("plurks");
                for (int x = 0; x < obj_plurks.length(); x++) {
                    plurks.add(new Plurks(obj_plurks.getJSONObject(x)));
                    if(false) {
                        Plurks debug = new Plurks(obj_plurks.getJSONObject(x));
                        String debug_posted = debug.getPosted();
                        //debug_posted = debug_posted.replaceAll("GMT", "");
                        debug_posted = debug_posted.trim();
                        //debug_posted = debug_posted.substring(5, debug_posted.length());
                        Log.d(TAG, "posted: " + debug_posted);
                        SimpleDateFormat sdf = new SimpleDateFormat("E,dd MMM yyyy HH:mm:ss z", Locale.ENGLISH);
//                        Date curr = new Date();
//                        System.out.println("現在時間: " + sdf.format(curr));
                        try {
                            sdf.parse(debug_posted);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    if (false) {
                        Log.d(TAG, "" + obj_plurks.getJSONObject(x));
                    }
                }

                String posted = plurks.get(plurks.size()-1).getReadablePostedDate(); // plurks.size()-1
                bt_more.setVisibility(View.VISIBLE);
                bt_more.setText("最舊貼文:\n"+ posted);

                for (int x = 0; x < group_plurk_users.size(); x++) {
                    ArrayList<Plurks> childList = new ArrayList<Plurks>();
                    String group_id = group_plurk_users.get(x).getId();
                    //System.out.println("group_id: "+ group_id);
                    for(int y=0; y<plurks.size(); y++) {
                        //System.out.println("owner_id: " + plurks.get(y).getOwner_id());
                        if (plurks.get(y).getOwner_id().equals(group_id)) {
                            childList.add(plurks.get(y));
                        }
                    }
                    child_plurks.add(childList);
                }

                Log.d(TAG, "plurks: " + obj_plurks.length());
            } catch(JSONException jsone) {
                Log.e(TAG, jsone.getMessage());
            }

            if(false) {
                Log.d(TAG, "Group Size: " + group_plurk_users.size());
                Log.d(TAG, "Chiid Size: " + child_plurks.size());

                for(int x=0; x<group_plurk_users.size(); x++) {
                    Log.d(TAG, "Group " + x + ": " + group_plurk_users.get(x).getFull_name());
                }

                for(int x=0; x<child_plurks.size(); x++) {
                    for(int y=0; y<child_plurks.get(x).size(); y++) {
                        Log.d(TAG, x + " Child " + y + ": " + child_plurks.get(x).get(y).getContent());
                    }
                }
            }

            mAdapter = new ChatRoomExpandableListAdapter(getSherlockActivity(), group_plurk_users, child_plurks, mImageFetcher);
            list.setAdapter(mAdapter);
        }
    }
}