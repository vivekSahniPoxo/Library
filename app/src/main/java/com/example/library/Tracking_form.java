package com.example.library;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.speedata.libuhf.IUHFService;
import com.speedata.libuhf.UHFManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Tracking_form extends AppCompatActivity {
    Spinner spinner_shelve_no, rack_number_Spinner;
    String[] value = new String[]{"Title", "Author", "Branch", "Selected Rack"};
    EditText rfid_number,etShelveNo,etRackNo;
    Button Add_Racking, Submit_racking, Submit_Restart;
    List<String> rack_no, list_shelve;
    String rack_Number_Selected, shelve_number_selected, shelve,rackNo;
    ProgressDialog dialog;
    List<DataModel_Racking> Item_Add_Item;
    List<DataModel_Racking> Item_Add_Temp;
    RecyclerView recyclerView;
    Adapter_Racking adapter_racking;
    CoordinatorLayout constraintLayout;
    List<List_Data_Model> data_models;
    DataModel_Racking dataModel_racking;
    IUHFService iuhfService;
    String RfidNo;
    private ToneGenerator toneGenerator;
    List<String> rfidList;


    AutoCompleteTextView autocomplete,autoCompleteRacks;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking_form);
        //Component Initialization
        constraintLayout = findViewById(R.id.coordinator);
        spinner_shelve_no = findViewById(R.id.Shelve_number_racking);
        rack_number_Spinner = findViewById(R.id.Rack_number_Racking);
        rfid_number = findViewById(R.id.Rfid_Number_Racking);
        Add_Racking = findViewById(R.id.Add_Button_Racking);
        Submit_racking = findViewById(R.id.Submit_racking);
        recyclerView = findViewById(R.id.tracking_recyclerview);
        Submit_Restart = findViewById(R.id.Restart_racking);
        etShelveNo = findViewById(R.id.et_shelvNo);
        autoCompleteRacks = findViewById(R.id.autoCompleteTextViewRacks);

        etRackNo = findViewById(R.id.et_rack_no);

//    Initialize of List and Model Class object
        Item_Add_Item = new ArrayList<>();
        data_models = new ArrayList<>();
        rfidList = new ArrayList<>();
        dataModel_racking = new DataModel_Racking();
        list_shelve = new ArrayList<>();


//     Disable Spinner
        spinner_shelve_no.setEnabled(false);

//        iuhfService = UHFManager.getUHFService(this);
//        iuhfService.openDev();
//        iuhfService.inventoryStart();

//        try {
//
//            autoCompleteRacks = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextViewRacks);
//
//            ArrayAdapter<String> adapter = new ArrayAdapter<String>(Tracking_form.this, android.R.layout.select_dialog_item, rack_no);
//
//            autocomplete.setThreshold(2);
//            autocomplete.setAdapter(adapter);
//        } catch (Exception e){
//
//        }




        //Listener on Add button
        Add_Racking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Item_Add_Item.add(new DataModel_Racking(rack_Number_Selected, shelve_number_selected, rfid_number.getText().toString()));
//                SetUPRecylerview();
                if (rfid_number.length() > 0) {
                    try {
                        FetchData();
                        rfid_number.setText("");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    rfid_number.setError("Please Enter Accession Number...");
                }
            }
        });


        //Dialog box initialize
        dialog = new ProgressDialog(this);
        dialog.show();
        dialog.setMessage(getString(R.string.Dialog_Text));
        dialog.setCancelable(false);

        Submit_racking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (etRackNo.getText().toString().equals("") && etShelveNo.getText().toString().equals("")){
                        Toast.makeText(Tracking_form.this,"Rack No & Shelve No should not be empty",Toast.LENGTH_SHORT).show();
                    } else {
                        Update_shelve();
                        dialog.show();
                        dialog.setMessage(getString(R.string.Dialog_Text));
                        dialog.setCancelable(false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

        //Method for swipe left to delete
        enableSwipeToDeleteAndUndo();


        //Method for Spinner value from Api
        //FetchRack_NO();
        dialog.dismiss();
       // list_shelve.add("Choose");

        //Initialize List
        rack_no = new ArrayList<>();
        rack_no.add("Choose");


        // Set up Detail Spinner
        final ArrayAdapter<String> spinnerArrayAdapter1 = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, rack_no);
        spinnerArrayAdapter1.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        rack_number_Spinner.setAdapter(spinnerArrayAdapter1);
        rack_number_Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                rack_Number_Selected = parent.getItemAtPosition(position).toString();
                try {
                    dialog.show();
                    dialog.setMessage(getString(R.string.Dialog_Text));
                    dialog.setCancelable(false);
                    FetchShelve_No(rack_Number_Selected);

                    autocomplete = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView1);

                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(Tracking_form.this,android.R.layout.select_dialog_item, list_shelve);

                    autocomplete.setThreshold(2);
                    autocomplete.setAdapter(adapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        Submit_Restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Clear();
            }

        });

        //Set up Spinner For Showing Shelve Number
        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, list_shelve);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner_shelve_no.setAdapter(spinnerArrayAdapter);
        spinner_shelve_no.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                shelve_number_selected = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

//        data_models.add(new List_Data_Model(dataModel_racking.getRFIDNo(),shelve_number_selected,rack_Number_Selected));

    }

    //Method for Racks Number
    private void FetchRack_NO() {
       // String url = "https://library.poxorfid.com/api/Inventory/FetchRacks";
        //String url =    "http://164.52.223.163:4558/api/Inventory/FetchRacks";
        String url = "http://192.168.0.113:83/api/Inventory/FetchRacks";
       // String url = "http://192.168.100.5:81/api/Inventory/FetchRacks";
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dialog.dismiss();
                rack_no.clear();
                rack_no.add("Choose");
                spinner_shelve_no.setEnabled(true);


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
                            rack_no.add(rackNo);

                        }

//                    dialog.dismiss();
//                    JSONObject object = new JSONObject(response);
//
//                    String one = object.getString("1");
//                    String two = object.getString("2");
//                    String three = object.getString("3");
//
//                    rack_no.add(one);
//                    rack_no.add(two);
//                    rack_no.add(three);
//                    System.out.println(one + "Value for spinner");

                } catch (JSONException jsonException) {
                    jsonException.printStackTrace();
                }
//                    final List<String> List = new ArrayList<>(Arrays.asList(value));


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
               // Toast.makeText(Tracking_form.this, error.getMessage(), Toast.LENGTH_LONG).show();
                dialog.dismiss();

            }
        });

        queue.add(request);

    }

    //Method for Fetch Shelve Number
    private void FetchShelve_No(String rack_Number_Selected) throws JSONException {
        String url = "http://192.168.0.113:83/api/Inventory/FetchShelves";
       // String url = "http://192.168.100.5:81/api/Inventory/FetchShelves";
       // String url = "https://library.poxorfid.com/api/Inventory/FetchShelves";
        //String url = "http://164.52.223.163:4558/api/Inventory/FetchShelves";
        JSONObject object = new JSONObject();
        object.put("RackNo", rack_Number_Selected);
        final String requestBody = object.toString();
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                list_shelve.clear();
                //list_shelve.add("Choose");
                spinner_shelve_no.setEnabled(true);
                try {
                    JSONArray array = new JSONArray(response);
                    for (int i = 0; i < array.length(); i++) {
                        shelve = array.getString(i);
                        list_shelve.add(shelve);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
//                Toast.makeText(Tracking_form.this, "Data Updated Successfully...." + response, Toast.LENGTH_SHORT).show();
//
                Log.i("VOLLEY", shelve);

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

    //Method for Submit the Report to Server
    private void Update_shelve() throws JSONException {
       //String url = "http://192.168.100.5:81/api/Inventory/UpdateBookLocation";
        String url = "http://192.168.0.113:83/api/Inventory/UpdateBookLocation";
        //String url = "https://library.poxorfid.com/api/Inventory/UpdateBookLocation";
        //String url = "http://164.52.223.163:4558/api/Inventory/UpdateBookLocation";
        JSONArray array = new JSONArray();
        JSONObject obj = new JSONObject();

//       obj.put("RFIDNo", rfid_number.getText().toString());
        for (int i = 0; i < data_models.size(); i++) {
            obj.put("RFIDNo", data_models.get(i).getRfidNumber());
//            obj.put("RackNo", data_models.get(i).getRackNumber());
           // obj.put("ShelfNo", data_models.get(i).getShelveNumber());
        }
        obj.put("RackNo", etRackNo.getText());
        obj.put("ShelfNo", etShelveNo.getText());
        array.put(obj);
        RequestQueue queue = Volley.newRequestQueue(this);


        final String requestBody = array.toString();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(Tracking_form.this, "Data Updated Successfully....", Toast.LENGTH_SHORT).show();

                Log.i("VOLLEY", response);
//                Item_Add_Item.clear();
//                Item_Add_Temp.clear();
                Clear();
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
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                System.out.println("Response Code Racking" + response.statusCode);
                return super.parseNetworkResponse(response);
            }

        };

        queue.add(stringRequest);
    }

    //Method for Fetch data Books data by Accession Number
    private void FetchData() throws JSONException {

      //  String url = "https://library.poxorfid.com/api/BooksInfo/FetchBookByAccessNo";
        String url = "http://192.168.0.113:83/api/BooksInfo/FetchBookByAccessNo";
        //String url = "http://192.168.100.5:81/api/BooksInfo/FetchBookByAccessNo";
        JSONObject obj = new JSONObject();
//
//        obj.put("AccessNo", "B1228");
        obj.put("AccessNo", rfid_number.getText().toString());
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, url, obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            String access_details = response.getString("AccessNo");
                            String Title = response.getString("Title");
                            String publisher = response.getString("Publisher");
                            String rfid = response.getString("RFIDNo");
                            String subject = response.getString("SubjectTitle");
                            String language = response.getString("Language");
                            String edition = response.getString("Edition");

//                           Set up Recyclerview
                            Item_Add_Item.add(new DataModel_Racking(rfid, Title, publisher));
                            dataModel_racking = new DataModel_Racking(rfid, publisher, Title);
                            adapter_racking = new Adapter_Racking(Item_Add_Item, getApplicationContext());
                            recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                            recyclerView.setAdapter(adapter_racking);


                            data_models.add(new List_Data_Model(dataModel_racking.getRfid(), shelve_number_selected, rack_Number_Selected));
                            System.out.println("DATA VALUE OF LIST" + data_models);
                            dialog.dismiss();
//                           System.out.println("Search Response "+response.toString());
                            Log.e("response Search", response.toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dialog.dismiss();
                        System.out.println("Negative Response" + error.getMessage());
                    }
                });


        queue.add(jsObjRequest);

    }

    //Method for Clearing Data
    public void Clear() {
        try {
            Item_Add_Item.clear();
            Item_Add_Temp.clear();
            adapter_racking = new Adapter_Racking(Item_Add_Item, getApplicationContext());
            recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            recyclerView.setAdapter(adapter_racking);
            rfid_number.setText("");
            list_shelve.clear();
            spinner_shelve_no.setEnabled(false);
            final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, list_shelve);
            spinnerArrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
            spinner_shelve_no.setAdapter(spinnerArrayAdapter);
        } catch (Exception e){

        }


    }

    //Method for Swipe left to delete
    private void enableSwipeToDeleteAndUndo() {

        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(this) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {


                final int position = viewHolder.getAdapterPosition();
                final DataModel_Racking item = adapter_racking.getData().get(position);
                adapter_racking.removeItem(position);


                Snackbar snackbar = Snackbar.make(constraintLayout, "Item was removed from the list.", Snackbar.LENGTH_LONG);
                snackbar.setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        adapter_racking.restoreItem(item, position);
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



//    @Override
//    public boolean dispatchKeyEvent(KeyEvent event) {
//        if (event.getAction() == KeyEvent.ACTION_DOWN) {
//
//
//
//            //data_models.add(new List_Data_Model(RfidNo, shelve_number_selected, rack_Number_Selected));
//
//            int selectedItemOfMySpinner = rack_number_Spinner.getSelectedItemPosition();
//            String actualPositionOfMySpinner = (String) rack_number_Spinner.getItemAtPosition(selectedItemOfMySpinner);
//
//
//            if (rack_number_Spinner.getSelectedItem().toString().trim().equals("Choose") ) {
//                Toast.makeText(this, "Please select rack no", Toast.LENGTH_SHORT).show();
//
//            } else {
//                if (spinner_shelve_no.getSelectedItem().toString().trim().equals("Choose")) {
//                    Toast.makeText(this, "Please select  shelve no", Toast.LENGTH_SHORT).show();
//                } else {
//                    iuhfService = UHFManager.getUHFService(this);
//                    iuhfService.openDev();
//                    iuhfService.inventoryStart();
//                    RfidNo = iuhfService.read_area(1, "2", "6", "00000000").toString();
//                    Item_Add_Item.add(new DataModel_Racking(RfidNo, shelve_number_selected, rack_Number_Selected));
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                        dataModel_racking = new DataModel_Racking(RfidNo, shelve_number_selected, rack_Number_Selected);
//                        Item_Add_Temp = Item_Add_Item.stream().distinct().collect(Collectors.toList());
//                        adapter_racking = new Adapter_Racking(Item_Add_Temp, getApplicationContext());
//                        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
//                        recyclerView.setAdapter(adapter_racking);
//                        data_models.add(new List_Data_Model(dataModel_racking.getRfid(), shelve_number_selected, rack_Number_Selected));
//                    }
//                }
//            }
//
//        }
//        return super.dispatchKeyEvent(event);
//    }


    @Override
    public void onBackPressed() {
        startActivity(new Intent(Tracking_form.this, MainActivity.class));
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_F1) {
            try {
                toneGenerator = new ToneGenerator(AudioManager.STREAM_ALARM, ToneGenerator.MAX_VOLUME);
                toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP, 100);
                VibrationUtil.vibratePhone(this);
            } catch (Exception e){
                Log.d("exception",e.toString());

            }

            try {
               // if (rack_number_Spinner.getSelectedItem().toString().trim().equals("Choose")) {
                    //Toast.makeText(this, "Please select rack no", Toast.LENGTH_SHORT).show();
                   // globalSnackbar("Please select rack no");

//                } else {
//
//                    if (spinner_shelve_no.getSelectedItem().toString().trim().equals("Choose")) {
//                        //Toast.makeText(this, "Please select  shelve no", Toast.LENGTH_SHORT).show();
//                        globalSnackbar("Please select  shelve no");

                    //}
               // else {
                        iuhfService = UHFManager.getUHFService(this);
                        iuhfService.openDev();
                        iuhfService.setAntennaPower(30);
                        //iuhfService.inventoryStart();
                        if (!rfidList.contains(RfidNo)) {
                            rfidList.add(RfidNo);
                            RfidNo = iuhfService.read_area(1, "2", "6", "00000000").toString();
                            // Item_Add_Item.add(new DataModel_Racking(RfidNo, shelve_number_selected, rack_Number_Selected));
                            // Item_Add_Item.add(new DataModel_Racking(RfidNo, shelve_number_selected, rack_Number_Selected));
                            Item_Add_Item.add(new DataModel_Racking(RfidNo, etShelveNo.getText().toString(), etRackNo.getText().toString()));
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                dataModel_racking = new DataModel_Racking(RfidNo, shelve_number_selected, rack_Number_Selected);
                                Item_Add_Temp = Item_Add_Item.stream().distinct().collect(Collectors.toList());
                                adapter_racking = new Adapter_Racking(Item_Add_Temp, getApplicationContext());
                                recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                                recyclerView.setAdapter(adapter_racking);
                                data_models.add(new List_Data_Model(dataModel_racking.getRfid(), shelve_number_selected, rack_Number_Selected));
                            }

                            //}
                        }

            } catch (Exception e){

            }

            return true;
        } else {
            if (keyCode == KeyEvent.KEYCODE_BACK){
                startActivity(new Intent(Tracking_form.this, MainActivity.class));
                finish();
            }
        }
        return super.onKeyUp(keyCode, event);
    }


    public void globalSnackbar(String text) {
        View rootView = findViewById(android.R.id.content);
        if (rootView != null) {
            Snackbar.make(rootView, text, Snackbar.LENGTH_SHORT).show();
        }
    }











}
