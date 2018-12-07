package com.tanginan.www.sikatuna_parish;

import org.json.JSONException;
import org.json.JSONObject;

public class Group {
    Integer groupId = 0;
    String groupName = "";
    String groupLeader = "";
    String groupContactNumber = "";

    public Group(JSONObject group) throws JSONException {
        setGroupId(group.getInt("id"));
        setGroupName(group.getString("name"));
        setGroupLeader(group.getString("leader"));
        setGroupContactNumber(group.getString("contact_number"));
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupLeader() {
        return groupLeader;
    }

    public void setGroupLeader(String groupLeader) {
        this.groupLeader = groupLeader;
    }

    public String getGroupContactNumber() {
        return groupContactNumber;
    }

    public void setGroupContactNumber(String groupContactNumber) {
        this.groupContactNumber = groupContactNumber;
    }
}
