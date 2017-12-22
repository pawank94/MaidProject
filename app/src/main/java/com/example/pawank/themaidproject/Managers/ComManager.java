package com.example.pawank.themaidproject.Managers;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.pawank.themaidproject.utils.Callback;
import com.example.pawank.themaidproject.utils.MiscUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by pawan.k on 24-01-2017.
 */

public class ComManager {
    private static final String TAG="ComManager";
    private int mFoodMenuTokenId;
    private static String urlToConnect;
    static JSONObject jObj = null;
    static Map<String,String> tempMap;
    private String KEY_CHECKSUM="CHECKSUM";
    /*login  API */
    private String KEY_LOGIN_USERNAME="USERNAME";
    private String KEY_LOGIN_PASSWORD="PASSWORD";
    private String login_module = "login";
    private String login_subModule = null;
    /* *****************************************/
    /*food menu API*/

    private String get_food_menu_module = "getFoodMenu";
    private String get_food_menu_subModule = null;

    private String update_food_menu_module = "updateFoodMenu";
    private String update_food_menu_subModule = null;
    private String attach_list_to_food_module = "attachListToFood";
    private String attach_list_to_food_subModule = null;
    private String KEY_FOOD_ID = "FOOD_ID";
    private String KEY_SHOPPING_LIST_ID = "LIST_ID";
    private String KEY_UPDATE_FM_DISHNAME = "DISHNAME";
    private String KEY_UPDATE_FM_ISDEFAULT = "ISDEFAULT";
    private String KEY_UPDATE_FM_DATE = "DATE";
    private String KEY_UPDATE_FM_MEAL = "MEAL";
    private String KEY_UPDATE_FM_DAY = "DAY";
    /*****************************************/
    //*shopping list API *//
    private String get_all_shopping_list_module = "getAllLists";
    private String update_shopping_list_module = "updateList";
    private String delete_shopping_list_module = "deleteList";
    private String get_all_shopping_list_subModule = null;

    private String KEY_LIST_JSON="LIST_JSON";
    private String KEY_LIST_ID = "LIST_ID";
    private String KEY_LIST_TITLE = "LIST_TITLE";

    /********************************************************/
    //*attendence API*//
    private String get_employee_details_module = "getEmployeeDetails";
    private String get_employee_details_subModule = null;

    private String get_absent_days_module = "getAbsentDays";
    private String get_absent_days_subModule = null;
    private String KEY_MONTH="MONTH";

    private String report_absent_module = "reportAbsentEntry";
    private String report_absent_subModule = null;

    private String report_absent_range_module="reportAbsentRange";
    private String report_absent_range_subModule=null;

    private String remove_absent_module = "removeAbsentEntry";
    private String remove_absent_subModule = null;


    private String KEY_DATE="DATE";
    private String KEY_ID="ID";
    private String KEY_SHIFT="SHIFT";
    private String KEY_EMP_ID="EMP_ID";
    private String KEY_REASON="REASON";
    private String KEY_FROM_DATE="FROM_DATE";
    private String KEY_TO_DATE="TO_DATE";
    private String KEY_FROM_SHIFT="FROM_SHIFT";
    private String KEY_TO_SHIFT="TO_SHIFT";
    /******************************************************/
    /*Firebase Server Key Module*/
    private String firebase_server_key_module = "setNotificationToken";
    private String KEY_TOKEN="TOKEN";
    /****************************************************************/
    private String currentModule,currentSubModule;
    Context context;
    SQLManager sqlManager;
    Map<String,String> mp;

    public ComManager(Context c)
    {
        context=c;
        sqlManager=new SQLManager(context);
    }
    public static void updateConnectionAddress(){
        urlToConnect=PrefManager.getSharedVal("url_to_connect");
    }
    public static boolean isNetConnected(Context context)
    {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo= cm.getActiveNetworkInfo();
        return networkInfo!=null && networkInfo.isConnected();
    }
    public void postLogin(Context ctx, String userName, String password, String IP, final Callback loginCallback) {
        String ipToConnect = getProperUrl(IP);
        sqlManager.putUrlToConnectTableAddress(ipToConnect);
        PrefManager.setSharedVal("url_to_connect",ipToConnect);
        updateConnectionAddress();
        Callback callback= new Callback(){
            @Override
            public void onSuccess(Object obj) throws JSONException, ParseException {
                Log.d("postLogin Callback","response"+jObj);
                jObj = (JSONObject)obj;
                if(jObj.getString("STATUS").equals("OK"))
                {
                    loginCallback.onSuccess(jObj);
                 }
                else{
                    loginCallback.onFailure(jObj);
                }
            }
            @Override
                public void onFailure(Object obj) throws JSONException {
                    loginCallback.onFailure(null);
                }
        };
        currentModule=login_module;
        currentSubModule=login_subModule;
        mp = new HashMap<>();
        //TODO:Hard Coding
        mp.put(KEY_LOGIN_USERNAME,userName);
        mp.put(KEY_LOGIN_PASSWORD,password);
        MiscUtils.logD(TAG,"post Login:"+mp.toString());
        this.makeHttpRequest(ctx,urlToConnect,mp,currentModule,currentSubModule,callback);
    }

    //********************food menu module***************************//
    public void getFoodMenu(Context ctx,final Callback FoodMenucallback) {
       updateConnectionAddress();
        Callback callback = new Callback() {
            @Override
            public void onSuccess(Object obj) throws JSONException, ParseException {
                jObj = (JSONObject)obj;
                if(jObj.getString("STATUS").equals("OK")) {
                    sqlManager.putFoodMenu(jObj);
                    FoodMenucallback.onSuccess(jObj);
                }
                else {
                    FoodMenucallback.onFailure(jObj);
                }
            }
            @Override
            public void onFailure(Object obj) throws JSONException {
                FoodMenucallback.onFailure(jObj);
            }
        };
        currentModule= get_food_menu_module;
        currentSubModule= get_food_menu_subModule;
        mp = new HashMap<>();
       // MiscUtils.logD("checksum",PrefManager.getSharedVal("checksum"));
        mp.put(KEY_CHECKSUM,PrefManager.getSharedVal("checksum"));
        this.makeHttpRequest(ctx,urlToConnect,mp,currentModule,currentSubModule,callback);
    }

    public void attachListToFood(Context ctx,String fInsertionId,String sIntertionId,final Callback parentCallback){
        updateConnectionAddress();
        Callback callback = new Callback() {
            @Override
            public void onSuccess(Object obj) throws JSONException, ParseException {
                jObj = (JSONObject)obj;
                if(jObj.getString("STATUS").equals("OK")) {
                    parentCallback.onSuccess(obj);
                }
                else {
                    parentCallback.onFailure(obj);
                }
            }
            @Override
            public void onFailure(Object obj) throws JSONException {
                parentCallback.onFailure(jObj);
            }
        };
        currentModule= attach_list_to_food_module;
        currentSubModule= attach_list_to_food_subModule;
        mp = new HashMap<>();
        // MiscUtils.logD("checksum",PrefManager.getSharedVal("checksum"));
        mp.put(KEY_CHECKSUM,PrefManager.getSharedVal("checksum"));
        mp.put(KEY_FOOD_ID,fInsertionId);
        mp.put(KEY_SHOPPING_LIST_ID,sIntertionId);
        this.makeHttpRequest(ctx,urlToConnect,mp,currentModule,currentSubModule,callback);
    }

    public void updateFoodMenu(Context ctx,String dishname, String date, String day,String meal, boolean isDefault, final Callback updateFoodMenuCallback) {
       updateConnectionAddress();
        Callback callback = new Callback() {
            @Override
            public void onSuccess(Object obj) throws JSONException, ParseException {
                jObj = (JSONObject)obj;
                if(jObj.getString("STATUS").equals("OK")) {
                    //sql Manager
                    //TODO: token logic
                    updateFoodMenuCallback.onSuccess(jObj);
                }
                else {
                    updateFoodMenuCallback.onFailure(jObj);
                }
            }
            @Override
            public void onFailure(Object obj) throws JSONException {
                jObj = (JSONObject)obj;
                updateFoodMenuCallback.onFailure(jObj);
            }
        };
        currentModule= update_food_menu_module;
        currentSubModule= update_food_menu_subModule;
        mp = new HashMap<>();
        mp.put(KEY_UPDATE_FM_DISHNAME,dishname);
        mp.put(KEY_UPDATE_FM_DATE,date);
        mp.put(KEY_UPDATE_FM_ISDEFAULT,isDefault?"Y":"N");
        mp.put(KEY_UPDATE_FM_MEAL,meal.toUpperCase());
        mp.put(KEY_UPDATE_FM_DAY,day);
        mp.put(KEY_CHECKSUM,PrefManager.getSharedVal("checksum"));
        Log.d(TAG,"update food menu:"+mp.toString());
        this.makeHttpRequest(ctx,urlToConnect,mp,currentModule,currentSubModule,callback);
    }

    //******************************Shopping List Modules******************//
    public void getAllShoppingList(Context ctx, final Callback shoppingListCallback) {
        updateConnectionAddress();
        Callback callback = new Callback() {
            @Override
            public void onSuccess(Object obj) throws JSONException, ParseException {
                jObj = (JSONObject)obj;
                if(jObj.getString("STATUS").equals("OK")) {
                    //sql Manager
                    sqlManager.putShoppingList((JSONObject) obj);
                    //TODO: token logic
                    shoppingListCallback.onSuccess(jObj);
                }
                else {
                    shoppingListCallback.onFailure(jObj);
                }
            }
            @Override
            public void onFailure(Object obj) throws JSONException {
                jObj = (JSONObject)obj;
                shoppingListCallback.onFailure(jObj);
            }
        };
        currentModule= get_all_shopping_list_module;
        currentSubModule= get_all_shopping_list_subModule;
        mp = new HashMap<>();
        mp.put(KEY_CHECKSUM,PrefManager.getSharedVal("checksum"));
        this.makeHttpRequest(ctx,urlToConnect,mp,currentModule,currentSubModule,callback);
    }

    public void uploadShoppingList(Context ctx,final Callback parentCallback,JSONObject jsonObject) {
        updateConnectionAddress();
        Callback callback = new Callback() {
            @Override
            public void onSuccess(Object obj) throws JSONException, ParseException {
                jObj = (JSONObject)obj;
                if(jObj.getString("STATUS").equals("OK")) {
                    parentCallback.onSuccess(jObj);
                }
                else {
                    parentCallback.onFailure(jObj);
                }
            }
            @Override
            public void onFailure(Object obj) throws JSONException {
                jObj = (JSONObject)obj;
                parentCallback.onFailure(jObj);
            }
        };
        currentModule= update_shopping_list_module;
        currentSubModule= update_food_menu_subModule;
        Log.d(TAG,jsonObject.toString());
        mp = new HashMap<>();
        mp.put(KEY_CHECKSUM,PrefManager.getSharedVal("checksum"));
        mp.put(KEY_LIST_JSON,jsonObject.toString());
        MiscUtils.logD(TAG,"upload shoppping list:"+mp.toString());
        this.makeHttpRequest(ctx,urlToConnect,mp,currentModule,currentSubModule,callback);
    }

    public void deleteShoppingList(Context ctx,int list_id,String list_title,final Callback parentCallback){
        updateConnectionAddress();
        Callback callback = new Callback() {
            @Override
            public void onSuccess(Object obj) throws JSONException, ParseException {
                jObj = (JSONObject)obj;
                if(jObj.getString("STATUS").equals("OK")) {
                    parentCallback.onSuccess(jObj);
                }
                else {
                    parentCallback.onFailure(jObj);
                }
            }
            @Override
            public void onFailure(Object obj) throws JSONException {
                jObj = (JSONObject)obj;
                parentCallback.onFailure(jObj);
            }
        };
        currentModule= delete_shopping_list_module;
        currentSubModule= update_food_menu_subModule;
        mp = new HashMap<>();
        mp.put(KEY_CHECKSUM,PrefManager.getSharedVal("checksum"));
        mp.put(KEY_LIST_ID,list_id+"");
        mp.put(KEY_LIST_TITLE,list_title);
        MiscUtils.logD(TAG,"delete shoppping list:"+mp.toString());
        this.makeHttpRequest(ctx,urlToConnect,mp,currentModule,currentSubModule,callback);
    }
    //**************************Attendence modules*****************************//
    public void getAllEmployeeDetails(Context ctx,final Callback parentCallback) {
        updateConnectionAddress();
        Callback callback = new Callback() {
            @Override
            public void onSuccess(Object obj) throws JSONException, ParseException {
                jObj = (JSONObject)obj;
                if(jObj.getString("STATUS").equals("OK")) {
                    sqlManager.putEmployeeDetail(jObj);
                    parentCallback.onSuccess(jObj);
                }
                else {
                    parentCallback.onFailure(jObj);
                }
            }
            @Override
            public void onFailure(Object obj) throws JSONException {
                jObj = (JSONObject)obj;
                parentCallback.onFailure(jObj);
            }
        };
        currentModule= get_employee_details_module;
        currentSubModule= get_employee_details_subModule;
        mp = new HashMap<>();
        mp.put(KEY_CHECKSUM,PrefManager.getSharedVal("checksum"));
        MiscUtils.logD(TAG,"getting Employee Details!");
        this.makeHttpRequest(ctx,urlToConnect,mp,currentModule,currentSubModule,callback);
    }

    public void getAbsentDaysForEmployee(Context ctx, String eid, int month, final Callback attendenceDataCallback) {
        updateConnectionAddress();
        Callback callback = new Callback() {
            @Override
            public void onSuccess(Object obj) throws JSONException, ParseException {
                jObj = (JSONObject)obj;
                if(jObj.getString("STATUS").equals("OK")) {
                    Log.d(TAG,jObj.toString());
                    attendenceDataCallback.onSuccess(jObj);
                }
                else {
                    attendenceDataCallback.onFailure(jObj);
                }
            }
            @Override
            public void onFailure(Object obj) throws JSONException {
                jObj = (JSONObject)obj;
                if(jObj!=null)
                    attendenceDataCallback.onFailure(jObj);
                else{
                    attendenceDataCallback.onFailure(new JSONObject());
                }
            }
        };
        currentModule= get_absent_days_module;
        currentSubModule= get_absent_days_subModule;
        mp = new HashMap<>();
        mp.put(KEY_CHECKSUM,PrefManager.getSharedVal("checksum"));
        mp.put(KEY_MONTH,month+"");
        mp.put(KEY_EMP_ID,eid);
        MiscUtils.logD(TAG,"get Absent Days:"+mp.toString());
        this.makeHttpRequest(ctx,urlToConnect,mp,currentModule,currentSubModule,callback);
    }

    public void reportAbsent(Context ctx, String date, String shift, String eid, String reason, final Callback parentCallback) {
        updateConnectionAddress();
        Callback callback = new Callback() {
            @Override
            public void onSuccess(Object obj) throws JSONException, ParseException {
                jObj = (JSONObject)obj;
                if(jObj.getString("STATUS").equals("OK")) {
                    Log.d(TAG,jObj.toString());
                    parentCallback.onSuccess(jObj);
                }
                else {
                    parentCallback.onFailure(jObj);
                }
            }
            @Override
            public void onFailure(Object obj) throws JSONException {
                jObj = (JSONObject)obj;
                parentCallback.onFailure(jObj);
            }
        };
        currentModule= report_absent_module;
        currentSubModule= report_absent_subModule;
        mp = new HashMap<>();
        mp.put(KEY_CHECKSUM,PrefManager.getSharedVal("checksum"));
        mp.put(KEY_DATE,date);
        mp.put(KEY_SHIFT,shift.toUpperCase());
        mp.put(KEY_EMP_ID,eid);
        mp.put(KEY_REASON,reason);
        MiscUtils.logD(TAG,"report Absent:"+mp.toString());
        this.makeHttpRequest(ctx,urlToConnect,mp,currentModule,currentSubModule,callback);
    }

    public void reportAbsentRange(Context ctx, String first, String first_shift, String second, String second_shift, String eid, String reason, final Callback parentCallback) {
        updateConnectionAddress();
        Callback callback = new Callback() {
            @Override
            public void onSuccess(Object obj) throws JSONException, ParseException {
                jObj = (JSONObject)obj;
                if(jObj.getString("STATUS").equals("OK")) {
                    Log.d(TAG,jObj.toString());
                    parentCallback.onSuccess(jObj);
                }
                else {
                    parentCallback.onFailure(jObj);
                }
            }
            @Override
            public void onFailure(Object obj) throws JSONException {
                jObj = (JSONObject)obj;
                parentCallback.onFailure(jObj);
            }
        };
        currentModule= report_absent_range_module;
        currentSubModule= report_absent_range_subModule;
        mp = new HashMap<>();
        mp.put(KEY_CHECKSUM,PrefManager.getSharedVal("checksum"));
        mp.put(KEY_FROM_DATE,first);
        mp.put(KEY_FROM_SHIFT,first_shift);
        mp.put(KEY_TO_DATE,second);
        mp.put(KEY_TO_SHIFT,second_shift);
        mp.put(KEY_EMP_ID,eid);
        mp.put(KEY_REASON,reason);
        MiscUtils.logD(TAG,"report Absent Range:"+mp.toString());
        this.makeHttpRequest(ctx,urlToConnect,mp,currentModule,currentSubModule,callback);
    }

    public void removeAbsentEntries(Context ctx, String id,final Callback parentCallback) {
        updateConnectionAddress();
        Callback callback = new Callback() {
            @Override
            public void onSuccess(Object obj) throws JSONException, ParseException {
                jObj = (JSONObject)obj;
                if(jObj.getString("STATUS").equals("OK")) {
                    Log.d(TAG,jObj.toString());
                    parentCallback.onSuccess(jObj);
                }
                else {
                    parentCallback.onFailure(jObj);
                }
            }
            @Override
            public void onFailure(Object obj) throws JSONException {
                jObj = (JSONObject)obj;
                parentCallback.onFailure(jObj);
            }
        };
        currentModule= remove_absent_module;
        currentSubModule= remove_absent_subModule;
        mp = new HashMap<>();
        mp.put(KEY_CHECKSUM,PrefManager.getSharedVal("checksum"));
        mp.put(KEY_ID,id);
        MiscUtils.logD(TAG,"remove Absent Entries: "+mp.toString());
        this.makeHttpRequest(ctx,urlToConnect,mp,currentModule,currentSubModule,callback);
    }

    public static String getProperUrl(String ip) {
        return ip + "/SHEapp/Api";
    }
    //Firebase Key to server
    public void sendFirebaseDeviceKeyToServer(Context ctx, final String token) {
       updateConnectionAddress();
        currentModule=firebase_server_key_module;
        Callback callback = new Callback() {
            @Override
            public void onSuccess(Object obj) throws JSONException, ParseException {
                MiscUtils.logD(TAG,"Firebase Device Key Sent:"+token);
            }

            @Override
            public void onFailure(Object obj) throws JSONException {
                MiscUtils.logE(TAG,"Failure in sending key");
            }
        };
        mp = new HashMap<>();
        mp.put(KEY_CHECKSUM,PrefManager.getSharedVal("checksum"));
        mp.put(KEY_TOKEN,token);
        this.makeHttpRequest(ctx,urlToConnect,mp,currentModule,currentSubModule,callback);

    }

    /** function to make http request**/
    public void makeHttpRequest(Context ctx, String url,final Map<String,String> map, final String module,final String subModule,final Callback callback)
    {
        if(!isNetConnected(context)){
            Toast.makeText(context,"No Data Connection",Toast.LENGTH_SHORT).show();
            try {
                callback.onFailure(null);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return;
        }
        if(module!=null){
            url=url+"/"+module;
        }
        if(subModule!=null) {
            url = url + "/" + subModule;
        }
        MiscUtils.logD(TAG,url);
        StringRequest sr = new StringRequest(Request.Method.POST,url,new Response.Listener<String>(){
            @Override
            public void onResponse(String response) {
                Log.d(TAG+" response",response);
                try {
                    if(response!=null) {
                        MiscUtils.logD(TAG,"API responded successfully");
                        jObj = new JSONObject(response);
                        if(callback!=null)
                            callback.onSuccess(jObj);
                    }
                    else{
                        if(callback!=null) {
                            MiscUtils.logE(TAG,"API failed");
                            callback.onFailure(null);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        },new Response.ErrorListener(){
        @Override
        public void onErrorResponse(VolleyError error) {
            error.printStackTrace();
            try {
                if(callback!=null)
                    callback.onFailure(jObj);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return map;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(ctx);
        requestQueue.add(sr);
    }

}
