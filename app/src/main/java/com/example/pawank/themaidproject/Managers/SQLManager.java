package com.example.pawank.themaidproject.Managers;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import com.example.pawank.themaidproject.DataClass.ModuleNotification;
import com.example.pawank.themaidproject.Services.FirebaseMainService;
import com.example.pawank.themaidproject.utils.MiscUtils;

import org.json.JSONObject;

import java.util.ArrayList;

public class SQLManager extends SQLiteOpenHelper {
    Context context;
    private static final String TAG="SQLManager";
    private static final String database_name = "maindb.db";
    private static final int database_version = 1;
    private static final int dummy_no=1;
    /**credential Table**/
    private static final String credential_table_name="credentialTable";
    private static final String credential_column_1="username";
    private static final String credential_type_1="varchar(20)";
    private static final String credential_column_2="checksum";
    private static final String credential_type_2="varchar(20)";
    private static final String credential_column_3="userId";
    private static final String credential_type_3="varchar(20)";
    //**table for tokenId
    private static final String token_table_name="tokenTable";
    private static final String token_column_1="json";
    private static final String token_type_1="varchar(1000)";
    //**url data table
    private static final String url_table_name="urlTable";
    private static final String url_column_1="address";
    private static final String url_type_1="varchar(20)";
    //*food menu table**/
    private static final String food_menu_table_name="foodMenuTable";
    private static final String food_menu_column_1="tokenId";
    private static final String food_menu_type_1="varchar(20)";
    private static final String food_menu_column_2="json";
    private static final String food_menu_type_2="varchar(1000)";
    //*shopping list table *//
    private static final String shopping_list_table_name="shoppingListTable";
    private static final String shopping_list_column_1="tokenId";
    private static final String shopping_list_type_1="varchar(20)";
    private static final String shopping_list_column_2="json";
    private static final String shopping_list_type_2="varchar(1000)";
    //*mentions table*//
    private static final String mention_table_name="mentionTable";
    private static final String mention_column_1="tokenId";
    private static final String mention_type_1="varchar(20)";
    private static final String mention_column_2="json";
    private static final String mention_type_2="varchar(1000)";
    //*Attendence Table*//
    private static final String attendence_table_name="attendenceTable";
    private static final String attendence_column_1="tokenId";
    private static final String attendence_type_1="varchar(20)";
    private static final String attendence_column_2="json";
    private static final String attendence_type_2="varchar(1000)";
    //*Employee table*//
    private static final String employee_table_name="employeeTable";
    private static final String employee_column_1="tokenId";
    private static final String employee_type_1="varchar(20)";
    private static final String employee_column_2="json";
    private static final String employee_type_2="varchar(1000)";
    //*Activities table*//
    private static final String activities_table_name="activitiesTable";
    private static final String activities_column_1="date";
    private static final String activities_type_1="varchar(20)";
    private static final String activities_column_2="content";
    private static final String activities_type_2="varchar(1000)";
    private static final String activities_column_3="module";
    private static final String activities_type_3="varchar(1000)";
    //TODO: hardcoded values

    private Cursor cursor;
    private SQLiteDatabase sql_db;
    public SQLManager(Context context) {
        super(context,database_name,null,database_version);
        this.context=context;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String query1="create table "+token_table_name+"(id integer primary key AUTOINCREMENT NOT NULL,"+token_column_1+" "+token_type_1+")";
        String query2="create table "+url_table_name+"(id integer primary key AUTOINCREMENT NOT NULL,"+url_column_1+" "+url_type_1+")";
        String query3="create table "+credential_table_name+"(id integer primary key AUTOINCREMENT NOT NULL,"+credential_column_1+" "+credential_type_1+","+credential_column_2+" "+credential_type_2+","+credential_column_3+" "+credential_type_3+")";
        String query4="create table "+food_menu_table_name+"(id integer primary key AUTOINCREMENT NOT NULL,"+food_menu_column_1+" "+food_menu_type_1+","+food_menu_column_2+" "+food_menu_type_2+")";
        String query5="create table "+shopping_list_table_name+"(id integer primary key AUTOINCREMENT NOT NULL,"+shopping_list_column_1+" "+shopping_list_type_1+","+shopping_list_column_2+" "+shopping_list_type_2+")";
        String query6="create table "+mention_table_name+"(id integer primary key AUTOINCREMENT NOT NULL,"+mention_column_1+" "+mention_type_1+","+mention_column_2+" "+mention_type_2+")";
        String query7="create table "+attendence_table_name+"(id integer primary key AUTOINCREMENT NOT NULL,"+attendence_column_1+" "+attendence_type_1+","+attendence_column_2+" "+attendence_type_2+")";
        String query8="create table "+employee_table_name+"(id integer primary key AUTOINCREMENT NOT NULL,"+employee_column_1+" "+employee_type_1+","+employee_column_2+" "+employee_type_2+")";
        String query9="create table "+activities_table_name+"("+activities_column_1+" "+activities_type_1+","+activities_column_2+" "+activities_type_2+","+activities_column_3+" "+activities_type_3+")";
        db.execSQL(query1);
        MiscUtils.logD(TAG,token_table_name+" created");
        db.execSQL(query2);
        MiscUtils.logD(TAG,url_table_name+" created");
        db.execSQL(query3);
        MiscUtils.logD(TAG,credential_table_name+" created");
        db.execSQL(query4);
        MiscUtils.logD(TAG,food_menu_table_name+" created");
        db.execSQL(query5);
        MiscUtils.logD(TAG,shopping_list_table_name+" created");
        db.execSQL(query6);
        MiscUtils.logD(TAG,mention_table_name+" created");
        db.execSQL(query7);
        MiscUtils.logD(TAG,attendence_table_name+" created");
        db.execSQL(query8);
        MiscUtils.logD(TAG,employee_table_name+" created");
        db.execSQL(query9);
        MiscUtils.logD(TAG,activities_table_name+" created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //TODO: on version upgrade
    }

    public boolean isTableEmpty(String table_name){
        String query="select * from "+table_name;
        sql_db = this.getReadableDatabase();
        cursor = sql_db.rawQuery(query,null);
        if(cursor.getCount()==0)
            return true;
        else
            return false;
    }
    public void putUrlToConnectTableAddress(String address){
        sql_db=this.getWritableDatabase();
        if(!isTableEmpty(url_table_name)) {
            MiscUtils.logD(TAG,"url table value updated");
            String query_to_update = "update urlTable set "+url_column_1+"='"+address+"' where id=1";
            Log.d(TAG,"query: "+query_to_update);
            sql_db.execSQL(query_to_update);
        }
        else{
            MiscUtils.logD(TAG,"value inserted");
            String query_to_insert_address = "insert into urlTable values(1,'" + address + "')";
            Log.d(TAG,"query: "+query_to_insert_address);
            sql_db.execSQL(query_to_insert_address);
        }
        sql_db.close();
    }
    public String getUrlTableAddress(Context context) {
        sql_db=this.getReadableDatabase();
        String urlAddress=null;
        String query = "select * from urlTable";
        cursor=sql_db.rawQuery(query,null);
        cursor.moveToFirst();
        if(cursor.getCount()==0)
        {
            Toast.makeText(context,"No url address Available",Toast.LENGTH_SHORT).show();
        }
        else{
            urlAddress=cursor.getString(cursor.getColumnIndex(url_column_1));
            MiscUtils.logD(TAG,"url found");
            PrefManager.setSharedVal("url_to_connect",urlAddress);
        }
        cursor.close();
        sql_db.close();
        return urlAddress;
    }
    public String getCheckSum(){
        String query="select "+credential_column_2+" from credentialTable";
        sql_db=this.getReadableDatabase();
        cursor=sql_db.rawQuery(query,null);
        cursor.moveToFirst();
        if(cursor.getCount()==0)
            return null;
        else
            return cursor.getString(cursor.getColumnIndex(credential_column_2));

    }
    public String getUserName() {
        String query="select "+credential_column_1+" from credentialTable";
        sql_db=this.getReadableDatabase();
        cursor=sql_db.rawQuery(query,null);
        cursor.moveToFirst();
        if(cursor.getCount()==0)
            return null;
        else
            return cursor.getString(cursor.getColumnIndex(credential_column_1));
    }

    public void updateCredentialTable(String userName, String checksum,String userId) {
        if(this.getUserName()==null && this.getCheckSum()==null)
        {
            String query="insert into "+credential_table_name+" values(1,'"+userName+"','"+checksum+"','"+userId+"')";
            sql_db.execSQL(query);
            Log.d(TAG,query);
            PrefManager.setSharedVal("checksum",checksum);
            PrefManager.setSharedVal("username",userName);
            PrefManager.setSharedVal("userid",userId);
        }
    }
    public String getToken(String tableName) {
        String query = "select tokenId from "+tableName;
        sql_db=this.getReadableDatabase();
        cursor=sql_db.rawQuery(query,null);
        cursor.moveToFirst();
        if(cursor.getCount()==0)
            return null;
        else
            return cursor.getString(cursor.getColumnIndex("tokenId")); // returning JSON if token found else null
    }
    public String getData(String tableName,String fieldName) {
        String query = "select "+fieldName+" from "+tableName;
        sql_db=this.getReadableDatabase();
        cursor=sql_db.rawQuery(query,null);
        cursor.moveToFirst();
        if(cursor.getCount()==0)
            return null;
        else
            return MiscUtils.JSONParseForApp(cursor.getString(cursor.getColumnIndex(fieldName))); // returning JSON if token found else null
    }

    public void putFoodMenu(JSONObject jObj) {
        //TODO: HARD CODING TOKEN
        int token = 123;
        sql_db=this.getWritableDatabase();
        if(!isTableEmpty(food_menu_table_name))
        {
            MiscUtils.logD(TAG,"food menu value updated");
            String query_to_update = "update "+food_menu_table_name+" set "+food_menu_column_2+"='"+MiscUtils.JSONParseForSql(jObj)+"' where id=1";
            Log.d(TAG,"query: "+query_to_update);
            sql_db.execSQL(query_to_update);
            query_to_update = "update "+food_menu_table_name+" set " +food_menu_column_1+"='"+token+"' where id=1";
            Log.d(TAG,"query: "+query_to_update);
            sql_db.execSQL(query_to_update);


        }
        else{
            MiscUtils.logD(TAG,"food menu value inserted");
            String query_to_insert="insert into "+food_menu_table_name+" values(1,'"+token+"','"+MiscUtils.JSONParseForSql(jObj)+"')";
            Log.d(TAG,"query: "+query_to_insert);
            sql_db.execSQL(query_to_insert);
        }
        sql_db.close();
    }

    public void putShoppingList(JSONObject jObj)
    {
        int token=123;
        sql_db=this.getWritableDatabase();
        if(!isTableEmpty(shopping_list_table_name))
        {
            MiscUtils.logD(TAG,"Shopping List value updated");
            String query_to_update = "update "+shopping_list_table_name+" set "+shopping_list_column_2+"='"+MiscUtils.JSONParseForSql(jObj)+"' where id=1";
            Log.d(TAG,"query: "+query_to_update);
            sql_db.execSQL(query_to_update);
            query_to_update = "update "+shopping_list_table_name+" set " +shopping_list_column_1+"='"+token+"' where id=1";
            Log.d(TAG,"query: "+query_to_update);
            sql_db.execSQL(query_to_update);


        }
        else{
            MiscUtils.logD(TAG,"Shopping List value inserted");
            String query_to_insert="insert into "+shopping_list_table_name+" values(1,'"+token+"','"+MiscUtils.JSONParseForSql(jObj)+"')";
            Log.d(TAG,"query: "+query_to_insert);
            sql_db.execSQL(query_to_insert);
        }
        sql_db.close();
    }

    public void saveActivities() {
        sql_db=this.getWritableDatabase();
        if(!isTableEmpty(activities_table_name)) {
            String query_drop_activities_table = "delete from " + activities_table_name;
            sql_db.execSQL(query_drop_activities_table);
        }
        for(ModuleNotification mn : FirebaseMainService.getAllActivities()){
            String query = "insert into "+activities_table_name+" values('"+mn.getDate()+"','"+mn.getnContent()+"','"+mn.getModule()+"')";
            sql_db.execSQL(query);
        }
        MiscUtils.logD(TAG,"activities table updated");
        sql_db.close();
    }

    public ArrayList<ModuleNotification> getActivities(){
        sql_db = this.getReadableDatabase();
        ArrayList<ModuleNotification> array = new ArrayList<>();
        if(!isTableEmpty(activities_table_name)){
            String query = "select * from "+activities_table_name;
            Cursor c = sql_db.rawQuery(query,null);
            if(c.moveToFirst()){
                do{
                    ModuleNotification mn = new ModuleNotification();
                    mn.setDate(c.getString(c.getColumnIndex(activities_column_1)));
                    mn.setnContent(c.getString(c.getColumnIndex(activities_column_2)));
                    mn.setModule(c.getString(c.getColumnIndex(activities_column_3)));
                    array.add(mn);
                }while(c.moveToNext());
            }
        }
        sql_db.close();
        return array;
    }

    public void putEmployeeDetail(JSONObject jObj)
    {
        int token=123;
        sql_db=this.getWritableDatabase();
        if(!isTableEmpty(employee_table_name))
        {
            MiscUtils.logD(TAG,"employees value updated");
            String query_to_update = "update "+employee_table_name+" set "+employee_column_2+"='"+MiscUtils.JSONParseForSql(jObj)+"' where id=1";
            Log.d(TAG,"query: "+query_to_update);
            sql_db.execSQL(query_to_update);
            query_to_update = "update "+employee_table_name+" set " +employee_column_1+"='"+token+"' where id=1";
            Log.d(TAG,"query: "+query_to_update);
            sql_db.execSQL(query_to_update);


        }
        else{
            MiscUtils.logD(TAG,"employee value inserted");
            String query_to_insert="insert into "+employee_table_name+" values(1,'"+token+"','"+MiscUtils.JSONParseForSql(jObj)+"')";
            Log.d(TAG,"query: "+query_to_insert);
            sql_db.execSQL(query_to_insert);
        }
        sql_db.close();
    }
}
