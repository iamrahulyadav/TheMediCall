package themedicall.com;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import themedicall.com.Adapter.EmergencyContactRecycleView;
import themedicall.com.GetterSetter.EmergencyContactGetterSetter;
import themedicall.com.Globel.CustomProgressDialog;
import themedicall.com.Globel.Glob;

import themedicall.com.VolleyLibraryFiles.AppSingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmergencyContact extends AppCompatActivity {
    RecyclerView recyclerView_emergency_contact;
    List<EmergencyContactGetterSetter> emergencyContactList;
    CustomProgressDialog dialog;
    String cancel_req_tag = "Emergency contact";
    private static final String TAG = "Emergency contact";
    StringRequest strReq;
    String id , name , image , contact ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_contact);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        initiate();
        getEmergencyContacts();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public void initiate()
    {
        recyclerView_emergency_contact = (RecyclerView) findViewById(R.id.recycler_view_emergency_contact);
        recyclerView_emergency_contact.setHasFixedSize(true);
        recyclerView_emergency_contact.setLayoutManager(new LinearLayoutManager(EmergencyContact.this , LinearLayoutManager.VERTICAL , false));
        emergencyContactList = new ArrayList<>();
        dialog=new CustomProgressDialog(EmergencyContact.this , 1);

    }

    public void getEmergencyContacts()
    {
        // Tag used to cancel the request
        String cancel_req_tag = "Emergency Contact";

        dialog.show();
        strReq = new StringRequest(Request.Method.POST, Glob.GET_EMERGENCY_CONTACT_LIST, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e("tag", "Emergency Contact Response: " + response.toString());
                dialog.dismiss();
                try {


                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    if (!error) {


                        JSONObject labObject = new JSONObject(response);
                        JSONArray appealArray = labObject.getJSONArray("contacts");


                        for (int i = 0; i < appealArray.length(); i++) {

                            JSONObject labInnerObject = appealArray.getJSONObject(i);

                            id = labInnerObject.getString("id");
                            name = labInnerObject.getString("name");
                            image = labInnerObject.getString("image");
                            contact = labInnerObject.getString("contact");



                            Log.e("tag" , "Emergency Contact id "+ id);
                            Log.e("tag" , "Emergency Contact contact "+name);
                            Log.e("tag" , "Emergency Contact location "+image);
                            Log.e("tag" , "Emergency Contact location "+contact);

                            emergencyContactList.add(new EmergencyContactGetterSetter(id, contact, image, name));

                        }

                        Log.e("tag" , "Emergency Contact list size " + emergencyContactList.size());


                        EmergencyContactRecycleView emergencyContactRecycleView = new EmergencyContactRecycleView(EmergencyContact.this , emergencyContactList);
                        recyclerView_emergency_contact.setAdapter(emergencyContactRecycleView);
                        emergencyContactRecycleView.notifyDataSetChanged();





                        //Toast.makeText(EmergencyContact.this , " You are successfully Added!", Toast.LENGTH_SHORT).show();

                    } else {

                        String errorMsg = jObj.getString("error_message");
                        Toast.makeText(EmergencyContact.this, errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Emergency Contact Error: " + error.getMessage());
                Toast.makeText(EmergencyContact.this, error.getMessage(), Toast.LENGTH_LONG).show();
                dialog.dismiss();
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
        strReq.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Adding request to request queue
        // Adding request to request queue
        AppSingleton.getInstance(EmergencyContact.this).addToRequestQueue(strReq, cancel_req_tag);
    }



}
