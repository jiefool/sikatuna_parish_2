package com.tanginan.www.sikatuna_parish;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class EventViewModel extends ViewModel {

    ApiUtils apiUtils;
    ArrayList<Event> elist =  new ArrayList<Event>();
    ArrayList<Priest> ulist =  new ArrayList<Priest>();

    public void loadData(Context context){
        apiUtils = new ApiUtils(context);
        loadEvents();
        loadPriests();
    }

    public LiveData<List<Event>> eventList;

    private final MutableLiveData<Event> selectedEvent = new MutableLiveData<Event>();

    public MutableLiveData<Event> getSelectedArticle() {
        return selectedEvent;
    }

    public void setSelectedArticle(Event event) {
        selectedEvent.setValue(event);
    }

    public List<Event> getEventData(){
        return elist;
    }

    public List<Priest> getPriestData(){
        return ulist;
    }

    public void loadEvents() {
        JsonHttpResponseHandler jhtrh = new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                System.out.println("EVENTS:"+response);
                try {
                    JSONArray events = response.getJSONArray("events");
                    elist = new ArrayList<Event>();
                    for(int i=0;i<events.length();i++){
                        JSONObject event = events.getJSONObject(i);
                        Event nEvent = new Event(event);
                        System.out.println("Event:"+nEvent.getStatus());
                        elist.add(nEvent);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

            }
        };
        apiUtils.getEvents(jhtrh);
    }

    public void loadPriests(){
        JsonHttpResponseHandler jhtrh = new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                try {
                    ulist = new ArrayList<Priest>();
                    for(int i=0;i<response.length();i++){
                        JSONObject priest = response.getJSONObject(i);
                        Priest nPriest = new Priest(priest);
                        System.out.println("Priest:"+nPriest.getName());
                        ulist.add(nPriest);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        };
        apiUtils.getPriestUsers(jhtrh);
    }
}
