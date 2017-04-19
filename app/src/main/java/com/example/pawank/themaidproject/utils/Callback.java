package com.example.pawank.themaidproject.utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.Objects;

/**
 * Created by nexusstar on 27/1/17.
 */

public interface Callback {
    public void onSuccess(Object obj) throws JSONException, ParseException;
    public void onFailure(Object obj) throws JSONException;
}
