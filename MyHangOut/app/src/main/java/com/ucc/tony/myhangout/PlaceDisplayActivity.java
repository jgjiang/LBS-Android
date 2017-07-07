package com.ucc.tony.myhangout;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class PlaceDisplayActivity extends ListActivity {

    private ArrayList<FoursquareVenue> venuesList;

    final String CLIENT_ID = "MVNEYYI1H00M4WW5UPH1XNY3IBGVW3C1UIOITZ5KIZDBPBSJ";
    final String CLIENT_SECRET = "JBKKVEGJFDBCIJRS5YYRONRH3BUQKOIH1UV00YT5CEFTLMSV";

    private Double latitude = 0.0;
    private Double longtitude = 0.0;

    private ArrayAdapter myAdapter;

    private ArrayList venueLat;
    private ArrayList venueLng;
    private ArrayList listTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // get the longitude and latitude from the last activity
        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();
        latitude = bundle.getDouble("lat");
        longtitude = bundle.getDouble("long");

        new FoursquareResult().execute();


    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
       // super.onListItemClick(l, v, position, id);

        double venueLatD = Double.valueOf(venueLat.get(position).toString());
        double venueLngD = Double.valueOf(venueLng.get(position).toString());
        String venueTitle = listTitle.get(position).toString();

        //double dou1 = Double.valueOf(strList.get(0).trim());

        // use Intent to start activity and send data to next activity
        Intent mapIntent = new Intent();
        mapIntent.putExtra("venueLat", venueLatD);
        mapIntent.putExtra("venueLng", venueLngD);
        mapIntent.putExtra("title",venueTitle);
        mapIntent.setClass(PlaceDisplayActivity.this, MapsActivity.class);

        // start MapsActivity
        startActivity(mapIntent);

    }

    public class FoursquareResult extends AsyncTask <View, Void, String>{

        private String temp;


        // This step is used to perform background computation that can take a long time.
        @Override
        protected String doInBackground(View... urls) {
            temp = makeCall("https://api.foursquare.com/v2/venues/search?client_id=" + CLIENT_ID +
                    "&client_secret=" + CLIENT_SECRET + "&v=20170115&ll="+latitude.toString() +","+longtitude.toString());

            Log.d("Foursquare Access URL","https://api.foursquare.com/v2/venues/search?client_id=" + CLIENT_ID +
            "&client_secret=" + CLIENT_SECRET + "&v=20170115&ll="+latitude.toString() +","+longtitude.toString());
            return "";

        }


        // setup UI element before the task is executed
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        // invoked on the UI thread after the background computation finishes
        @Override
        protected void onPostExecute(String result) {

            if (temp == null){
                Log.d("Response Error","cannot obtain response");

            } else {
                venuesList = (ArrayList<FoursquareVenue>) parseFoursquareResult(temp);

                listTitle = new ArrayList();
                venueLat = new ArrayList();
                venueLng = new ArrayList();

                for (int i=0;i<venuesList.size();i++){

                    listTitle.add(i, venuesList.get(i).getName() + ", " + venuesList.get(i).getCategory() + "," + venuesList.get(i).getCity());
                    venueLat.add(i,venuesList.get(i).getLat());
                    venueLng.add(i,venuesList.get(i).getLng());

                    Log.d("---",listTitle.get(i).toString());
                }

                myAdapter = new ArrayAdapter(PlaceDisplayActivity.this, R.layout.row_layout, R.id.listText, listTitle);
                setListAdapter(myAdapter);
            }

        }

        // Http request method
        public String makeCall(String url){
            //StringBuffer bufferStr = new StringBuffer(url);
            //String replyString = "";
            HttpURLConnection conn = null;
            String response = "";

            try {
                URL myURL = new URL(url);
                Log.d("url", myURL.toString());

                conn = (HttpURLConnection) myURL.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Charset", "UTF-8");
                conn.setReadTimeout(5000);
                conn.setConnectTimeout(15000);
                conn.setDoInput(true);

                // check if the response is correct (code is 200)
                if(conn.getResponseCode() == 200){
                    InputStream is = conn.getInputStream();
                    response = FileUtils.convertStreamToString(is);
                    //Log.d("===",response);

                }
            } catch (Exception e){

                e.printStackTrace();
            } finally {
                if (conn != null){
                    conn.disconnect();
                }
            }

            return response;
        }

    }

    // read JSON file and extract venue information
    private static ArrayList parseFoursquareResult(final String response){
        ArrayList<FoursquareVenue> resultList = new ArrayList<FoursquareVenue>();
        FoursquareVenue fsv;
        try {
            JSONObject jsonObject = new JSONObject(response);

            if (jsonObject.has("response")){
                if (jsonObject.getJSONObject("response").has("venues")){
                    JSONArray resultArray = jsonObject.getJSONObject("response").getJSONArray("venues");
                    for (int i= 0;i<resultArray.length();i++){
                        fsv = new FoursquareVenue();

                        if (resultArray.getJSONObject(i).has("name")){
                            fsv.setName(resultArray.getJSONObject(i).getString("name"));
                            if (resultArray.getJSONObject(i).has("location")) {
                                if (resultArray.getJSONObject(i).getJSONObject("location").has("address")) {

                                    if (resultArray.getJSONObject(i).getJSONObject("location").has("city")) {
                                        fsv.setCity(resultArray.getJSONObject(i).getJSONObject("location").getString("city"));
                                    }



                                    if (resultArray.getJSONObject(i).getJSONObject("location").has("lat")) {
                                        // get the venue latitude
                                        fsv.setLat(resultArray.getJSONObject(i).getJSONObject("location").getString("lat"));
                                        Log.d("VenueLat",resultArray.getJSONObject(i).getJSONObject("location").getString("lat"));
                                    }

                                    if (resultArray.getJSONObject(i).getJSONObject("location").has("lng")) {
                                        // get the venue longitude and set the value of the venue instance
                                        fsv.setLng(resultArray.getJSONObject(i).getJSONObject("location").getString("lng"));
                                        Log.d("VenueLng",resultArray.getJSONObject(i).getJSONObject("location").getString("lng"));
                                    }



                                    if (resultArray.getJSONObject(i).has("categories")) {
                                        if (resultArray.getJSONObject(i).getJSONArray("categories").length() > 0) {
                                            if (resultArray.getJSONObject(i).getJSONArray("categories").getJSONObject(0).has("icon")) {
                                                fsv.setCategory(resultArray.getJSONObject(i).getJSONArray("categories").getJSONObject(0).getString("name"));
                                            }
                                        }
                                    }
                                    resultList.add(fsv);
                                }
                            }
                        }

                    }
                }
            }


        } catch (Exception e){
            e.printStackTrace();
        }

        return resultList;

    }




}
