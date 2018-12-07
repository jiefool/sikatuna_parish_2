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

    ArrayList<Event> elist =  new ArrayList<Event>();
    ArrayList<Priest> ulist =  new ArrayList<Priest>();
    ArrayList<Group> glist = new ArrayList<>();

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

    public void setElist(ArrayList<Event> elist) {
        this.elist = elist;
    }

    public void setUlist(ArrayList<Priest> ulist) {
        this.ulist = ulist;
    }

    public ArrayList<Group> getGlist() {
        return glist;
    }

    public void setGlist(ArrayList<Group> glist) {
        this.glist = glist;
    }
}
