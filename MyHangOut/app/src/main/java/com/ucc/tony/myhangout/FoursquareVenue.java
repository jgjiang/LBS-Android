package com.ucc.tony.myhangout;


/**
 * Created by tony on 13/03/2017.
 */

public class FoursquareVenue {
    private String name;
    private String city;
    private String category;
    private String lat;
    private String lng;

    public FoursquareVenue(){
        this.name = "";
        this.city = "";
        this.setCategory("");
        this.lng = "";
        this.lat = "";
    }


    public String getLat() {
        return lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }


    public String getCity(){
        if (city.length()>0){
            return city;
        }

        return city;
    }

    public void setCity(String city){
        if (city!=null){
            this.city = city.replaceAll("\\(", "").replaceAll("\\)", "");
        }
    }

    public  void  setName(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public String getCategory(){
        return category;
    }

    public void setCategory(String category){
        this.category = category;
    }



}
