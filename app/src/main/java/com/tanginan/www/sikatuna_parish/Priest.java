package com.tanginan.www.sikatuna_parish;

import org.json.JSONException;
import org.json.JSONObject;

public class Priest {
    private Integer id;
    private String name;
    private String photo;

    public void setPriest(JSONObject priest) throws JSONException {
        setId(priest.getInt("id"));
        setName(priest.getString("name"));
        setPhoto(priest.getString("photo"));
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
