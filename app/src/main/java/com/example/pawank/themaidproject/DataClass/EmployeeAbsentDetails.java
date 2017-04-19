package com.example.pawank.themaidproject.DataClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by pawan.k on 31-03-2017.
 */

public class EmployeeAbsentDetails {
    String eid;
    String id;
    String date;
    String shift;
    String reason;
    String status;
    String acted_by;
    String reported_by;
    Boolean both_shift;



    public EmployeeAbsentDetails() {
        eid=date=shift=reason=status=acted_by=reported_by=id=null;
    }

    public static ArrayList<EmployeeAbsentDetails> populateAbsentDaysArray(JSONObject jObj) throws JSONException {
        ArrayList<EmployeeAbsentDetails> empAbsentDetails = new ArrayList<>();
        JSONArray workObject = jObj.getJSONArray("DATA");
        for(int i=0;i<workObject.length();i++)
        {
            EmployeeAbsentDetails curObj = new EmployeeAbsentDetails();
            JSONObject curJObj = workObject.getJSONObject(i);
            curObj.setId(curJObj.getString("ID"));
            curObj.setDate(curJObj.getString("DATE"));
            curObj.setShift(curJObj.getString("SHIFT"));
            curObj.setReason(curJObj.getString("REASON"));
            curObj.setStatus(curJObj.getString("STATUS"));
            curObj.setActed_by(curJObj.getString("ACTED_BY"));
            curObj.setReported_by(curJObj.getString("REPORTED_BY"));
            curObj.setBoth_shift(false);
            empAbsentDetails.add(curObj);
        }
        return empAbsentDetails;
    }

    public String getEid() {
        return eid;
    }

    public void setEid(String eid) {
        this.eid = eid;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getShift() {
        return shift;
    }

    public void setShift(String shift) {
        this.shift = shift;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getActed_by() {
        return acted_by;
    }

    public void setActed_by(String acted_by) {
        this.acted_by = acted_by;
    }

    public String getReported_by() {
        return reported_by;
    }

    public void setReported_by(String reported_by) {
        this.reported_by = reported_by;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Boolean getBoth_shift() {
        return both_shift;
    }

    public void setBoth_shift(Boolean both_shift) {
        this.both_shift = both_shift;
    }
}
