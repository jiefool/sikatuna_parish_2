package com.tanginan.www.sikatuna_parish;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Event {
    private int id;
    private int userId;
    private String name;
    private Date timeStart;
    private Date timeEnd;
    private Date alarm;
    private String details;

    public Event(JSONObject event) throws JSONException, ParseException {
        this.setId(event.getInt("id"));
        this.setName(event.getString("name"));
        this.setUserId(event.getInt("user_id"));
        this.setTimeStart(formatDate(event.getString("time_start")));
        this.setTimeEnd(formatDate(event.getString("time_end")));
        this.setAlarm(formatDate(event.getString("alarm")));
        this.setDetails(event.getString("details"));
    }

    public Date formatDate(String date) throws ParseException {
        SimpleDateFormat format = dateFormat();
        return format.parse(date);
    }

    private SimpleDateFormat dateFormat() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(Date timeStart) {
        this.timeStart = timeStart;
    }

    public Date getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(Date timeEnd) {
        this.timeEnd = timeEnd;
    }

    public Date getAlarm() {
        return alarm;
    }

    public void setAlarm(Date alarm) {
        this.alarm = alarm;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}