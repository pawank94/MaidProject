package com.example.pawank.themaidproject.DataClass;

/**
 * Created by nexusstar on 8/4/17.
 */

public class ModuleNotification {
    String date;
    String nContent;
    String module;

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public ModuleNotification() {
        date = nContent = null;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getnContent() {
        return nContent;
    }

    public void setnContent(String nContent) {
        this.nContent = nContent;
    }
}
