package idv.funnybrain.plurkchat.data;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Freeman on 2014/4/3.
 */
public class Me {
    private String location;
    private boolean verified_account;
    private int recruited;
    private String about;
    private String avatar_small;
    private String nick_name;
    private int id;
    private double karma;
    private String timezone;
    private int bday_privacy;
    private boolean post_anonymous_plurk;
    private boolean setup_weibo_sync;
    private String avatar_medium;
    private int gender;
    private String accept_private_plurk_from;
    private boolean setup_twitter_sync;
    private int fans_count;
    private String date_of_birth;
    private String privacy;
    private int profile_views;
    private int plurks_count;
    private int avatar;
    private String avatar_big;
    private boolean setup_facebook_sync;
    private String relationship;
    private String display_name;
    private String name_color;
    private int friends_count;
    private int dateformat;
    private int response_count;
    private int has_profile_image;
    private Language default_lang;
    private String full_name;
    private String page_title;

    public Me(JSONObject me) throws JSONException {
        location = me.getString("location");
        verified_account = me.getBoolean("verified_account");
        recruited = me.getInt("recruited");
        about = me.getString("about");
        avatar_small = me.getString("avatar_small");
        nick_name = me.getString("nick_name");
        id = me.getInt("id");
        karma = me.getDouble("karma");
        timezone = me.getString("timezone");
        bday_privacy = me.getInt("bday_privacy");
        post_anonymous_plurk = me.getBoolean("post_anonymous_plurk");
        setup_weibo_sync = me.getBoolean("setup_weibo_sync");
        avatar_medium = me.getString("avatar_medium");
        gender = me.getInt("gender");
        accept_private_plurk_from = me.getString("accept_private_plurk_from");
        setup_twitter_sync = me.getBoolean("setup_twitter_sync");
        fans_count = me.getInt("fans_count");
        date_of_birth = me.getString("date_of_birth");
        privacy = me.getString("privacy");
        profile_views = me.getInt("profile_views");
        plurks_count = me.getInt("plurks_count");
        avatar = me.getInt("avatar");
        avatar_big = me.getString("avatar_big");
        setup_facebook_sync = me.getBoolean("setup_facebook_sync");
        relationship = me.getString("relationship");
        display_name = me.getString("display_name");
        name_color = me.getString("name_color");
        friends_count = me.getInt("friends_count");
        dateformat = me.getInt("dateformat");
        response_count = me.getInt("response_count");
        has_profile_image = me.getInt("has_profile_image");
        String lang = me.getString("default_lang");
        default_lang = Language.getLang(lang);
        full_name = me.getString("full_name");
        page_title = me.getString("page_title");
    }

    public int getId() {
        return id;
    }

    public String getDisplay_name() {
        return display_name;
    }
}
