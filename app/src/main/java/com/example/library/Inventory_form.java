package com.example.library;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.ToneGenerator;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.library.utils.RFIDService;
import com.example.library.utils.all_scann_books.data.AllBookSDataModel;
import com.example.library.utils.all_scann_books.retrofit.RetrofitApiClient2;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.speedata.libuhf.IUHFService;
import com.speedata.libuhf.UHFManager;
import com.speedata.libuhf.bean.SpdInventoryData;
import com.speedata.libuhf.interfaces.OnSpdInventoryListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;


public class Inventory_form extends AppCompatActivity {



    long lastTimeMillis;
    private int soundId;
    private SoundPool soundPool;
    boolean isInventoryRunning = false;
    HashSet<String> scannedRfidList = new HashSet<>();

    Spinner Select_Inventory, Inventory_Details;
   // String[] value = new String[]{"Choose", "Titles", "Author", "Department", "Racks"};
    String[] value = new String[]{"Choose", "Titles", "Author","Racks"};
    List<String> data_value;
    String inventory_details_option, Select_option, Submit_rfid;
    ProgressDialog dialog;
    Button search_book, Search, Submit, NewBtn, Back_Btn,btnRfid,btnStartFor_AllBooks;
    List<DataModel_Inventory> List_Inventory;
    List<String> matchedRfid;
    ArrayList<String> TmpList;
    List<Integer> TempRfidList;
    TextView total, found, not_found,dropDownSearch;
    Adapter_Inventory adapter_list;
    RecyclerView recyclerView;
    EditText Accession;
    String rfid;

    String RFID;
    IUHFService iuhfService;
    DataModel_Inventory dataModel_inventory;
    boolean submit_foundStatus;
    int counter = 0, len, not_founded;
    CoordinatorLayout coordinatorLayout;
    String RfidNo;
    SharedPreferences sh;
    List<DataModel_Inventory_InventoryStatus> ListStatus;
    List<DataModel_Inventory_InventoryStatus> TempList;
    List<DataModel_Inventory_InventoryStatus> StatusList;
    private ToneGenerator toneGenerator;
    TextView tvCount,txtCount;
    CardView rfidCount;
    List<String> rfidNoList;
    Dialog dialogTag;


    private boolean isServiceRunning = false;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_form);



        //Initialize of Components
        Select_Inventory = findViewById(R.id.spinner);
        Inventory_Details = findViewById(R.id.spinner_details);
        dialog = new ProgressDialog(this);
        search_book = findViewById(R.id.Search_Books);
        total = findViewById(R.id.Total);
        found = findViewById(R.id.Found);
        not_found = findViewById(R.id.not_found);
        recyclerView = findViewById(R.id.recyclerView);
        Search = findViewById(R.id.Search_);
        Accession = findViewById(R.id.Search_Data);
        NewBtn = findViewById(R.id.New_Button);
        coordinatorLayout = findViewById(R.id.coordinator);
        Back_Btn = findViewById(R.id.Back_Button);
        dataModel_inventory = new DataModel_Inventory();
        Submit = findViewById(R.id.Submit_Button);
        btnRfid  = findViewById(R.id.btnTemp);
        dropDownSearch=findViewById(R.id.testView);
        tvCount = findViewById(R.id.tv_count);
        rfidCount = findViewById(R.id.cardView_count);
        txtCount = findViewById(R.id.count);
        btnStartFor_AllBooks = findViewById(R.id.btnStartFor_AllBooks);
        rfidNoList = new ArrayList<>();

        List_Inventory = new ArrayList<>();

        List_Inventory.add(new DataModel_Inventory("Title", "language", "edition", "publisher", "access_details", "Author", "subject", "rfid"));
        //Method for Left Swipe to Delete
        enableSwipeToDeleteAndUndo();


        sh = getSharedPreferences("pref", 0);

        try {
            iuhfService = UHFManager.getUHFService(this);
            iuhfService.openDev();
        } catch (Exception e){

        }
       // iuhfService.inventoryStart();
        initSoundPool();



        btnStartFor_AllBooks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!isInventoryRunning) {
                    // Start inventory service
                    iuhfService.setOnInventoryListener(new OnSpdInventoryListener() {
                        @Override
                        public void getInventoryData(SpdInventoryData var1) {
                            try {

                                if (!scannedRfidList.contains(var1.getEpc())) {
                                    scannedRfidList.add(var1.getEpc());
                                    tvCount.setText(String.valueOf(scannedRfidList.size()));
                                    rfid = var1.getEpc();
                                }
                            } catch (Exception e){

                            }

                            try {
                                long timeMillis = System.currentTimeMillis();
                                long l = timeMillis - lastTimeMillis;
                                if (l < 100) {
                                    return;
                                }
                                lastTimeMillis = System.currentTimeMillis();
                                soundPool.play(soundId, 1, 1, 0, 0, 1);
                                Log.d("RFFFF",rfid);
                                //Toast.makeText(Inventory_form.this,rfid,Toast.LENGTH_SHORT).show();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (!rfidNoList.contains(var1.getEpc())) {
                                            rfidNoList.add(var1.getEpc());
                                            txtCount.setText(String.valueOf(scannedRfidList.size()));

                                        }

                                    }
                                });
                            } catch (Exception e){
                                Log.d("exception",e.toString());
                            }
                        }

                        @Override
                        public void onInventoryStatus(int i) {

                        }
                    });

                    // Start inventory service (you might need to call this method to start the service)
                    iuhfService.inventoryStart();
                    btnStartFor_AllBooks.setText("Stop");
                    isInventoryRunning = true;
                    txtCount.setText(String.valueOf(scannedRfidList.size()));
                } else {
                    btnStartFor_AllBooks.setText("Start");
                    isInventoryRunning = false;
                    iuhfService.inventoryStop();
                }
            }
        });

        btnRfid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ListStatus == null ) {
                    Toast.makeText(Inventory_form.this, "Please select Book Details", Toast.LENGTH_SHORT).show();
                }else if (ListStatus.isEmpty()){
                  Toast.makeText(Inventory_form.this,"No data found for search",Toast.LENGTH_SHORT).show();
                } else {

                    if (!isInventoryRunning) {
                        // Start inventory service
                        iuhfService.setOnInventoryListener(new OnSpdInventoryListener() {
                            @Override
                            public void getInventoryData(SpdInventoryData var1) {
                                // Your inventory data handling code
                                try {

                                    if (!scannedRfidList.contains(var1.getEpc())) {
                                        scannedRfidList.add(var1.getEpc());
                                        tvCount.setText(String.valueOf(scannedRfidList.size()));
                                        rfid = var1.getEpc();
                                    }
                                } catch (Exception e) {
                                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                                }
                                // ...
//                            rfid = var1.getEpc();
                                // Play sound and show toast
                                try {
                                    long timeMillis = System.currentTimeMillis();
                                    long l = timeMillis - lastTimeMillis;
                                    if (l < 100) {
                                        return;
                                    }
                                    lastTimeMillis = System.currentTimeMillis();
                                    soundPool.play(soundId, 1, 1, 0, 0, 1);
                                    Log.d("RFFFF", rfid);
                                    //Toast.makeText(Inventory_form.this,rfid,Toast.LENGTH_SHORT).show();
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Accession.setFocusable(true);
                                            Accession.setFocusableInTouchMode(true);
                                            //RfidNo = iuhfService.read_area(1, "2", "6", "00000000").toString();
                                            Accession.setText(rfid);
                                            List temp = new ArrayList<>();
                                            if (!temp.contains(var1.getEpc())) {
                                                temp.add(var1.getEpc());
                                                adapter_list.getFilter(var1.getEpc());
                                                adapter_list.notifyDataSetChanged();

                                                int fnd = adapter_list.getFilter(String.valueOf(adapter_list.bookFoundCount));
                                                counter += fnd;
                                                Log.d("emovjv", String.valueOf(adapter_list.bookFoundCount));
                                                Accession.setText("");
                                                not_founded = len - fnd;
                                                total.setText(String.valueOf(len));
                                                found.setText(String.valueOf(fnd));
                                                not_found.setText(String.valueOf(not_founded));

                                                //adding  data found and not
                                                String rfid = sh.getString("RFID NO", null);
                                                String status = sh.getString("Status", null);
                                                ListStatus.add(new DataModel_Inventory_InventoryStatus(rfid, status));
                                            }

                                        }
                                    });
                                } catch (Exception e) {
                                    Log.d("exception", e.toString());
                                }
                            }

                            @Override
                            public void onInventoryStatus(int i) {

                            }
                        });

                        // Start inventory service (you might need to call this method to start the service)
                        iuhfService.inventoryStart();
                        btnRfid.setText("Stop");
                        isInventoryRunning = true;
                    } else {
                        btnRfid.setText("Start");
                        isInventoryRunning = false;
                        iuhfService.inventoryStop();
                    }
                }
            }
        });



        dropDownSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Initialize dialog
                Dialog dialog;
                dialog=  new Dialog(Inventory_form.this);

                // set custom dialog
                dialog.setContentView(R.layout.dialog_searchable_spinner);

                // set custom height and width
                dialog.getWindow().setLayout(900,900);

                // set transparent background
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                // show dialog
                dialog.show();

                // Initialize and assign variable
                EditText editText=dialog.findViewById(R.id.edit_text);
                ListView listView=dialog.findViewById(R.id.list_view);

                // Initialize array adapter
                ArrayAdapter<String> adapter=new ArrayAdapter<>(Inventory_form.this, android.R.layout.simple_list_item_1,data_value);

                // set adapter
                listView.setAdapter(adapter);
                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        adapter.getFilter().filter(s);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        // when item selected from list
                        // set selected item on textView
                        dropDownSearch.setText(adapter.getItem(position));
                        try {
                           // FetchData(inventory_details_option, Select_option);
                            FetchData(String.valueOf(dropDownSearch.getText()), Select_option);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(Inventory_form.this,adapter.getItem(position),Toast.LENGTH_SHORT).show();

                        // Dismiss dialog
                        dialog.dismiss();
                    }
                });
            }
        });







        NewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Clear();
            }
        });
        Back_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Inventory_form.this, MainActivity.class));
                finish();
            }
        });

        Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                    try {
                        dialog.show();
                        dialog.setMessage(getString(R.string.Dialog_Text));
                        dialog.setCancelable(false);
                        submit_Report();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                
            }
        });



        Search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iuhfService.openDev();
                RfidNo = iuhfService.read_area(1, "2", "6", "00000000");
                Accession.setText(rfid);
                //if (Accession.length() > 0) {
                    String Search_value = Accession.getText().toString();
                    int fnd = adapter_list.getFilter(Search_value);
                    counter = counter + fnd;
                // counter =  fnd;
                    Log.d("counter", String.valueOf(fnd));
//                    Toast.makeText(Inventory_form.this, "Counted "+counter, Toast.LENGTH_SHORT).show();

                    Accession.setText("");
                    not_founded = len - counter;
                    total.setText(String.valueOf(len));
                    found.setText(String.valueOf(counter));
                    not_found.setText(String.valueOf(not_founded));
                    //adding  data found and not
                    String rfid = sh.getString("RFID NO", null);
                    String status = sh.getString("Status", null);
                    ListStatus.add(new DataModel_Inventory_InventoryStatus(rfid,status));


//                    count();
//                } else {
//                    Accession.setError("Enter input please....");
//                }
            }

        });

        search_book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    List_Inventory.clear();
                    FetchData((String) dropDownSearch.getText(),Select_option);

                   // FetchData(inventory_details_option, Select_option);
                    dialog.show();
                    dialog.setMessage(getString(R.string.Dialog_Text));
                    dialog.setCancelable(false);
                    Log.d("listSize",String.valueOf(ListStatus.size()));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
//                Toast.makeText(Inventory_form.this, "Select value"+inventory_details_option+Select_option, Toast.LENGTH_SHORT).show();
            }
        });

//     Enable DetailSpinner
        Inventory_Details.setEnabled(false);


        //Initialize Array
        data_value = new ArrayList<String>();
        ListStatus = new ArrayList<>();
        data_value.add("Choose");

        //Set up Local Data with Adapter
        final List<String> List = new ArrayList<>(Arrays.asList(value));
        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, List);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        Select_Inventory.setAdapter(spinnerArrayAdapter);

        // Implemented onSelected Listener on Spinner
        Select_Inventory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position == 1) {
                    // this is use
                   //String url = "http://192.168.0.113:83/api/Inventory/FetchTitles";
                    //check
                    //String url = "http://192.168.100.5:81/api/Inventory/FetchTitles";
                    String url = "http://164.52.223.163:4558/api/Inventory/FetchTitles";
                    //String url = "https://library.poxorfid.com/api/Inventory/FetchTitles";
                    SelectByTitle(url);
                    data_value.clear();
                    dialog.show();
                    dialog.setMessage(getString(R.string.Dialog_Text));
                    dialog.setCancelable(false);
                    rfidCount.setVisibility(View.GONE);
                    btnStartFor_AllBooks.setVisibility(View.GONE);
                    btnRfid.setVisibility(View.VISIBLE);

////                    Toast.makeText(Inventory_form.this,"Title",Toast.LENGTH_LONG).show();
                } else if (position == 2) {
                    String url = "http://192.168.0.113:83/api/Inventory/FetchAuthor";
                   //String url = "http://164.52.223.163:4558/api/Inventory/FetchAuthor";
                   // String url = "https://library.poxorfid.com/api/Inventory/FetchAuthor";

                    //check
                    //String url = "http://192.168.100.5:81/api/Inventory/FetchAuthor";
                    SelectByTitle(url);
                    data_value.clear();
                    dialog.show();
                    dialog.setMessage(getString(R.string.Dialog_Text));
                    dialog.setCancelable(false);
                    rfidCount.setVisibility(View.GONE);
                    btnStartFor_AllBooks.setVisibility(View.GONE);
                    btnRfid.setVisibility(View.VISIBLE);

//                    Toast.makeText(Inventory_form.this,"Author",Toast.LENGTH_LONG).show();

//                } else if (position == 3) {
//                     //String url = "http://192.168.100.5:81/api/Inventory/FetchDepartments";
//                    //String url = "http://164.52.223.163:4558/api/Inventory/FetchDepartments";
//                   String url = "http://192.168.0.113:83/api/Inventory/FetchDepartments";
//                    //String url = "https://library.poxorfid.com/api/Inventory/FetchDepartments";
//                    SelectByTitle(url);
//                    dialog.show();
//                    data_value.clear();
//                    dialog.setMessage(getString(R.string.Dialog_Text));
//                    dialog.setCancelable(false);

                } else if (position == 3) {
                   // String url = "http://192.168.100.5:81/api/Inventory/FetchRacks";
                   // String url = "http://164.52.223.163:4558/api/Inventory/FetchRacks";
                   String url = "http://192.168.0.113:83/api/Inventory/FetchRacks";
                    //String url = "https://library.poxorfid.com/api/Inventory/FetchRacks";
//                    SelectByTitle(url);
                    rfidCount.setVisibility(View.GONE);
                    btnStartFor_AllBooks.setVisibility(View.GONE);
                    btnRfid.setVisibility(View.VISIBLE);
                    FetchRacks(url);
                    dialog.show();
                    data_value.clear();
                    dialog.setMessage(getString(R.string.Dialog_Text));
                    dialog.setCancelable(false);

                } else  if(position==4){
                    rfidCount.setVisibility(View.VISIBLE);
                    txtCount.setText(String.valueOf(scannedRfidList.size()));
                    btnStartFor_AllBooks.setVisibility(View.VISIBLE);
                    btnRfid.setVisibility(View.GONE);

                }
                Select_option = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Set up Detail Spinner


        Inventory_Details.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                inventory_details_option = parent.getItemAtPosition(position).toString();
                System.out.println("Select Value " + inventory_details_option);
//                Toast.makeText(Inventory_form.this, "Select Value" + inventory_details_option, Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Set The Value
        found.setText("0");
        not_found.setText("0");
        total.setText(String.valueOf(len));

    }

    private void FetchRacks(String url) {
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {



                try {
                    JSONArray array = new JSONArray(response);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.getJSONObject(i);
                        // Log.d("rackNo", (String) array.get(i));
                        String id = object.getString("Id");
                        String rackNo = object.getString("RackNo");
                        //rackNo = (String) array.get(i);
                        // Log.d("rackNo", (String) array.get(Integer.parseInt("Id")));
                        //rack_no.add(id);
                        data_value.add(rackNo);

                    }
                    RearrangeItems(data_value);
                } catch (Exception e){

                }


//                try {
//                    JSONObject jsonArray = new JSONObject(response);
//                    for (int i = 0; i < jsonArray.length(); i++) {
//                        i = i + 1;
//                        data_value.add(jsonArray.getString(String.valueOf(i)));
//                        i = i - 1;
//                    }
////
////                    System.out.println("Sorted List "+data_value);
//                    RearrangeItems(data_value);
////                    final List<String> List = new ArrayList<>(Arrays.asList(value));
////
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
                List_Inventory = new ArrayList<>();
                dialog.dismiss();
                Inventory_Details.setEnabled(true);

//                System.out.println("Array Value " + data_value);
//
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Inventory_form.this, error.getMessage(), Toast.LENGTH_LONG).show();
                dialog.dismiss();

            }
        });

        queue.add(request);

    }

    //Submit Report to Server Method
    private void submit_Report() throws JSONException {

        //Getting Shared Data


        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

        //String url = "http://164.52.223.163:4558/api/Inventory/SubmitInventoyRecord";
       String url = "http://192.168.0.113:83/api/Inventory/SubmitInventoyRecord";

       // String url = "http://192.168.100.5:81/api/Inventory/SubmitInventoyRecord";
       // String url = "https://library.poxorfid.com/api/Inventory/SubmitInventoyRecord";

        JSONObject object = new JSONObject();
        object.put("inventoryID", "");
        object.put("inventoryBasedOn", Select_option);
        object.put("doneBy", "Admin");
        object.put("total", String.valueOf(len));
        object.put("found", String.valueOf(counter));
        object.put("notFound", String.valueOf(not_founded));
        object.put("date", currentDate);
        //JSONArray array = new JSONArray();
        JSONArray array = new JSONArray();


        for (int i = 0; i < ListStatus.size(); i++) {

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("rfidNo", ListStatus.get(i).getRFIDNUMBER());
            Log.d("rfidNo",ListStatus.get(i).getRFIDNUMBER());
            Log.d("listSize",String.valueOf(ListStatus.size()));
            jsonObject.put("foundStatus", ListStatus.get(i).getStatus());
            Log.d("foundStatus",ListStatus.get(i).getStatus());
            array.put(jsonObject);
        }

//        object.put("inventoryList", "" + array);*/
//        JSONObject jsonObject  = new JSONObject();
//        jsonObject.put("rfidNo","B11");
//        jsonObject.put("foundStatus","B12");
//
//        JSONArray  array = new JSONArray();
//        array.put(jsonObject);

        object.put("inventoryList", array);
//
        RequestQueue queue = Volley.newRequestQueue(this);
        System.out.println("JSON DATA " + object);

        final String requestBody = object.toString();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @SuppressLint("CommitPrefEdits")
            @Override
            public void onResponse(String response) {
                Toast.makeText(Inventory_form.this, response + "Submitted....", Toast.LENGTH_SHORT).show();
                Log.i("VOLLEY Submit", response);
                dialog.dismiss();
                ListStatus.clear();
                sh.edit().clear();
                sh.edit().apply();
                Clear();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("VOLLEY Negative", error.toString() + "Submitted Response : " + error.networkResponse.data + "UTF-8");
                System.out.println("Error Submitting" + error.getCause());
                dialog.dismiss();
            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return requestBody == null ? null : requestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                    return null;
                }
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                System.out.println("Response Code " + response.statusCode);
                return super.parseNetworkResponse(response);
            }
        };

        queue.add(stringRequest);
    }

    //Method for Count Number of Search
//    public void count() {
//        while (dataModel_inventory.getColor() == "Green") {
//
//            System.out.println(counter + "Search DATA ");
//        }
//        counter++;
//
//        System.out.println(counter + "Search DATA ");
//        not_founded = len - counter;
//        total.setText(String.valueOf(len));
//        found.setText(String.valueOf(counter));
//        not_found.setText(String.valueOf(not_founded));
//
//    }

    //Method Fetch Data for DetailsSpinner
    public void SelectByTitle(String url) {

        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        data_value.add(jsonArray.getString(i));

                    }
//
//                    System.out.println("Sorted List "+data_value);
                    RearrangeItems(data_value);
//                    final List<String> List = new ArrayList<>(Arrays.asList(value));
//
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                List_Inventory = new ArrayList<>();
                dialog.dismiss();
                Inventory_Details.setEnabled(true);

//                System.out.println("Array Value " + data_value);
//
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Inventory_form.this,"No data found", Toast.LENGTH_LONG).show();
                dialog.dismiss();



            }
        });

        queue.add(request);

    }


    //Method for Fetch Books Details
    private void FetchData(String dropDownSearch, String select_option) throws JSONException {
   // private void FetchData(String inventory_details_option, String select_option) throws JSONException {

       // String url = "http://192.168.100.5:81/api/Inventory/InventoryRecords";
      // String url = "http://164.52.223.163:4558/api/Inventory/InventoryRecords";
        //String url = "https://library.poxorfid.com/api/Inventory/InventoryRecords";
        // this is use
        String url = "http://192.168.0.113:83/api/Inventory/InventoryRecords";
        JSONObject obj = new JSONObject();
//        obj.put("AccessNo", "B1228");
        obj.put("SearchParameter", select_option.trim());
       // obj.put("SearchValue", inventory_details_option.trim());
        obj.put("SearchValue", dropDownSearch);

        RequestQueue queue = Volley.newRequestQueue(this);


        final String requestBody = obj.toString();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    List_Inventory.clear();
                        found.setText(String.valueOf(""));
                        not_found.setText(String.valueOf(""));
                    JSONArray array = new JSONArray(response);


                    len = array.length();
                    total.setText(String.valueOf(len));


                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.getJSONObject(i);
                        String access_details = object.getString("AccessNo");
                        String Title = object.getString("Title");
                        String publisher = object.getString("Publisher");
                        String Author = object.getString("Author");
                        String subject = object.getString("SubjectTitle");
                        String language = object.getString("Language");
                        String edition = object.getString("Edition");
                       // String RFIDNO = object.getString("RFIDNo");
                        rfid = object.getString("RFIDNo");
                        RFID = object.getString("RFIDNo");
                        Log.d("rfidno",object.getString("RFIDNo"));
                        List_Inventory.add(new DataModel_Inventory(Title, language, edition, publisher, access_details, Author, subject, rfid));
                        ListStatus.add(new DataModel_Inventory_InventoryStatus(rfid, "0"));

                    }

                    adapter_list = new Adapter_Inventory(List_Inventory, getApplicationContext());
                    recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    recyclerView.setAdapter(adapter_list);
                    dialog.dismiss();
//                    Toast.makeText(Inventory_form.this, name, Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(Inventory_form.this,"No Records",Toast.LENGTH_SHORT).show();
                }
                Log.i("VOLLEY", response);
                dialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("VOLLEY Negative", error.toString());
                dialog.dismiss();
            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return requestBody == null ? null : requestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                    return null;
                }
            }
        };

        queue.add(stringRequest);
    }

    //Method for Clear Data from components
    public void Clear() {
        List_Inventory.clear();

        adapter_list = new Adapter_Inventory(List_Inventory, getApplicationContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(adapter_list);
        Accession.setText("");
        total.setText("");
        not_found.setText("");
        found.setText("");
        data_value.clear();
        data_value.add("Choose");
        final ArrayAdapter<String> spinnerArrayAdapter1 = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, data_value);
        spinnerArrayAdapter1.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        Inventory_Details.setAdapter(spinnerArrayAdapter1);

    }

    //Method For Left swipe for Delete
    private void enableSwipeToDeleteAndUndo() {
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(this) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {


                final int position = viewHolder.getAdapterPosition();
                final DataModel_Inventory item = adapter_list.getData().get(position);
                adapter_list.removeItem(position);


                Snackbar snackbar = Snackbar.make(coordinatorLayout, "Item was removed from the list.", Snackbar.LENGTH_LONG);
                snackbar.setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        adapter_list.restoreItem(item, position);
                        recyclerView.scrollToPosition(position);
                    }
                });
//
                snackbar.setActionTextColor(Color.YELLOW);
                snackbar.show();

            }
        };

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(recyclerView);
    }

    private void RearrangeItems(List<String> data_value) {
        Collections.sort(this.data_value, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                return s1.compareToIgnoreCase(s2);
            }
        });
        final ArrayAdapter<String> spinnerArrayAdapter1 = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, this.data_value);
        spinnerArrayAdapter1.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        Inventory_Details.setAdapter(spinnerArrayAdapter1);

//        Adapter adapter = new Adapter(List_Inventory, Inventory_form.this);
//        recyclerView.setAdapter(adapter);
    }
    //Fetching Racks Function





//    @Override
//    public void onBackPressed() {
//        startActivity(new Intent(Inventory_form.this, MainActivity.class));
//        finish();
//    }


//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_F1) {
//            stopInventoryService();
//
//            try {
//
//                toneGenerator = new ToneGenerator(AudioManager.STREAM_ALARM, ToneGenerator.MAX_VOLUME);
//                toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP, 100);
//                VibrationUtil.vibratePhone(this);
//            } catch (Exception e){
//                Log.d("exception",e.toString());
//
//            }
//
//
////            try {
////                toneGenerator = new ToneGenerator(AudioManager.STREAM_ALARM, ToneGenerator.MAX_VOLUME);
////                toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP, 100);
////                VibrationUtil.vibratePhone(this);
////            } catch (Exception e){
////                Log.d("exception",e.toString());
////
////            }
//
//            try {
//            Accession.setFocusable(true);
//            Accession.setFocusableInTouchMode(true);
////            iuhfService = UHFManager.getUHFService(this);
////            iuhfService.openDev();
////            iuhfService.inventoryStart();
//            RfidNo = iuhfService.read_area(1, "2", "6", "00000000").toString();
//            Accession.setText(RfidNo);
//
//           // Log.d("rfid",RfidNo);
//           // Toast.makeText(Inventory_form.this, "E200001D370C01951420B0FD", Toast.LENGTH_SHORT).show();
////            Log.d("rfidNoFromSharePref",sh.getString("RFID NO",null));
////            Log.e("rfidNoFromSharePref",sh.getString("RFID NO",null));
//
//
//
//
//                String Search_value = Accession.getText().toString();
//                int fnd = adapter_list.getFilter(Search_value);
//
//
//                //if (Search_value.equals(sh.getString("RFID NO",null))) {
////                    counter = counter + fnd;
//
//                //counter =  fnd;
//
//                    Accession.setText("");
//                    not_founded = len - fnd;
//                    total.setText(String.valueOf(len));
//                    found.setText(String.valueOf(fnd));
//                    not_found.setText(String.valueOf(not_founded));
//
//                    //adding  data found and not
//                    String rfid = sh.getString("RFID NO", null);
//                    String status = sh.getString("Status", null);
//                    ListStatus.add(new DataModel_Inventory_InventoryStatus(rfid, status));
//               // }
//            } catch (Exception e) {
//
//            }
//            return true;
//        } else {
//            if (keyCode == KeyEvent.KEYCODE_BACK){
//                startActivity(new Intent(Inventory_form.this, MainActivity.class));
//                finish();
//            }
//        }
//        return super.onKeyUp(keyCode, event);
//    }





    public void initSoundPool() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes attributes = new AudioAttributes.Builder()
                    .setLegacyStreamType(AudioManager.STREAM_MUSIC)
                    .build();
            soundPool = new SoundPool.Builder()
                    .setMaxStreams(1)
                    .setAudioAttributes(attributes)
                    .build();
        } else {
            soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        }
        soundId = soundPool.load(this, R.raw.beep, 1);
    }




//    @Override
//    protected void onPause() {
//        super.onPause();
//        stopInventoryService();
//    }

    @Override
    protected void onStop() {
        super.onStop();
        stopInventoryService();
    }

    private void stopInventoryService() {
        if (isInventoryRunning) {
            // Stop inventory service
            iuhfService.inventoryStop();
            isInventoryRunning = false;
        }
    }
    @Override
    public void onBackPressed() {
        stopInventoryService();
        // Create an intent for the target activity
        Intent intent = new Intent(this, MainActivity.class);

        // Add flags to clear the activity stack and start a new task
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

        // Start the target activity
        startActivity(intent);

        // Finish the current activity
        finish();
    }
    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }

    private void submitAllScannItem(AllBookSDataModel allBookSDataModel) {
        if (!isNetworkAvailable(getApplicationContext())) {
            Snackbar.make(findViewById(android.R.id.content), "No Internet", Snackbar.LENGTH_SHORT).show();
            return;
        } else {
            dialog.show();
        }

        RetrofitApiClient2.getResponseFromApi().postInventoryData(allBookSDataModel).enqueue(new Callback<String>() {

            @Override
            public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                if (response.code()==200) {
                    dialog.dismiss();

                    scannedRfidList.clear();
                    txtCount.setText("");
                    Toast.makeText(Inventory_form.this, response.body(), Toast.LENGTH_SHORT).show();
                } else if (response.code() == 404) {
                    Snackbar.make(findViewById(android.R.id.content), response.body(), Snackbar.LENGTH_SHORT).show();
                    dialog.dismiss();
                } else if (response.code() == 400) {
                    Snackbar.make(findViewById(android.R.id.content), response.body(), Snackbar.LENGTH_SHORT).show();
                    dialog.dismiss();
                } else if (response.code() == 500) {
                    Snackbar.make(findViewById(android.R.id.content), response.body(), Snackbar.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Toast.makeText(Inventory_form.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }



//    private void dialogForTag() {
//        dialogTag = new Dialog(this);
//        dialogTag.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialogTag.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        dialogTag.setContentView(R.layout.all_book_layout);
//        dialogTag.setCancelable(true);
//        dialogTag.show();
//
//        Button send = dialogTag.findViewById(R.id.submitButton);
//        EditText  etName = dialogTag.findViewById(R.id.usernameEditText);
//
//        send.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialogTag.dismiss();
//                AllBookSDataModel allData = null;
//                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//                    allData = new AllBookSDataModel(
//                            etName.getText().toString(), // Replace with your EditText field reference
//                            0, // You can provide an appropriate value for someValue1
//                            scannedRfidList.size(), // You can provide an appropriate value for someValue2
//                            0, // You can provide an appropriate value for someValue3
//                            LocalDateTime.now().toString(), // You can provide an appropriate timestamp
//                }
//
//                submitAllScannItem(allData);
//
//
//            }
//        });
//
//
//    }


}





