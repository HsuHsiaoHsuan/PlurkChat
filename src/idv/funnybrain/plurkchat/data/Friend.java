package idv.funnybrain.plurkchat.data;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Freeman on 2014/4/5.
 "email_confirmed":true,
 "uid":1367985,
 "following_tl":1,
 "location":"Taipei, Taiwan",
 "verified_account":false,
 "settings":true,
 "following_im":1,
 "recruited":17,
 "date_of_birth":"Thu, 06 Oct 1983 00:01:00 GMT",
 "avatar":0,
 "nick_name":"brianhsu",
 "relationship":"single",
 "id":1367985,
 "karma":128.46,
 "display_name":"墳墓（Brian Hsu）"
 ,"name_color":null,
 "following":true,
 "timezone":"Asia\/Taipei",
 "dateformat":0,
 "bday_privacy":2,
 "gender":1,
 "has_profile_image":1,
 "default_lang":"tr_ch",
 "full_name":"BrianHsu"
 *
 */
public class Friend {
    private boolean email_confirmed;
    private String uid;
    private int following_tl;
    private String location;
    private boolean verified_account;
//    private boolean settings;
    private int following_im;
    private int recruited;
    private String date_of_birth;
    private String avatar;
    private String nick_name;
    private String relationship;
    private String id;
    private double karma;
    private String display_name;
//    private String name_color;
    private boolean following;
    private String timezone;
    private String dateformat;
    private int bday_privacy;
    private int gender;
    private int has_profile_image;
    private String default_lang;
    private String full_name;

    public Friend(JSONObject friend) throws JSONException {
        email_confirmed = friend.getBoolean("email_confirmed");
        uid = friend.getString("uid");
        following_tl = friend.getInt("following_tl");
        location = friend.getString("location");
        verified_account = friend.getBoolean("verified_account");
//        settings = friend.getBoolean("settings");
        following_im = friend.getInt("following_im");
        recruited = friend.getInt("recruited");
        date_of_birth = friend.getString("date_of_birth");
        avatar = friend.getString("avatar");
        nick_name = friend.getString("nick_name");
        relationship = friend.getString("relationship");
        id = friend.getString("id");
        karma = friend.getDouble("karma");
        display_name = friend.getString("display_name");
//        System.out.println("NONONONONONONONONONO: " + friend.getString("name_color"));
//        name_color = friend.getString("name_color");
        following = friend.getBoolean("following");
        timezone = friend.getString("timezone");
        dateformat = friend.getString("dateformat");
        bday_privacy = friend.getInt("bday_privacy");
        gender = friend.getInt("gender");
        has_profile_image = friend.getInt("has_profile_image");
        default_lang = friend.getString("default_lang");
        full_name = friend.getString("full_name");
    }

    public String getId() { return id; }

    public String getDisplay_name() { return display_name; }

    public String getNick_name() { return nick_name; }

    public String getFull_name() { return full_name; }

    public int getHas_profile_image() { return has_profile_image; }

    public String getAvatar() { return avatar; }
}
