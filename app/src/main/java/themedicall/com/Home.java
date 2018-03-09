package themedicall.com;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.model.LatLng;

import com.squareup.picasso.Picasso;
import themedicall.com.Adapter.CitiesListCustomAdapter;
import themedicall.com.Adapter.CustomCityNewAdapter;
import themedicall.com.Adapter.DoctorsFilterAdapter;
import themedicall.com.Adapter.HospitalFilterAdapter;
import themedicall.com.BroadCasts.CheckConnectivity;
import themedicall.com.BroadCasts.MyReceiverForNetworkDialog;
import themedicall.com.GetterSetter.CitiesGetterSetter;
import themedicall.com.GetterSetter.DoctorSearchFilterGetterSetter;
import themedicall.com.GetterSetter.HospitalSearchFilterGetterSetter;
import themedicall.com.Globel.CircleTransformPicasso;
import themedicall.com.Globel.CustomProgressDialog;
import themedicall.com.Globel.DatabaseHelper;
import themedicall.com.Globel.Glob;
import themedicall.com.Services.GetAllCitiesListService;
import themedicall.com.Services.GettingAllHospitalListService;
import themedicall.com.VolleyLibraryFiles.AppSingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CALL_PHONE;


public class Home extends NavigationDrawer implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, SearchView.OnQueryTextListener {

    ImageView askQuestion, healthEventBtn, discountedOffers, findDoctor, freeConsultation, medicineReminder, labs, pharmacies, homeCare, blood, jobs,
            hospitals, blog, ambulance, medicalRecordBtn, facebookBtn, youtubeBtn , bottomnavigationmedistudy , bottomnavigationmedipedia;
    EditText mainSearchView ;
    public static final int REQUEST_PERMISSION_CODE = 30;
    ProgressDialog progressDialog;
    private static final String TAG = "Home";
    String currentLocation = "testing";
    String route = null;
    String street_number = null;
    String postal_code = null;
    String country_name = null;
    String cancel_req_tag = "Home";
    Boolean locationApiHasRun = false;

    public static Double lat, lang;
    public static LatLng userCurrentLocation;

    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    LocationRequest mLocationRequest;
    public static String city, state, country, postalCode, knownName;
    SharedPreferences sharedPreferencesCityAndLatLng;
    public static SharedPreferences.Editor cityAndLatLngEditor;
    JSONObject jsonObject;
    JSONArray jsonArray;
    Button locationFilter;
    ImageView userIcon;
    ImageView searchViewImg;
    ImageView doctorFilterImage;
    RelativeLayout.LayoutParams params;

    //public static ArrayList<CitiesGetterSetter> CityList = new ArrayList<CitiesGetterSetter>();
    String cityName;
    String cityId;
    CitiesListCustomAdapter citiesListCustomAdapter;
    ListView cityListView;
    SearchView search_view;
    CustomCityNewAdapter customCityNewAdapter;
    public boolean hasGetCitiesRun, hasAddressAndGetCitiesRun = false;
    StringRequest strReq;
    private BroadcastReceiver mNetworkReceiver;
    CustomProgressDialog dialog;
    ArrayList<CitiesGetterSetter> tempCityList;
    private static String uniqueID = null;
    private static final String PREF_UNIQUE_ID = "PREF_UNIQUE_ID";
    MyReceiverForNetworkDialog myReceiver;
    int radioSelectedId ;
    RadioButton radioButton;
    String radioButtonText ;
    private Timer timer;
    JSONObject object;
    JSONArray hospitalArray;
    String  hospital_id , hospital_name  , hospital_address;
    String  doctor_id , doctor_full_name , speciality_designation;
    ArrayList<HospitalSearchFilterGetterSetter> HospitalNameFilter ;
    ArrayList<DoctorSearchFilterGetterSetter> doctorNameFilter ;
    ArrayList specialityDesignationList;

    AutoCompleteTextView autoCompleteSearch;

    private int timerHandler = 2;
    Handler mHandler;
    private String textToSearch;
    private String filter;
    ProgressBar advanceSearchBar;
    SharedPreferences sharedPreferencesCity ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_actionbar_navigation_drawer);
        customActionBarView = getSupportActionBar().getCustomView();
        getSupportActionBar().setElevation(0);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.landingScreenBackgroundColor)));


        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.activity_home, null, false);
        drawer.addView(view, 0);

        initiate();
        googleApiClientCode();
        SelectCity();
        clickListener();
        //callingHospitalListservice();
        setImageInActionbar();
        openSearchViewDialog();

    }

    public void googleApiClientCode() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        } else
            Toast.makeText(this, "Not Connected!", Toast.LENGTH_SHORT).show();

    }




    public void initiate() {


        tempCityList = new ArrayList<>();
        HospitalNameFilter = new ArrayList<HospitalSearchFilterGetterSetter>();
        doctorNameFilter = new ArrayList<DoctorSearchFilterGetterSetter>();
        dialog=new CustomProgressDialog(Home.this, 1);
        mainSearchView = (EditText) findViewById(R.id.mainSearchView);
        askQuestion = (ImageView) findViewById(R.id.askQuestionBtn);
        healthEventBtn = (ImageView) findViewById(R.id.healthEventBtn);
        discountedOffers = (ImageView) findViewById(R.id.discountBtn);
        findDoctor = (ImageView) findViewById(R.id.doctorBtn);
        freeConsultation = (ImageView) findViewById(R.id.videoAudioCallBtn);
        medicineReminder = (ImageView) findViewById(R.id.medicineReminderBtn);
        labs = (ImageView) findViewById(R.id.labsBtn);
        pharmacies = (ImageView) findViewById(R.id.pharmacybtn);
        homeCare = (ImageView) findViewById(R.id.homecarebtn);
        blood = (ImageView) findViewById(R.id.bloodBtn);
        jobs = (ImageView) findViewById(R.id.jobBtn);
        hospitals = (ImageView) findViewById(R.id.hospitalbtn);
        blog = (ImageView) findViewById(R.id.blogBtn);
        ambulance = (ImageView) findViewById(R.id.ambluanceBtn);
        medicalRecordBtn = (ImageView) findViewById(R.id.medicalRecordBtn);
        facebookBtn = (ImageView) findViewById(R.id.facebookBtn);
        youtubeBtn = (ImageView) findViewById(R.id.youtubeBtn);
        bottomnavigationmedistudy = (ImageView) findViewById(R.id.bottomnavigationmedistudy);
        bottomnavigationmedipedia = (ImageView) findViewById(R.id.bottomnavigationmedipedia);
        sharedPreferencesCityAndLatLng = getSharedPreferences("CityPreferences" , MODE_PRIVATE);
        cityAndLatLngEditor = sharedPreferencesCityAndLatLng.edit();
        locationFilter = (Button) customActionBarView.findViewById(R.id.locationFilter);
        userIcon = (ImageView) customActionBarView.findViewById(R.id.userIcon);
        searchViewImg = (ImageView) customActionBarView.findViewById(R.id.searchViewImg);
        doctorFilterImage = (ImageView) customActionBarView.findViewById(R.id.doctorFilterImage);

        params = (RelativeLayout.LayoutParams)locationFilter.getLayoutParams();
        params = (RelativeLayout.LayoutParams)userIcon.getLayoutParams();
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        searchViewImg.setVisibility(View.GONE);
        doctorFilterImage.setVisibility(View.GONE);



        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);



    }



    public void clickListener() {
        askQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(Home.this, AskAQuestion.class);
//                startActivity(intent);
                openFacebookAppGroup();
            }
        });

        healthEventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(Home.this, HealthEvents.class);
//                startActivity(intent);
                openFacebookAppGroupEvents();
            }
        });
        findDoctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, SelectDiseaseForFindDoc.class);
                startActivity(intent);
            }
        });

        freeConsultation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                freeConsultaionDialog();
            }
        });

        blood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, Blood.class);
                startActivity(intent);
            }
        });
        jobs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(Home.this, Jobs.class);
//                startActivity(intent);
                //Toast.makeText(Home.this, "Coming Soon", Toast.LENGTH_SHORT).show();
                comingSoonDialog();
            }
        });

        blog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, Blog.class);
                startActivity(intent);
//                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://themedicall.com/blog"));
//                startActivity(intent);
            }
        });

        hospitals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, Hospitals.class);
                startActivity(intent);
            }
        });

        homeCare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Home.this, HomeCare.class);
                startActivity(intent);
                //Toast.makeText(Home.this, "Coming soon", Toast.LENGTH_SHORT).show();
 //               comingSoonDialog();

            }
        });

        discountedOffers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, DiscountedOffers.class);
                startActivity(intent);
                //Toast.makeText(Home.this, "Coming soon", Toast.LENGTH_SHORT).show();
    //            comingSoonDialog();

            }
        });

        labs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, Labs.class);
                startActivity(intent);
            }
        });
        pharmacies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this , Pharmacy.class);
                startActivity(intent);
            }
        });

        ambulance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this , Emergency.class);
                startActivity(intent);
            }
        });
        medicineReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(Home.this , MedicineReminder.class);
//                startActivity(intent);
               // Toast.makeText(Home.this, "Coming Soon", Toast.LENGTH_SHORT).show();
                comingSoonDialog();

            }
        });

        medicalRecordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(Home.this  , MapsActivity.class);
//                startActivity(intent);

            }
        });

        bottomnavigationmedipedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(Home.this, "Coming Soon", Toast.LENGTH_SHORT).show();
                comingSoonDialog();

//                Intent intent = new Intent(Home.this  , MediPedia.class);
//                startActivity(intent);


            }
        });


        bottomnavigationmedistudy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // Toast.makeText(Home.this, "Coming Soon", Toast.LENGTH_SHORT).show();
               comingSoonDialog();

//                Intent intent = new Intent(Home.this  , MediStudy.class);
//                startActivity(intent);

            }
        });


    }


    public void openSearchViewDialog()
    {
        mainSearchView.setFocusable(false);
        mainSearchView.setClickable(true);
        mainSearchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainSearchDialog();
            }
        });
    }

    public void mainSearchDialog()
    {
        final Dialog dialog = new Dialog(Home.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_main_search_dialog);
        dialog.show();

        final RadioGroup mainSearchRadioGroup = dialog.findViewById(R.id.mainSearchRadioGroup);
        advanceSearchBar = dialog.findViewById(R.id.advanceSearchBar);
        autoCompleteSearch = dialog.findViewById(R.id.autoCompleteSearch);

        radioSelectedId = mainSearchRadioGroup.getCheckedRadioButtonId();
        radioButton = (RadioButton) dialog.findViewById(radioSelectedId);
        radioButtonText = radioButton.getText().toString();
        Log.e("tag" , "radio button in text in main search : "+radioButtonText);

        mainSearchRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                radioSelectedId = mainSearchRadioGroup.getCheckedRadioButtonId();
                radioButton = (RadioButton) dialog.findViewById(radioSelectedId);
                radioButtonText = radioButton.getText().toString();
                Log.e("tag" , "radio button in text in main search : "+radioButtonText);
            }
        });






        autoCompleteSearch.addTextChangedListener(new TextWatcher() {


            @Override
            public void afterTextChanged(final Editable editable) {


                        if(radioButtonText.equals("Doctors"))
                        {
                            if(editable.toString().length() >= 3)
                            {
                                Log.e("tag" , "auto complete text : "+ editable.toString());
                                Log.e("tag" , "doctor radio button  selected: ");
                                doctorNameFilter.clear();
                                useHandler(editable.toString() , "Doctors");

                            }
                            else
                            {
                                Log.e("tag" , "web service not call "+editable.toString());
                            }
                        }
                        else if(radioButtonText.equals("Hospitals"))
                        {
                            if(editable.toString().length() >= 3)
                            {
                                Log.e("tag" , "auto complete text : "+ editable.toString());
                                Log.e("tag" , "radio button in text in main search : "+radioButtonText);

                                HospitalNameFilter.clear();
                                useHandler(editable.toString() , "Hospitals");

                            }
                            else
                            {
                                Log.e("tag" , "web service not call "+editable.toString());
                            }

                        }

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {


            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //  Toast.makeText(Hospitals.this, "text in beforeTextChanged "+charSequence.toString(), Toast.LENGTH_SHORT).show();

            }


        });
    }



    public void getHospitalsNameFilter(final String filterName)
    {

        // Tag used to cancel the request
        String cancel_req_tag = "Hospital Name Filter";

        advanceSearchBar.setVisibility(View.VISIBLE);

        StringRequest strReq = new StringRequest(Request.Method.POST, Glob.GET_HOS_NAME_FILTER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e(TAG, "Hospital Filter Response: " + response.toString());
                advanceSearchBar.setVisibility(View.GONE);
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    if (!error) {


                        object = new JSONObject(response);


                        hospitalArray = jObj.getJSONArray("hospital_names");

                        for (int i = 0; i < hospitalArray.length(); i++) {

                            JSONObject practiceObject = hospitalArray.getJSONObject(i);

                            hospital_id = practiceObject.getString("hospital_id");
                            hospital_name = practiceObject.getString("hospital_name");
                            hospital_address = practiceObject.getString("hospital_addr");

                            HospitalNameFilter.add(new HospitalSearchFilterGetterSetter(hospital_id , hospital_name , hospital_address));
                        }

                        if(HospitalNameFilter.size() != 0)

                        {

                            HospitalFilterAdapter hospitalFilterAdapter = new HospitalFilterAdapter(getApplicationContext(), HospitalNameFilter);
                            autoCompleteSearch.setAdapter(hospitalFilterAdapter);
                            hospitalFilterAdapter.notifyDataSetChanged();
                            autoCompleteSearch.showDropDown();


                        }
                        else
                        {
//                            String errorMsg = jObj.getString("error_message");
//                            errorNotFound.add(errorMsg);

                            Toast.makeText(Home.this, "Record not found", Toast.LENGTH_SHORT).show();
                        }

                        Log.e("tag" , "hospital filter size size "+HospitalNameFilter.size());
                        // Log.e("tag" , "hospital Name and Id "+hashMap.toString());

                        //Toast.makeText(getContext() , " You are successfully Added!", Toast.LENGTH_SHORT).show();

                    } else {

                        String errorMsg = jObj.getString("error_message");

                        //Toast.makeText(getApplicationContext() , "error "+ errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Hospital Filter Error: " + error.getMessage());
                //Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                advanceSearchBar.setVisibility(View.GONE);
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url

                Map<String, String> params = new HashMap<String, String>();
                //Toast.makeText(FindDoctor.this, "speciality_id in service"+speciality_id, Toast.LENGTH_SHORT).show();
                params.put("key", Glob.Key);
                params.put("city", locationFilter.getText().toString());
                params.put("name", filterName);

                return params;
            }
        };
        strReq.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Adding request to request queue
        AppSingleton.getInstance(getApplicationContext()) .addToRequestQueue(strReq, cancel_req_tag);
    }




    public void getDoctorsNameFilter(final String filterName)
    {

        // Tag used to cancel the request
        String cancel_req_tag = "Doctors Name Filter";

        advanceSearchBar.setVisibility(View.VISIBLE);

        StringRequest strReq = new StringRequest(Request.Method.POST, Glob.GET_DOCTOR_NAME_FILTER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e(TAG, "Doctors Filter Response: " + response.toString());
                advanceSearchBar.setVisibility(View.GONE);

                try {


                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    if (!error) {


                        object = new JSONObject(response);


                        hospitalArray = jObj.getJSONArray("doctors");

                        for (int i = 0; i < hospitalArray.length(); i++) {

                            specialityDesignationList = new ArrayList();

                            JSONObject practiceObject = hospitalArray.getJSONObject(i);

                            doctor_id = practiceObject.getString("doctor_id");
                            doctor_full_name = practiceObject.getString("doctor_full_name");

                            Log.e("tag" ,"doctor id is : "+doctor_id);
                            Log.e("tag" ,"doctor full name is : "+doctor_full_name);

                            JSONArray specialityArray = practiceObject.getJSONArray("speciality");
                            for (int k = 0; k < specialityArray.length(); k++) {
                                JSONObject specialityObject = specialityArray.getJSONObject(k);
                                JSONObject innerSpecialityObject = specialityObject.getJSONObject("speciality");
                                speciality_designation = innerSpecialityObject.getString("speciality_designation");
                                specialityDesignationList.add(speciality_designation);

                            }
                            Log.e("tag" , "specialty list in filter : "+specialityDesignationList.toString());
//
//
                            StringBuilder specialistBuilder = new StringBuilder();
                            StringBuilder QualificationBuilder = new StringBuilder();
                            String specialistPrefix = "";
                            String qualificationPrefix = "";

                            for (Object str : specialityDesignationList) {
                                specialistBuilder.append(specialistPrefix);
                                specialistPrefix = " , ";
                                specialistBuilder.append(str);
                            }

                            Log.e("tag" , "specialty list in filter with comma separated : "+specialistBuilder.toString());


                            doctorNameFilter.add(new DoctorSearchFilterGetterSetter(doctor_id , doctor_full_name , specialistBuilder.toString()));
                        }

                        if(doctorNameFilter.size() != 0)

                        {
                            DoctorsFilterAdapter doctorsFilterAdapter = new DoctorsFilterAdapter(getApplicationContext(), doctorNameFilter);
                            autoCompleteSearch.setAdapter(doctorsFilterAdapter);
                            doctorsFilterAdapter.notifyDataSetChanged();
                            autoCompleteSearch.showDropDown();

                        }


                        else
                        {

                            Toast.makeText(Home.this, "Record not found", Toast.LENGTH_SHORT).show();
                        }

                        Log.e("tag" , "Doctors filter size size "+doctorNameFilter.size());

                        //Toast.makeText(getContext() , " You are successfully Added!", Toast.LENGTH_SHORT).show();

                    } else {

                        String errorMsg = jObj.getString("error_message");
                        advanceSearchBar.setVisibility(View.GONE);
                        //Toast.makeText(get    ApplicationContext() , "error "+ errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Doctors Filter Error: " + error.getMessage());
                //Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                //        searchViewProgress.setVisibility(View.GONE);
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url

                Map<String, String> params = new HashMap<String, String>();
                //Toast.makeText(FindDoctor.this, "speciality_id in service"+speciality_id, Toast.LENGTH_SHORT).show();
                params.put("key", Glob.Key);
                params.put("city", locationFilter.getText().toString());
                params.put("name", filterName);

                return params;
            }
        };
        strReq.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Adding request to request queue
        AppSingleton.getInstance(getApplicationContext()) .addToRequestQueue(strReq, cancel_req_tag);
    }



    public void freeConsultaionDialog()
    {
        final Dialog dialog = new Dialog(Home.this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.custom_call_dialog);
        TextView yes = (TextView) dialog.findViewById(R.id.yesCall);
        TextView no = (TextView) dialog.findViewById(R.id.noCall);
        dialog.show();

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                freeConsultationCall();
                dialog.dismiss();
            }
        });

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }


    public void comingSoonDialog()
    {
        final Dialog dialog = new Dialog(Home.this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.custom_coming_soon_dialog);
        TextView close = (TextView) dialog.findViewById(R.id.closeDialog);
        dialog.show();

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

    }


    public void freeConsultationCall()
    {

                //First checking if the app is already having the permission
                if (checkPermission()) {
                    //If permission is already having then showing the toast
                    // Toast.makeText(Home.this, "You already have the permission", Toast.LENGTH_LONG).show();
                    //Existing the method with return
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:03108808880"));
                    if (ActivityCompat.checkSelfPermission(Home.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    startActivity(callIntent);

                    return;
                }
                else
                {
                    //If the app has not the permission then asking for the permission
                    requestPermission();

                }

    }


    /*Ending the updates for the location service*/
    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        unregisterReceiver(myReceiver);
        super.onStop();
    }

    @Override
    protected void onStart() {
        if(mGoogleApiClient!=null){

            mGoogleApiClient.connect();
        }

        myReceiver = new MyReceiverForNetworkDialog();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Glob.MY_ACTION);
        registerReceiver(myReceiver, intentFilter);
        networkChange();
        super.onStart();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        settingRequest();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(this, "Connection Suspended!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Connection Failed!", Toast.LENGTH_SHORT).show();
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, 90000);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.i("Current Location", "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    /*Method to get the enable location settings dialog*/
    public void settingRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);    // 10 seconds, in milliseconds
        mLocationRequest.setFastestInterval(1000);   // 1 second, in milliseconds
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient,
                        builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {

            @Override
            public void onResult(@NonNull LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can
                        // initialize location requests here.
                        getLocation();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied, but this can be fixed
                        // by showing the user a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(Home.this, 1000);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way
                        // to fix the settings so we won't show the dialog.
                        break;
                }
            }

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e("TAG", "the resquest code is: " + requestCode);
        Log.e("TAG", "the resquest code is result: " + resultCode);

        // final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);

        switch (requestCode) {
            case 1000:
                switch (resultCode) {
                    case RESULT_OK:
                        // All required changes were successfully made
                        Toast.makeText(this, "Location Service Enabled", Toast.LENGTH_SHORT).show();
                        getLocation();
                        break;
                    case RESULT_CANCELED:
                        // The user was asked to change settings, but chose not to
                        Toast.makeText(this, "Location Service not Enabled", Toast.LENGTH_SHORT).show();
                        break;

                    default:
                        break;
                }
                break;
            case 123:
                if (resultCode == RESULT_OK) {
                    Log.e("TAg", "sscuceess code is: " + requestCode);
                    Bundle b = data.getExtras();
                    if (b != null) {
                        String success =  b.getString("succeess");

                        if (success.equals("yes")){
                            finish();
                            Intent i = new Intent(Home.this, SignIn.class);
                            startActivity(i);
                        }
                    }
                } else if (resultCode == 0) {
                    System.out.println("RESULT CANCELLED");
                }

                break;
        }

    }



    public void getLocation() {

        if (checkPermission()) {

            Log.e("tag", "check permission");


//            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
//                    != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
//
//            } else {
            /*Getting the location after aquiring location service*/
                mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                        mGoogleApiClient);

                if (mLastLocation != null) {
                    lat = mLastLocation.getLatitude();
                    lang = mLastLocation.getLongitude();

                    userCurrentLocation = new LatLng(lat, lang);

                    cityAndLatLngEditor.putString("lat" , String.valueOf(lat));
                    cityAndLatLngEditor.putString("lang" , String.valueOf(lang));
                    cityAndLatLngEditor.commit();

                    Log.e("tag", "check permission");


                    //getAddress(this , lat , lang);
                    Log.e("tag ", "lat " + lat);
                    Log.e("tag ", "lang " + lang);

                    if(locationApiHasRun)
                    {
                        Log.e("tag" , "location api service already run");
                    }
                    else
                    {
                        getAddressApi();
                    }
                    //getCitiesService();



                } else {
                /*if there is no last known location. Which means the device has no data for the loction currently.
                * So we will get the current location.
                * For this we'll implement Location Listener and override onLocationChanged*/
                    Log.i("Current Location", "No data for location found");

                    if (!mGoogleApiClient.isConnected())
                        mGoogleApiClient.connect();

                    LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, Home.this);
                }
            //}
            return;

        }
        else
        {
            requestPermission();
        }
    }


    /*When Location changes, this method get called. */
    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;

        lat =  mLastLocation.getLatitude();
        lang = mLastLocation.getLongitude();

        Log.e("tag " , "lat onLocationChange "+lat);
        Log.e("tag " , "lang onLocationChange "+lang);
    }




//    public boolean checkLocationPermission(){
//        if (ContextCompat.checkSelfPermission(this,
//                Manifest.permission.ACCESS_FINE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED) {
//
//            // Asking user if explanation is needed
//            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
//                    Manifest.permission.ACCESS_FINE_LOCATION)) {
//
//                // Show an explanation to the user asynchronously -- don't block
//                // this thread waiting for the user's response! After the user
//                // sees the explanation, try again to request the permission.
//
//                //Prompt the user once explanation has been shown
//                ActivityCompat.requestPermissions(this,
//                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
//                        MY_PERMISSIONS_REQUEST_LOCATION);
//
//
//            } else {
//                // No explanation needed, we can request the permission.
//                ActivityCompat.requestPermissions(this,
//                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
//                        MY_PERMISSIONS_REQUEST_LOCATION);
//            }
//            return false;
//        } else {
//            return true;
//        }
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
//        switch (requestCode) {
//            case MY_PERMISSIONS_REQUEST_LOCATION: {
//                // If request is cancelled, the result arrays are empty.
//                if (grantResults.length > 0
//                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//
//                    // permission was granted. Do the
//                    // contacts-related task you need to do.
//                    if (ContextCompat.checkSelfPermission(this,
//                            Manifest.permission.ACCESS_FINE_LOCATION)
//                            == PackageManager.PERMISSION_GRANTED) {
//
//                       /* if (mGoogleApiClient == null) {
//                            buildGoogleApiClient();
//                        }*/
//
//                        getLocation();
//
//                    }
//
//                } else {
//
//                    // Permission denied, Disable the functionality that depends on this permission.
//                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
//                }
//                return;
//            }
//
//            // other 'case' lines to check for other permissions this app might request.
//            // You can add here other case statements according to your requirement.
//        }
//    }



    private void requestPermission() {

        ActivityCompat.requestPermissions(Home.this, new String[]
                {
                        CALL_PHONE,
                        ACCESS_FINE_LOCATION
                }, REQUEST_PERMISSION_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {

            case REQUEST_PERMISSION_CODE:

                if (grantResults.length > 0) {

                    boolean CameraPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean ReadContactsPermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (CameraPermission && ReadContactsPermission) {

                        getLocation();
                        //getCitiesService();

                        if(locationApiHasRun)
                        {
                            Log.e("tag" , "location api service already run");
                        }
                        else
                        {
                            getAddressApi();
                        }



                        Log.e("tag ", "lat in permission " + lat);
                        Log.e("tag ", "lang in permission " + lang);

                        //Toast.makeText(Home.this, "Permission Granted", Toast.LENGTH_LONG).show();
                    }
                    else {
                        //Toast.makeText(Home.this,"Permission Denied",Toast.LENGTH_LONG).show();

                        String pakistan = "Pakistan";
                        cityAndLatLngEditor.putString("city" , pakistan);
                        cityAndLatLngEditor.commit();
                        locationFilter.setText(pakistan);

                    }
                }

                break;
        }
    }

    public boolean checkPermission() {

        int FirstPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), CALL_PHONE);
        int SecondPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION);

        return FirstPermissionResult == PackageManager.PERMISSION_GRANTED &&
                SecondPermissionResult == PackageManager.PERMISSION_GRANTED ;

    }


    public void SelectCity()
    {

        locationFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(Home.this);
                dialog.setContentView(R.layout.custom_citylist_search);
                dialog.setTitle("Select City");
                cityListView = (ListView) dialog.findViewById(R.id.cityList);
                search_view = (SearchView) dialog.findViewById(R.id.search_view);
                Button bt_dilaog_done = (Button) dialog.findViewById(R.id.bt_dilaog_done);
                bt_dilaog_done.setVisibility(View.GONE);
                dialog.show();



                DatabaseHelper databaseHelper = new DatabaseHelper(getApplicationContext());
                List cityList = databaseHelper.getAllPots();
                Log.e("TAG", "the city list count: " + databaseHelper.getCount());
                Log.e("TAG", "the city list from db: " + cityList.size());

                customCityNewAdapter = new CustomCityNewAdapter(getApplicationContext(), cityList);
                cityListView.setAdapter(customCityNewAdapter);
                search_view.setOnQueryTextListener(Home.this);


                cityListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        TextView city_title = (TextView) view.findViewById(R.id.city_title);
                        TextView city_id = (TextView) view.findViewById(R.id.city_id);
                        String City = city_title.getText().toString();
                        String City_id = city_id.getText().toString();

                        //Toast.makeText(Home.this, "id "+City_id, Toast.LENGTH_SHORT).show();

                        cityAndLatLngEditor.putString("city" , City);
                        cityAndLatLngEditor.commit();
                        locationFilter.setText(City);

                        dialog.dismiss();

                    }
                });

            }
        });



    }


    @Override
    public boolean onQueryTextSubmit(String s) {


        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        customCityNewAdapter.getFilter().filter(s);
        return false;
    }

    public void  getAddressApi()
    {

        // Tag used to cancel the request
        //progressDialog.setMessage("Adding you ...");
       // showDialog();

        //String addressUrl = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + lat + "," + lang + "&key=" + getResources().getString(R.string.google_maps_key);

        StringRequest strReq = new StringRequest(Request.Method.GET, "http://maps.googleapis.com/maps/api/geocode/json?latlng="+lat+","+lang+"&sensor=true", new Response.Listener<String>()
        //StringRequest strReq = new StringRequest(Request.Method.GET, "http://maps.googleapis.com/maps/api/geocode/json?latlng=32.3735542,80.1872261&sensor=true", new Response.Listener<String>()

        {

            @Override
            public void onResponse(String response) {
                Log.e(TAG, "lat lang Response: " + response.toString());
                //hideDialog();
                String city = "";
                String country = "";
                try {





                        jsonObject = new JSONObject(response);

                        String status = jsonObject.getString("status");
                        if(status.equals("OVER_QUERY_LIMIT"))
                        {

                            String pakistan = "Pakistan";
                            cityAndLatLngEditor.putString("city" , pakistan);
                            cityAndLatLngEditor.commit();
                            locationFilter.setText(pakistan);

                            Toast.makeText(Home.this, "OVER_QUERY_LIMIT", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            JSONObject data = jsonObject.getJSONArray("results").getJSONObject(0);
                            Log.e("TAG", "the result from address: " + data);
                            JSONArray cityName = data.getJSONArray("address_components");

                            for (int i = 0; i < cityName.length(); i++) {
                                JSONObject zero2 = cityName.getJSONObject(i);
                                String long_name = zero2.getString("long_name");
                                JSONArray mtypes = zero2.getJSONArray("types");
                                String Type = mtypes.getString(0);

                                if (Type.equalsIgnoreCase("locality") || Type.equalsIgnoreCase("administrative_area_level_2")  || Type.equalsIgnoreCase("administrative_area_level_3")) {
                                    city = long_name;
                                }

                                if(Type.equalsIgnoreCase("country"))
                                {
                                    country = long_name;
                                }

                                if(country.equals("Pakistan"))
                                {
                                    cityAndLatLngEditor.putString("city" , city);
                                    cityAndLatLngEditor.commit();
                                    locationFilter.setText(city);
                                }
                                else
                                {
                                    String pakistan = "Pakistan";
                                    cityAndLatLngEditor.putString("city" , pakistan);
                                    cityAndLatLngEditor.commit();
                                    locationFilter.setText(pakistan);
                                }


                            }

                            Log.e("TAG", "address_components city " + city);
                            Log.e("TAG", "address_components country " + country);






                            locationApiHasRun = true ;
                        }




                        //Toast.makeText(getApplicationContext() , " You are successfully Added!", Toast.LENGTH_SHORT).show();





                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Registration Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                //hideDialog();
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
    private void showDialog() {
        if (!progressDialog.isShowing())
            progressDialog.show();
    }

    private void hideDialog() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }

    @Override
    protected void onResume() {
        sharedPreferencesCityAndLatLng = getSharedPreferences("CityPreferences" , MODE_PRIVATE);
        city = sharedPreferencesCityAndLatLng.getString("city" , null);
        Log.e("tag" , "city in on resume "+city);
        if(city == null)
        {
            locationFilter.setText("Pakistan");

        }
        else
        {
            Log.e("tag", "onResume home: "+city );
            locationFilter.setText(city);
        }

        setImageInActionbar();
//        myReceiver = new MyReceiverForNetworkDialog();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Glob.MY_ACTION);
        registerReceiver(myReceiver, intentFilter);
//        networkChange();

        super.onResume();


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }


    public static Intent getOpenFacebookIntent(Context context) {

        try {
            context.getPackageManager().getPackageInfo("com.facebook.katana", 0);
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/guardautozonepk/"));
        } catch (Exception e) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/guardautozonepk/"));
        }



    }

    private void openFacebookAppGroup() {
        String facebookUrl = "https://www.facebook.com/groups/MediCallers/";
        String facebookID = "1716383438664609";

        try {
            int versionCode = getApplicationContext().getPackageManager().getPackageInfo("com.facebook.katana", 0).versionCode;

            if(!facebookID.isEmpty()) {
                // open the Facebook app using facebookID (fb://profile/facebookID or fb://page/facebookID)
                Uri uri = Uri.parse("fb://group/" + facebookID);
                startActivity(new Intent(Intent.ACTION_VIEW, uri));
            } else if (versionCode >= 3002850 && !facebookUrl.isEmpty()) {
                // open Facebook app using facebook url
                Uri uri = Uri.parse("fb://facewebmodal/f?href=" + facebookUrl);
                startActivity(new Intent(Intent.ACTION_VIEW, uri));
            } else {
                // Facebook is not installed. Open the browser
                Uri uri = Uri.parse(facebookUrl);
                startActivity(new Intent(Intent.ACTION_VIEW, uri));
            }
        } catch (PackageManager.NameNotFoundException e) {
            // Facebook is not installed. Open the browser
            Uri uri = Uri.parse(facebookUrl);
            startActivity(new Intent(Intent.ACTION_VIEW, uri));
        }
    }


    private void openFacebookAppGroupEvents() {
        String facebookUrl = "https://www.facebook.com/groups/MediCallers/events/";
        String facebookID = "1716383438664609";

        try {
            int versionCode = getApplicationContext().getPackageManager().getPackageInfo("com.facebook.katana", 0).versionCode;

            if(!facebookID.isEmpty()) {
                // open the Facebook app using facebookID (fb://profile/facebookID or fb://page/facebookID)
                Uri uri = Uri.parse("fb://group/" + facebookID);
                startActivity(new Intent(Intent.ACTION_VIEW, uri));
            } else if (versionCode >= 3002850 && !facebookUrl.isEmpty()) {
                // open Facebook app using facebook url
                Uri uri = Uri.parse("fb://facewebmodal/f?href=" + facebookUrl);
                startActivity(new Intent(Intent.ACTION_VIEW, uri));
            } else {
                // Facebook is not installed. Open the browser
                Uri uri = Uri.parse(facebookUrl);
                startActivity(new Intent(Intent.ACTION_VIEW, uri));
            }
        } catch (PackageManager.NameNotFoundException e) {
            // Facebook is not installed. Open the browser
            Uri uri = Uri.parse(facebookUrl);
            startActivity(new Intent(Intent.ACTION_VIEW, uri));
        }
    }

    public void callingHospitalListservice()
    {

        Intent startingServiceHoppitalList = new Intent(Home.this, GettingAllHospitalListService.class);
        startService(startingServiceHoppitalList);
    }


    public void callingCitiesListService()
    {
        Intent citiesService = new Intent(Home.this , GetAllCitiesListService.class);
        startService(citiesService);

    }



    @Override
    protected void onDestroy() {
        //stopService(new Intent(Home.this, GettingAllHospitalListService.class));
        stopService(new Intent(Home.this, GetAllCitiesListService.class));
        Log.e("tag" , "onDestroy Call in Home");
        super.onDestroy();
    }


    public void networkChange(){
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(new CheckConnectivity(), intentFilter);
    }

    public void setImageInActionbar()
    {
        SharedPreferences sharedPreferences = getSharedPreferences("usercrad", 0);
        if (sharedPreferences!=null){

            String userId = sharedPreferences.getString("userid", null);
            if (userId!=null){
                String userFullName = sharedPreferences.getString("userfullname", null);
                String profile_img = sharedPreferences.getString("profile_img", null);
                final String userTable = sharedPreferences.getString("usertable", null);

                final String PROFILE_IMAGE_URL = Glob.IMAGE_BACK_URL+profile_img;
                Log.e("TAg", "the Profile Image is url is: " + PROFILE_IMAGE_URL);


                Picasso.with(this).load(PROFILE_IMAGE_URL).placeholder(R.drawable.loginuser).transform(new CircleTransformPicasso()).into(userIcon);

                userIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (userTable.equals(getResources().getString(R.string.doctors))) {

                            Intent intent = new Intent(Home.this , UpdateDoctorProfile.class);
                            startActivity(intent);

                            //starting service for getting all data from server
                        }

                        if (userTable.equals(getResources().getString(R.string.patients))) {

                            /*Intent intent = new Intent(NavigationDrawer.this, MedicalRecord.class);
                            startActivity(intent);*/

                        }

                        if (userTable.equals(getResources().getString(R.string.labs))) {


                          /*  Intent intent = new Intent(NavigationDrawer.this, UpdateDoctorProfile.class);
                            startActivity(intent);*/

                        }


                        if (userTable.equals(getResources().getString(R.string.hospitals))) {

                           /* Intent intent = new Intent(NavigationDrawer.this, MedicalRecord.class);
                            startActivity(intent);*/

                        }
                        if (userTable.equals(getResources().getString(R.string.pharmacies))) {

                          /*  Intent intent = new Intent(NavigationDrawer.this, UpdateDoctorProfile.class);
                            startActivity(intent);*/

                        }
                        if (userTable.equals(getResources().getString(R.string.blood_donors))) {

                            /*Intent intent = new Intent(NavigationDrawer.this, MedicalRecord.class);
                            startActivity(intent);*/

                        }
                        if (userTable.equals(getResources().getString(R.string.ambulances))) {

                          /*  Intent intent = new Intent(NavigationDrawer.this, UpdateDoctorProfile.class);
                            startActivity(intent);*/

                        }
                        if (userTable.equals(getResources().getString(R.string.health_professionals))) {

                          /*  Intent intent = new Intent(NavigationDrawer.this, UpdateDoctorProfile.class);
                            startActivity(intent);*/

                        }
                    }
                });

            }
            else {
                imageView.setImageResource(R.drawable.loginuser);
                userIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Home.this , SignIn.class);
                        startActivity(intent);
                    }
                });
            }
        }
    }

    //Thread for starting mainActivity
    private Runnable mRunnableStartMainActivity = new Runnable() {
        @Override
        public void run() {
            Log.d("Handler", " Calls");
            timerHandler--;
           /* mHandler = new Handler();
            mHandler.postDelayed(this, 1000);*/

            Log.e("TAG", "runnin timmer is: " + timerHandler);

            if (timerHandler == 0){


                Log.e("TAG", "Seached Text Is: " + timerHandler);
                mHandler.removeCallbacks(mRunnableStartMainActivity);
                timerHandler = 2;
                if (textToSearch.length()>=3) {

                    if(filter.equals("Doctors"))
                    {
                        getDoctorsNameFilter(textToSearch);
                    }
                    else if(filter.equals("Hospitals")) {
                        getHospitalsNameFilter(textToSearch);

                    }

                }

            }
        }
    };


    //handler for the starign activity

    public void useHandler(String text , String filter) {

        textToSearch = text;
        this.filter = filter;
        mHandler = new Handler();
        mHandler.postDelayed(mRunnableStartMainActivity, 1000);

    }
}
