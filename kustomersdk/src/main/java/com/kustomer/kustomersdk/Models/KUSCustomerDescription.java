package com.kustomer.kustomersdk.Models;

import android.support.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Junaid on 1/20/2018.
 */

public class KUSCustomerDescription {

    //region Properties
    @Nullable
    private String email;
    @Nullable
    private String phone;
    @Nullable
    private String twitter;
    @Nullable
    private String facebook;
    @Nullable
    private String instagram;
    @Nullable
    private String linkedin;
    @Nullable
    private JSONObject custom;
    //endregion

    //region Methods
    public HashMap<String, Object> formData(){
        HashMap<String, Object> formData = new HashMap<>();

        if(email != null){
            JSONObject object = new JSONObject();
            JSONArray array = new JSONArray();
            try {
                object.put("email",email);
                array.put(object);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            formData.put("emails", array );
        }

        if(phone != null){
            JSONObject object = new JSONObject();
            JSONArray array = new JSONArray();
            try {
                object.put("phone",phone);
                array.put(object);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            formData.put("phones", array);
        }

        List<JSONObject> socials = new ArrayList<>();
        if(twitter != null){
            JSONObject object = new JSONObject();
            try {
                object.put("username",twitter);
                object.put("type","twitter");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            socials.add(object);
        }

        if(facebook != null){
            JSONObject object = new JSONObject();
            try {
                object.put("username",twitter);
                object.put("type","facebook");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            socials.add(object);
        }

        if(instagram != null){
            JSONObject object = new JSONObject();
            try {
                object.put("username",twitter);
                object.put("type","instagram");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            socials.add(object);
        }

        if(linkedin != null){
            JSONObject object = new JSONObject();
            try {
                object.put("username",twitter);
                object.put("type","linkedin");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            socials.add(object);
        }

        if(socials.size() > 0){
            formData.put("socials",socials);
        }

        if(custom != null)
            formData.put("custom",custom);

        return formData;
    }
    //endregion

    //region Accessors

    @Nullable
    public String getEmail() {
        return email;
    }

    public void setEmail(@Nullable String email) {
        this.email = email;
    }

    @Nullable
    public String getPhone() {
        return phone;
    }

    public void setPhone(@Nullable String phone) {
        this.phone = phone;
    }

    @Nullable
    public String getTwitter() {
        return twitter;
    }

    public void setTwitter(@Nullable String twitter) {
        this.twitter = twitter;
    }

    @Nullable
    public String getFacebook() {
        return facebook;
    }

    public void setFacebook(@Nullable String facebook) {
        this.facebook = facebook;
    }

    @Nullable
    public String getInstagram() {
        return instagram;
    }

    public void setInstagram(@Nullable String instagram) {
        this.instagram = instagram;
    }

    @Nullable
    public String getLinkedin() {
        return linkedin;
    }

    public void setLinkedin(@Nullable String linkedin) {
        this.linkedin = linkedin;
    }

    @Nullable
    public JSONObject getCustom() {
        return custom;
    }

    public void setCustom(@Nullable JSONObject custom) {
        this.custom = custom;
    }

    //endregion
}
