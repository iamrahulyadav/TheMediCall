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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;
import themedicall.com.Adapter.LabsListRecycleView;
import themedicall.com.BroadCasts.MyReceiverForNetworkDialog;
import themedicall.com.GetterSetter.LabsGetterSetter;
import themedicall.com.Globel.CustomProgressDialog;
import themedicall.com.Globel.Glob;
import themedicall.com.Globel.PaginationScrollListener;
import themedicall.com.VolleyLibraryFiles.AppSingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class AllLabsFragment extends Fragment {

    public static RecyclerView recyclerView_all_labs;
    public static TextView emptyViewAllLabList ;
    public static ProgressBar bar ;
    public static LinearLayoutManager linearLayoutManager ;


    StringRequest strReq;
    String cancel_req_tag = "Hospitals";
    private static final String TAG = "Hospitals";
    String lab_name , lab_address , lab_img , lab_id , lab_contact , lab_offer_discount;
    Double lab_lat , lab_lng;
    CustomProgressDialog dialog;
    SharedPreferences sharedPreferencesCityAndLatLng;
    public static SharedPreferences.Editor cityAndLatLngEditor;
    Double currentLat , currentLang;
    LatLng currentLatLang ;

    LabsListRecycleView labsListRecycleView;


    List<LabsGetterSetter> allLabsList;
    List<LabsGetterSetter> nextAllLabsList;

    public boolean allLabsServiceHasRun = false;


    private int totalLab;
    private static final int PAGE_START = 0;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int TOTAL_PAGES = 40;
    private int currentPage = PAGE_START;
    private String offset = "0";
    private boolean isLoadingFinished = false;
    int indicator = -1;
    MyReceiverForNetworkDialog myReceiver;
    SharedPreferences sharedPreferences ;
    public static String city;
    double roundAboutKm;

    public AllLabsFragment() {
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
        View view = inflater.inflate(R.layout.fragment_all_labs, container, false);

        initiate(view);

        return view;
    }

    public void initiate(View view)
    {
        emptyViewAllLabList = (TextView) view.findViewById(R.id.emptyViewAllLabList);
        bar = (ProgressBar) view.findViewById(R.id.loadmore_all_lab_progress);
        recyclerView_all_labs = (RecyclerView) view.findViewById(R.id.recycler_view_all_labs);
        recyclerView_all_labs.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(getActivity() , LinearLayoutManager.VERTICAL, false);
        recyclerView_all_labs.setLayoutManager(linearLayoutManager);
        allLabsList = new ArrayList<>();
        dialog=new CustomProgressDialog(getActivity(), 1);


        sharedPreferencesCityAndLatLng = getActivity().getSharedPreferences("CityPreferences" , MODE_PRIVATE);
        currentLat = Double.valueOf(sharedPreferencesCityAndLatLng.getString("lat" , "0"));
        currentLang = Double.valueOf(sharedPreferencesCityAndLatLng.getString("lang" , "0"));
        Log.e("tag" , "lat in sp "+currentLat);
        Log.e("tag" , "lang in sp "+currentLang);
        currentLatLang = new LatLng(currentLat , currentLang);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if(isVisibleToUser)
        {
            Log.e("tag" , "fragment visible");
            if(allLabsServiceHasRun)
            {
                Log.e("tag" , "fragment all lab service already run");
            }
            else
            {
                getAllLabs();
            }

            allLabPaginationScrollListener();
        }
        else
        {
            Log.e("tag" , "fragment not visible");
        }
        super.setUserVisibleHint(isVisibleToUser);
    }


    public void getAllLabs()
    {
        // Tag used to cancel the request
        String cancel_req_tag = "All Lab";

        dialog.show();
        strReq = new StringRequest(Request.Method.POST, Glob.All_LAB_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e("tag", "All Lab Response: " + response.toString());
                dialog.dismiss();
                try {


                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    if (!error) {


                        JSONObject labObject = new JSONObject(response);
                        String total = labObject.getString("total");
                        Log.e("TAG", " The total available data is: " + total);
                        totalLab = Integer.parseInt(total);
                        JSONArray labArray = labObject.getJSONArray("labs");

                        nextAllLabsList = new ArrayList<>();

                        for (int i = 0; i < labArray.length(); i++) {

                            JSONObject labInnerObject = labArray.getJSONObject(i);

                            lab_name = labInnerObject.getString("lab_name");
                            lab_address = labInnerObject.getString("lab_address");
                            lab_img = labInnerObject.getString("lab_img");
                            lab_id = labInnerObject.getString("lab_id");
                            lab_contact = labInnerObject.getString("lab_contact");
                            lab_lat = labInnerObject.getDouble("lab_lat");
                            lab_lng = labInnerObject.getDouble("lab_lng");
                            lab_offer_discount = labInnerObject.getString("lab_offer_discount");


                            LatLng allLabLocation = new LatLng(lab_lat, lab_lng);


                            if(currentLang == 0.0)
                            {
                                roundAboutKm = 0.0;
                            }
                            else
                            {
                                Double allLabDistanceInMeter = SphericalUtil.computeDistanceBetween(currentLatLang, allLabLocation);
                                Double allLabDistanceINKM = allLabDistanceInMeter / 1000 ;
                                roundAboutKm = round(allLabDistanceINKM, 1);
                            }




                            Log.e("tag" , "ALL Lab in distance "+roundAboutKm);
                            //Log.e("tag" , "ALL Lab in distance in Km "+allLabDistanceINKM );


                            allLabsList.add(new LabsGetterSetter(lab_id , lab_img , roundAboutKm , lab_name , lab_address , lab_contact , lab_offer_discount));

                            //Log.d("tag " , "hos name "+hospitalName);


                            //HospitalNameList.add(new CitiesGetterSetter(hospitalId , hospitalName));


                        }

                        allLabsServiceHasRun =true ;

                        if(allLabsList.size() == 0)
                        {
                            emptyViewAllLabList.setVisibility(View.VISIBLE);
                        }
                        else
                        {
                            labsListRecycleView = new LabsListRecycleView(getActivity() , allLabsList);
                            recyclerView_all_labs.setAdapter(labsListRecycleView);
                            labsListRecycleView.notifyDataSetChanged();
                        }







                        //Toast.makeText(getApplicationContext() , " You are successfully Added!", Toast.LENGTH_SHORT).show();

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
                Log.e(TAG, "All Lab Error: " + error.getMessage());
                Toast.makeText(getActivity(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url

                Log.e("tag" , "off set in first all lab service : "+offset);


                Map<String, String> params = new HashMap<String, String>();
                //params.put("doctor_user_name", signUpUserNameText);
                params.put("key", Glob.Key);
                params.put("city", Labs.city);
                params.put("offset", offset);

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







    public void nextGetAllLabs(final String offset)
    {

        Log.e("tag" , "tag offset in get next all lab : "+offset);


        // Tag used to cancel the request
        String cancel_req_tag = "All Lab";

        strReq = new StringRequest(Request.Method.POST, Glob.All_LAB_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "All Lab Response: " + response.toString());
                try {


                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    if (!error) {


                        JSONObject labObject = new JSONObject(response);
                        String total = labObject.getString("total");
                        Log.e("TAG", " The total available data is: " + total);
                        totalLab = Integer.parseInt(total);
                        JSONArray labArray = labObject.getJSONArray("labs");

                        if (nextAllLabsList.size()>1){
                            nextAllLabsList.clear();
                        }

                        Log.e("tag" , "nextAllLabsList : "+nextAllLabsList.size());


                        for (int i = 0; i < labArray.length(); i++) {


                            JSONObject labInnerObject = labArray.getJSONObject(i);

                            lab_name = labInnerObject.getString("lab_name");
                            lab_address = labInnerObject.getString("lab_address");
                            lab_img = labInnerObject.getString("lab_img");
                            lab_id = labInnerObject.getString("lab_id");
                            lab_contact = labInnerObject.getString("lab_contact");
                            lab_lat = labInnerObject.getDouble("lab_lat");
                            lab_lng = labInnerObject.getDouble("lab_lng");
                            lab_offer_discount = labInnerObject.getString("lab_offer_discount");


                            LatLng allLabLocation = new LatLng(lab_lat, lab_lng);



                            if(currentLang == 0.0)
                            {
                                roundAboutKm = 0.0;
                            }
                            else
                            {
                                Double allLabDistanceInMeter = SphericalUtil.computeDistanceBetween(currentLatLang, allLabLocation);
                                Double allLabDistanceINKM = allLabDistanceInMeter / 1000 ;
                                roundAboutKm = round(allLabDistanceINKM, 1);
                            }




                            Log.e("tag" , "ALL Lab in distance "+roundAboutKm);
                           // Log.e("tag" , "ALL Lab in distance in Km "+allLabDistanceINKM );


                            allLabsList.add(new LabsGetterSetter(lab_id , lab_img , roundAboutKm , lab_name , lab_address , lab_contact , lab_offer_discount));

                            //Log.d("tag " , "hos name "+hospitalName);


                            //HospitalNameList.add(new CitiesGetterSetter(hospitalId , hospitalName));

                            nextAllLabsList.add(new LabsGetterSetter(lab_id , lab_img , roundAboutKm , lab_name , lab_address , lab_contact , lab_offer_discount));

                        }

                        Log.e("Tag" , "nextAllLabsList : "+nextAllLabsList.size());
                        //labsListRecycleView.notifyDataSetChanged();

                        List<LabsGetterSetter> loadNextData = LabsGetterSetter.creatData(nextAllLabsList.size(), nextAllLabsList);
                        labsListRecycleView.addAll(loadNextData);
                        labsListRecycleView.notifyDataSetChanged();

                        // callBack.onSuccess(nextAllLabsList);

                        if (bar.getVisibility() == View.VISIBLE){
                            bar.setVisibility(View.GONE);
                        }


                        //Toast.makeText(getApplicationContext() , " You are successfully Added!", Toast.LENGTH_SHORT).show();

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
                Log.e(TAG, "All Lab Error: " + error.getMessage());
                Toast.makeText(getActivity(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url

                Log.e("tag" , "off set in next all lab service : "+ offset);


                Map<String, String> params = new HashMap<String, String>();
                //params.put("doctor_user_name", signUpUserNameText);
                params.put("key", Glob.Key);
                params.put("city", Labs.city);
                params.put("offset", offset);

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


    // onPagination scroll listener
    public void allLabPaginationScrollListener(){

        recyclerView_all_labs.addOnScrollListener(new PaginationScrollListener(linearLayoutManager, totalLab) {


            @Override
            protected void loadMoreItems() {


                isLoading = true;
                currentPage += 1;
                int currentOffest = Integer.parseInt(offset);
                currentOffest = currentOffest + 10;
                offset = Integer.toString(currentOffest);

                Log.e("tag" , "tag offset in pagination listener : "+offset);

                loadNextAllLabPage(offset);

                //loadNextPage();
                //getBloodGroupListingService()
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                Log.e("TAG", "I AM CALLING 123: ");


                if (isLoadingFinished){


                }
            }

            @Override
            public int getTotalPageCount() {

                Log.e("TAG", "Total Data count: " +totalLab );
                Log.e("TAG", "Total Data count layout: " +linearLayoutManager.getItemCount() );
                if (indicator == 1){

                    return TOTAL_PAGES;
                }
                else {
                    return 0;
                }

                //return TOTAL_PAGES;
                // return (isLoadingFinished) ? TOTAL_PAGES : 0;
                //return 0;
            }

            @Override
            public boolean isLastPage() {

                return (totalLab-1 == labsListRecycleView.getItemCount()) ? true : false;

            }

            @Override
            public boolean isLoading() {

                return (totalLab-1 == labsListRecycleView.getItemCount()) ? true : false;

            }
        });
    }

    //


    //
    private void loadNextAllLabPage(String offset) {
        Log.e(TAG, "loadNextPage: " + currentPage);

        //  List<FindDoctorGetterSetter> myCurrentArrayList = getDocListNext();


        Log.e("tag" , "tag offset in load next record : " +offset);


        nextGetAllLabs(offset);


/*
        // List<BloodReqiredGetterSetter> myCurrentArrayList = gettingNextPageForData();


      //  Log.e("TAG", "MY Current 123: " + myCurrentArrayList.size());


        List<FindDoctorGetterSetter> loadNextData = FindDoctorGetterSetter.creatData(myCurrentArrayList.size(), myCurrentArrayList);

        Log.e("TAG", "MY Current: " + loadNextData.size());
        Log.e("TAG", "MY Current list list: " + myCurrentArrayList.size());


        loadNextData.subList(loadNextData.size() - myCurrentArrayList.size(), loadNextData.size()).clear();

        //List<BloodReqiredGetterSetter> currntList = loadNextData.subList(loadNextData.size()-20, loadNextData.size()).clear();
        Log.e(TAG, "MY Current Total Item Count abc : " + myCurrentArrayList.size());
*/


     /*   findDoctorListRecycleView.removeLoadingFooter();

            isLoading = false;


        findDoctorListRecycleView.addAll(loadNextData);


            if (currentPage != TOTAL_PAGES) {
                findDoctorListRecycleView.addLoadingFooter();


            } else {
                isLastPage = true;

            }*/

        if (bar.getVisibility()== View.GONE){
            bar.setVisibility(View.VISIBLE);
        }


    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }




}
