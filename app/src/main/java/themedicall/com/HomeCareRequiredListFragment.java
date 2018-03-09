package themedicall.com;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;
import themedicall.com.Adapter.HomeCareRequiredAdapter;
import themedicall.com.GetterSetter.HomeCareRequiredGetterSetter;
import themedicall.com.Globel.CustomProgressDialog;
import themedicall.com.Globel.Glob;

import themedicall.com.VolleyLibraryFiles.AppSingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class HomeCareRequiredListFragment extends Fragment {

    RecyclerView homeCareRequiredListRecyclerView;
    LinearLayoutManager linearLayoutManager ;
    ArrayList<HomeCareRequiredGetterSetter> homeCareRequiredList ;
    CustomProgressDialog dialog;
    StringRequest strReq;
    String cancel_req_tag = "appeal blood";
    private static final String TAG = "appeal blood";
    String home_care_id ,home_care_contact , home_care_google_address , home_care_home_address , home_care_care_required , created_at , home_care_status , area_id , area_title;
    Double currentLat , currentLang , home_care_lat , home_care_lng;
    LatLng currentLatLang ;
    SharedPreferences sharedPreferencesCityAndLatLng;
    String nurseImageUrl = "https://themedicall.com/public/android/female-nurse.png";
    String doctorImageUrl = "https://themedicall.com/public/android/male-doctor.png";
    String url = null;
    String description , status;
    boolean homeCareRequiredServiceHasRun = false;
    public HomeCareRequiredListFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home_care_required_list, container, false);
        initiate(view);
        return view;
    }


    public void initiate(View view)
    {

        dialog=new CustomProgressDialog(getActivity() , 1);
        homeCareRequiredListRecyclerView = (RecyclerView) view.findViewById(R.id.homeCareRequiredListRecyclerView);
        homeCareRequiredListRecyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(getActivity() , LinearLayoutManager.VERTICAL, false);
        homeCareRequiredListRecyclerView.setLayoutManager(linearLayoutManager);
        homeCareRequiredList = new ArrayList<>();

        sharedPreferencesCityAndLatLng = getActivity().getSharedPreferences("CityPreferences" , MODE_PRIVATE);
        currentLat = Double.valueOf(sharedPreferencesCityAndLatLng.getString("lat" , "0"));
        currentLang = Double.valueOf(sharedPreferencesCityAndLatLng.getString("lang" , "0"));
        Log.e("tag" , "lat in sp "+currentLat);
        Log.e("tag" , "lang in sp "+currentLang);
        currentLatLang = new LatLng(currentLat , currentLang);
        Log.e("tag"  , "currentLatLang in blood appeal : " +currentLang);




    }



    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {

        if(isVisibleToUser)
        {
            Log.e("tag" , "fragment visible");
            if(homeCareRequiredServiceHasRun)
            {
                Log.e("tag" , "home care required service already run");
            }
            else
            {
                getHomeCareRequired();
            }

        }
        else
        {
            Log.e("tag" , "fragment not visible");
        }

        super.setUserVisibleHint(isVisibleToUser);
    }

    public void getHomeCareRequired()
    {
        // Tag used to cancel the request
        String cancel_req_tag = "Home Care Required";

        dialog.show();
        strReq = new StringRequest(Request.Method.POST, Glob.GET_HOME_CARE_REQUEST , new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e("tag", "Home Care Required Response: " + response.toString());
                dialog.dismiss();
                try {


                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    if (!error) {


                        JSONObject labObject = new JSONObject(response);
                        JSONArray appealArray = labObject.getJSONArray("appeals");


                        for (int i = 0; i < appealArray.length(); i++) {

                            JSONObject labInnerObject = appealArray.getJSONObject(i);


                            home_care_id = labInnerObject.getString("home_care_id");
                            home_care_contact = labInnerObject.getString("home_care_contact");
                            home_care_google_address = labInnerObject.getString("home_care_google_address");
                            home_care_home_address = labInnerObject.getString("home_care_home_address");
                            home_care_care_required = labInnerObject.getString("home_care_care_required");
                            home_care_lat = labInnerObject.getDouble("home_care_lat");
                            home_care_lng = labInnerObject.getDouble("home_care_lng");
                            created_at = labInnerObject.getString("created_at");
                            home_care_status = labInnerObject.getString("home_care_status");
                            area_id = labInnerObject.getString("area_id");

                            JSONObject userNameObject = labInnerObject.getJSONObject("user");
                            String user_full_name = userNameObject.getString("user_full_name");


                            if(area_id.equals("0"))
                            {
                                Log.e("tag" , "area not found");
                            }
                            else {

                                JSONObject areaObject = labInnerObject.getJSONObject("area");
                                area_title = areaObject.getString("area_title");
                            }

                            if(home_care_status.equals("0"))
                            {
                                status = "Not Arranged...";
                            }
                            else
                            {
                                status = "Arranged...";
                            }

                            Log.e("tag" , "home_care id "+home_care_id);
                            Log.e("tag" , "home_care contact "+home_care_contact);
                            Log.e("tag" , "home_care google address "+home_care_google_address);
                            Log.e("tag" , "home_care home address "+home_care_home_address);
                            Log.e("tag" , "home_care required  "+home_care_care_required);
                            Log.e("tag" , "home_care lat "+home_care_lat);
                            Log.e("tag" , "home_care lng "+home_care_lng);
                            Log.e("tag" , "home_care created at "+created_at);
                            Log.e("tag" , "home_care status "+home_care_status);

                            LatLng allLabLocation = new LatLng(home_care_lat , home_care_lng);
                            Double allLabDistanceInMeter = SphericalUtil.computeDistanceBetween(currentLatLang, allLabLocation);
                            Double allLabDistanceINKM = allLabDistanceInMeter / 1000 ;


                            if(home_care_care_required.equals("Doctor"))
                            {
                                url = doctorImageUrl;
                            }
                            else
                            {
                                url = nurseImageUrl;
                            }

                            description  =  home_care_care_required + " required at "+ area_title + " .";


                            double roundAboutKm = round(allLabDistanceINKM, 1);

                            Log.e("tag" , "Blood Appeal in distance "+roundAboutKm);
                            Log.e("tag" , "Blood Appeal in distance in Km "+allLabDistanceINKM );


                            homeCareRequiredList.add(new HomeCareRequiredGetterSetter(url  , status , created_at , user_full_name , description , home_care_id  , roundAboutKm));

                        }


                        Log.e("tag" , "home care list size : " + homeCareRequiredList.size());


                        HomeCareRequiredAdapter homeCareRequiredAdapter = new HomeCareRequiredAdapter(getActivity() , homeCareRequiredList);
                        homeCareRequiredListRecyclerView.setAdapter(homeCareRequiredAdapter);
                        homeCareRequiredAdapter.notifyDataSetChanged();





                        //Toast.makeText(getActivity() , " You are successfully Added!", Toast.LENGTH_SHORT).show();

                        homeCareRequiredServiceHasRun = true;

                    } else {

                        String errorMsg = jObj.getString("error_message");
                        Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Blood Appeal Error: " + error.getMessage());
                Toast.makeText(getActivity(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
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
        AppSingleton.getInstance(getActivity()).addToRequestQueue(strReq, cancel_req_tag);
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }





}
