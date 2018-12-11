package com.tanginan.www.sikatuna_parish;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class PriestList {
    List<Priest> list;

    public PriestList(List<Priest> list){
        this.list = list;
    }

    public Priest getPriestByName(String name){
        Priest priest = null;
        for(int i=0;i<list.size();i++){
            if(list.get(i).getName().equals(name)){
               priest = list.get(i);
            }
        }
        return priest;
    }

    public ArrayList<String> getPriestNames(){
        ArrayList<String> priestNames = new ArrayList<>();
        for(int i=0;i<list.size();i++){
            priestNames.add(list.get(i).getName());
        }
        return priestNames;
    }

    public String getPriestNameByUserId(Integer userId){
        Priest p = new Priest();
        for(int i=0;i<list.size();i++){
            if (list.get(i).getId() == userId){
                 p = list.get(i);
            }
        }
        return p.getName();
    }
}
