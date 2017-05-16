package com.example.pawank.themaidproject.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;
import android.util.LruCache;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.example.pawank.themaidproject.DataClass.Employees;
import com.example.pawank.themaidproject.DataClass.ShoppingList;
import com.example.pawank.themaidproject.DataClass.ShoppingListItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static android.R.id.message;

/**
 * Created by pawan.k on 07-02-2017.
 * Miscellaneous utilities used throughout the app
 */

public class MiscUtils {
    private static final String TAG = "Misc Utils";
    public static LruCache<String,Bitmap> cache;
    public static RandomAccessFile raf=null;
    private static String FILE_NAME = "console_log.log";
    public static FileInputStream fis = null;
    public static FileOutputStream fos = null;
    public static BufferedInputStream bis = null;
    public static BufferedOutputStream bos = null;
    public static SimpleDateFormat STRINGDATEFORMAT = new SimpleDateFormat("EEE MMM dd kk:mm:ss z yyyy", Locale.ENGLISH);
    private static Context ctx;

    public static void initMiscUtil(Context context){
        ctx = context;
    }

    public static String trimmedString(String str, int len)
    {
        if(str.length()>len)
            return str.substring(0,len-3)+"..";
        else
            return str;
    }

    public static String JSONParseForSql(JSONObject jObj){
        return jObj.toString().replace("\"","\\\"");
    }

    public static String JSONParseForApp(String jObj){
        return jObj.replace("\\\"","\"");

    }

    //Bitmap decode functions
    public static Bitmap getDecodedImage(Context ctx, int resId, int height){
        int reqHeight=height;
        int reqWidth = ((Activity)ctx).getWindowManager().getDefaultDisplay().getWidth();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        //inJustDecodeBounds set to true return NULL bitmap
        BitmapFactory.decodeResource(ctx.getResources(),resId,options);
        options.inSampleSize = calculateinSampleSize(options,reqHeight,reqWidth);
        options.inJustDecodeBounds = false;
        Bitmap bmp = BitmapFactory.decodeResource(ctx.getResources(),resId,options);
        return bmp;
    }

    //helper to decode factory
    private static int calculateinSampleSize(BitmapFactory.Options options, int reqHeight, int reqWidth) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    //Cache Functions
    public static void initCache(){
        //giving 1/8 memory to cache
        int cacheSize = (int)(Runtime.getRuntime().maxMemory()/1024)/8;
        cache = new LruCache<String,Bitmap>(cacheSize){
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount()/1024;
            }
        };
    }
    public static void cacheBitmap(Context ctx,String key,int resId,int height){
        Bitmap bmp = getDecodedImage(ctx,resId,height);
        cache.put(key,bmp);
    }
    public static Bitmap getBitmap(String key){
        return cache.get(key);
    }

    //Shopping List JSON Parser
    public static JSONObject shoppingListJSONParser(ShoppingList sl) throws JSONException {
        JSONObject workObject = new JSONObject();
        workObject.put("LIST_ID",sl.getId()+"");//All entries in String
        workObject.put("TITLE",sl.getTitle());
        ArrayList<ShoppingListItem> shoppingListItems = sl.getItems();
        JSONObject shoppingItems = new JSONObject();
        int count=1;
        for(ShoppingListItem i : shoppingListItems)
        {
            JSONObject itemObject = new JSONObject();
            itemObject.put("ID",i.getId()+"");
            itemObject.put("TITLE",i.getTitle());
            itemObject.put("TYPE",i.getType());
            itemObject.put("IS_DIRTY",i.getIs_dirty()+"");
            itemObject.put("IS_BOUGHT",i.is_bought);
            shoppingItems.put(count+"",itemObject);
            count++;
        }
        workObject.put("ITEMS",shoppingItems);
        return workObject;
    }

    public static void initPrivateStorage(Context applicationContext) {
        try {
            ctx.openFileOutput(ctx.getPackageName(),ctx.MODE_PRIVATE);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    public static void initInputStream()
    {
        try{
            File console_log_file = null;
            console_log_file = new File(ctx.getFilesDir(),FILE_NAME);
            if(!console_log_file.exists())
                    console_log_file.createNewFile();
                fis = new FileInputStream(console_log_file);
                bis = new BufferedInputStream(fis);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void initOutputStream()
    {
        try{
            File console_log_file = null;
            console_log_file = new File(ctx.getFilesDir(),FILE_NAME);
            if(!console_log_file.exists())
                console_log_file.createNewFile();
            fos = new FileOutputStream(console_log_file,true);
            bos = new BufferedOutputStream(fos);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public static String getConsoleLogs(Activity act) {
        int x;
        StringBuilder log_content=new StringBuilder();
        try {
            initInputStream();
            if(bis==null)
                throw new Exception("Error in RandomAccessFile module");
            log_content.delete(0,log_content.length());
            while((x=bis.read())!=-1)
            {
                log_content.append((char)x);
            }
            bis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return log_content.toString();
    }
    public static void writeConsoleLogs(String TAG,String message){
        try {
            initOutputStream();
            if(bos==null)
                throw new Exception("Error in RandomAccessFile module");
            String to_write="<font color=#EF9A9A><b>"+TAG+"</font>"+": "+message+"<br/>---------------------------------<br/>";
            bos.write(to_write.getBytes());
            bos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void logD(String TAG,String message){
        Log.d(TAG,message);
        writeConsoleLogs(TAG,message);

    }

    public static void logE(String TAG, String message) {
        Log.e(TAG,message);
        writeConsoleLogs(TAG,"<font color=red>"+message+"</font>");
    }


    public static void initConsoleFile(Context ctx) {
        File console_log_file = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            console_log_file = new File(ctx.getFilesDir(),FILE_NAME);
        }
        else{
            console_log_file = new File(ctx.getFilesDir(),FILE_NAME);
        }
        if(console_log_file.exists()) {
            console_log_file.delete();
        }
    }

    public static void forceHideKeyboard(Activity activity){
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        Log.d(TAG,view.getId()+"");
        if (view == null) {
            Log.d(TAG,"null");
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        //Raambadh
        activity.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
    }

    //***********************date Methods**************************//
    public static String getNewDateFormatLongMonth(String joining_date) {
        Date d=null;
        SimpleDateFormat sdf1 = new SimpleDateFormat("dd MMM, yyyy");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
        try {
            d = sdf2.parse(joining_date);
        }catch(Exception e){
            e.printStackTrace();
        }
        return sdf1.format(d).toString();
    }

    public static String getNewDateFormatLongMonth1(String date){
        Date d=null;
        SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat sdf2 = new SimpleDateFormat("dd MMM, yyyy");
        try {
            d = sdf1.parse(date);
        }catch(Exception e){
            e.printStackTrace();
        }
        return sdf2.format(d).toString();
    }

    public static Date getDateObject(String date) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.parse(date);
    }

    public static String getShoppingListDateFormat(String date){
        if(date==null || date.equals("null"))
            return "NA";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat compareFormat = new SimpleDateFormat("dd-MM-yyyy");
        String date1="";
        try {
            date1 = compareFormat.format(sdf.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String date2 = compareFormat.format(new Date());
        if(date1.equals(date2))
        {
            return date.substring(11,date.length());
        }
        else{
            return date1;
        }
    }

    public static String getDayImageResource(int day) {
        switch (day)
        {
            case Calendar.SUNDAY:
                return "SUNDAY";
            case Calendar.MONDAY:
                return "MONDAY";
            case Calendar.TUESDAY:
                return "TUESDAY";
            case Calendar.WEDNESDAY:
                return "WEDNESDAY";
            case Calendar.THURSDAY:
                return "THURSDAY";
            case Calendar.FRIDAY:
                return "FRIDAY";
            case Calendar.SATURDAY:
                return "SATURDAY";
            default:
                MiscUtils.logE(TAG,"invalid parameter passed:"+day);
                return "null";
        }
    }

    public static String getStringFromDateObject(Date d){
        SimpleDateFormat sdf2 = new SimpleDateFormat("dd MMM, yyyy");
        return sdf2.format(d);

    }

    ////////////////////////////////////////////////////////////////

    public static CharSequence getNotificationStyleTitle(String fragment) {
        if(fragment.indexOf("_")!=-1)
            fragment = fragment.replaceAll("_"," ");
        String to_return = fragment.substring(0,1).toUpperCase() + fragment.substring(1,fragment.length()).toLowerCase();
        return to_return;
    }

    public static void forceShowKeyboard(View v,Dialog dialog){
        v.requestFocus();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
//        InputMethodManager imm = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.showSoftInput(v, InputMethodManager.SHOW_IMPLICIT);
    }
}
