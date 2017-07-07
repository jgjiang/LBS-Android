package com.ucc.tony.myhangout;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class MainActivity extends AppCompatActivity implements SensorEventListener, Animation.AnimationListener{

    private SensorManager sensorManager ;
    private Sensor sensor ;
    private Animation out_top_Annotation = null;//top half pic return to normal state
    private Animation out_bottom_Annotation = null;//bottom half pic return to normal state
    private Animation in_top_Annotation = null;// top half pic goes to animation
    private Animation in_bottom_Annotation = null;// bottom half pic goes to animation

    private ImageView imageView_top = null;
    private ImageView imageView_bottom = null;
    private Vibrator vibrator = null;

    private TextView locationTextView;
    private TextView addressTextView;
    private Criteria criteria;
    public static final int SHOW_LOCATION = 0;
    private Location bestLocation;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView_top = (ImageView) findViewById(R.id.shakeimageView1);
        imageView_bottom = (ImageView) findViewById(R.id.shakeimageView2);
        locationTextView = (TextView) findViewById(R.id.location_tv);
        addressTextView = (TextView) findViewById(R.id.textView);


        int duration = 450;//animation duration: 0.45s
        sensorManager =  (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this,sensor, SensorManager.SENSOR_DELAY_GAME);
        vibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);

        //obtain the best location (gps or network)
        getBestLocation();

        if (getBestLocation()!= null){
            new Thread(getThread).start();
            Log.d("0000","thread");
        }

        //set animation of the image
        this.out_top_Annotation = new TranslateAnimation(0,0,0,-(float)getWindowManager().getDefaultDisplay().getHeight()/10);
        this.out_top_Annotation.setDuration(duration);

        this.out_bottom_Annotation = new TranslateAnimation(0,0,0,(float)getWindowManager().getDefaultDisplay().getHeight()/10);
        this.out_bottom_Annotation.setDuration(duration);

        this.in_top_Annotation = new TranslateAnimation(0,0,-(float)getWindowManager().getDefaultDisplay().getHeight()/10,0);
        this.in_top_Annotation.setDuration(duration);

        this.in_bottom_Annotation = new TranslateAnimation(0,0,(float)getWindowManager().getDefaultDisplay().getHeight()/10,0);
        this.in_bottom_Annotation.setDuration(duration);

        this.out_bottom_Annotation.setAnimationListener(this);
        this.in_bottom_Annotation.setAnimationListener(this);

    }


    // new thread to deal with HTTP request
    private Thread getThread =  new Thread(new Runnable() {
        @Override
        public void run() {

            HttpURLConnection conn = null;

            try {
                StringBuilder urlBuilder = new StringBuilder();

                urlBuilder.append("https://maps.googleapis.com/maps/api/geocode/json?latlng=");
                urlBuilder.append(getBestLocation().getLatitude()).append(",");
                urlBuilder.append(getBestLocation().getLongitude());
                urlBuilder.append("&key="+"AIzaSyB_6R34w9kogsPD8Zg5EalpKSWUO5prYjM");
                // urlBuilder.append("&sensor=false");
                URL url = new URL(urlBuilder.toString());
                Log.d("URL", urlBuilder.toString());

                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Charset", "UTF-8");
                conn.setReadTimeout(5000);
                conn.setConnectTimeout(15000);
                conn.setDoInput(true);

                // check if the response is correct
                if(conn.getResponseCode() == 200){
                    // read data from JSON

                    InputStream is = conn.getInputStream();

                    String response = FileUtils.convertStreamToString(is);
                    Log.d("rs",response);

                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray resultArray = jsonObject.getJSONArray("results");


                    if (resultArray.length() > 0){
                        JSONObject subObject = resultArray.getJSONObject(0);
                        String address = subObject.getString("formatted_address");
                        Message message = new Message();
                        message.what = SHOW_LOCATION;
                        message.obj = address;
                        getHandler.sendMessage(message);

                    }

                }

            } catch (Exception e){
                e.printStackTrace();

            } finally {
                if (conn != null){
                    conn.disconnect(); // cancel connection
                }
            }

        }
    });

    // handler used to display the address
    private Handler getHandler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            Log.d("====", "run");
            switch (msg.what) {
                case SHOW_LOCATION:
                    String currentPosition = (String) msg.obj;
                    addressTextView.setText(currentPosition);
                    break;
                default:
                    break;
            }

        }
    };



    // define a method to get location
    private Location getBestLocation() {
        criteria = new Criteria();
        criteria.setAltitudeRequired(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        criteria.setAltitudeRequired(true);
        Location bestLocation = LocationUtils.getBestLocation(this, criteria);
        if (bestLocation == null) {
            Toast.makeText(this, "Cannot find location", Toast.LENGTH_LONG).show();

        } else {
            //locationTextView.setText("lat ==" + bestLocation.getLatitude() + "lng==" + bestLocation.getLongitude());
        }
        return bestLocation;
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[SensorManager.DATA_X];
        float y = event.values[SensorManager.DATA_Y];
        float z = event.values[SensorManager.DATA_Z];
        if(Math.abs(x)>=20||Math.abs(y)>=20||Math.abs(z)>=20){//accelerated velocity > 20
            imageView_bottom.startAnimation(out_bottom_Annotation);// start animation
            imageView_top.startAnimation(out_top_Annotation);
            vibrator.vibrate(1000);
            sensorManager.unregisterListener(this);// cancel the location listener
        }

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        // TODO Auto-generated method stub
        if(animation == this.out_bottom_Annotation){
            imageView_bottom.startAnimation(in_bottom_Annotation);
            imageView_top.startAnimation(in_top_Annotation);
        }else if(animation.equals(this.in_bottom_Annotation)){
            // re-listen the sensor movement after animation done
            sensorManager.registerListener(this,sensor, SensorManager.SENSOR_DELAY_GAME);
        }

        Intent intent = new Intent();
        intent.putExtra("lat",getBestLocation().getLatitude());
        intent.putExtra("long",getBestLocation().getLongitude());
        intent.setClass(MainActivity.this, PlaceDisplayActivity.class);
        startActivity(intent);

    }

    @Override
    public void onAnimationRepeat(Animation animation) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onAnimationStart(Animation animation) {
        // TODO Auto-generated method stub

    }

}
