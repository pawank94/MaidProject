package com.example.pawank.themaidproject.DataClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by pawan.k on 30-03-2017.
 */

public class Employees {
    String eid;
    String name;
    String id_proof;
    String mobile;
    String joining_date;
    String config_json;
    String last_modified;
    String is_active;
    String absentDays;
    String type;

    public Employees(){
        eid=name=id_proof=mobile=joining_date=config_json=last_modified=null;
    }

    public static ArrayList<Employees> populateEmployeeArray(Object obj) throws JSONException {
        JSONObject jObj = (JSONObject)obj;
        ArrayList<Employees> arr = new ArrayList<>();
        JSONArray workObject = jObj.getJSONArray("DATA");
        for(int i=0;i<workObject.length();i++)
        {
            JSONObject currentObject = workObject.getJSONObject(i);
            Employees emp = new Employees();
            emp.setEid(currentObject.getString("EID"));
            emp.setName(currentObject.getString("NAME"));
            emp.setId_proof(currentObject.getString("ID_PROOF"));
            emp.setMobile(currentObject.getString("MOBILE"));
            emp.setJoining_date(currentObject.getString("JOINING_DATE"));
            emp.setConfig_json(currentObject.getString("CONFIG_JSON"));
            emp.setLast_modified(currentObject.getString("LAST_MODIFIED"));
            emp.setIs_active(currentObject.getString("IS_ACTIVE"));
            emp.setAbsentDays("-1");
            //get Type from Prefix
            String type = emp.getName().split(" ")[0].toUpperCase();
            if(type.equals("COOK"))
                emp.setType(type);
            else
                emp.setType("MAID");
            arr.add(emp);
        }
        return arr;
    }

    public String getIs_active() {
        return is_active;
    }

    public void setIs_active(String is_active) {
        this.is_active = is_active;
    }

    public String getEid() {
        return eid;
    }

    public void setEid(String eid) {
        this.eid = eid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId_proof() {
        return id_proof;
    }

    public void setId_proof(String id_proof) {
        this.id_proof = id_proof;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getJoining_date() {
        return joining_date;
    }

    public void setJoining_date(String joining_date) {
        this.joining_date = joining_date;
    }

    public String getConfig_json() {
        return config_json;
    }

    public void setConfig_json(String config_json) {
        this.config_json = config_json;
    }

    public String getLast_modified() {
        return last_modified;
    }

    public void setLast_modified(String last_modified) {
        this.last_modified = last_modified;
    }

    public String getAbsentDays() {
        return absentDays;
    }

    public void setAbsentDays(String absentDays) {
        this.absentDays = absentDays;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
