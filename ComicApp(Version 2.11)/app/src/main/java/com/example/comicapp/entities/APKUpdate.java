package com.example.comicapp.entities;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

public class APKUpdate extends BmobObject {
    private String updateMessage;
    private String version;
    private BmobFile APKFile;
    private boolean isForcedUpdate;

    public String getUpdateMessage() {
        return updateMessage;
    }

    public void setUpdateMessage(String updateMessage) {
        this.updateMessage = updateMessage;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public BmobFile getAPKFile() {
        return APKFile;
    }

    public void setAPKFile(BmobFile APKFile) {
        this.APKFile = APKFile;
    }

    public boolean isForcedUpdate() {
        return isForcedUpdate;
    }

    public void setForcedUpdate(boolean forcedUpdate) {
        isForcedUpdate = forcedUpdate;
    }

    public String getAPKUrl() {
        return APKFile.getFileUrl();
    }
}
