package themedicall.com;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import themedicall.com.Adapter.CitiesListCustomAdapter;
import themedicall.com.GetterSetter.CitiesGetterSetter;
import themedicall.com.Globel.DatabaseHelper;
import themedicall.com.Globel.Glob;
import themedicall.com.Services.GetAllCitiesListService;
import themedicall.com.VolleyLibraryFiles.AppSingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Splash extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 3500;
    ImageView splash_Image ;



    //service data
    float value = 0;
    String cityName ;
    String cityId;
    CitiesListCustomAdapter citiesListCustomAdapter ;
    StringRequest strReq ;
    JSONObject jsonObject;
    JSONArray jsonArray;
    String cancel_req_tag = "City Service";
    private static final String TAG = "City Service";
    public static List<CitiesGetterSetter> CityList;

    //


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        splash_Image = (ImageView) findViewById(R.id.splash_Image);

        CityList = new ArrayList<>();

        Glide.with(getApplicationContext())
                .load(R.drawable.splashscreen)
                .asGif()
                .into(splash_Image);

        Intent citiesService = new Intent(Splash.this , GetAllCitiesListService.class);
        DatabaseHelper databaseHelper = new DatabaseHelper(getApplicationContext());
        int dbCount = databaseHelper.getCount();

        if (dbCount>1){
            Log.e("TAG", "Already data in database");
            handlerCall();
        }
        else {

            getCitiesService();
            //startService(citiesService);
        }

        getSupportActionBar().hide();



        //handlerCall();


    }

    private void handlerCall(){

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                //Intent i = new Intent(Splash.this, MapActivityForSelectingHospital.class);
               // Intent i = new Intent(Splash.this, Home.class);
                Intent i = new Intent(Splash.this, AppIntoTutorial.class);
                startActivity(i);
                finish();

            }
        },SPLASH_TIME_OUT);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_splash, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void getCitiesService()
    {


        strReq = new StringRequest(Request.Method.GET, Glob.URL+"get-cities/"+Glob.Key, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e(TAG, "City Service Response: " + response.toString());


                try {

                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    if (!error) {


                        jsonObject = new JSONObject(response);
                        jsonArray = jsonObject.getJSONArray("cities");


                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject finalobject = jsonArray.getJSONObject(i);

                            cityId = finalobject.getString("city_id");
                            cityName = finalobject.getString("city_title");
                            CityList.add(new CitiesGetterSetter(cityId , cityName));

                        }


                        SaveToDatabase saveToDatabase = new SaveToDatabase();
                        saveToDatabase.execute();

                      /*  Log.e("TAg", "the cities size " + CityList.size());
                        DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
                        if (dbHelper.getCount()<1) {
                            if (CityList.size() > 1) {
                                for (CitiesGetterSetter CGS : CityList) {
                                    long isInderted = dbHelper.insertCitiesInTable(CGS);
                                    if (isInderted > -1) {
                                        Log.e("TAG", "City inserted to table");

                                        CitiesGetterSetter sss = CityList.get(CityList.size() - 1);
                                        if (sss.equals(CGS)){
                                            handlerCall();
                                        }
                                    }
                                }
                            }
                        }*/
                        Log.e("tag" , "cities added");


                        stopService(new Intent(getApplicationContext() , GetAllCitiesListService.class));

                        //Toast.makeText(getApplicationContext() , "Cities Added Successfully!", Toast.LENGTH_SHORT).show();

                    } else {

                        String errorMsg = jObj.getString("error_message");
                        Toast.makeText(getApplicationContext(), "else part of service "+errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "City Service Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), "onErrorResponse "+error.getMessage(), Toast.LENGTH_LONG).show();

                getCitiesService();

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url



                Map<String, String> params = new HashMap<String, String>();
                //params.put("doctor_user_name", signUpUserNameText);
                params.put("key", Glob.Key);
                return params;
            }
        };
        // Adding request to request queue
        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(strReq, cancel_req_tag);
    }


    private class SaveToDatabase extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {

          /*  sLayout.setVisibility(View.GONE);
            pb.setVisibility(View.VISIBLE);*/

        }

        @Override
        protected String doInBackground(String... params) {


            DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
            if (dbHelper.getCount()<1) {
                if (CityList.size() > 1) {
                    for (CitiesGetterSetter CGS : CityList) {
                        long isInderted = dbHelper.insertCitiesInTable(CGS);
                        if (isInderted > -1) {
                            Log.e("TAG", "City inserted to table");

                            CitiesGetterSetter sss = CityList.get(CityList.size() - 1);
                            if (sss.equals(CGS)){
                                //Intent i = new Intent(Splash.this, Home.class);
                                Intent i = new Intent(Splash.this, AppIntoTutorial.class);

                                startActivity(i);
                                finish();

                                //handlerCall();
                            }
                        }
                    }
                }
            }



            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);

            Log.e("TAG", "the progress values is: " + values[0]);
        }


        @Override
        protected void onPostExecute(String result) {


        }
    }

    @Override
    public void onBackPressed() {

        DatabaseHelper databaseHelper = new DatabaseHelper(getApplicationContext());
        int dbCount = databaseHelper.getCount();
        if (dbCount>1){

            super.onBackPressed();
        }
        else {

            Toast.makeText(this, "Please wait while data is loading...", Toast.LENGTH_SHORT).show();

        }

    }
}
